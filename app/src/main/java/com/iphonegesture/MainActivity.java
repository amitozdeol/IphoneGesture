package com.iphonegesture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    public static int OVERLAY_PERMISSION_REQ_CODE_FLOATINGBAR = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_FLOATINGBAR_HIDE = 5678;
    public static Button btnStartService, btnStopService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = (Button)findViewById(R.id.startService);
        btnStopService = (Button)findViewById(R.id.stopService);

        btnStartService.setOnClickListener(lst_StartService);
        btnStopService.setOnClickListener(lst_ShowMsg);

    }

    //start service
    Button.OnClickListener lst_StartService = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(Utils.LogTag, "lst_StartService -> Utils.canDrawOverlays(Main.this): " + Utils.canDrawOverlays(MainActivity.this));

            if(Utils.canDrawOverlays(MainActivity.this))
                startFloatingBar();
            else{
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_FLOATINGBAR);
            }
        }

    };

    //stop service
    Button.OnClickListener lst_ShowMsg = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(Utils.canDrawOverlays(MainActivity.this))
                stopFloatingBar();
            else{
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_FLOATINGBAR_HIDE);
            }

        }

    };

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }


    private void startFloatingBar(){
        Intent svc = new Intent(this, FloatingBar.class);
        startService(svc);
        finish();
    }
    private void stopFloatingBar(){
        Intent svc = new Intent(this, FloatingBar.class);
        stopService(svc);
        finish();
    }

    //Alert message for permission
    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_FLOATINGBAR) {
            if (!Utils.canDrawOverlays(MainActivity.this)) {
                needPermissionDialog(requestCode);
            }else{
                startFloatingBar();
            }

        }else if(requestCode == OVERLAY_PERMISSION_REQ_CODE_FLOATINGBAR_HIDE){
            if (!Utils.canDrawOverlays(MainActivity.this)) {
                needPermissionDialog(requestCode);
            }else{
                stopFloatingBar();
            }

        }

    }
}