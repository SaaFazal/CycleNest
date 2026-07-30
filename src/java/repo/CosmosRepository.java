/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author sahad
 */
package repo;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.Item;
import model.RentalRequest;
import orchestrator.CosmosConfig;

import java.util.*;
import java.util.stream.Collectors;

public class CosmosRepository implements Repository, AutoCloseable {

    private final CosmosClient client;
    private final CosmosContainer itemsC;
    private final CosmosContainer rentalsC;

    public CosmosRepository() {
        // Configure Java system properties to fix Azure connection issues
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
        System.setProperty("reactor.netty.ioWorkerCount", "1");
        System.setProperty("reactor.netty.pool.maxConnections", "500");

        if (CosmosConfig.ENDPOINT == null || CosmosConfig.KEY == null
                || CosmosConfig.ENDPOINT.isBlank() || CosmosConfig.KEY.isBlank()) {
            throw new IllegalStateException("Missing COSMOS_ENDPOINT / COSMOS_KEY");
        }

        System.out.println("[CosmosRepository] ENDPOINT=" + CosmosConfig.ENDPOINT
                + " DB=" + CosmosConfig.DB
                + " ITEMS=" + CosmosConfig.ITEMS
                + " RENTALS=" + CosmosConfig.RENTALS);

        try {
            // Initialize Cosmos client with gateway mode for reliable connectivity
            this.client = new CosmosClientBuilder()
                    .endpoint(CosmosConfig.ENDPOINT)
                    .key(CosmosConfig.KEY)
                    .consistencyLevel(ConsistencyLevel.SESSION)
                    .gatewayMode()
                    .buildClient();

            System.out.println("[CosmosRepository] Client initialized successfully");
        } catch (RuntimeException re) {
            System.err.println("[CosmosRepository] Failed to initialize client: " + re.getMessage());
            throw re;
        }

        try {
            CosmosDatabase db = client.getDatabase(CosmosConfig.DB);

            this.itemsC = db.getContainer(CosmosConfig.ITEMS);
            this.rentalsC = db.getContainer(CosmosConfig.RENTALS);

            // Read metadata to prove access works and to log PK paths
            CosmosContainerResponse r1 = this.itemsC.read();
            CosmosContainerResponse r2 = this.rentalsC.read();

            String pkItems = r1.getProperties().getPartitionKeyDefinition().getPaths().toString();
            String pkRentals = r2.getProperties().getPartitionKeyDefinition().getPaths().toString();
            System.out.println(
                    "[CosmosRepository] Containers verified. PK items=" + pkItems + " pk rentals=" + pkRentals);

        } catch (CosmosException ce) {
            System.out.println("[CosmosRepository] CosmosException status=" + ce.getStatusCode()
                    + " subStatus=" + ce.getSubStatusCode()
                    + " activityId=" + ce.getActivityId());
            System.out.println("[CosmosRepository] Diagnostics:\n" + ce.getDiagnostics());
            close();
            throw new IllegalStateException("CosmosRepository init failed. See diagnostics above.", ce);
        } catch (RuntimeException re) {
            System.out.println("[CosmosRepository] RuntimeException during container read: " + re);
            close();
            throw re;
        }
    }

    // ---------- Items ----------
    @Override
    public List<Item> listItems(String locationFilter, Double maxDailyRate) {
        StringBuilder sql = new StringBuilder("SELECT * FROM c WHERE 1=1");
        List<SqlParameter> params = new ArrayList<>();

        if (locationFilter != null && !locationFilter.isBlank()) {
            sql.append(" AND c.location = @loc");
            params.add(new SqlParameter("@loc", locationFilter));
        }
        if (maxDailyRate != null) {
            sql.append(" AND c.dailyRate <= @rate");
            params.add(new SqlParameter("@rate", maxDailyRate));
        }

        CosmosQueryRequestOptions opts = new CosmosQueryRequestOptions();
        var feed = itemsC.queryItems(new SqlQuerySpec(sql.toString(), params), opts, ObjectNode.class);

        return feed.stream().map(this::toItem).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(String id) {
        try {
            
            PartitionKey pk = new PartitionKey(id);
            ObjectNode doc = itemsC.readItem(id, pk, ObjectNode.class).getItem();
            return Optional.ofNullable(toItem(doc));
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404)
                return Optional.empty();
            throw e;
        }
    }

    private Item toItem(ObjectNode n) {
        Item it = new Item();
        it.setId(n.path("id").asText(null));
        it.setOwnerId(n.path("ownerId").asText(null));
        it.setName(n.path("name").asText(null));
        it.setCategory(n.path("category").asText(null));
        it.setPostcode(n.path("postcode").asText(null));
        it.setLocation(n.path("location").asText(null));
        if (n.has("dailyRate"))
            it.setDailyRate(n.path("dailyRate").asDouble());
        it.setAvailable(n.path("available").asBoolean(false));
        it.setCondition(n.path("condition").asText(null));
        it.setDescription(n.path("description").asText(null));
        return it;
    }

    // ---------- Rentals ----------
    @Override
    public RentalRequest createRental(String itemId, String renterId, int days) {
        String id = java.util.UUID.randomUUID().toString();
        RentalRequest r = new RentalRequest(id, itemId, renterId, "pending", days);
        rentalsC.createItem(r, new PartitionKey(r.getId()), new CosmosItemRequestOptions());
        return r;
    }

    @Override
    public Optional<RentalRequest> findRentalById(String requestId) {
        try {
            RentalRequest r = rentalsC
                    .readItem(requestId, new PartitionKey(requestId), RentalRequest.class)
                    .getItem();
            return Optional.ofNullable(r);
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404)
                return Optional.empty();
            throw e;
        }
    }

    @Override
    public boolean cancelRental(String requestId) {
        try {
            PartitionKey pk = new PartitionKey(requestId);
            RentalRequest r = rentalsC.readItem(requestId, pk, RentalRequest.class).getItem();
            r.setStatus("cancelled");
            rentalsC.replaceItem(r, requestId, pk, new CosmosItemRequestOptions());
            return true;
        } catch (CosmosException e) {
            if (e.getStatusCode() == 404)
                return false;
            throw e;
        }
    }

    @Override
    public void close() {
        if (client != null)
            client.close();
    }
}
