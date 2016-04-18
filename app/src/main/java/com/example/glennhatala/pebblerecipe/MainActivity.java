package com.example.glennhatala.pebblerecipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView txt1;

    private static final int KEY_BUTTON_UP = 0;
    private static final int KEY_BUTTON_DOWN = 1;
    private static final UUID APP_UUID = UUID.fromString("672ceda8-1ca2-402a-95dd-5109d97bef36");
    private PebbleKit.PebbleDataReceiver mDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = (TextView)findViewById(R.id.txt1);
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
