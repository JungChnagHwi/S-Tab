package com.sixb.stab.ocr.dto;

import lombok.Getter;

import java.util.*;

@Getter
public class Message {

	private final String version = "V2";
	private final String requestId = UUID.randomUUID().toString();
	private final long timestamp = System.currentTimeMillis();
	private final List<Image> images = List.of(new Image());

	@Getter
	public static class Image {
		private final String format = "png";
		private final String name = "image";
	}

}
