package com.tp.vkplayer.api;

import android.os.AsyncTask;

/**
 * author s.titaevskiy on 17.05.15.
 */
public class RequestAPITask extends AsyncTask<String, Void, String> {

	private final RequestAPITaskListener callback;

	public RequestAPITask(RequestAPITaskListener callback) {
		this.callback = callback;
	}

	@Override
	protected String doInBackground(String... params) {
		String response = "";

		if (params.length > 0) {
			response = APIHelper.httpRequest(params[0]);
		}

		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		callback.onPostExecute(response);
	}

	public interface RequestAPITaskListener {
		void onPostExecute(String response);
	}
}
