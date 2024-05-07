package com.sixb.stab.ocr.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixb.stab.ocr.dto.ImageRecognitionResults;
import com.sixb.stab.ocr.dto.Message;
import com.sixb.stab.ocr.feign.OcrFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
@RequiredArgsConstructor
public class OcrService {

	private final OcrFeignClient ocrFeignClient;

	public ImageRecognitionResults getOcrResult(MultipartFile file) throws IOException {
		String message = new ObjectMapper().writeValueAsString(new Message());
		return ocrFeignClient.getTextFromImage(message, file);
	}

}
