/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Item;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("items")
@Produces(MediaType.APPLICATION_JSON)
public class ItemsResource {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final repo.Repository REPO = initRepo();

    private static repo.Repository initRepo() {
        try {
            var r = new repo.CosmosRepository();
            System.out.println("[ItemsResource] Repository initialized successfully");
            return r;
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("[ItemsResource] Cosmos initialization failed, using in-memory fallback");
            return new repo.InMemoryStore();
        }
    }

    @GET
    public Response search(@QueryParam("location") String location,
            @QueryParam("maxRate") Double maxRate,
            @QueryParam("category") String category,
            @QueryParam("available") Boolean available) {
        try {
            List<Item> base = REPO.listItems(location, maxRate);

            if (category != null && !category.isBlank()) {
                base.removeIf(i -> i.getCategory() == null ||
                        !i.getCategory().equalsIgnoreCase(category));
            }

            // Filter by availability if parameter is provided
            if (available != null) {
                base.removeIf(i -> i.isAvailable() != available);
            }

            return Response.ok(MAPPER.writeValueAsString(base))
                    .type(MediaType.APPLICATION_JSON)
                    .header("X-Repo", REPO.getClass().getName())
                    .build();

        } catch (Throwable t) {
            t.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to search items\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
