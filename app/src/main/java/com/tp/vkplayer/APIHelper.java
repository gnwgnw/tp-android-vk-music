package com.tp.vkplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * author s.titaevskiy on 14.05.15.
 */
public class APIHelper {

	public static String buildUrl(String path, Map<String, String> params) {
		StringBuilder builder = new StringBuilder(path);

		if (!params.isEmpty()) {
			builder.append("?");

			for (Map.Entry<String, String> param : params.entrySet()) {
				builder.append(param.getKey())
						.append("=")
						.append(param.getValue())
						.append("&");
			}

			builder.append("v=5.32");
		}
		return builder.toString();
	}

	public static String httpRequest(String url) {
		final StringBuilder response = new StringBuilder();
		HttpURLConnection connection = null;
		String encodedUrl = "";

		try {
			encodedUrl = URLEncoder.encode(url, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			connection = (HttpURLConnection) new URL(encodedUrl).openConnection();
			connection.connect();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return response.toString();
	}
}
