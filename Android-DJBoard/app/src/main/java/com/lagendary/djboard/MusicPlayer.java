package com.lagendary.djboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.android.common.logger.Log;

/**
 * Created by joshua on 6/22/15.
 */
public class MusicPlayer {


    private static final String TAG = "MusicPlayer";
    private SoundPool soundPool;


    private static final String SHARED_PREF_SOUND_SET = "sound_set";

    private static final String SHARED_PREF_BPM = "sound_BPM";

    private static final String SHARED_PREF_SOUND_PATH_PREFIX = "sound_path_";
    private Uri[] soundUris = new Uri[SOUND_POOL_NO];
    private static final int[] DEFAULT_SOUND_RES_IDS = {R.raw.yo, R.raw.piano, R.raw.chaser, R.raw.beatbox, R.raw.chaser, R.raw.drum_loop, R.raw.piano, R.raw.chaser, R.raw.beatbox};

    public static final String[] SOUND_TITLES = {"Board Up", "Stick Up Right", "Stick Up Left", "Board Turn 180", "UltraSound", "Base Sound 1", "Base Sound 2", "Base Sound 3", "Base Sound 4"};

    public static final int SOUND_POOL_NO = 9;

    public static final int BOARD_UP_SOUND_INDEX = 0;
    public static final int STICK_UP_RIGHT_INDEX = 1;
    public static final int STICK_UP_LEFT_INDEX = 2;
    public static final int BOARD_TURN_180_INDEX = 3;
    //public static final int KNOCK_FRONT_SOUND_INDEX = 2;
    //public static final int KNOCK_MID_SOUND_INDEX = 3;
    //public static final int KNOCK_BACK_SOUND_INDEX = 4;
    public static final int BASE_SOUND_ULTRA_SONIC_INDEX = 4;

    public static final int BASE_SOUND_1_INDEX = 5;
    public static final int BASE_SOUND_2_INDEX = 6;
    public static final int BASE_SOUND_3_INDEX = 7;
    public static final int BASE_SOUND_4_INDEX = 8;

    private int[] soundIds = new int[SOUND_POOL_NO];
    private int[] streamIds = new int[SOUND_POOL_NO];

    private long loopTime = 1846; // 130bpm, 4 beat time
    private Handler handler = new Handler();

    private MediaPlayer[] players = new MediaPlayer[SOUND_POOL_NO];
    private Equalizer[] eqs = new Equalizer[SOUND_POOL_NO];

    private boolean[] soundPlaying = new boolean[SOUND_POOL_NO]; // sparse, depends on if is toggled by the developer

    private Context context;

    private static final double VTH1 = 0.0;
    private static final double VTH2 = 1.2;
    private static final double VTH3 = 5.0;
    private static final double VTH4 = 10.0;

    private static final int AUDIO_SESSION = 8;

    private double prevBoardVelocity = -999;
    private double boardVelocity = -999;
    private Runnable scheduleWheelMoveRunnable = new Runnable() {

        @Override
        public void run() {
            checkBaseSound(boardVelocity, prevBoardVelocity);
            prevBoardVelocity = boardVelocity;

            handler.postDelayed(scheduleWheelMoveRunnable, loopTime);
        }

    };

    public MusicPlayer(Context context){
        this.context = context;
        initUris();
        //initSoundPool();
    }

    public void stopAllMusic() {
        for(int i = 0; i < SOUND_POOL_NO; i++){
            stopMusic(i);
            //soundPool.release();
            //initSoundPool();
            soundPlaying[i] = false;
        }
        handler.removeCallbacksAndMessages(null);
        prevBoardVelocity = -999.0;
        boardVelocity = -999.0;
    }

