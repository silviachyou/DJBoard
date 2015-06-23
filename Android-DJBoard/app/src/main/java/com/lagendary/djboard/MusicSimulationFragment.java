package com.lagendary.djboard;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * A placeholder fragment containing a simple view.
 */
public class MusicSimulationFragment extends Fragment {

    private static final String BT_TAG = "$";

    private LinearLayout view;
    private Button simulateButton;
    private EditText commandEditText;

    private MusicPlayer player;

    private Handler mHandler;

    private boolean isSimulating = false;

    public MusicSimulationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        player = new MusicPlayer(getActivity());

        view = (LinearLayout) inflater.inflate(R.layout.fragment_music_simulate, container, false);
        simulateButton = (Button) view.findViewById(R.id.simulate_button);
        simulateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSimulating) {
                    simulateButton.setText(R.string.simulate_music);
                    stopSimulation();
                }else{
                    simulateButton.setText(R.string.abort_simulation);
                    startSimulation();
                }
            }
        });

        commandEditText = (EditText) view.findViewById(R.id.command_edit_text);

        mHandler = new Handler();
        return view;
    }

    private void stopSimulation() {
        isSimulating = false;
        mHandler.removeCallbacksAndMessages(null);
        player.stopAllMusic();
    }

    private void startSimulation() {
        isSimulating = true;
        String text = commandEditText.getText().toString();
        String[] commands = text.split("\n");
        for(int i = 0; i < commands.length; i++){
            String command = commands[i];
            if(command.length() < 4) {
                continue;
            }
            String tag = command.substring(0, 1);
            if(!tag.equals(BT_TAG)) {
                continue;
            }

            int secondSpacePlace = command.indexOf(' ', 2);
            if(secondSpacePlace == -1){
                continue;
            }

            String time = command.substring(2, secondSpacePlace);
            int timeInt = -1;
            try{
                timeInt = Integer.parseInt(time);
            }catch (NumberFormatException e){
                continue;
            }
            final String realCommand = command.substring(secondSpacePlace + 1);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player.actionForMessage(realCommand);
                }
            }, timeInt);
        }
    }


}
