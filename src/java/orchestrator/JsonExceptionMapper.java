/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

/**
 *
 * @author sahad
 */
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import javax.ws.rs.Produces;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonExceptionMapper implements ExceptionMapper<Throwable> {
    @Override public Response toResponse(Throwable ex) {
        String msg = ex.getMessage() == null ? "Server error" : ex.getMessage();
        String body = "{\"error\":\"" + msg.replace("\"","'") + "\"}";
        return Response.serverError().entity(body).type(MediaType.APPLICATION_JSON).build();
    }
}
