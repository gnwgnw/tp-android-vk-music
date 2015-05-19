package com.tp.vkplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by S.Grechkin-Pogrebnyakov on 17.05.2015.
 */

public class PlayMusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer;
    private boolean isPlay;
    private ArrayList<SongObject> songs;
    private int currentSongPos;
    private int repeatMode;
    private boolean randomPlay;
    private int bufferPosition;

    static public final int DO_NOT_REPEAT = 0;
    static public final int REPEAT_ALL = 1;
    static public final int REPEAT_ONE = 2;


    @Override
    public void onCreate() {
        super.onCreate();
        currentSongPos = 0;
        bufferPosition = 0;
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
        mediaPlayer.setOnBufferingUpdateListener(this);
    }

    public void setSongs( ArrayList<SongObject> songs ) {
        this.songs = songs;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferPosition = percent * mp.getDuration() / 100;
    }

    public int getBufferPosition() {
        return bufferPosition;
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
        Intent notIntent = new Intent(this, PlayControlActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play_pause_button)
                .setTicker(song.getTitle())
                .setOngoing(true)
                .setContentTitle("Playing")
        .setContentText(song.getTitle());
        Notification not = builder.build();

        startForeground(42, not);
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
        if( isPlaying() )
        mediaPlayer.start();
        else playSong();
    }

    public void playPrev(){
        mediaPlayer.reset();
        if ( repeatMode == REPEAT_ONE ) {
            playSong();
            return;
        }

        currentSongPos--;
        if(currentSongPos < 0) {
            switch (repeatMode) {
                case DO_NOT_REPEAT:
                    currentSongPos = 0;
                    break;
                case  REPEAT_ALL:
                    currentSongPos = songs.size() - 1;
            }
        }
        playSong();
    }

    public void playNext(){
        mediaPlayer.reset();
        if ( repeatMode == REPEAT_ONE ) {
            playSong();
            return;
        }

        currentSongPos++;
        if(currentSongPos >= songs.size()) {
            switch (repeatMode) {
                case DO_NOT_REPEAT:
                    currentSongPos--;
                    return;
                case  REPEAT_ALL:
                    currentSongPos = 0;
            }
        }
        playSong();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(getApplicationContext(), "Error loading file", Toast.LENGTH_SHORT).show();
        return true;
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
        stopForeground(true);

    }
}
