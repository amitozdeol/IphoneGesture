package com.iphonegesture;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class FloatingBar extends Service implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private static final int flingActionMinDstVac = 120;
    private static final int flingActionMinSpdVac = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 4500;   //speed of gesture
    GestureDetectorCompat gestureScanner;
    WindowManager wm;
    View mView;
    @Override
    public IBinder
    onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gestureScanner = new GestureDetectorCompat(this,this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        mView = new View(this);
        mView.setOnTouchListener(this);
        params.gravity = Gravity.BOTTOM;
        params.height = (height*5) / 100;         // 10%
        params.width = width;                      // 100%
        params.setTitle("Load Average");
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mView != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
    }

    private void openRecentApps() {
        try {
            Class serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClass.getMethod("getService", String.class);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
            Class statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
            Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[]{retbinder});
            Method clearAll = statusBarClass.getMethod("toggleRecentApps");
            clearAll.setAccessible(true);
            clearAll.invoke(statusBarObject);
        } catch (Exception e) {
            //trying for nougat and above OS
            String topPackageName ;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService("usagestats");
                long time = System.currentTimeMillis();
                // We get usage stats for the last 10 seconds
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
                System.out.println(stats.toString());
                // Sort the stats by the last time used
                if(stats != null) {
                    SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                    for (UsageStats usageStats : stats) {
                        mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                    }
                    if(mySortedMap != null && !mySortedMap.isEmpty()) {
                        topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                }
            }
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Toast.makeText(this,"Overlay button event", Toast.LENGTH_SHORT).show();
        gestureScanner.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal, float flingActionYcoSpdPsgVal) {
        Log.i("Test", "On Fling");
        final boolean enoughSpeed = Math.abs(flingActionYcoSpdPsgVal) > SWIPE_THRESHOLD_VELOCITY;

        if(fstMsnEvtPsgVal.getY() - lstMsnEvtPsgVal.getY() > flingActionMinDstVac && Math.abs(flingActionYcoSpdPsgVal) > flingActionMinSpdVac)
        {
            // TskTdo :=> On Top to Bottom fling
            //fast swipe
//            if (enoughSpeed){
//                Toast.makeText(this,"Overlay button event2", Toast.LENGTH_SHORT).show();
//            }
//            //slow swipe
//            else{
                //Toast.makeText(this,"Overlay button event1", Toast.LENGTH_SHORT).show();
                openRecentApps();
            //}
            return false;
        }
        return false;
    }


    //    GestureDetector flingActionVar = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener()
//    {
//        private static final int flingActionMinDstVac = 120;
//        private static final int flingActionMinSpdVac = 200;
//
//        @Override
//        public boolean onFling(MotionEvent fstMsnEvtPsgVal, MotionEvent lstMsnEvtPsgVal, float flingActionXcoSpdPsgVal, float flingActionYcoSpdPsgVal)
//        {
//            if(fstMsnEvtPsgVal.getX() - lstMsnEvtPsgVal.getX() > flingActionMinDstVac && Math.abs(flingActionXcoSpdPsgVal) > flingActionMinSpdVac)
//            {
//                // TskTdo :=> On Right to Left fling
//
//                return false;
//            }
//            else if (lstMsnEvtPsgVal.getX() - fstMsnEvtPsgVal.getX() > flingActionMinDstVac && Math.abs(flingActionXcoSpdPsgVal) > flingActionMinSpdVac)
//            {
//                // TskTdo :=> On Left to Right fling
//
//                return false;
//            }
//
//            if(fstMsnEvtPsgVal.getY() - lstMsnEvtPsgVal.getY() > flingActionMinDstVac && Math.abs(flingActionYcoSpdPsgVal) > flingActionMinSpdVac)
//            {
//                // TskTdo :=> On Bottom to Top fling
//                //Toast.makeText(this,"Overlay button event1", Toast.LENGTH_SHORT).show();
//
//                return false;
//            }
//            else if (lstMsnEvtPsgVal.getY() - fstMsnEvtPsgVal.getY() > flingActionMinDstVac && Math.abs(flingActionYcoSpdPsgVal) > flingActionMinSpdVac)
//            {
//                // TskTdo :=> On Top to Bottom fling
//
//                return false;
//            }
//            return false;
//        }
//    });
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        return false;
    }
    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }
    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }
}
