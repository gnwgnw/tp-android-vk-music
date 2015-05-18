package com.tp.vkplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * Created by Mily-V on 10.05.2015.
 */

public class MainActivity extends Activity {

    private static ViewFlipper flipper;
    private static final String TAG = "MainActivity";
    private static float fromPosition = 0;
    private static float toPosition = 0;

    // выбран ли поиск по названию песни
    public boolean isSearchSongs() {
        TextView searchSongs = (TextView) findViewById(R.id.mainActivity_textview_for_search_songs);
        if ( searchSongs.getVisibility() == View.VISIBLE)
            return true;
        else return false;
    }

    // выбран ли поиск по Имени исполнителя
    public boolean isSearchArtists() {
        TextView searchArtists = (TextView) findViewById(R.id.mainActivity_textview_for_search_artists);
        if ( searchArtists.getVisibility() == View.VISIBLE)
            return true;
        else return false;
    }

    protected boolean onTouchHandler(View v, MotionEvent event) {
        int MOVE_LENGTH = 100;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                toPosition = event.getX();
                if ((fromPosition - MOVE_LENGTH) > toPosition)
                {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(v.getContext(),
                            R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(v.getContext(),
                            R.anim.go_next_out));
                    flipper.showNext();
                }
                else if ((fromPosition + MOVE_LENGTH) < toPosition)
                {
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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layouts[] = new int[]{ R.layout.layout_search_songs, R.layout.layout_search_artists };
        flipper = (ViewFlipper) findViewById(R.id.mainActivity_flipper_for_search_choice);
        for (int layout : layouts)
            flipper.addView(inflater.inflate(layout, null));

        TextView searchSongs = (TextView) findViewById(R.id.mainActivity_textview_for_search_songs);
        searchSongs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchHandler(v, event);
            }
        });

        TextView searchArtists = (TextView) findViewById(R.id.mainActivity_textview_for_search_artists);
        searchArtists.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchHandler(v, event);
            }
        });

        final ImageButton startSearch = (ImageButton) findViewById(R.id.mainActivity_button_to_start_search);
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchResultActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {    }

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item){     }

}
