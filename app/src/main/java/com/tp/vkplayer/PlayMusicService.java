package com.tp.vkplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.tp.vkplayer.base.SongObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by S.Grechkin-Pogrebnyakov on 17.05.2015.
 */

public class PlayMusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    static public enum RepeatMode{
        DO_NOT_REPEAT,
        REPEAT_ALL,
        REPEAT_ONE
    }

    private MediaPlayer mediaPlayer;
    public boolean isPlay;
    private ArrayList<SongObject> songs;
    private SongObject currSong;
    private int currentSongPos;
    private RepeatMode repeatMode = RepeatMode.DO_NOT_REPEAT;
    private boolean randomPlay;
    private int bufferPosition;
    private boolean loaded;
    private Random rand;


    @Override
    public void onCreate() {
        super.onCreate();
        rand=new Random();
        currentSongPos = 0;
        bufferPosition = 0;
        isPlay = false;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
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

    public int getBufferPosition() {
        return bufferPosition;
    }

    public void setRepeatMode( RepeatMode mode ) {
        repeatMode = mode;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRandomPlay( boolean randomPlay ) {
        this.randomPlay = randomPlay;
    }

    public boolean isRandomPlay() {
        return randomPlay;
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

    public void playSong() {
        if ( isPlay ) {
            resumePlayer();
            return;
        }
        currSong = songs.get(currentSongPos);
        try{
            mediaPlayer.setDataSource(currSong.getUrl());
        } catch (Exception e) {
            Log.e("ACHTUNG!!!", "Error setting data source", e);
            return;
        }
        mediaPlayer.prepareAsync();

        Intent notIntent = new Intent(this, PlayControlActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
            .setSmallIcon(R.drawable.play_pause_button)
            .setTicker(currSong.getTitle())
            .setOngoing(true)
            .setContentTitle("Playing")
            .setContentText(currSong.getTitle());
        Notification not = builder.build();

        startForeground(42, not);
    }

    public void setSong( int id ) {
        currentSongPos = id;
    }

    public int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public String getSongName() {
        return currSong.getTitle();
    }

    public String getSongArtist() {
        return currSong.getArtist();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void seek(int position) {
        mediaPlayer.seekTo(position);
    }

    public void resumePlayer() {
        mediaPlayer.start();
    }

    private void resetPlay() {
        mediaPlayer.reset();
        isPlay = false;
        bufferPosition = 0;
    }

    public void playPrev(){
        resetPlay();
        if ( repeatMode == RepeatMode.REPEAT_ONE ) {
            playSong();
            return;
        }
        if (randomPlay) {
            int newSong = currentSongPos;
            while (newSong == currentSongPos) {
                newSong = rand.nextInt(songs.size());
            }
            currentSongPos = newSong;
        } else
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
        resetPlay();
        if ( repeatMode == RepeatMode.REPEAT_ONE ) {
            playSong();
            return;
        }
        if (randomPlay) {
            int newSong = currentSongPos;
            while (newSong == currentSongPos) {
                newSong = rand.nextInt(songs.size());
            }
            currentSongPos = newSong;
        } else
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
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferPosition = percent * mp.getDuration() / 100;
        loaded = (percent == 100);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
       // Toast.makeText(getApplicationContext(), "Error loading file", Toast.LENGTH_SHORT).show();
        if (what == -38) return true;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPlay = true;
        resumePlayer();
    }

    @Override
    public boolean onUnbind( Intent intent ) {
        if( !mediaPlayer.isPlaying() ) {
            stopForeground(true);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        if(isPlay)
            mediaPlayer.stop();
        mediaPlayer.release();
    }
}
