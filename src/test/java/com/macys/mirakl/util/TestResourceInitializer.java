package com.macys.mirakl.util;

import java.io.File;
import java.nio.file.Files;

import org.springframework.util.ResourceUtils;

public class TestResourceInitializer {

	public static String readJsonFile(String location) throws Exception {
		String fileContent = null;
		File file = ResourceUtils.getFile("classpath:" + location);
		if (file.exists()) {
			byte[] fileData = Files.readAllBytes(file.toPath());
			fileContent = new String(fileData);
		}
		return fileContent;
	}

}
