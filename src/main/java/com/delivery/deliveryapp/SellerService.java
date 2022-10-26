package com.delivery.deliveryapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {
    @Autowired
    SellerRepository sellerRepository;

    public List<Seller> getAll() {
        List<Seller> sellers = sellerRepository.findAll();
        return sellers;
    }

    public List<HashMap<String, String>> sellersNearLocation(float lat, float lng, SellerCategory category) {
        List<HashMap<String, String>> sellers = new ArrayList<>();
        for (Seller s : this.getAll()) {
            double distance = calculateDistance(lat, lng, s);
            if (distance < 10) {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", s.getName());
                map.put("description", String.valueOf(s.getDescription()));
                map.put("distance", String.format("%.0f", distance) + " km");
                map.put("time", calculateDeliveryTime(distance));
                sellers.add(map);
            }
        }
        sellers.sort(Comparator.comparing((HashMap<String, String> s) -> Integer.parseInt(s.get("distance").split(" ")[0])));
        return sellers;
    }

    public String addSeller(Seller s) throws Exception {
        if (s.getName() == null) {
            throw new Exception("Sellers must have a name");
        }
        if (s.getName().length() < 3) {
            throw new Exception("Sellers must have a name longer than 2");
        }
        if (s.getLat() == 0) {
            throw new Exception("Lat must be provided");
        }
        if (s.getLng() == 0) {
            throw new Exception("Lng must be provided");
        }
        sellerRepository.save(s);
        return s.getName() + " successfully added";
    }

    private int calculateDistance(float lat, float lng, Seller s) {
        // lat2 = s.getLat == sLatRadi
        // lat1 = lat == latRadi
        // lon2 = s.getLng
        // lon1 = lon
        // modified from https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
        double dLat = Math.toRadians(s.getLat() - lat);
        double dLng = Math.toRadians(s.getLng() - lng);

        // convert to radians
        double latRadi = Math.toRadians(lat);
        double sLatRadi = Math.toRadians(s.getLat());

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLng / 2), 2) * Math.cos(latRadi) * Math.cos(sLatRadi);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) Math.round(rad * c);
    }

    private String calculateDeliveryTime(double distance) {
        // Delivery cyclists move at about 0.5 km per minute
        double speed = 0.5; // km per minute
        double time = distance / speed; // minutes
        time = 5 * Math.round(time / 5);
        // Restaurants take about 10 minutes to cook food
        time = time + 10;
        return (int) time + "-" + (int) (time + 5) + " min";
    }
}
