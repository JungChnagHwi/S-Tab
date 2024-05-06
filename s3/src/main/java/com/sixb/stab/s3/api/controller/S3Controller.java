package com.sixb.stab.s3.api.controller;

import com.sixb.stab.s3.api.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLConnection;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	@GetMapping
	public ResponseEntity<String> getPresignedUrl(@RequestParam long userId,
												  @RequestParam String filename) {
		String mimeType = URLConnection.guessContentTypeFromName(filename);
		String type;

		if (mimeType.split("/")[0].equals("image")) {
			type = "image";
		} else if (mimeType.split("/")[1].equals("pdf")) {
			type = "pdf";
		} else {
			return ResponseEntity.badRequest().body("잘못된 파일 타입입니다.");
		}

		String presignedUrl = s3Service.getPresignedUrl(userId, mimeType, type, filename);
		return ResponseEntity.ok(presignedUrl);
	}

}
