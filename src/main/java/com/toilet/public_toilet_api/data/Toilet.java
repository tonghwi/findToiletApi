package com.toilet.public_toilet_api.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Toilet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String toiletName; //화장실이름
    private String roadAddress; //도로명주소
    private String lotNumberAddress;//지번주소

    private Integer maleToiletCount;
    private Integer maleUrinalCount;
    private Integer maleDisabledToiletCount;
    private Integer maleDisabledUrinalCount;
    private Integer maleChildToiletCount;
    private Integer maleChildUrinalCount;

    private Integer femaleToiletCount;
    private Integer femaleDisabledToiletCount;
    private Integer femaleChildToiletCount;

    private String openTime; //열려있는시간
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String toiletType ; //수세식 or 수거식



}
