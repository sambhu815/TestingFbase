package com.fastbase;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fastbase.support.ConnectivityReceiver;
import com.fastbase.support.MyApplication;
import com.fastbase.support.PrefManager;
import com.fastbase.support.SupportUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Splash_Activity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    String str_url;
    String str_id;

    SupportUtil support;
    PrefManager manager;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        support = new SupportUtil(this);
        manager = new PrefManager(this);

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        str_id = manager.getDeviceId();
        Log.e("Id", str_id);

        str_url = "http://mobileapi.fastbase.com/home/login?Key=" + str_id + "@1";

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        boolean isConnect = ConnectivityReceiver.isConnected();
        showSnack(isConnect);
    }

    private void showSnack(boolean isConnect) {
        if (isConnect) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);

                        if (manager.isLoggedIn()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(in);
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                    Intent in = new Intent(Intent.ACTION_VIEW);
                                    in.setData(Uri.parse(str_url));
                                    startActivity(in);
                                    finish();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            progressBar.setVisibility(View.GONE);
            Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connectiion.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Setting", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}
