package com.tp.vkplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;

import java.util.ArrayList;

/**
 * Created by S.Grechkin-Pogrebnyakov on 18.05.2015.
 */

public class PlayControlActivity extends Activity {

    private PlayMusicService playMusicService;
    private boolean musicBound=false;
    private MusicController controller;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayMusicService.MusicBinder binder = (PlayMusicService.MusicBinder)service;
            //get service
            playMusicService = binder.getService();
            musicBound = true;

            // TODO убрать это отсюда!!!
            ArrayList<SongObject> songs = new ArrayList<SongObject>();
            SongObject testSong = new SongObject("Serg", "blabla", "/sdcard/Tuneblast Music/Guns N' Roses - Knockin' On Heaven's Door.mp3");
            songs.add(testSong);
            testSong = new SongObject("Serg", "blabla", "/sdcard/Tuneblast Music/180194403.mp3");
            songs.add(testSong);
            playMusicService.setSongs(songs);
            playMusicService.setSong(0);
            playMusicService.playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if ( !musicBound ) {
            Intent playIntent = new Intent( this, PlayMusicService.class );
            bindService( playIntent, musicConnection, Context.BIND_AUTO_CREATE );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_play_control);

        Intent playIntent = new Intent( this, PlayMusicService.class );
        startService( playIntent );
//        setController();
        ImageButton playButton = (ImageButton)findViewById(R.id.play_control_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    if ( !playMusicService.isPlaying() )
                        playMusicService.resumePlayer();
                    else
                        playMusicService.pausePlayer();
                }
        });
        ImageButton prevButton = (ImageButton)findViewById(R.id.play_control_prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    playMusicService.playPrev();
            }
        });
        ImageButton nextButton = (ImageButton)findViewById(R.id.play_control_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    playMusicService.playNext();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        playMusicService = null;
        super.onDestroy();
    }

    //play next
    private void playNext(){
        playMusicService.playNext();
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        playMusicService.playPrev();
        controller.show(0);
    }

//    private void setController() {
//        controller = new MusicController(this);
//        controller.setPrevNextListeners(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playNext();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playPrev();
//            }
//        });
//        controller.setMediaPlayer(this);
//        controller.setAnchorView(findViewById(R.id.songAlbum));
//        controller.setEnabled(true);
//    }

    public int getDuration() {
        if ( playMusicService != null && musicBound && playMusicService.isPlaying() )
            return playMusicService.getDuration();
        else return 0;
    }

    public int getCurrentPosition() {
        if ( playMusicService != null && musicBound && playMusicService.isPlaying() )
            return playMusicService.getPosition();
        else return 0;
    }

    public void seekTo(int pos) {
        if ( playMusicService != null && musicBound )
            playMusicService.seek(pos);
    }


}
