package com.cf.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class MessageEngine {
	private String proterties;
	private String encoding;
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final Map<String, String> VALUES = new HashMap<String, String>();
	private boolean debug = false;

	public void setProterties(String proterties) {
		this.proterties = proterties;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void init() {

		if (null == this.encoding || this.encoding.trim().length() == 0) {
			encoding = DEFAULT_ENCODING;
		}
		String path = MessageEngine.class.getClassLoader().getResource("")
				.getPath();
		File file = new File(path + File.separator + this.proterties);
		Properties properties = null;
		if (file.exists() && file.isDirectory()) {
			properties = new Properties();
			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					continue;
				}
				InputStream inStream = null;
				InputStreamReader reader = null;
				try {
					inStream = new FileInputStream(files[i]);
					reader = new InputStreamReader(inStream, "UTF-8");
					properties.load(reader);
					Iterator<Entry<Object, Object>> it = properties.entrySet()
							.iterator();
					String fileName = files[i].getName();
					fileName = fileName.substring(0,
							fileName.lastIndexOf(".") + 1);
					while (it.hasNext()) {
						Entry<Object, Object> en = it.next();
						VALUES.put(fileName + en.getKey(), en.getValue()
								.toString().trim());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (null != inStream) {
							inStream.close();

						}
						if (null != reader) {
							reader.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	public String getValue(String key) {
		if (debug) {
			init();
		}
		return VALUES.get(key);
	}
}
