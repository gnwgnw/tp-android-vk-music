package com.tp.vkplayer;

/**
 * author s.titaevskiy on 16.05.15.
 */
public class MusicObject {

	private String artist;
	private String title;
	private String url;

	public MusicObject(String artist, String title, String url) {
		this.artist = artist;
		this.title = title;
		this.url = url;
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
