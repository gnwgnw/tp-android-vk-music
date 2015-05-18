package com.tp.vkplayer;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * author s.titaevskiy on 16.05.15.
 */
public class API {

	private final String USER_NAME = "+79153891621";
	private final String USER_PASSWORD = "MilyaMilya";
	private final String CLIENT_ID = "4917967";

	private final APIListener apiListener;
	private String accessToken;

	public API(APIListener apiListener) {
		this.apiListener = apiListener;
		//TODO add token to DB
	}

	public void initialize(Context context) {
		final Map<String, String> params = new HashMap<>();
		params.put("client_id", CLIENT_ID);
		params.put("response_type", "token");
		params.put("scope", "audio");

		final String url = APIHelper.buildUrl("https://oauth.vk.com/authorize", params);

		WebView webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);
		CookieManager.getInstance().setCookie(".vk.com", "remixsid=");

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				view.setWebViewClient(new WebViewClient() {
					@Override
					public void onPageFinished(WebView view, String url) {
						accessToken = APIHelper.getAccessTokenFromUrl(url);
						apiListener.onAccessTokenCame();
					}
				});

				view.loadUrl("javascript:" +
						"document.getElementsByName('email')[0].value='" + USER_NAME + "';" +
						"document.getElementsByName('pass')[0].value='" + USER_PASSWORD + "';" +
						"document.forms[0].submit();");
			}
		});

		webView.loadUrl(url);
	}

	public void searchSongs(String query, int performer, int count, int offset) {
		final Map<String, String> params = new HashMap<>();
		params.put("access_token", accessToken);
		params.put("auto_complete", String.valueOf(1));
		params.put("q", query);
		params.put("performer_only", String.valueOf(performer));
		params.put("count", String.valueOf(count));
		params.put("offset", String.valueOf(offset));

		final String url = APIHelper.buildUrl("https://api.vk.com/method/audio.search", params);

		final RequestAPITask apiTask =
				new RequestAPITask(new RequestAPITask.RequestAPITaskListener() {
					@Override
					public void onPostExecute(String response) {
						apiListener.onSearchDone(parseSongsResponse(response));
					}
				});
		apiTask.execute(url);
	}

	private List<SongObject> parseSongsResponse(String response) {
		final List<SongObject> songs = new LinkedList<>();

		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONArray items = jsonObject.getJSONObject("response").getJSONArray("items");

			for (int i = 0; i < items.length(); ++i) {
				songs.add(new SongObject(items.getJSONObject(i)));
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		return songs;
	}

	public interface APIListener {
		void onAccessTokenCame();

		void onSearchDone(List<SongObject> songs);
	}
}
