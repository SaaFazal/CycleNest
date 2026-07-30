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

import java.util.List;
import java.util.Optional;

public interface Repository {
  
    List<Item> listItems(String locationFilter, Double maxDailyRate);
    Optional<Item> findItemById(String id);

    RentalRequest createRental(String itemId, String renterId, int days);
    Optional<RentalRequest> findRentalById(String requestId);
    boolean cancelRental(String requestId);
}

