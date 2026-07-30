/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sahad
 */
public class Item {
    private String id;           
    private String ownerId;
    private String name;
    private String category;
    private String postcode;     
    private String location;
    private double dailyRate;
    private boolean available;
    private String condition;
    private String description;

    public Item() { }

    public Item(String id, String ownerId, String name, String category,
                String postcode, String location, double dailyRate,
                boolean available, String condition, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.category = category;
        this.postcode = postcode;
        this.location = location;
        this.dailyRate = dailyRate;
        this.available = available;
        this.condition = condition;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String v) { this.id = v; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String v) { this.ownerId = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String v) { this.postcode = v; }

    public String getLocation() { return location; }
    public void setLocation(String v) { this.location = v; }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double v) { this.dailyRate = v; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean v) { this.available = v; }

    public String getCondition() { return condition; }
    public void setCondition(String v) { this.condition = v; }

    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
}

