package com.toilet.public_toilet_api.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToiletDataUpdater {
    private final ToiletExcelImporter toiletExcelImporter;
    private final ToiletRepository toiletRepository;

    @Transactional
    public void updateAllFromWeb(){
        try {
            // ✅ SSL 인증서 검증 우회 (테스트용)
            SSLBypass.disableSSLVerification();
        } catch (Exception e) {
            System.out.println("SSL 우회 실패: " + e.getMessage());
            return;
        }
        try {
            System.out.println("🔥 deleteAll 호출 전: " + toiletRepository.count());
            toiletRepository.truncateTable();
            System.out.println("🔥 deleteAll 호출 후: " + toiletRepository.count());
        } catch (Exception e) {
            System.out.println("❌ 삭제 중 예외 발생: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
        List<String> sidoCodes = List.of(
                "6110000", "6260000", "6270000", "6280000", "6290000",
                "6300000", "6310000", "5690000", "6410000", "6530000", "6430000",
                "6440000", "6540000", "6460000", "6470000", "6480000", "6500000"
        );

        for (String code : sidoCodes){
            try{
                String url = "https://www.localdata.go.kr/lif/etcDataDownload.do?localCodeEx="+code+"&sidoCodeEx="+code+"&sigunguCodeEx=&opnSvcIdEx=12_04_01_E&startDateEx=&endDateEx=&fileType=xlsx&opnSvcNmEx=%25EA%25B3%25B5%25EC%25A4%2591%25ED%2599%2594%25EC%259E%25A5%25EC%258B%25A4%25EC%25A0%2595%25EB%25B3%25B4";

                try(InputStream is = new URL(url).openStream()){
                    toiletExcelImporter.importFromExcel(is);
                }
                System.out.println(code + "처리 완료");
            } catch (Exception e){
                System.out.println("["+code+"] 처리 중 오류 발생 :" + e.getMessage());
            }
        }
    }
}
