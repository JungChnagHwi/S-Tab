package com.sixb.stab.ocr.feign;

import com.sixb.stab.ocr.config.FormConfig;
import com.sixb.stab.ocr.dto.ImageRecognitionResults;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "OcrFeignClient",
		url = "${feign.naver-cloud.clova-ocr-api.url}",
		configuration = FormConfig.class)
public interface OcrFeignClient {

	@PostMapping(value = "${feign.naver-cloud.clova-ocr-api.path}",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			headers = "X-OCR-SECRET=${feign.naver-cloud.clova-ocr-api.secret-key}")
	ImageRecognitionResults getTextFromImage(@RequestPart("message") String message,
											 @RequestPart("file") MultipartFile file);

}
