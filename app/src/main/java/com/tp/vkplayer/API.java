package com.tp.vkplayer;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author s.titaevskiy on 16.05.15.
 */
public class API {

	private final String USER_NAME = "";
	private final String USER_PASSWORD = "";
	private final String CLIENT_ID = "4917967";
	
	private final AccessTokenListener accessTokenListener;
	private String accessToken;

	public API(AccessTokenListener accessTokenListener) {
		this.accessTokenListener = accessTokenListener;
	}

	public String initialize(Context context) {
		String url = new StringBuilder("https://oauth.vk.com")
				.append("/authorize")
				.append("?")
				.append("client_id=")
				.append(CLIENT_ID)
				.append("&")
				.append("response_type=")
				.append("token")
				.append("&")
				.append("scope=")
				.append("audio")
				.toString();

		WebView webView = new WebView(context);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				view.setWebViewClient(new WebViewClient() {
					@Override
					public void onPageFinished(WebView view, String url) {
						Pattern p = Pattern.compile("access_token=([^&]+)");
						Matcher m = p.matcher(url);
						m.find();
						accessToken = m.group(1);
						accessTokenListener.onAccessTokenCame();
					}
				});

				view.loadUrl("javascript:" +
						"document.getElementsByName('email')[0].value='" + USER_NAME + "';" +
						"document.getElementsByName('pass')[0].value='" + USER_PASSWORD + "';" +
						"document.forms[0].submit();");
			}
		});

		webView.loadUrl(url);
		return webView.getUrl();
	}

	public List<MusicObject> searchSong(String query, int count, int offset) {
		return null;
	}

	private void parseResponse() {

	}

	public interface AccessTokenListener {
		void onAccessTokenCame();
	}
}
