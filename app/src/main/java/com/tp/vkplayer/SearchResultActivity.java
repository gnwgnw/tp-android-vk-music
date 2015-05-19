package com.tp.vkplayer;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mily-V on 18.05.2015.
 */
public class SearchResultActivity extends ActionBarActivity implements API.APIListener {

	private final int MAX_SEARCH = 50;
	private final List<SongObject> songs = new LinkedList<>();

	private API api;
	private Toolbar toolbar;
	private SongArrayAdapter adapter;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);

		toolbar = (Toolbar) findViewById(R.id.searchResult_tool_bar);
		setSupportActionBar(toolbar);

		adapter = new SongArrayAdapter(this, songs);

		ListView listView = (ListView) findViewById(R.id.search_result_list_view);
		listView.setAdapter(adapter);
		//TODO set listView clickListener

		query = getIntent().getExtras().getString(MainActivity.QUERY);

		api = new API(this, this);
		api.initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem searchItem = menu.findItem(R.id.tool_bar_action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint("Изменить поиск");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.tool_bar_action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAccessTokenCame() {
		Log.i("TOKEN", "Token");
		api.searchSongs(query, 0, MAX_SEARCH, 0);
	}

	@Override
	public void onSearchDone(List<SongObject> songs) {
		Log.i("SEARCH", songs.toString());
		this.songs.addAll(songs);
		adapter.notifyDataSetChanged();
	}
}
