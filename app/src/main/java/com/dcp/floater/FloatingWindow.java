package com.dcp.floater;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class FloatingWindow extends Service {

    private WindowManager wm;
    private LinearLayout ll;
    private Button stopBtn;
    private LinearLayout chatLayout;
    private ImageView closeIcon;
    private InputBox inputBox;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);
        stopBtn = new Button(this);
        chatLayout = new LinearLayout(this);
        closeIcon = new ImageView(this);
        inputBox = new InputBox(this, wm);

        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stopBtn.setText("Stop");
        stopBtn.setLayoutParams(btnParameters);

        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(66, 255, 0, 0));
        ll.setLayoutParams(llParameters);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(400, 250, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.TOP | Gravity.START;

        ll.addView(stopBtn);
        ll.addView(chatLayout);
        wm.addView(ll, parameters);

        // Initially hide the chat layout
        chatLayout.setVisibility(View.GONE);

        // Close icon parameters
        final WindowManager.LayoutParams closeIconParams = new WindowManager.LayoutParams(100, 100, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        closeIconParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        closeIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeIcon.setVisibility(View.GONE);
        wm.addView(closeIcon, closeIconParams);

        ll.setOnTouchListener(new View.OnTouchListener() {

            private WindowManager.LayoutParams updatedParameters = parameters;
            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - touchedX);
                        int Ydiff = (int) (event.getRawY() - touchedY);

                        if (Xdiff < 10 && Ydiff < 10) {
                            // Handle click event
                        }

                        if (event.getRawY() >= (wm.getDefaultDisplay().getHeight() - closeIcon.getHeight())) {
                            wm.removeView(ll);
                            inputBox.hide();
                            stopSelf();
                        }

                        closeIcon.setVisibility(View.GONE);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - touchedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - touchedY));

                        wm.updateViewLayout(ll, updatedParameters);
                        inputBox.updatePosition(updatedParameters.x + ll.getWidth() + 5, updatedParameters.y);

                        // Show close icon only when near the bottom
                        if (event.getRawY() >= (wm.getDefaultDisplay().getHeight() - 200)) {
                            closeIcon.setVisibility(View.VISIBLE);
                        } else {
                            closeIcon.setVisibility(View.GONE);
                        }
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int screenWidth = wm.getDefaultDisplay().getWidth();
                int widgetRightEdge = parameters.x + ll.getWidth();
                int gap = 7;

                if (widgetRightEdge + 400 + gap > screenWidth) {
                    // Position input box to the left of the stop button
                    inputBox.show(parameters.x - 460 - gap-90, parameters.y);
                } else {
                    // Position input box to the right of the stop button
                    inputBox.show(parameters.x + ll.getWidth() + gap, parameters.y);
                }

                chatLayout.setVisibility(View.VISIBLE);
                stopBtn.setText("Submit");
            }
        });
    }

    public void addChatMessage(View message) {
        chatLayout.addView(message);
    }
}