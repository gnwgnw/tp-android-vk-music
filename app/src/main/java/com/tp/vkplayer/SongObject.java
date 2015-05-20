package com.tp.vkplayer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author s.titaevskiy on 16.05.15.
 */
public class SongObject {

	private String artist;
	private String title;
	private String url;
	//TODO album img - iTunes

	public SongObject(String artist, String title, String url) {
		this.artist = artist;
		this.title = title;
		this.url = url;
	}

	public SongObject(JSONObject jsonObject) {
		try {
			this.artist = jsonObject.getString("artist");
			this.title = jsonObject.getString("title");
			this.url = jsonObject.getString("url");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}
}
