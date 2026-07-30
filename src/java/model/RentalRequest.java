/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sahad
 */

public class RentalRequest {
    private String id;           // request id
    private String itemId;
    private String renterId;
    private String status;       // "pending" | "cancelled" | "approved" (future)
    private int days;

    public RentalRequest() { }

    public RentalRequest(String id, String itemId, String renterId, String status, int days) {
        this.id = id;
        this.itemId = itemId;
        this.renterId = renterId;
        this.status = status;
        this.days = days;
    }

    public String getId() { return id; }
    public void setId(String v) { this.id = v; }

    public String getItemId() { return itemId; }
    public void setItemId(String v) { this.itemId = v; }

    public String getRenterId() { return renterId; }
    public void setRenterId(String v) { this.renterId = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public int getDays() { return days; }
    public void setDays(int v) { this.days = v; }
}

