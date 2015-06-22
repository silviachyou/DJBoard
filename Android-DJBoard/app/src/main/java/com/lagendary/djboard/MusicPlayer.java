package com.lagendary.djboard;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.android.common.logger.Log;

/**
 * Created by joshua on 6/22/15.
 */
public class MusicPlayer {


    private static final String TAG = "MusicPlayer";
    private SoundPool soundPool;

    private static final int[] SOUND_RES_IDS = {R.raw.drum_loop, R.raw.yooo, R.raw.piano, R.raw.chaser, R.raw.beatbox, R.raw.saw_wave};

    private static final int SOUND_POOL_NO = SOUND_RES_IDS.length;

    private static final int DRUM_SOUND_INDEX = 0;
    private static final int BOARD_UP_SOUND_INDEX = 1;
    private static final int BASE_SOUND_PIANO_INDEX = 2;
    private static final int BASE_SOUND_CHASER_INDEX = 3;
    private static final int BASE_SOUND_BEATBOX_INDEX = 4;

    private static final int BASE_SOUND_SAW_WAVE_INDEX = 5;

    private int[] soundIds = new int[SOUND_POOL_NO];
    private int[] streamIds = new int[SOUND_POOL_NO];


    private MediaPlayer[] players = new MediaPlayer[SOUND_POOL_NO];

    private boolean[] soundPlaying = new boolean[SOUND_POOL_NO]; // sparse, depends on if is toggled by the developer

    private Context context;

    public MusicPlayer(Context context){
        this.context = context;
    }

    private void initSoundPool() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int arg2) {
                Log.d(TAG, "music " + sampleId + " load complete");
                //soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            }

        });


        for(int i = 0; i < SOUND_POOL_NO; i++) {
            soundIds[i] = soundPool.load(context, SOUND_RES_IDS[i], 1);
        }
    }


    private boolean toggleLoopMusicWithNoFromSoundPool(int musicPlayerNo, float volume){
        soundPlaying[musicPlayerNo] = !soundPlaying[musicPlayerNo];
        if(soundPlaying[musicPlayerNo]) {
            streamIds[musicPlayerNo] = soundPool.play(soundIds[musicPlayerNo], volume, volume, 1, -1, 1.0f);
        }else{
            soundPool.stop(streamIds[musicPlayerNo]);
        }
        return soundPlaying[musicPlayerNo];
    }

    private void toggleLoopMusicWithNoWithVolume(int musicPlayerNo, float volume){

        soundPlaying[musicPlayerNo] = !soundPlaying[musicPlayerNo];
        if(soundPlaying[musicPlayerNo]) {
            playLoopMusicWithNo(musicPlayerNo, volume);
        }else{
            stopMusic(musicPlayerNo);
        }

    }

    private void playLoopMusicWithNo(int musicPlayerNo, float volume){
        playMusic(musicPlayerNo, SOUND_RES_IDS[musicPlayerNo], true, volume);
    }

    private void playMusic(int musicPlayerNo, int resId, boolean looping, float volume){
        stopMusic(musicPlayerNo);
        players[musicPlayerNo] = MediaPlayer.create(context, resId);
        players[musicPlayerNo].setLooping(looping);
        players[musicPlayerNo].setVolume(volume, volume);
        players[musicPlayerNo].start();
    }

    private void stopMusic(int musicPlayerNo){
        if (players[musicPlayerNo] != null) {
            players[musicPlayerNo].release();
            players[musicPlayerNo] = null;
        }
    }

    public boolean actionForMessage(String msg){
        switch (msg) {
            case "wheelmove":
                soundPool.stop(streamIds[DRUM_SOUND_INDEX]);
                streamIds[DRUM_SOUND_INDEX] = soundPool.play(soundIds[DRUM_SOUND_INDEX], 1.0f, 1.0f, 1, -1, 1.0f);
                break;
            case "wheelstop":
                soundPool.stop(streamIds[DRUM_SOUND_INDEX]);
                break;
            case "boardup":
                streamIds[BOARD_UP_SOUND_INDEX] = soundPool.play(soundIds[BOARD_UP_SOUND_INDEX], 1.0f, 1.0f, 1, 0, 1.0f);
                //playMusic(0, R.raw.the_night_out);
                break;
            case "boarddown":
                //stopMusic(0);
                break;
            case "knockFront":
                boolean frontStarted = toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_PIANO_INDEX, 0.5f);
                return frontStarted;
            case "knockMid":
                boolean middleStarted = toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_CHASER_INDEX, 0.5f);
                return middleStarted;
            case "knockBack":
                boolean backStarted = toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_BEATBOX_INDEX, 1.0f);
                return backStarted;
            case "turnright":
                break;
            case "turnleft":
                break;
            case "stopRolling":
                break;
            case "stop_ultrasound":
                toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_SAW_WAVE_INDEX, 1.0f);
                break;
            default:
                if(msg.startsWith("ultrasound:")) {
                    String no = msg.substring(11).trim();
                    double noDouble = Double.parseDouble(no);

                    if(!soundPlaying[BASE_SOUND_SAW_WAVE_INDEX]) {
                        toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_SAW_WAVE_INDEX, 1.0f);
                    }
                    float rate = 1.0f + (float) ((noDouble - 40.0) / 40.0);

                    soundPool.setRate(streamIds[BASE_SOUND_SAW_WAVE_INDEX], rate);
                }else if(msg.startsWith("v= ")) {
                }else if(msg.startsWith("d= ")) {
                }
                break;
        }
        return true;
    }

}
