package com.tp.vkplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by S.Grechkin-Pogrebnyakov on 18.05.2015.
 */

public class PlayControlActivity extends Activity {

    private PlayMusicService playMusicService;
    private boolean musicBound=false;
    private SeekBar seekBar;
    private TextView songTitleView;
    private TextView artistNameView;
    private int seekMsg = 42;
    private PlayMusicService.RepeatMode repeatMode;
    private boolean randomPlay;


    private Handler seekHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if( msg.what == seekMsg ) {
                if( playMusicService == null || !playMusicService.isPlay ) {
                    sendEmptyMessageDelayed(seekMsg, 500);
                    return;
                }
                seekBar.setProgress(getCurrentPosition());
                seekBar.setMax(getDuration());
                seekBar.setSecondaryProgress(getBufferPosition());   // for buffer progress
                songTitleView.setText(getSongName());
                artistNameView.setText(getSongArtist());
                if (isPlaying() || !isLoaded()) {
                    sendEmptyMessageDelayed(seekMsg, 1000);
                }
            }
        }
    };


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayMusicService.MusicBinder binder = (PlayMusicService.MusicBinder)service;
            //get service
            playMusicService = binder.getService();
            musicBound = true;
            repeatMode = getRepeatMode();
            randomPlay = isRandomPlay();

            // TODO убрать это отсюда!!!
            ArrayList<SongObject> songs = new ArrayList<SongObject>();
            SongObject testSong = new SongObject("Serg", "blabla", "/sdcard/Tuneblast Music/Guns N' Roses - Knockin' On Heaven's Door.mp3");
            songs.add(testSong);
            testSong = new SongObject("Vasya", "qwerty", "/sdcard/Tuneblast Music/180194403.mp3");
            songs.add(testSong);
            testSong = new SongObject("111", "222", "https://cs1-35v4.vk-cdn.net/p19/e53261ad32f5dd.mp3?extra=emCxk1n5F8wPwQQwZ3PfyUi8Qhx_-HBCzOvbpx7E3O0cOHS7I1YbxRb9TjxmHv5ZcnURn7jYVxSqeZNJM6tlz8cf9jQ");
            songs.add(testSong);
            playMusicService.setSongs(songs);
            playMusicService.setSong(0);
            startPlay();
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

