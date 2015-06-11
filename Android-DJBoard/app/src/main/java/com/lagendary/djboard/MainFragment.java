/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lagendary.djboard;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.logger.Log;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";


    private static final String BOARD_ACTION_TAG = "Action";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    //status_display_layout views
    private View statusDisplayLayout;
    private ProgressBar wheelProgressBar;
    private TextView wheelMovingTextView;
    private TextView boardTurningTextView;
    private TextView ultraSonicTextView;
    private TextView knockFrontTextView;
    private TextView knockMidTextView;
    private TextView knockBackTextView;
    private TextView boardInfoTextView;

    private StringBuilder msgBuilder = new StringBuilder();

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService mChatService = null;

    private SoundPool soundPool;

    private static final int[] SOUND_RES_IDS = {R.raw.drum_loop, R.raw.yooo, R.raw.piano, R.raw.chaser, R.raw.beatbox};

    private static final int SOUND_POOL_NO = SOUND_RES_IDS.length;

    private static final int DRUM_SOUND_INDEX = 0;
    private static final int BOARD_UP_SOUND_INDEX = 1;
    private static final int BASE_SOUND_PIANO_INDEX = 2;
    private static final int BASE_SOUND_CHASER_INDEX = 3;
    private static final int BASE_SOUND_BEATBOX_INDEX = 4;

    private int[] soundIds = new int[SOUND_POOL_NO];
    private int[] streamIds = new int[SOUND_POOL_NO];

    private double boardVelocity = 0.0;
    private int boardDistance = 0;


    private MediaPlayer[] players = new MediaPlayer[SOUND_POOL_NO];

    private boolean[] soundPlaying = new boolean[SOUND_POOL_NO]; // sparse, depends on if is toggled by the developer



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
        initSoundPool();
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);
        statusDisplayLayout = view.findViewById(R.id.status_display_layout);
        wheelProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        wheelMovingTextView = (TextView) view.findViewById(R.id.wheel_moving_text);
        boardInfoTextView = (TextView) view.findViewById(R.id.board_info_text);
        boardTurningTextView = (TextView) view.findViewById(R.id.board_turning_text);
        ultraSonicTextView = (TextView) view.findViewById(R.id.ultrasonic_text);
        knockFrontTextView = (TextView) view.findViewById(R.id.knock_state_front);
        knockMidTextView = (TextView) view.findViewById(R.id.knock_state_mid);
        knockBackTextView = (TextView) view.findViewById(R.id.knock_state_back);
        resetViews();
    }

    private void resetViews() {
        setWheelMoving(false);
        knockBackTextView.setBackgroundColor(getResources().getColor(R.color.red_100));
        knockFrontTextView.setBackgroundColor(getResources().getColor(R.color.red_100));
        knockMidTextView.setBackgroundColor(getResources().getColor(R.color.red_100));
        ultraSonicTextView.setText("");
        boardTurningTextView.setText("");
        boardDistance = 0;
        boardVelocity = 0.0;
        notifyBoardInfoUpdated();
    }

    private void notifyBoardInfoUpdated() {
        boardInfoTextView.setText(getString(R.string.board_info_distance) + ": " + boardDistance + "  " +  getString(R.string.board_info_velocity) + ": " + boardVelocity);
    }

    private void setWheelMoving(boolean moving){
        wheelMovingTextView.setText(getResources().getString(moving ? R.string.wheel_moving_state_moving : R.string.wheel_moving_state_stopped));
        wheelProgressBar.setVisibility(moving ? View.VISIBLE : View.INVISIBLE);
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
            soundIds[i] = soundPool.load(getActivity(), SOUND_RES_IDS[i], 1);
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private boolean sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            message = message + "\0";
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
            return true;
        }
        return false;
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            resetViews();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    processReceivedMessage(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
            case R.id.toggle_bluetooth_log:
                mConversationView.setVisibility(mConversationView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                return true;
        }
        return false;
    }


    public boolean toggleLoopMusicWithNoFromSoundPool(int musicPlayerNo, float volume){
        soundPlaying[musicPlayerNo] = !soundPlaying[musicPlayerNo];
        if(soundPlaying[musicPlayerNo]) {
            streamIds[musicPlayerNo] = soundPool.play(soundIds[musicPlayerNo], volume, volume, 1, -1, 1.0f);
        }else{
            soundPool.stop(streamIds[musicPlayerNo]);
        }
        return soundPlaying[musicPlayerNo];
    }

    public void toggleLoopMusicWithNoWithVolume(int musicPlayerNo, float volume){

        soundPlaying[musicPlayerNo] = !soundPlaying[musicPlayerNo];
        if(soundPlaying[musicPlayerNo]) {
            playLoopMusicWithNo(musicPlayerNo, volume);
        }else{
            stopMusic(musicPlayerNo);
        }

    }

    public void playLoopMusicWithNo(int musicPlayerNo, float volume){
        playMusic(musicPlayerNo, SOUND_RES_IDS[musicPlayerNo], true, volume);
    }

    public void playMusic(int musicPlayerNo, int resId, boolean looping, float volume){
        stopMusic(musicPlayerNo);
        players[musicPlayerNo] = MediaPlayer.create(getActivity(), resId);
        players[musicPlayerNo].setLooping(looping);
        players[musicPlayerNo].setVolume(volume, volume);
        players[musicPlayerNo].start();
    }

    public void stopMusic(int musicPlayerNo){
        if (players[musicPlayerNo] != null) {
            players[musicPlayerNo].release();
            players[musicPlayerNo] = null;
        }
    }


    public void processReceivedMessage(String msg) {
        for(int i = 0 ;i < msg.length(); i++){
            char c = msg.charAt(i);
            if(c != '\n') {
                msgBuilder.append(c);
            }else{
                //is empty char
                String action = msgBuilder.toString();
                actionForMessage(action);
                msgBuilder = new StringBuilder();
            }
        }
    }

    private void actionForMessage(String msg){
        mConversationArrayAdapter.add(BOARD_ACTION_TAG + ":  " + msg);
        switch (msg) {
            case "wheelmove":
                soundPool.stop(streamIds[DRUM_SOUND_INDEX]);
                streamIds[DRUM_SOUND_INDEX] = soundPool.play(soundIds[DRUM_SOUND_INDEX], 1.0f, 1.0f, 1, -1, 1.0f);
                setWheelMoving(true);
                //playMusic(0, R.raw.the_night_out);
                break;
            case "wheelstop":
                soundPool.stop(streamIds[DRUM_SOUND_INDEX]);
                setWheelMoving(false);
                //stopMusic(0);
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
                knockFrontTextView.setBackgroundColor(getResources().getColor(frontStarted ? R.color.red_500 : R.color.red_100));
                break;
            case "knockMid":
                boolean middleStarted = toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_CHASER_INDEX, 0.5f);
                knockMidTextView.setBackgroundColor(getResources().getColor(middleStarted ? R.color.red_500 : R.color.red_100));
                break;
            case "knockBack":
                boolean backStarted = toggleLoopMusicWithNoFromSoundPool(BASE_SOUND_BEATBOX_INDEX, 1.0f);
                knockFrontTextView.setBackgroundColor(getResources().getColor(backStarted ? R.color.red_500 : R.color.red_100));
                break;
            case "turnright":
                boardTurningTextView.setText(getString(R.string.board_turning_turn_right));
                break;
            case "turnleft":
                boardTurningTextView.setText(getString(R.string.board_turning_turn_left));
                break;
            case "stopRolling":
                boardTurningTextView.setText("");
                break;
            case "stop_ultrasound":
                ultraSonicTextView.setText(getString(R.string.ultrasonic_stopped));
                break;
            default:
                if(msg.startsWith("ultrasound:")) {
                    String no = msg.substring(11).trim();
                    double noDouble = Double.parseDouble(no);
                    ultraSonicTextView.setText(getString(R.string.ultrasonic_distance) + ": " + noDouble + "cm");
                }else if(msg.startsWith("v= ")) {
                    boardVelocity = Double.parseDouble(msg.substring(3).trim());
                    notifyBoardInfoUpdated();
                }else if(msg.startsWith("d= ")) {
                    boardDistance = Integer.parseInt(msg.substring(3).trim());
                    notifyBoardInfoUpdated();
                }
                break;
        }
    }

}
