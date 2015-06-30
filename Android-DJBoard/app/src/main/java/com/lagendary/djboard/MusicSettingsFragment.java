package com.lagendary.djboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MusicSettingsFragment extends Fragment {

    private LinearLayout view;
    private ListView listView;
    private MusicItemAdapter adapter;

    private Button setSoundSetButton;
    private Button setBPMButton;

    private static final int REQUEST_PICK_MUSIC = 27;

    public MusicSettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (LinearLayout) inflater.inflate(R.layout.fragment_music_settings, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        adapter = new MusicItemAdapter(getActivity());

        setSoundSetButton = (Button) view.findViewById(R.id.set_sound_set_button);
        setSoundSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectSoundSetDialog();
            }
        });
        setBPMButton = (Button) view.findViewById(R.id.set_bpm_button);
        setBPMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetBPMDialog();
            }
        });


        listView.setAdapter(adapter);
        reloadList();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            Uri uriSound = data.getData();
            //String soundUriStr = uriSound.toString();
            String path = Util.getPath(getActivity(), uriSound);
            try{
                MusicPlayer.setUriStringOfMusic(getActivity(), path, requestCode - REQUEST_PICK_MUSIC);
            }catch (Exception e){
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }

            reloadList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSelectSoundSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayList<String> array = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            array.add(i+"");
        }
        String[] strarr = array.toArray(new String[array.size()]);

        builder.setItems(strarr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicPlayer.setSoundSetNo(getActivity(), which);
                Toast.makeText(getActivity(), "set: "+which, Toast.LENGTH_LONG).show();
                reloadList();
            }
        });
        builder.setTitle("Select sound set:");
        builder.show();
    }

    private void showSetBPMDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set BPM");

        final EditText editText = new EditText(getActivity());
        editText.setText(MusicPlayer.getBPM(getActivity()) + "");
        builder.setView(editText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    int bpm = Integer.parseInt(editText.getText().toString());
                    MusicPlayer.setBPM(getActivity(), bpm);
                    Toast.makeText(getActivity(), "BPM set to "+bpm+"!", Toast.LENGTH_LONG).show();
                    reloadList();
                }catch (Exception e){
                    Toast.makeText(getActivity(), "BPM must be an integer!", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);

        builder.show();
    }

    private void reloadList() {
        adapter.clear();
        for(int i = 0; i < MusicPlayer.SOUND_POOL_NO; i++){
            adapter.add(new MusicItem(MusicPlayer.SOUND_TITLES[i], MusicPlayer.getUriStringOfMusic(getActivity(), i)));
        }
        adapter.notifyDataSetChanged();
        int set = MusicPlayer.getSoundSetNo(getActivity());
        setSoundSetButton.setText("Set: "+set);
        setBPMButton.setText("BPM: "+MusicPlayer.getBPM(getActivity()));
    }

    private void startSelectMusic(int no) {

        try {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    REQUEST_PICK_MUSIC + no);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_MUSIC + no);
        }

    }

    private class MusicItem{
        String title;
        String uriString;

        MediaPlayer player;

        public MusicItem(String title, String uriString) {
            this.title = title;
            this.uriString = uriString;
        }
    }

    private class MusicItemAdapter extends ArrayAdapter<MusicItem>{

        public MusicItemAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_music_setting, null);
            }

            final MusicItem item = getItem(position);

            Button b = (Button) convertView.findViewById(R.id.button);
            TextView tv = (TextView) convertView.findViewById(R.id.title);


            Button playBtn = (Button) convertView.findViewById(R.id.play);
            Button resetBtn = (Button) convertView.findViewById(R.id.reset);

            b.setText(item.uriString);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSelectMusic(position);
                }
            });

            tv.setText(item.title);

            if(item.player != null){
                playBtn.setText(item.player.isPlaying() ? "STOP" : "PLAY");
            }
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.player == null) {
                        item.player = MediaPlayer.create(parent.getContext(), Uri.parse(item.uriString));
                        item.player.setLooping(true);
                    }

                    if(item.player.isPlaying()){
                        item.player.pause();
                    }else{
                        item.player.start();
                    }
                    notifyDataSetChanged();
                }
            });

            resetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayer.resetUriOfMusic(getActivity(), position);
                    reloadList();
                }
            });

            return convertView;
        }
    }

}
