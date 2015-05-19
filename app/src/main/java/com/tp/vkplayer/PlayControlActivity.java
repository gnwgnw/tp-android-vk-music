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

import java.util.ArrayList;

/**
 * Created by S.Grechkin-Pogrebnyakov on 18.05.2015.
 */

public class PlayControlActivity extends Activity {

    private PlayMusicService playMusicService;
    private boolean musicBound=false;
    private SeekBar seekBar;

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
            ((ImageButton)findViewById(R.id.play_control_play_button)).setImageResource(R.drawable.pause_button);
            seekBar.setMax(playMusicService.getDuration());
            seekHandler.sendEmptyMessage(seekMsg);
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

        final Intent playIntent = new Intent( this, PlayMusicService.class );
        startService( playIntent );

//        seekThread = new Thread(run);
//        seekThread.start();

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
                    playMusicService.pausePlayer();
                    seekTo(progress);
                    playMusicService.resumePlayer();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                touched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                touched = false;
            }
        });
//        setController();
        final ImageButton playPauseButton = (ImageButton)findViewById(R.id.play_control_play_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    if ( !playMusicService.isPlaying() ) {
                        playMusicService.resumePlayer();
                        playPauseButton.setImageResource(R.drawable.pause_button);
                    }
                    else {
                        playMusicService.pausePlayer();
                        playPauseButton.setImageResource(R.drawable.play_button);
                    }
                }
        });
        ImageButton prevButton = (ImageButton)findViewById(R.id.play_control_prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    playPrev();
            }
        });
        ImageButton nextButton = (ImageButton)findViewById(R.id.play_control_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( playMusicService != null && musicBound )
                    playNext();
            }
        });
    }

    private int seekMsg = 42;

    Handler seekHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if( msg.what == seekMsg )
                if (playMusicService != null && playMusicService.isPlaying()) {
                    seekBar.setProgress(playMusicService.getPosition());
                    // SystemClock.sleep();

                    sendEmptyMessageDelayed(seekMsg, 1000);
                }
        }
    };

//    Handler seekPaintHandler = new Handler() {
//        @Override
//    public void handleMessage( Message msg ) {
//            seekBar.setProgress(msg.getData().getInt("seek"));
//        }
//    };

//    Runnable run = new Runnable() {
//        @Override
//        public void run() {
//            Looper.prepare();
//            seekHandler = new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    if( msg.what == seekMsg )
//                        if (playMusicService.isPlaying()) {
//                            //seekBar.setProgress(playMusicService.getPosition());
//                            Bundle seekBundle = new Bundle();
//                            seekBundle.putInt("seek", playMusicService.getPosition());
//                            Message seekMessage = new Message();
//                            seekMessage.setData(seekBundle);
//                            seekPaintHandler.sendMessage(seekMessage);
//                           // SystemClock.sleep();
//
//                            sendEmptyMessageDelayed(seekMsg, 1000);
//                        } else {
//                            seekBar.setProgress(0);
//                        }
//                }
//            };
//            Looper.loop();
//        }
//    };

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
        playMusicService = null;
        //seekThread.stop();
        super.onDestroy();
    }

    //play next
    private void playNext(){
        playMusicService.playNext();
        //controller.show(0);
    }

    //play previous
    private void playPrev(){
        playMusicService.playPrev();
        //controller.show(0);
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
