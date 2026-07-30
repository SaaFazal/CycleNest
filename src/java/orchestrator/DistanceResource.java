/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST endpoint for calculating distances between locations using OSRM.
 * 
 * @author sahad
 */
@Path("distance")
public class DistanceResource {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDistance(@QueryParam("oLat") Double oLat,
            @QueryParam("oLng") Double oLng,
            @QueryParam("dLat") Double dLat,
            @QueryParam("dLng") Double dLng) {
        if (oLat == null || oLng == null || dLat == null || dLng == null) {
            return jsonError(Response.Status.BAD_REQUEST,
                    "Missing query params: oLat, oLng, dLat, dLng");
        }

        String urlStr = "https://router.project-osrm.org/route/v1/driving/"
                + oLng + "," + oLat + ";" + dLng + "," + dLat
                + "?overview=false&alternatives=false&steps=false";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .timeout(Duration.ofSeconds(12))
                    .GET()
                    .build();

            CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(request,
                    HttpResponse.BodyHandlers.ofString());

            HttpResponse<String> response = future.join();

            return Response.ok(parseOSRMResponse(response), MediaType.APPLICATION_JSON).build();

        } catch (java.util.concurrent.CompletionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;

            if (cause instanceof java.net.http.HttpTimeoutException) {
                return jsonError(Response.Status.GATEWAY_TIMEOUT, "Timeout calling OSRM");
            } else if (cause instanceof IOException) {
                return jsonError(Response.Status.BAD_GATEWAY,
                        "I/O error calling OSRM: " + cause.getMessage());
            } else {
                return jsonError(Response.Status.INTERNAL_SERVER_ERROR,
                        "Error calling OSRM: " + cause.getMessage());
            }
        } catch (Exception e) {
            return jsonError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Failed to create OSRM request: " + e.getMessage());
        }
    }

    private String parseOSRMResponse(HttpResponse<String> response) throws Exception {
        int status = response.statusCode();
        String body = response.body();

        if (status != 200) {
            throw new IOException("OSRM returned non-200 status: " + status);
        }

        JsonNode root = mapper.readTree(body);

        if (!"Ok".equalsIgnoreCase(root.path("code").asText())) {
            throw new IOException("OSRM returned non-Ok code");
        }

        JsonNode routes = root.path("routes");
        if (!routes.isArray() || routes.size() == 0) {
            throw new IOException("Missing routes[0] in OSRM response");
        }

        JsonNode route0 = routes.get(0);
        double distanceMeters = route0.path("distance").asDouble(Double.NaN);
        double durationSeconds = route0.path("duration").asDouble(Double.NaN);

        if (Double.isNaN(distanceMeters) || Double.isNaN(durationSeconds)) {
            throw new IOException("Missing distance/duration in OSRM response");
        }

        DistanceResult result = new DistanceResult(distanceMeters, durationSeconds, "osrm-route");
        return mapper.writeValueAsString(result);
    }

    private static Response jsonError(Response.Status status, String msg) {
        String body = "{\"error\":\"" + msg.replace("\"", "'") + "\"}";
        return Response.status(status).entity(body).type(MediaType.APPLICATION_JSON).build();
    }
}
