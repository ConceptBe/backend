package kr.co.conceptbe.idea.domain.vo;

import java.util.Arrays;

import kr.co.conceptbe.idea.exception.NotFoundCooperationWayException;

public enum CooperationWay {

    NO_MATTER("상관없음"),
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    ;

    private final String value;

    CooperationWay(String value) {
        this.value = value;
    }

    public static CooperationWay from(String input) {
        return Arrays.stream(values())
                .filter(cooperationWay -> cooperationWay.value.equals(input))
                .findFirst()
                .orElseThrow(NotFoundCooperationWayException::new);
    }

    public String getValue() {
        return value;
    }

    public boolean isInclude(String value) {
        if (this == NO_MATTER) {
            return true;
        }

        return this.value.equals(value);
    }

}
