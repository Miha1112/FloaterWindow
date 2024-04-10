package com.dcp.floater;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

public class FloatingWindow extends Service {

    private WindowManager wm;
    private RelativeLayout ll,ll2;
    private Context context;
    private Button stopBtn,newBtn,thirdBtn;
    private TextView textView;

    int i = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        ll = new RelativeLayout(this);
        stopBtn = new Button(this);
        newBtn = new Button(this);
        textView = new TextView(this);

        LinearLayout.LayoutParams btnParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParameters.setMargins(0, 0, 0, 50);
        btnParameters.width = 300;
        stopBtn.setText("Stop");
        stopBtn.setLayoutParams(btnParameters);

        LinearLayout.LayoutParams newBtnParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newBtnParameters.width = 300;
        newBtnParameters.setMargins(300, 0, 0, 50); // Відстань 50 пікселів внизу нової кнопки
        newBtn.setText("New button");
        newBtn.setLayoutParams(newBtnParameters);

        LinearLayout.LayoutParams textParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParameters.setMargins(250, 150, 0, 100); // Відстань 100 пікселів внизу тексту
        textView.setText("Text");
        textView.setTextColor(Color.BLUE);
        textView.setLayoutParams(textParameters);

        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setBackgroundColor(Color.argb(66, 255, 0, 0));
        ll.setLayoutParams(llParameters);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(500, 500,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE , PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.CENTER | Gravity.CENTER;


        ll.addView(newBtn);
        ll.addView(stopBtn);
        ll.addView(textView);
        wm.addView(ll, parameters);


        ll.setOnTouchListener(new View.OnTouchListener() {

            private  WindowManager.LayoutParams updatedParameters = parameters;
            int x, y;
            float touchedX, touchedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        touchedX = event.getRawX();
                        touchedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - touchedX);
                        int Ydiff = (int) (event.getRawY() - touchedY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {

                            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                            if (activityManager != null) {
                                List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(1);
                                if (!taskInfoList.isEmpty()) {
                                    ComponentName topActivity = taskInfoList.get(0).topActivity;
                                    if (topActivity != null && topActivity.getPackageName().equals(getApplicationContext().getPackageName())) {
                                        // If the app is already in the foreground, move it to the front
                                        int taskId = taskInfoList.get(0).id;
                                        activityManager.moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME);
                                    }
                                }
                            }

                            // Close the floating window
                           // wm.removeView(ll);
                          //  stopSelf();

                        }

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - touchedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - touchedY));

                        wm.updateViewLayout(ll, updatedParameters);

                    default:
                        break;
                }

                return false;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
                wm.removeView(ll);
                stopSelf();
            }
        });
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(ll);
                updateView();
            }
        });

    }
    private void updateView(){
        //new element of floatingWindow for change  eny parameters write here
        ll2 = new RelativeLayout(this);
        thirdBtn = new Button(this);
        LinearLayout.LayoutParams btnParameters = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParameters.setMargins(0, 0, 0, 50);
        thirdBtn.setText("1 click show text, 3 click open previous window");
        thirdBtn.setLayoutParams(btnParameters);
        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll2.setBackgroundColor(Color.argb(66, 255, 0, 0));
        ll2.setLayoutParams(llParameters);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(500, 500,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE , PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.CENTER | Gravity.CENTER;


        ll2.addView(thirdBtn);
        wm.addView(ll2, parameters);
        thirdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("i: " + i);
                i++;
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                    }
                };
                if (i==1){
                    Toast.makeText(getApplicationContext(),"One click on button",Toast.LENGTH_SHORT).show();
                    handler.postDelayed(runnable,800);
                }else if (i == 3){
                    System.out.println("CLick 3 times on button");
                    wm.removeView(ll2);
                    stopSelf();

                    // Запуск нового вікна
                    Intent intent = new Intent(context, FloatingWindow.class);
                    startService(intent);
                }
            }
        });
    }

    private void returnToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
