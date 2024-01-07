package kr.co.conceptbe.member;

import java.util.Arrays;
import kr.co.conceptbe.member.exception.NotFoundRegionException;
import lombok.Getter;

@Getter
public enum Region {
    SEOUL("서울특별시"),
    BUSAN("부산광역시"),
    DAEGU("대구광역시"),
    INCHEON("인천광역시"),
    GWANGJU("광주광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    SEJONG("세종특별자치시"),
    GYEONGGI("경기도"),
    GANGWON("강원특별자치도"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    JEONBUK("전라북도"),
    JEONNAM("전라남도"),
    GYEUNGBUK("경상북도"),
    GYEUNGNAM("경상남도"),
    JEJU("제주특별자치도"),
    ;

    private String name;

    Region(String name) {
        this.name = name;
    }

    public static Region from(String inputRegion) {
        return Arrays.stream(values())
            .filter(region -> region.getName().equals(inputRegion))
            .findFirst()
            .orElseThrow(NotFoundRegionException::new);
    }
}
