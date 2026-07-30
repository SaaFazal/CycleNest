/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

/**
 * Configuration for Cosmos DB connection.
 * 
 * @author sahad
 */
public final class CosmosConfig {

    private static final String HARDCODED_ENDPOINT = "https://myfreedbn1249874.documents.azure.com:443/";
    private static final String HARDCODED_KEY = ""; // Removed for public repo
    private static final String HARDCODED_DB = "coursework";
    private static final String HARDCODED_ITEMS = "items";
    private static final String HARDCODED_RENTALS = "rentals";

    public static final String ENDPOINT = pick("COSMOS_ENDPOINT", HARDCODED_ENDPOINT);
    public static final String KEY = pick("COSMOS_KEY", HARDCODED_KEY);
    public static final String DB = pick("COSMOS_DB", HARDCODED_DB);
    public static final String ITEMS = pick("COSMOS_ITEMS_CONTAINER", HARDCODED_ITEMS);
    public static final String RENTALS = pick("COSMOS_RENTALS_CONTAINER", HARDCODED_RENTALS);

    private static String pick(String k, String dflt) {
        String v = System.getenv(k);
        if (v == null || v.isEmpty())
            v = System.getProperty(k);
        return (v == null || v.isEmpty()) ? dflt : v;
    }

    private CosmosConfig() {
    }
}
