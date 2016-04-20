package com.example.glennhatala.pebblerecipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txt1;
    Button btn1;
    Button btn2;
    Button btn3;

    private static final UUID APP_UUID = UUID.fromString("672ceda8-1ca2-402a-95dd-5109d97bef36");
    private PebbleKit.PebbleDataReceiver mDataReceiver;
    private boolean isDone = false;
    private int indexCur = 0;

    private static final int KEY_BUTTON_UP = 0;
    private static final int KEY_BUTTON_DOWN = 1;
    private static final int SEND_DONE = 2;
    private static final int SEND_NEXT = 3;

    private static final int KEY_RESULT = 1;
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_INGREDIENT = 1;
    private static final int TYPE_STEP = 2;

    private static final int RESULT = 3;
    private static final int RESULT_DONE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = (TextView)findViewById(R.id.txt1);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    public void onClick(View v) {
        String[] ingredients = new String[]{"apple", "bread", "milk"};
        PebbleDictionary dict = new PebbleDictionary();
        switch(v.getId()) {
            case R.id.btn1:
                isDone = false;
                dict.addString(0, "nothing");
                dict.addString(TYPE_TITLE, "A recipe title");
                PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
                break;
            case R.id.btn2:
                    sendNextItem(ingredients, indexCur);
                break;
            case R.id.btn3:
                isDone = false;
                dict.addString(0, "nothing");
                dict.addString(TYPE_STEP, "a step");
                PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
                break;
        }
    }

    public void sendNextItem(String[] items, int index) {
        PebbleDictionary dict = new PebbleDictionary();
        dict.addString(0, "nothing");
        dict.addString(TYPE_INGREDIENT, items[index]);
        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
        index++;
        if (index < items.length) {
            sendNextItem(items, index);
        }
        else {
            dict = new PebbleDictionary();
            dict.addInt32(RESULT, RESULT_DONE);
            PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isConnected = PebbleKit.isWatchConnected(this);
        txt1.setText("Pebble connected: " + isConnected);
        if (isConnected) {
            PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
        }
        //pushMessage();
        if(mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {
                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    PebbleKit.sendAckToPebble(context, transactionId);
                    Log.i("receiveData", "Got message from Pebble!");

                    // Up received?
                    if(dict.getInteger(KEY_BUTTON_UP) != null) {
                        txt1.setText("up");
                    }
                    // Down received?
                    if(dict.getInteger(KEY_BUTTON_DOWN) != null) {
                        txt1.setText("down");
                    }
                    if(dict.getInteger(SEND_NEXT) != null) {
                        indexCur = dict.getInteger(SEND_NEXT).intValue();
                    }
                    if(dict.getInteger(SEND_DONE) != null) {
                        isDone = true;
                    }
                }
            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }
    }



    public void pushMessage() {
        final Intent pebbleIntent = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        final Map data = new HashMap();
        data.put("title", "Test Message");
        data.put("body", "Body");
        final JSONObject json = new JSONObject(data);
        final String notificationData = new JSONArray().put(json).toString();

        pebbleIntent.putExtra("messageType", "PEBBLE_ALERT");
        pebbleIntent.putExtra("sender", "PebbleKit Android");
        pebbleIntent.putExtra("notificationData", notificationData);
        sendBroadcast(pebbleIntent);
    }
}
