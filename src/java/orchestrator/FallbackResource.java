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

@Path("{path: .*}")                 
@Produces(MediaType.APPLICATION_JSON)
public class FallbackResource {

    private Response json404() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"not found\"}")
                .build();
    }

    @GET
    public Response get() { return json404(); }

    @POST
    public Response post(String body) { return json404(); }

    @PUT
    public Response put(String body) { return json404(); }

    @DELETE
    public Response delete() { return json404(); }

    @OPTIONS
    public Response options() { return json404(); }

    @HEAD
    public Response head() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

