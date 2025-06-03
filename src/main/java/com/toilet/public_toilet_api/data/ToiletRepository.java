package com.toilet.public_toilet_api.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ToiletRepository extends JpaRepository<Toilet,Long> {


    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE toilet", nativeQuery = true)
    void truncateTable();

    @Query(value = """
        SELECT *, (
            6371000 * acos(
                cos(radians(:lat)) * cos(radians(latitude)) *
                cos(radians(longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(latitude))
            )
        ) AS distance
        FROM toilets
        HAVING distance <= :radius
        ORDER BY distance
        LIMIT 100
        """, nativeQuery = true)
    List<Toilet> findNearbyToilets(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );
}
