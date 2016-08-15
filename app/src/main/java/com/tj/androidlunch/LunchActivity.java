package com.tj.androidlunch;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

public class LunchActivity extends Activity {

    private static final int START_MAIN_ACTIVITY_DELAY_TIME = 3000;

    private final StartMainActivityHandler mStartMainActivityHandler = new StartMainActivityHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isNeedShowTranslucentSystemBar()) {
            Window curWindow = getWindow();
            curWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            curWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_lunch);

        if (isNeedShowTranslucentSystemBar() && checkDeviceHasNavigationBar()){
            int navigationBarHeight = getNavigationBarHeight();
            if (navigationBarHeight > 0){
                View copyRightInfo = findViewById(R.id.app_copyright_info);
                FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams)copyRightInfo.getLayoutParams();
                flp.bottomMargin = navigationBarHeight + (int)getResources().getDimension(R.dimen.app_copyright_info_bottom_margin);
                copyRightInfo.setLayoutParams(flp);
            }
        }
    }

    static class StartMainActivityHandler extends Handler {

        private final WeakReference<LunchActivity> mLunchActivityWeakReference;

        public StartMainActivityHandler(LunchActivity splashActivity) {
            mLunchActivityWeakReference = new WeakReference<>(splashActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            LunchActivity splashActivity = mLunchActivityWeakReference.get();
            if (splashActivity != null){
                Intent intent = new Intent(splashActivity, MainActivity.class);
                splashActivity.startActivity(intent);
                splashActivity.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStartMainActivityHandler.sendMessageDelayed(Message.obtain() ,START_MAIN_ACTIVITY_DELAY_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStartMainActivityHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {}

    private boolean isNeedShowTranslucentSystemBar(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private int getNavigationBarHeight() {

        int navigationBarHeight = 0;

        Resources rs = getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id != 0){
            try {
                navigationBarHeight = rs.getDimensionPixelSize(id);
            }catch (Resources.NotFoundException exception){
                navigationBarHeight = 0;
            }
        }
        return navigationBarHeight;
    }

    private boolean checkDeviceHasNavigationBar() {
        return !ViewConfiguration.get(this).hasPermanentMenuKey() &&
                !KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    }

}
