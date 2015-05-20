package com.tp.vkplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by serg on 18.05.15.
 */
public abstract class MusicControllerActivity extends Activity {

    protected PlayMusicService playMusicService;
    protected boolean musicBound=false;

    protected TextView songTitleView;
    protected TextView artistNameView;
    protected ImageButton playPauseButton;

    protected int playImageResource;
    protected int pauseImageResource;

    protected int seekMsg = 42;

    protected abstract void onHandleMessage( Message msg);
    protected abstract void onConnected();

    protected Handler seekHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            onHandleMessage(msg);
        }
    };

    //connect to the service
    protected ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayMusicService.MusicBinder binder = (PlayMusicService.MusicBinder)service;
            //get service
            playMusicService = binder.getService();
            musicBound = true;
            onConnected();
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
    protected void onDestroy() {
        unbindService(musicConnection);
        playMusicService = null;
        super.onDestroy();
    }

    protected void startPlay() {
        if( playMusicService != null && musicBound ) {
            playMusicService.playSong();
            playPauseButton.setImageResource(pauseImageResource);
            seekHandler.sendEmptyMessage(seekMsg);
        }
    }

    protected void pausePlay() {
        if( playMusicService != null && musicBound ) {
            playMusicService.pausePlayer();
            playPauseButton.setImageResource(playImageResource);
        }
    }


    //play next
    protected void playNext(){
        if( playMusicService != null && musicBound ) {
            playMusicService.playNext();
            playPauseButton.setImageResource(pauseImageResource);
            seekHandler.sendEmptyMessage(seekMsg);
        }
    }

    //play previous
    protected void playPrev(){
        if( playMusicService != null && musicBound ) {
            playMusicService.playPrev();
            playPauseButton.setImageResource(pauseImageResource);
            seekHandler.sendEmptyMessage(seekMsg);
        }
    }

    protected void seekTo(int pos) {
        if ( playMusicService != null && musicBound )
            playMusicService.seek(pos);
    }

    protected PlayMusicService.RepeatMode getRepeatMode() {
        if( playMusicService != null && musicBound )
            return playMusicService.getRepeatMode();
        else return PlayMusicService.RepeatMode.DO_NOT_REPEAT;
    }

    protected boolean isRandomPlay() {
        return playMusicService != null && musicBound && playMusicService.isRandomPlay();
    }

    protected int getDuration() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getDuration();
        else return 0;
    }

    protected int getBufferPosition() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getBufferPosition();
        else return 0;
    }

    protected int getCurrentPosition() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getPosition();
        else return 0;
    }

    protected String getSongName() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getSongName();
        else
            return null;
    }

    protected String getSongArtist() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getSongArtist();
        else
            return null;
    }

    protected boolean isPlaying() {
        return playMusicService != null && musicBound && playMusicService.isPlaying();
    }

    protected boolean isLoaded() {
        return playMusicService == null || !musicBound || playMusicService.isLoaded();
    }

}
