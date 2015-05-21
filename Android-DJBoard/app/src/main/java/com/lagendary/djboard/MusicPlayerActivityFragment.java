package com.lagendary.djboard;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * A placeholder fragment containing a simple view.
 */
public class MusicPlayerActivityFragment extends Fragment {

    private LinearLayout view;

    private static int[] musicId = {R.raw.the_night_out, R.raw.gunfire, R.raw.household_drill, R.raw.male_cough, R.raw.paper_ripping};

    private MediaPlayer[] players = new MediaPlayer[musicId.length];

    public MusicPlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (LinearLayout) inflater.inflate(R.layout.fragment_music_player, container, false);
        for(int i = 0; i < musicId.length; i++){
            Button b = new Button(getActivity());
            final int mid = musicId[i];
            b.setText(i + "");
            final int no = i;
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playMusic(no);
                }
            });
            view.addView(b);
        }
        return view;
    }

    public void playMusic(int i){
        if (players[i] != null) {
            players[i].release();
            players[i] = null;
        }
        players[i] = MediaPlayer.create(getActivity(), musicId[i]);
        players[i].start();
    }
}
