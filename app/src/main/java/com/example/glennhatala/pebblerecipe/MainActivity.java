package com.example.glennhatala.pebblerecipe;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txt1;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btnOpen;

    private static final String DEBUG_TAG = "PebbleRecipeDebug";

    private static final UUID APP_UUID = UUID.fromString("672ceda8-1ca2-402a-95dd-5109d97bef36");
    private static final String DELIMITER = "|";
    private static final String BEGINNING = "~";
    private static final int MAX_LENGTH = 50;

    private PebbleKit.PebbleDataReceiver mDataReceiver;
    private boolean isDone = false;
    private int indexCur = 0;

    private static final int KEY_BUTTON_UP = 0;
    private static final int KEY_BUTTON_DOWN = 1;
    private static final int SEND_DONE = 2;
    private static final int SEND_NEXT = 3;

    private static final int TYPE_TITLE = 4;
    private static final int TYPE_INGREDIENT = 5;
    private static final int TYPE_STEP = 6;

    private static final int RESULT = 7;
    private static final int RESULT_DONE = 8;
    private static final int RESULT_SENDING = 9;
    private static final int INDEX = 10;
    private static final int SHOW_TYPE = 11;
    private static final int SHOW_ING = 12;
    private static final int SHOW_STEP = 13;

    private String title;
    private String url;
    private String[] ingredients;
    private String[] steps;
    private int sendingType = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = (TextView)findViewById(R.id.txt1);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btnOpen = (Button)findViewById(R.id.btnOpen);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btnOpen.setOnClickListener(this);

        TestIngredient testIng = new TestIngredient();
        testIng.fillWithTest();
        title = testIng.getTitle();
        url = testIng.getUrl();
        ingredients = testIng.getIngredients();
        steps = testIng.getSteps();
    }

    public void onClick(View v) {
        PebbleDictionary dict = new PebbleDictionary();
        switch(v.getId()) {
            case R.id.btn1:
                isDone = false;
                dict.addInt32(RESULT, RESULT_SENDING);
                dict.addString(TYPE_TITLE, title);
                PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
                dict.addInt32(RESULT, RESULT_DONE);
                break;
            case R.id.btn2:
                isDone = false;
                indexCur = 0;
                sendingType = TYPE_INGREDIENT;
                //sendNextItem(ingredients, indexCur, TYPE_INGREDIENT);
                sendIngredients(ingredients, indexCur, TYPE_INGREDIENT);
                break;
            case R.id.btn3:
                isDone = false;
                indexCur = 0;
                sendingType = TYPE_STEP;
                //sendNextItem(ingredients, indexCur, TYPE_INGREDIENT);
                sendSteps(steps, indexCur, sendingType);
                break;
            case R.id.btnOpen:
                boolean isConnected = PebbleKit.isWatchConnected(this);
                txt1.setText("Pebble connected: " + isConnected);
                if (isConnected) {
                    PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
                }
                break;
        }
    }

    public void sendIngredients(String[] items, int index, int type) {
        PebbleDictionary dict = new PebbleDictionary();
        String message = "";
        for (int i = 0; i < items.length; i++) {
            message += String.format("%d: %s", i + 1, items[i]);
        }
        message += "\n";
        Log.d(DEBUG_TAG, String.format("Length of message: %d", message.length()));

        dict.addInt32(RESULT, RESULT_SENDING);
        dict.addString(type, message);

        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
        Log.d(DEBUG_TAG, String.format("Sending: result - %d, message - %s",
                dict.getInteger(RESULT), dict.getString(type)));

    }
    public void sendSteps(String[] items, int index, int type) {
        PebbleDictionary dict = new PebbleDictionary();
        String message = "";
        String pattern = String.format("[%s%s]", DELIMITER, BEGINNING);
        //instead split send string with delimeters
        for (int i = 0; i < items.length; i++) {
            StringBuilder stringBuilder = new StringBuilder(Pattern.compile(pattern).matcher(items[i]).replaceAll(""));

                for (int j = 0; j < stringBuilder.length(); j += MAX_LENGTH) {

                    while ((j > 0 && j < stringBuilder.length()) && (stringBuilder.charAt(j) != ' ' && stringBuilder.charAt(j) != '\n')) {
                        j--;
                    }
                    stringBuilder.insert(j, BEGINNING);
                }
            message += String.format("%d: %s%n%S", i + 1, stringBuilder, DELIMITER);
        }
        message += "\n";
        Log.d(DEBUG_TAG, String.format("Length of message: %d", message.length()));

        dict.addInt32(RESULT, RESULT_SENDING);
        dict.addString(type, message);

        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
        Log.d(DEBUG_TAG, String.format("Sending: result - %d, message - %s",
                dict.getInteger(RESULT), dict.getString(type)));

    }

    public void sendNextItem(String[] items, int index, int type) {
        PebbleDictionary dict = new PebbleDictionary();

        if (index < items.length) {
            dict.addInt32(RESULT, RESULT_SENDING);
            dict.addInt32(INDEX, index);
            dict.addString(type, items[index]);
            PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
            Log.d(DEBUG_TAG, String.format("Sending: result - %d, type - %s, index - %d",
                    dict.getInteger(RESULT), dict.getString(type), dict.getInteger(INDEX)));
        }
        else {
            dict.addInt32(RESULT, RESULT_DONE);
            PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
            Log.d(DEBUG_TAG, String.format("Sending: %d",  dict.getInteger(RESULT)));
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
                        Log.d(DEBUG_TAG, String.format("Received: %d", dict.getInteger(SEND_NEXT)));
                        if (sendingType == TYPE_INGREDIENT) {
                            //sendNextItem(ingredients, indexCur, TYPE_INGREDIENT);
                            dict = new PebbleDictionary();
                            dict.addInt32(RESULT, RESULT_DONE);
                            dict.addInt32(SHOW_TYPE, SHOW_ING);
                            PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
                            Log.d(DEBUG_TAG, String.format("Sending: %d", dict.getInteger(RESULT)));
                        }
                        else if (sendingType == TYPE_STEP)
                            dict = new PebbleDictionary();
                            dict.addInt32(RESULT, RESULT_DONE);
                            dict.addInt32(SHOW_TYPE, SHOW_STEP);
                            PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dict);
                            Log.d(DEBUG_TAG, String.format("Sending: %d", dict.getInteger(RESULT)));
                    }
                    if(dict.getInteger(SEND_DONE) != null) {
                        isDone = true;
                        Log.d(DEBUG_TAG, String.format("Received: %d", dict.getInteger(SEND_DONE)));
                    }
                }
            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }
    }
}
