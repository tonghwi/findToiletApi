package com.toilet.public_toilet_api.data;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@RequiredArgsConstructor
public class ToiletExcelImporter {
    private final ToiletRepository toiletRepository;
    private final GeocodingService geocodingService;

    public void importFromExcel(InputStream is){
        try(Workbook workbook = new XSSFWorkbook(is);
            PrintWriter failWriter = new PrintWriter(new BufferedWriter(new FileWriter("failed_geocoding.txt",true)))){
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if (row == null){
                    continue;
                }

                Toilet toilet = new Toilet();

                toilet.setToiletName(getStringCell(row,3));
                toilet.setRoadAddress(getStringCell(row,4));
                toilet.setLotNumberAddress(getStringCell(row,5));
                toilet.setMaleToiletCount(getIntCell(row,6));
                toilet.setMaleUrinalCount(getIntCell(row,7));
                toilet.setMaleDisabledToiletCount(getIntCell(row,8));
                toilet.setMaleDisabledUrinalCount(getIntCell(row,9));
                toilet.setMaleChildToiletCount(getIntCell(row,10));
                toilet.setMaleChildUrinalCount(getIntCell(row,11));
                toilet.setFemaleToiletCount(getIntCell(row,12));
                toilet.setFemaleDisabledToiletCount(getIntCell(row,13));
                toilet.setFemaleChildToiletCount(getIntCell(row,14));
                toilet.setOpenTime(getStringCell(row,18));
                toilet.setToiletType(getStringCell(row,23));

                Double latitude = getDoubleCell(row,20);
                Double longitude = getDoubleCell(row,21);

                if(latitude == null || longitude == null){
                    String roadAddress = getStringCell(row, 4);
                    String lotAddress = getStringCell(row, 5);
                    GeocodingService.LatLng latLng = null;

                    for (String address : new String[]{roadAddress, lotAddress}) {
                        if (isEmpty(address)) continue;

                        int retry = 0;
                        while (latLng == null && retry < 3) {
                            try {
                                System.out.println("📍 카카오 요청 주소: " + address);
                                latLng = geocodingService.getCoordinates(address);
                                if (latLng == null) {
                                    System.out.println("⚠️ 주소 변환 실패: " + address + " (재시도 " + (retry + 1) + ")");
                                    Thread.sleep(300);
                                }
                            } catch (Exception e) {
                                String errMsg = "❌ 주소 변환 중 오류: " + address + " - " + e.getMessage();
                                System.out.println(errMsg);
                                failWriter.println(errMsg);
                                e.printStackTrace();
                            }
                            retry++;
                        }

                        if (latLng != null) break; // 성공했으면 다음 주소 시도 안 함
                    }

                    if (latLng != null) {
                        try {
                            toilet.setLatitude(Double.parseDouble(latLng.latitude()));
                            toilet.setLongitude(Double.parseDouble(latLng.longitude()));
                        } catch (NumberFormatException e) {
                            System.out.println("❌ 변환 실패: 위도/경도 Double 파싱 오류");
                            failWriter.println("❌ 변환 실패: " + latLng.latitude() + ", " + latLng.longitude());
                            continue; // 이 화장실은 저장 안 하고 넘어감
                        }
                    } else {
                        String failMsg = "❌ 최종 실패: 위도/경도 못찾음 - 도로명: " + roadAddress + ", 지번: " + lotAddress;
                        System.out.println(failMsg);
                        failWriter.println(failMsg);
                    }


                } else{
                    toilet.setLatitude(latitude);
                    toilet.setLongitude(longitude);
                }

                toiletRepository.save(toilet);
                // 8건마다 1초 대기 (초당 10건 제한 대비 안전)
                if (i % 8 == 0) {
                    Thread.sleep(1000);
                }

                // 50건마다 진행 로그
                if (i % 50 == 0) {
                    System.out.println("✅ " + i + "건 처리 완료");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double getDoubleCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) return null;

            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                return Double.parseDouble(cell.toString().trim());
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);                     // 인덱스 위치의 셀을 가져오기
        return (cell != null) ? cell.toString().trim() : null; // 셀 값이 있으면 문자열로, 없으면 null
    }

    // 셀에서 정수(Integer) 읽어오는 함수
    private Integer getIntCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);                     // 셀 가져오기
            if (cell == null) return null;

            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();        // 숫자 타입이면 그대로 반환
            } else {
                return Integer.parseInt(cell.toString().trim()); // 문자열이면 파싱해서 정수로
            }
        } catch (Exception e) {
            return null; // 파싱 실패하면 null 반환
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


}
