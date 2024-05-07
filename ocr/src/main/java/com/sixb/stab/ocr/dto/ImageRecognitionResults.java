package com.sixb.stab.ocr.dto;

import lombok.Data;

import java.util.*;

@Data
public class ImageRecognitionResults {

	private String version;
	private String requestId;
	private long timestamp;
	private List<Image> images;

	@Data
	public static class Image {
		private String uid;
		private String name;
		private String inferResult;
		private String message;
		private ValidationResult validationResult;
		private ConvertedImageInfo convertedImageInfo;
		private List<ImageField> fields;
	}

	@Data
	public static class ValidationResult {
		private String result;
	}

	@Data
	public static class ConvertedImageInfo {
		private int width;
		private int height;
		private int pageIndex;
		private boolean longImage;
	}

	@Data
	public static class ImageField {
		private String valueType;
		private String inferText;
		private float inferConfidence;
		private BoundingPoly boundingPoly;
		private String type;
		private boolean lineBreak;
	}

	@Data
	public static class BoundingPoly {
		private List<Vertex> vertices;
	}

	@Data
	public static class Vertex {
		private float x;
		private float y;
	}

}
