package com.sixb.note.api.controller;

import com.sixb.note.api.service.OAuthService;
import com.sixb.note.dto.idToken.IdTokenRequestDto;
import com.sixb.note.dto.token.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody IdTokenRequestDto idToken) {
        TokenResponse response = oAuthService.login(idToken);

        return ResponseEntity.ok(response);
    }

}
