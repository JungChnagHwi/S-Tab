package com.sixb.note.util;

import java.util.*;

public class IdCreator {

	public static String create(String header) {
		return header + "-" + UUID.randomUUID();
	}

}
