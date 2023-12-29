package kr.co.conceptbe.auth.infra.oauth.kakao.client;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import kr.co.conceptbe.auth.infra.oauth.kakao.dto.KakaoMemberResponse;
import kr.co.conceptbe.auth.infra.oauth.kakao.dto.KakaoResponseToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface KakaoApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoResponseToken requestToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange("https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse requestMemberInformation(@RequestHeader(name = AUTHORIZATION) String accessToken);
}
