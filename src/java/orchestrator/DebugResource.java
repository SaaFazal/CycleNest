/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

/**
 *
 * @author sahad
 */

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("debug")
@Produces(MediaType.APPLICATION_JSON)
public class DebugResource {

    @GET @Path("ping")
    public Response ping() {
        return Response.ok("{\"ok\":true}", MediaType.APPLICATION_JSON).build();
    }

    
@GET @Path("http")
@Produces(MediaType.APPLICATION_JSON)
public Response httpProbe() {
    try {
        java.net.URL u = new java.net.URL(orchestrator.CosmosConfig.ENDPOINT + "dbs");
        var conn = (java.net.HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode(); // expect 401
        String msg = "{\"ok\":true,\"code\":"+code+"}";
        return Response.ok(msg, MediaType.APPLICATION_JSON).build();
    } catch (Throwable t) {
        String msg = "{\"ok\":false,\"error\":\""+t.getClass().getSimpleName()+"\",\"message\":\""+t.getMessage()+"\"}";
        return Response.serverError().entity(msg).type(MediaType.APPLICATION_JSON).build();
    }
}


    // Try to construct CosmosRepository and return full error details
    @GET @Path("cosmos")
    public Response cosmos() {
        try (repo.CosmosRepository r = new repo.CosmosRepository()) {
            String json = String.format(
                "{\"status\":\"ok\",\"db\":\"%s\",\"items\":\"%s\",\"rentals\":\"%s\"}",
                safe(CosmosConfig.DB), safe(CosmosConfig.ITEMS), safe(CosmosConfig.RENTALS)
            );
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Throwable t) {
            String msg = String.format(
                "{\"status\":\"fail\",\"type\":\"%s\",\"message\":\"%s\",\"causeType\":\"%s\",\"causeMessage\":\"%s\"}",
                t.getClass().getSimpleName(), safe(t.getMessage()),
                (t.getCause()==null?"":t.getCause().getClass().getSimpleName()),
                (t.getCause()==null?"":safe(t.getCause().getMessage()))
            );
            return Response.serverError().entity(msg).type(MediaType.APPLICATION_JSON).build();
        }
    }

    private static String safe(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}





