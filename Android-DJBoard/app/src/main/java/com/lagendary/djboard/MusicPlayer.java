package com.lagendary.djboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.android.common.logger.Log;

import java.io.IOException;

/**
 * Created by joshua on 6/22/15.
 */
public class MusicPlayer {


    private static final String TAG = "MusicPlayer";
    private SoundPool soundPool;

    private static final String SHARED_PREF_SOUND_PATH_PREFIX = "sound_path_";
    private Uri[] soundUris = new Uri[SOUND_POOL_NO];
    private static final int[] DEFAULT_SOUND_RES_IDS = {R.raw.drum_loop, R.raw.yooo, R.raw.piano, R.raw.chaser, R.raw.beatbox, R.raw.saw_wave};

    public static final String[] SOUND_TITLES = {"Base Drum", "Board Up", "Knock Front", "Knock Mid", "Knock Back", "UltraSound"};

    public static final int SOUND_POOL_NO = 6;

    public static final int DRUM_SOUND_INDEX = 0;
    public static final int BOARD_UP_SOUND_INDEX = 1;
    public static final int KNOCK_FRONT_SOUND_INDEX = 2;
    public static final int KNOCK_MID_SOUND_INDEX = 3;
    public static final int KNOCK_BACK_SOUND_INDEX = 4;
    public static final int BASE_SOUND_SAW_WAVE_INDEX = 5;

    private int[] soundIds = new int[SOUND_POOL_NO];
    private int[] streamIds = new int[SOUND_POOL_NO];


    private MediaPlayer[] players = new MediaPlayer[SOUND_POOL_NO];

    private boolean[] soundPlaying = new boolean[SOUND_POOL_NO]; // sparse, depends on if is toggled by the developer

    private Context context;

    public MusicPlayer(Context context){
        this.context = context;
        initUris();
        initSoundPool();
    }

    public void stopAllMusic() {
        for(int i = 0; i < SOUND_POOL_NO; i++){
            stopMusic(i);
            soundPool.release();
            initSoundPool();
            soundPlaying[i] = false;
        }
    }

    public boolean actionForMessage(String msg){
        switch (msg) {
            case "wheelmove":
                playLoopMusicWithNo(DRUM_SOUND_INDEX, 1.0f);
                break;
            case "wheelstop":
                stopMusic(DRUM_SOUND_INDEX);
                break;
            case "boardup":
                playMusicOnceWithNo(BOARD_UP_SOUND_INDEX, 1.0f);
                //playMusic(0, R.raw.the_night_out);
                break;
            case "boarddown":
                //stopMusic(0);
                break;
            case "knockFront":
                boolean frontStarted = toggleLoopMusicWithNoWithVolume(KNOCK_FRONT_SOUND_INDEX, 0.5f);
                return frontStarted;
            case "knockMid":
                boolean middleStarted = toggleLoopMusicWithNoWithVolume(KNOCK_MID_SOUND_INDEX, 0.5f);
                return middleStarted;
            case "knockBack":
                boolean backStarted = toggleLoopMusicWithNoWithVolume(KNOCK_BACK_SOUND_INDEX, 1.0f);
                return backStarted;
            case "stickUp":
                return toggleLoopMusicWithNoWithVolume(KNOCK_BACK_SOUND_INDEX, 1.0f);
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

    public static String getUriStringOfMusic(Context context, int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(SHARED_PREF_SOUND_PATH_PREFIX + i, "android.resource://" + context.getPackageName() + "/" + DEFAULT_SOUND_RES_IDS[i]);
    }

    public static void setUriStringOfMusic(Context context, String str, int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(SHARED_PREF_SOUND_PATH_PREFIX + i, str).apply();
    }

    public static void resetUriOfMusic(Context context, int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().remove(SHARED_PREF_SOUND_PATH_PREFIX + i).apply();
    }

    private void initUris() {
        for(int i = 0; i < SOUND_POOL_NO; i++) {
            String uristr = getUriStringOfMusic(context, i);
            soundUris[i] = Uri.parse(uristr);
        }
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
            soundIds[i] = soundPool.load(soundUris[i].getPath(), 1);
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

    private boolean toggleLoopMusicWithNoWithVolume(int musicPlayerNo, float volume){

        soundPlaying[musicPlayerNo] = !soundPlaying[musicPlayerNo];
        if(soundPlaying[musicPlayerNo]) {
            playLoopMusicWithNo(musicPlayerNo, volume);
        }else{
            stopMusic(musicPlayerNo);
        }
        return soundPlaying[musicPlayerNo];

    }

    private void playMusicOnceWithNo(int musicPlayerNo, float volume){
        playMusic(musicPlayerNo, soundUris[musicPlayerNo], false, volume);
    }

    private void playLoopMusicWithNo(int musicPlayerNo, float volume){
        playMusic(musicPlayerNo, soundUris[musicPlayerNo], true, volume);
    }

    private void playMusic(int musicPlayerNo, Uri uri, boolean looping, float volume) {
        stopMusic(musicPlayerNo);
        try {
            if (players[musicPlayerNo] == null) {
                players[musicPlayerNo] = MediaPlayer.create(context, uri);
            }
            players[musicPlayerNo].setLooping(looping);
            players[musicPlayerNo].setVolume(volume, volume);
            players[musicPlayerNo].start();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopMusic(int musicPlayerNo){
        if (players[musicPlayerNo] != null) {
            players[musicPlayerNo].pause();
            players[musicPlayerNo].seekTo(0);
        }
    }



}
