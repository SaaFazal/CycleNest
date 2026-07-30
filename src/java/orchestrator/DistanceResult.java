/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orchestrator;

/**
 *
 * @author sahad
 */

public class DistanceResult {
    private double distance_meters;
    private double duration_seconds;
    private String service;

    public DistanceResult() { }

    public DistanceResult(double distance_meters, double duration_seconds, String service) {
        this.distance_meters = distance_meters;
        this.duration_seconds = duration_seconds;
        this.service = service;
    }

    public double getDistance_meters() { return distance_meters; }
    public void setDistance_meters(double v) { this.distance_meters = v; }

    public double getDuration_seconds() { return duration_seconds; }
    public void setDuration_seconds(double v) { this.duration_seconds = v; }

    public String getService() { return service; }
    public void setService(String s) { this.service = s; }
}
