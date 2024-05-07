package com.sixb.stab.ocr.api.controller;

import com.sixb.stab.ocr.api.service.OcrService;
import com.sixb.stab.ocr.dto.ImageRecognitionResults;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

	private final OcrService ocrService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> getOcrResult(@RequestParam MultipartFile file) throws IOException {
		ImageRecognitionResults response = ocrService.getOcrResult(file);
		return ResponseEntity.ok(response);
	}

}
