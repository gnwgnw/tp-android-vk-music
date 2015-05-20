package com.tp.vkplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.tp.vkplayer.api.API;
import com.tp.vkplayer.base.SongObject;
import com.tp.vkplayer.widgets.CustomSearchView;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mily-V on 10.05.2015.
 */

public class MainActivity extends Activity implements API.APIListener {

	public static final String QUERY = "query";
	public static final String PERFORMER = "performer";

	private static final String TAG = "MainActivity";
	private static ViewFlipper flipper;
	private static float fromPosition = 0;
	private static float toPosition = 0;

	// выбран ли поиск по названию песни
	public boolean isSearchSongs() {
		TextView searchSongs =
				(TextView) findViewById(R.id.main_activity_textview_search_songs);
		return searchSongs.getVisibility() == View.VISIBLE;
	}

	// выбран ли поиск по Имени исполнителя
	public boolean isSearchArtists() {
		TextView searchArtists =
				(TextView) findViewById(R.id.main_activity_textview_search_artists);
		return searchArtists.getVisibility() == View.VISIBLE;
	}

	protected boolean onTouchHandler(View v, MotionEvent event) {
		int MOVE_LENGTH = 100;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				fromPosition = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				toPosition = event.getX();
				if ((fromPosition - MOVE_LENGTH) > toPosition) {
					flipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(),
							R.anim.go_next_in));
					flipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(),
							R.anim.go_next_out));
					flipper.showNext();
				}
				else if ((fromPosition + MOVE_LENGTH) < toPosition) {
					flipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(),
							R.anim.go_prev_in));
					flipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(),
							R.anim.go_prev_out));
					flipper.showPrevious();
				}
			default:
				break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);

		new AsyncTaskExample().execute();


		LayoutInflater inflater = (LayoutInflater) getSystemService(Context
				.LAYOUT_INFLATER_SERVICE);
		int layouts[] = new int[]{R.layout.layout_search_songs, R.layout.layout_search_artists};
		flipper = (ViewFlipper) findViewById(R.id.main_activity_flipper_search_choice);
		for (int layout : layouts)
			flipper.addView(inflater.inflate(layout, null));

		TextView searchSongs = (TextView) findViewById(R.id.main_activity_textview_search_songs);
		searchSongs.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onTouchHandler(v, event);
			}
		});

		TextView searchArtists = (TextView) findViewById(R.id
				.main_activity_textview_search_artists);
		searchArtists.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onTouchHandler(v, event);
			}
		});

		ImageButton startSearch = (ImageButton) findViewById(R.id.main_activity_button_start_search);
		startSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//   Intent i = new Intent(MainActivity.this, PlayControlActivity.class);
				Intent i = new Intent(MainActivity.this, SearchResultActivity.class);
				CustomSearchView searchView = (CustomSearchView)
						findViewById(R.id.main_activity_edittext_input);
				i.putExtra(QUERY, searchView.getQuery());
				i.putExtra(PERFORMER, isSearchSongs() ? 0 : 1);
				startActivity(i);
			}
		});

	}

	@Override
	public void onAccessTokenCame() {
	}

	@Override
	public void onSearchDone(List<SongObject> songs) {
	}


	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {    }

	//@Override
	//public boolean onOptionsItemSelected(MenuItem item){     }

	public class AsyncTaskExample extends AsyncTask<Integer, Void, Void> {

		private ProgressBar progressBar;
		private ImageButton startSearch;
		private CustomSearchView searchView;
		private TextView appName;

		@Override
		protected void onPreExecute() {
			progressBar = (ProgressBar) findViewById(R.id.main_activity_progress_bar);
			progressBar.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				TimeUnit.SECONDS.sleep(1);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			appName = (TextView) findViewById(R.id.main_activity_textview_on_progress_bar);
			startSearch = (ImageButton) findViewById(R.id.main_activity_button_start_search);
			searchView = (CustomSearchView) findViewById(R.id.main_activity_edittext_input);
			appName.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			flipper.setVisibility(View.VISIBLE);
			startSearch.setVisibility(View.VISIBLE);
			searchView.setVisibility(View.VISIBLE);
		}

	}

}
