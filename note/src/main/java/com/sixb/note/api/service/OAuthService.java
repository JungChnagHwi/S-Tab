package com.sixb.note.api.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sixb.note.dto.idToken.IdTokenRequestDto;
import com.sixb.note.dto.token.TokenResponse;
import com.sixb.note.entity.BlackList;
import com.sixb.note.entity.RefreshToken;
import com.sixb.note.repository.BlackListRepository;
import com.sixb.note.repository.RefreshTokenRepository;
import com.sixb.note.utils.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
//@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;


    public TokenResponse login(IdTokenRequestDto idTokenDto) {
        // idtoken 받아서 파싱
        String idToken = idTokenDto.getIdToken();
//        System.out.println("idToken: "+ idToken);
        long userId = getUsercode(idToken);
//        System.out.println("usercode: "+userId);
        // refreshtoken이 있는지 보고 있으면 갱신
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId);

        if (refreshToken != null) {
            String ref = refreshToken.getRefreshToken();

            BlackList blackList = BlackList.builder()
                    .token(ref)
                    .expiration(jwtTokenProvider.getExpiration(ref))
                    .build();

            blackListRepository.save(blackList);
            refreshTokenRepository.deleteById(ref);
        }

        // access, refresh 만들어서 보내주기
        return createToken(userId);
    }

    private TokenResponse createToken(long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(userId));
        String refreshToken = jwtTokenProvider.createRefreshToken();

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(token);

        return TokenResponse.builder()
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private long getUsercode(String idToken) {
        // idToken을 통해 payload의 sub 부분 추출
        String[] splitToken = idToken.split("\\.");
        String base64EncodedPayload = splitToken[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(base64EncodedPayload));
//        System.out.println("Payload: " + payload);
        JSONObject jsonObj = new JSONObject(payload);
        String subject = jsonObj.getString("sub");
        return Long.parseLong(subject);
    }

}
