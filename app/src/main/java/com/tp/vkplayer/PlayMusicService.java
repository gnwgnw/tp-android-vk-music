package com.tp.vkplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by S.Grechkin-Pogrebnyakov on 17.05.2015.
 */

public class PlayMusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private boolean isPlay;
    private ArrayList<SongObject> songs;
    private int currentSongPos;
    private int repeatMode;
    private boolean randomPlay;

    static public final int DO_NOT_REPEAT = 0;
    static public final int REPEAT_ALL = 1;
    static public final int REPEAT_ONE = 2;


    @Override
    public void onCreate() {
        super.onCreate();
        currentSongPos = 0;
        isPlay = false;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        if (intent != null) {
            randomPlay = intent.getBooleanExtra("randomPlay", false);
            repeatMode = intent.getIntExtra("repeatMode", 0);
        }
        return START_STICKY;
    }

    public void initMusicPlayer() {
        //set player props
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void setSongs( ArrayList<SongObject> songs ) {
        this.songs = songs;
    }

    public class MusicBinder extends Binder {
        PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }

    private final IBinder musicBind = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind( Intent intent ) {
       // mediaPlayer.stop();
        return false;
    }

    public void playSong() {
        mediaPlayer.reset();
        SongObject song = songs.get(currentSongPos);
        try{
            mediaPlayer.setDataSource(song.getUrl());
        } catch (Exception e) {
            Log.e("ACHTUNG!!!", "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();
    }

    public void setSong( int id ) {
        currentSongPos = id;
    }

    public int getPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int position){
        mediaPlayer.seekTo(position);
    }

    public void resumePlayer(){
        mediaPlayer.start();
    }

    public void playPrev(){
        if ( repeatMode == REPEAT_ONE ) {
            playSong();
            return;
        }

        currentSongPos--;
        if(currentSongPos < 0) {
            switch (repeatMode) {
                case DO_NOT_REPEAT:
                    currentSongPos = 0;
                case  REPEAT_ALL:
                    currentSongPos = songs.size() - 1;
            }
        }
        playSong();
    }

    public void playNext(){
        currentSongPos++;
        if(currentSongPos >= songs.size()) currentSongPos=0;
        playSong();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("Player", "Completed");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPlay = true;
        mp.start();
    }

    @Override
    public void onDestroy() {
        if(isPlay)
            mediaPlayer.stop();
        mediaPlayer.release();

    }

}
