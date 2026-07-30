/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.RentalRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("rentals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentalsResource {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // COSMOS-ONLY
    private static final repo.Repository REPO = initRepo();

    private static repo.Repository initRepo() {
        try {
            System.out.println("[RentalsResource] Initializing CosmosRepository…");
            var r = new repo.CosmosRepository();
            System.out.println("[RentalsResource] CosmosRepository READY");
            return r;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException("Cosmos init failed. See logs for details.", t);
        }
    }

    // DTO for POST /rentals
    public static class CreateRentalDTO {
        public String itemId;
        public String renterId;
        public int days;
        public CreateRentalDTO() {}
    }

    @POST
    public Response createRental(String body, @Context UriInfo uriInfo) {
        try {
            CreateRentalDTO in = MAPPER.readValue(body, CreateRentalDTO.class);

            if (in.itemId == null || in.itemId.isBlank()
             || in.renterId == null || in.renterId.isBlank()
             || in.days <= 0) {
                return badRequest("itemId, renterId, days are required");
            }
            if (REPO.findItemById(in.itemId).isEmpty()) {
                return badRequest("Unknown itemId");
            }

            RentalRequest r = REPO.createRental(in.itemId, in.renterId, in.days);
            UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(r.getId());

            return Response.created(ub.build())
                    .entity(MAPPER.writeValueAsString(r))
                    .header("X-Repo", REPO.getClass().getName())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return serverError("Failed to create rental");
        }
    }

    @DELETE @Path("{id}")
    public Response cancelRental(@PathParam("id") String id) {
        try {
            boolean ok = REPO.cancelRental(id);
            if (!ok) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"request not found\"}").build();
            }
            return Response.noContent()
                    .header("X-Repo", REPO.getClass().getName())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return serverError("Failed to cancel rental");
        }
    }

    @GET @Path("{id}")
    public Response getRental(@PathParam("id") String id) {
        try {
            var opt = REPO.findRentalById(id);
            if (opt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"request not found\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return Response.ok(MAPPER.writeValueAsString(opt.get()), MediaType.APPLICATION_JSON)
                    .header("X-Repo", REPO.getClass().getName())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return serverError("Failed to fetch rental");
        }
    }

    private static Response badRequest(String msg) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + msg.replace("\"", "'") + "\"}").build();
    }
    private static Response serverError(String msg) {
        return Response.serverError()
                .entity("{\"error\":\"" + msg.replace("\"", "'") + "\"}").build();
    }
}




