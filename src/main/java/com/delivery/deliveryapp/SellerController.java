package com.delivery.deliveryapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
public class SellerController {
    @Autowired
    SellerService sellerService;

    @GetMapping("/locations")
    public ResponseEntity<List<?>> getAllLocations() {
        return ResponseEntity.status( HttpStatus.OK).body( sellerService.getAll() );
    }

    @GetMapping("/location/")
    public ResponseEntity getSellers(@RequestParam float lat, @RequestParam float lng, @RequestParam("category") SellerCategory category) {
        List<HashMap<String, String>> sellers = sellerService.sellersNearLocation(lat, lng, category);
        return ResponseEntity.status(HttpStatus.OK).body(sellers);
    }

    @PostMapping("/addSeller")
    public ResponseEntity addSeller(@RequestBody Seller seller) {
        String message = null;
        try {
            message = sellerService.addSeller(seller);
        } catch (Exception e) {
            String messageJSON = "{\"message\":\"" + e.getMessage()+ "\"}";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageJSON);
        }
        String messageJSON = "{\"message\":\"" + message + "\"}";
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(messageJSON);
    }

}
