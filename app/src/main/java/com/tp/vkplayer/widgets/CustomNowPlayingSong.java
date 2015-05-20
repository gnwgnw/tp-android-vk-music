package com.tp.vkplayer.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tp.vkplayer.R;


/**
 * Created by Mily-V on 20.05.2015.
 */
public class CustomNowPlayingSong extends LinearLayout{
	private TextView title;
	private TextView artist;
	private ImageButton playButton;
	private RelativeLayout layout;

	public CustomNowPlayingSong (Context context) {
		super(context);
	}

	public CustomNowPlayingSong (Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);

		((Activity)getContext())
				.getLayoutInflater()
				.inflate(R.layout.layout_now_playing_song, this, true);

		title = (TextView) findViewById(R.id.song_title);
		artist = (TextView) findViewById(R.id.song_artist);
		playButton = (ImageButton) findViewById(R.id.now_playing_button_play);
		layout = (RelativeLayout) findViewById(R.id.layout_now_playing_song);

	}
}