//    Thread seekThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_play_control);

        songTitleView = (TextView)findViewById(R.id.play_control_song_name);
        artistNameView = (TextView)findViewById(R.id.play_control_artist);

        Intent playIntent = new Intent( this, PlayMusicService.class );
        startService( playIntent );

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int currentPosition = 0;
//                while (true) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        return;
//                    }
//                    if( playMusicService == null || !playMusicService.isPlaying())
//                        continue;
//                    currentPosition = getCurrentPosition();
//                    final int total = getDuration();
//                    //final String totalTime = getAsTime(total);
//                    //final String curTime = getAsTime(currentPosition);
//
//                    seekBar.setMax(total); //song duration
//                    seekBar.setProgress(currentPosition);  //for current song progress
//                    seekBar.setSecondaryProgress(playMusicService.getBufferPosition());   // for buffer progress
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (isPlaying()) {
////                                if (!playPauseButton.isChecked()) {
////                                    playPauseButton.setChecked(true);
////                                }
////                            } else {
////                                if (playPauseButton.isChecked()) {
////                                    playPauseButton.setChecked(false);
////                                }
////                            }
////                            //musicDuration.setText(totalTime);
////                            //musicCurLoc.setText(curTime);
////                        }
////                    });
//                }
//            }
//        }).start();

        seekBar = (SeekBar)findViewById(R.id.play_control_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean touched = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (touched) {
                    seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pausePlay();
                touched = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startPlay();
                touched = false;
            }
        });
        final ImageButton playPauseButton = (ImageButton)findViewById(R.id.play_control_play_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    if ( !isPlaying() ) {
                        startPlay();
                    }
                    else {
                        pausePlay();
                    }
                else
                    Toast.makeText(getApplicationContext(), "Пожалуйста, подождите.", Toast.LENGTH_SHORT).show();
                }
        });
        ImageButton prevButton = (ImageButton)findViewById(R.id.play_control_prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playPrev();
            }
        });
        ImageButton nextButton = (ImageButton)findViewById(R.id.play_control_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    playNext();
            }
        });
        ImageButton repeatButton = (ImageButton)findViewById(R.id.play_control_repeat_button);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRepeatMode();
            }
        });
        ImageButton randomButton = (ImageButton)findViewById(R.id.play_control_random_button);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRandomMode();
            }
        });
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

    private void startPlay() {
        if( playMusicService != null && musicBound ) {
            playMusicService.playSong();
            ((ImageButton) findViewById(R.id.play_control_play_button)).setImageResource(R.drawable.pause_button);
            seekHandler.sendEmptyMessage(seekMsg);
        }
    }

    private void pausePlay() {
        if( playMusicService != null && musicBound ) {
            playMusicService.pausePlayer();
            ((ImageButton) findViewById(R.id.play_control_play_button)).setImageResource(R.drawable.play_button);
        }
    }


    //play next
    private void playNext(){
        if( playMusicService != null && musicBound ) {
            playMusicService.playNext();
            ((ImageButton) findViewById(R.id.play_control_play_button)).setImageResource(R.drawable.pause_button);
            seekHandler.sendEmptyMessage(seekMsg);
        }
    }

    //play previous
    private void playPrev(){
        if( playMusicService != null && musicBound ) {
            playMusicService.playPrev();
            ((ImageButton) findViewById(R.id.play_control_play_button)).setImageResource(R.drawable.pause_button);
            seekHandler.sendEmptyMessage(seekMsg);
        }
    }

    private void changeRepeatMode() {
        if (playMusicService == null || ! musicBound )
            return;
        ImageButton repeatButton = (ImageButton)findViewById(R.id.play_control_repeat_button);
        switch (repeatMode) {
            case DO_NOT_REPEAT:
                repeatMode = PlayMusicService.RepeatMode.REPEAT_ALL;
                playMusicService.setRepeatMode(repeatMode);
                repeatButton.setImageResource(R.drawable.repeat_button_repeat_all);
                break;
            case REPEAT_ALL:
                repeatMode = PlayMusicService.RepeatMode.REPEAT_ONE;
                playMusicService.setRepeatMode(repeatMode);
                repeatButton.setImageResource(R.drawable.repeat_button_repeat_one);
                break;
            case REPEAT_ONE:
                repeatMode = PlayMusicService.RepeatMode.DO_NOT_REPEAT;
                playMusicService.setRepeatMode(repeatMode);
                repeatButton.setImageResource(R.drawable.repeat_button_disabled);
                break;
        }

    }

    private void changeRandomMode() {
        if (playMusicService == null || ! musicBound )
            return;
        ImageButton randomButton = (ImageButton)findViewById(R.id.play_control_random_button);
        if (randomPlay) {
            randomPlay = false;
            playMusicService.setRandomPlay(false);
            randomButton.setImageResource(R.drawable.shuffle_button_disabled);
        } else {
            randomPlay = true;
            playMusicService.setRandomPlay(true);
            randomButton.setImageResource(R.drawable.shuffle_button);
        }
    }

    private PlayMusicService.RepeatMode getRepeatMode() {
        if( playMusicService != null && musicBound )
            return playMusicService.getRepeatMode();
        else return PlayMusicService.RepeatMode.DO_NOT_REPEAT;
    }

    private boolean isRandomPlay() {
        return playMusicService != null && musicBound && playMusicService.isRandomPlay();
    }

    private int getDuration() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getDuration();
        else return 0;
    }

    private int getBufferPosition() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getBufferPosition();
        else return 0;
    }

    private int getCurrentPosition() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getPosition();
        else return 0;
    }

    private String getSongName() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getSongName();
        else
            return null;
    }

    private String getSongArtist() {
        if ( playMusicService != null && musicBound )
            return playMusicService.getSongArtist();
        else
            return null;
    }

    private boolean isPlaying() {
        return playMusicService != null && musicBound && playMusicService.isPlaying();
    }

    private boolean isLoaded() {
        return playMusicService == null || !musicBound || playMusicService.isLoaded();
    }

    private void seekTo(int pos) {
        if ( playMusicService != null && musicBound )
            playMusicService.seek(pos);
    }

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        playMusicService = null;
        //seekThread.stop();
        super.onDestroy();
    }

}
