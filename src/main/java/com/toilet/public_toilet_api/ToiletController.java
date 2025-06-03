package com.toilet.public_toilet_api;

import com.toilet.public_toilet_api.data.Toilet;
import com.toilet.public_toilet_api.data.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/toilets")
@RequiredArgsConstructor
public class ToiletController {
    private final ToiletRepository toiletRepository;

    @GetMapping
    public ResponseEntity<List<Toilet>> getNearbyToilets(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "500") double radius) {
        List<Toilet> toilets = toiletRepository.findNearbyToilets(lat,lng,radius);
        return ResponseEntity.ok(toilets);

    }
}
