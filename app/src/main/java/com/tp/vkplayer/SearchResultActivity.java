package com.tp.vkplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


import com.tp.vkplayer.widgets.CustomToolBar;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mily-V on 18.05.2015.
 */
public class SearchResultActivity extends ActionBarActivity implements API.APIListener {

	private final int MAX_SEARCH = 300;
	private final List<SongObject> songs = new LinkedList<>();

	private API api;
	private SongArrayAdapter adapter;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);

		adapter = new SongArrayAdapter(this, songs);

		ListView listView = (ListView) findViewById(R.id.search_result_list_view);
		listView.setAdapter(adapter);
		//TODO set listView clickListener

		query = getIntent().getExtras().getString(MainActivity.QUERY);

		api = new API(this, this);
		api.initialize();


//		CustomToolBar toolbar = (CustomToolBar) findViewById(R.id.search_result_tool_bar);
//		toolbar.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				// вывод результата нового поиска
//			}
//		});
	}

	@Override
	public void onAccessTokenCame() {
		Log.i("TOKEN", "Token");
		api.searchSongs(query, 0, MAX_SEARCH, 0);
	}

	@Override
	public void onSearchDone(List<SongObject> songs) {
		Log.e("SEARCH", songs.toString());
		this.songs.addAll(songs);
		adapter.notifyDataSetChanged();
		findViewById(R.id.search_result_list_view).setVisibility(View.VISIBLE);
		findViewById(R.id.search_result_textview_no_found).setVisibility(View.GONE);
	}
}