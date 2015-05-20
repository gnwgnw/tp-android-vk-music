package com.tp.vkplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tp.vkplayer.api.API;
import com.tp.vkplayer.base.SongArrayAdapter;
import com.tp.vkplayer.base.SongObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mily-V on 18.05.2015.
 */
public class SearchResultActivity extends MusicControllerActivity implements API.APIListener {

	private final int MAX_SEARCH = 300;
	private final ArrayList<SongObject> songs = new ArrayList<>();

	private API api;
	private SongArrayAdapter adapter;
	private String query;
	private int performer;

    private int setSongsMsg = 13;
    private int setOneSongMsg = 22;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_search_result);

        Intent playIntent = new Intent( this, PlayMusicService.class );
        startService( playIntent );

		adapter = new SongArrayAdapter(this, songs);

		ListView listView = (ListView) findViewById(R.id.search_result_list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = new Message();
                Bundle bnd = new Bundle();
                bnd.putLong("song", id);
                msg.setData(bnd);
                msg.what = setOneSongMsg;
                seekHandler.sendMessage(msg);
            }
        });

		query = getIntent().getExtras().getString(MainActivity.QUERY);
		performer = getIntent().getExtras().getInt(MainActivity.PERFORMER);

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
		api.searchSongs(query, performer, MAX_SEARCH, 0);
	}

	@Override
	public void onSearchDone(List<SongObject> songs) {
		Log.e("SEARCH", songs.toString());
		this.songs.addAll(songs);
		adapter.notifyDataSetChanged();
        seekHandler.sendEmptyMessage(setSongsMsg);
		findViewById(R.id.search_result_list_view).setVisibility(View.VISIBLE);
		findViewById(R.id.search_result_textview_no_found).setVisibility(View.GONE);
	}

    @Override
    protected void onHandleMessage(Message msg) {
        if( msg.what == seekMsg ) {
            if( playMusicService == null || !playMusicService.isPlay ) {
                seekHandler.sendEmptyMessageDelayed(seekMsg, 500);
                return;
            }
            songTitleView.setText(getSongName());
            artistNameView.setText(getSongArtist());
            if (isPlaying() || !isLoaded()) {
                seekHandler.sendEmptyMessageDelayed(seekMsg, 1000);
            }
        } else if (msg.what == setSongsMsg) {
            if (playMusicService!=null && musicBound) {
                playMusicService.setSongs(songs);
            } else
                seekHandler.sendEmptyMessage(setSongsMsg);
        } else if (msg.what == setOneSongMsg) {
            if (playMusicService!=null && musicBound) {
                Bundle bnd = msg.getData();
                playMusicService.setSong((int)bnd.getLong("song"));
                startPlay();
            } else
                seekHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onConnected() {
    }
}