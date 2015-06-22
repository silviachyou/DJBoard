package com.lagendary.djboard;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * A placeholder fragment containing a simple view.
 */
public class MusicSettingsFragment extends Fragment {

    private LinearLayout view;

    public MusicSettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (LinearLayout) inflater.inflate(R.layout.fragment_music_settings, container, false);
        return view;
    }
}
