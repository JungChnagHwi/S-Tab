package com.sixb.note.util;

import java.time.Duration;

public class Const {

	public static final String PAGE = "page";
	public static final Duration PAGE_CACHE_EXPIRE_KEY_TIME = Duration.ofMinutes(50L);
	public static final Duration PAGE_CACHE_EXPIRE_TIME = Duration.ofHours(1L);
	public static final String INIT_PAGE_DATA = "{\"paths\": [], \"figures\": [], \"textBoxes\": [], \"images\": []}";

}
