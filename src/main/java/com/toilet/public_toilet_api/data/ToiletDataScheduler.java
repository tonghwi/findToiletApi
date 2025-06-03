package com.toilet.public_toilet_api.data;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToiletDataScheduler {

    private final ToiletDataUpdater toiletDataUpdater;

    @Scheduled(cron = "0 0 1 1 * *") // 매달 1일 새벽 1시
    public void updateMonthly() {
        System.out.println("🚽 공중화장실 데이터 갱신 시작");
        toiletDataUpdater.updateAllFromWeb();
    }
}