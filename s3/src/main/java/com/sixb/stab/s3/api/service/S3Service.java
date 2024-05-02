package com.sixb.stab.s3.api.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String getPresignedUrl(String nickname, String mimeType, String type, String fileName) {
		return amazonS3.generatePresignedUrl(getGeneratePresignedUrlRequest(bucket, mimeType, createPath(nickname, type, fileName))).toString();
	}

	private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String mimeType, String fileName) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
				new GeneratePresignedUrlRequest(bucket, fileName)
						.withMethod(HttpMethod.PUT)
						.withExpiration(getPresignedUrlExpiration());

		generatePresignedUrlRequest.addRequestParameter("Content-type", mimeType);

		return generatePresignedUrlRequest;
	}

	private Date getPresignedUrlExpiration() {
		return new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
	}

	private String createFileId() {
		return UUID.randomUUID().toString();
	}

	private String createPath(String nickname, String type, String fileName) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String now = format.format(new Date());
		return type + "/" + now + "/" + nickname + "/" + createFileId() + "_" + fileName;
	}

}
