/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repo;

/**
 *
 * @author sahad
 */
import model.Item;
import model.RentalRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryStore implements Repository {

    private final Map<String, Item> items = new ConcurrentHashMap<>();
    private final Map<String, RentalRequest> rentals = new ConcurrentHashMap<>();

    public InMemoryStore() {
        putItem(new Item("i-001", "u-100", "Road Bike", "Bike",   "NG1", "Nottingham", 15.0, true,  "Good",     "Lightweight, fast"));
        putItem(new Item("i-002", "u-101", "Tripod",    "Camera", "NG7", "Nottingham",  6.0, true,  "Excellent", "Sturdy aluminium"));
        putItem(new Item("i-003", "u-102", "Drill",     "Tools",  "NG2", "Nottingham",  8.0, false, "Fair",      "Cordless, 18V"));
    }

    private void putItem(Item it) {
        items.put(it.getId(), it);
    }

    @Override
public List<Item> listItems(String locationFilter, Double maxDailyRate) {
    return items.values().stream()
            .filter(i -> locationFilter == null || locationFilter.isBlank()
                      || (i.getLocation() != null && i.getLocation().equalsIgnoreCase(locationFilter)))
            // primitive double: no null check
            .filter(i -> maxDailyRate == null || i.getDailyRate() <= maxDailyRate)
            .collect(Collectors.toList());
}


    @Override
    public Optional<Item> findItemById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public RentalRequest createRental(String itemId, String renterId, int days) {
        String id = UUID.randomUUID().toString();
        RentalRequest r = new RentalRequest(id, itemId, renterId, "pending", days);
        rentals.put(id, r);
        return r;
    }

    @Override
    public Optional<RentalRequest> findRentalById(String requestId) {
        return Optional.ofNullable(rentals.get(requestId));
    }

    @Override
    public boolean cancelRental(String requestId) {
        RentalRequest r = rentals.get(requestId);
        if (r == null) return false;
        r.setStatus("cancelled");
        return true;
    }
}