    public boolean actionForMessage(String msg){
        switch (msg) {
            case "wheelmove":
                boardVelocity = 0.0;
                handler.post(scheduleWheelMoveRunnable);
                break;
            case "wheelstop":
                handler.removeCallbacksAndMessages(null);
                checkBaseSound(-999.0, 1.0);
                prevBoardVelocity = -999.0;
                boardVelocity = -999.0;
                break;
            case "boardup":
                playMusicOnceWithNo(BOARD_UP_SOUND_INDEX, 1.0f);
                //playMusic(0, R.raw.the_night_out);
                break;
            case "boarddown":
                //stopMusic(0);
                break;
            /*
            case "knockFront":
                boolean frontStarted = toggleLoopMusicWithNoWithVolume(KNOCK_FRONT_SOUND_INDEX, 0.5f);
                return frontStarted;
            case "knockMid":
                boolean middleStarted = toggleLoopMusicWithNoWithVolume(KNOCK_MID_SOUND_INDEX, 0.5f);
                return middleStarted;
            case "knockBack":
                boolean backStarted = toggleLoopMusicWithNoWithVolume(KNOCK_BACK_SOUND_INDEX, 1.0f);
                return backStarted;
                */
            case "stickUpLeft":
                playMusicOnceWithNo(STICK_UP_LEFT_INDEX, 1.0f);
                break;
            case "stickUpRight":
                playMusicOnceWithNo(STICK_UP_RIGHT_INDEX, 1.0f);
                break;
            case "turn180":
                playMusicOnceWithNo(BOARD_TURN_180_INDEX, 1.0f);
                break;
            case "turnright":
                break;
            case "turnleft":
                break;
            case "stopRolling":
                break;
            case "stop_ultrasound":
                stopMusic(BASE_SOUND_ULTRA_SONIC_INDEX);
                for(int i = 0; i < eqs.length; i++){
                    if(eqs[i] != null) {
                        eqs[i].setEnabled(false);
                        eqs[i] = null;
                    }
                }

                break;
            default:
                if(msg.startsWith("ultrasound:")) {
                    String no = msg.substring(11).trim();
                    double noDouble = Double.parseDouble(no);

                    if(!soundPlaying[BASE_SOUND_ULTRA_SONIC_INDEX]) {
                        //playLoopMusicWithNo(BASE_SOUND_ULTRA_SONIC_INDEX, 1.0f);
                        if( players[BASE_SOUND_1_INDEX] == null){
                            return false;
                        }

                    }

                    for(int i = 0; i < eqs.length; i++) {
                        if(players[i] == null){
                            continue;
                        }

                        if (eqs[i] == null) {
                            int sessionId = players[i].getAudioSessionId();
                            eqs[i] = new Equalizer(1, sessionId);
                            eqs[i].setEnabled(true);
                        }

                        /*
                        float rate = 1.0f + (float) ((noDouble - 40.0) / 40.0);
                        soundPool.setRate(streamIds[BASE_SOUND_ULTRA_SONIC_INDEX], rate);
                        */
                        ///*
                        double percentage = (Math.min(Math.max(noDouble, 100.0), 140.0) - 100) / 40;

                        short[] level = eqs[i].getBandLevelRange();
                        short minLv = (short) (level[0] + (level[1] - level[0]) / 2 * percentage);
                        short maxLv = (short) (level[1] - (level[1] - level[0]) / 2 * percentage);
                        short bandCount = eqs[i].getNumberOfBands();
                        short lvStep = (short) ((maxLv - minLv) / (bandCount - 1));

                        for (short j = 0; j < bandCount; j++) {
                            eqs[i].setBandLevel(j, (short) (minLv + lvStep * j));
                        }
                        //*/
                    }
                }else if(msg.startsWith("v= ")) {
                    try {
                        boardVelocity = Double.parseDouble(msg.substring(3).trim());
                    } catch (NumberFormatException e){}
                }else if(msg.startsWith("d= ")) {
                }
                break;
        }
        return true;
    }

    private void checkBaseSound(double velocity, double prevBoardVelocity){
        if(velocity > prevBoardVelocity){
            if(velocity >= VTH1) {
                playLoopMusicWithNo(BASE_SOUND_1_INDEX, 1.0f);
            }
            if(velocity >= VTH2) {
                playLoopMusicWithNo(BASE_SOUND_2_INDEX, 1.0f);
            }
            if(velocity >= VTH3) {
                playLoopMusicWithNo(BASE_SOUND_3_INDEX, 1.0f);
            }
            if(velocity >= VTH4) {
                playLoopMusicWithNo(BASE_SOUND_4_INDEX, 1.0f);
            }
        }else if(velocity < prevBoardVelocity){
            if(velocity < VTH1) {
                stopMusic(BASE_SOUND_1_INDEX);
            }
            if(velocity < VTH2) {
                stopMusic(BASE_SOUND_2_INDEX);
            }
            if(velocity < VTH3) {
                stopMusic(BASE_SOUND_3_INDEX);
            }
            if(velocity < VTH4) {
                stopMusic(BASE_SOUND_4_INDEX);
            }
        }
    }

    public static int getSoundSetNo(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(SHARED_PREF_SOUND_SET, 0);
    }

    public static void setSoundSetNo(Context context, int no) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(SHARED_PREF_SOUND_SET, no).apply();
    }

    public static int getBPM(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(SHARED_PREF_BPM + getSoundSetNo(context), 130);
    }

    public static void setBPM(Context context, int bpm){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(SHARED_PREF_BPM + getSoundSetNo(context), bpm).apply();
    }


    public static String getUriStringOfMusic(Context context, int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(SHARED_PREF_SOUND_PATH_PREFIX + getSoundSetNo(context) + "_" + i, "android.resource://" + context.getPackageName() + "/" + DEFAULT_SOUND_RES_IDS[i]);
    }

    public static void setUriStringOfMusic(Context context, String str, int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(SHARED_PREF_SOUND_PATH_PREFIX + getSoundSetNo(context) + "_" + i, str).apply();
    }

    public static void resetUriOfMusic(Context context, int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().remove(SHARED_PREF_SOUND_PATH_PREFIX + getSoundSetNo(context) + "_"  + i).apply();
    }

    private void initUris() {
        for(int i = 0; i < SOUND_POOL_NO; i++) {
            String uristr = getUriStringOfMusic(context, i);
            soundUris[i] = Uri.parse(uristr);
        }
        loopTime = (long) ((1000 * 60.0 * 4.0) / getBPM(context));
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
        if(players[musicPlayerNo] != null && players[musicPlayerNo].isPlaying()){
            return;
        }

        playMusic(musicPlayerNo, soundUris[musicPlayerNo], true, volume);
    }

    private void playMusic(final int musicPlayerNo, Uri uri, boolean looping, float volume) {
        stopMusic(musicPlayerNo);
        try {
            if (players[musicPlayerNo] == null) {
                players[musicPlayerNo] = MediaPlayer.create(context, uri);
            }
            players[musicPlayerNo].setLooping(looping);

            //players[musicPlayerNo].setLooping(looping);
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
