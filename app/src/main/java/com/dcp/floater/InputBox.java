package com.dcp.floater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InputBox {

    private Context context;
    private WindowManager wm;
    private EditText inputBox;
    private LinearLayout inputLayout;
    private WindowManager.LayoutParams inputBoxParams;

    public InputBox(Context context, WindowManager wm) {
        this.context = context;
        this.wm = wm;
        createInputBox();
    }

    private void createInputBox() {
        inputLayout = new LinearLayout(context);
        inputBox = new EditText(context);

        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputBox.setLayoutParams(inputParams);
        inputBox.setFocusable(true);
        inputBox.setFocusableInTouchMode(true);
        inputBox.setBackgroundColor(Color.LTGRAY); // Set background color to light grey

        inputLayout.addView(inputBox);

        inputBoxParams = new WindowManager.LayoutParams(400, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        inputBoxParams.gravity = Gravity.TOP | Gravity.START;

        wm.addView(inputLayout, inputBoxParams);

        inputBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String input = inputBox.getText().toString();
                    if (!input.isEmpty()) {
                        // Send input to backend service
                        Intent intent = new Intent(context, BackendService.class);
                        intent.putExtra("input", input);
                        context.startService(intent);

                        // Display the input in the chat layout
                        TextView userMessage = new TextView(context);
                        userMessage.setText(input);
                        ((FloatingWindow) context).addChatMessage(userMessage);

                        // Simulate backend response
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TextView responseMessage = new TextView(context);
                                responseMessage.setText("Got it");
                                ((FloatingWindow) context).addChatMessage(responseMessage);
                            }
                        }, 2000); // Simulate delay
                    }
                    return true;
                }
                return false;
            }
        });

        inputLayout.setVisibility(View.GONE);
    }

    public void show(int x, int y) {
        inputBoxParams.x = x;
        inputBoxParams.y = y;
        wm.updateViewLayout(inputLayout, inputBoxParams);
        inputLayout.setVisibility(View.VISIBLE);
        inputBox.requestFocus();
    }

    public void hide() {
        inputLayout.setVisibility(View.GONE);
    }

    public void updatePosition(int x, int y) {
        inputBoxParams.x = x;
        inputBoxParams.y = y;
        wm.updateViewLayout(inputLayout, inputBoxParams);
    }
}