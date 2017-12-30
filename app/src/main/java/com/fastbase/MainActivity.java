package com.fastbase;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fastbase.adapter.PeriodeAdapter;
import com.fastbase.adapter.WebLeadsAdapter;
import com.fastbase.adapter.WebListAdapter;
import com.fastbase.fragment.Home_Fragment;
import com.fastbase.model.WebList;
import com.fastbase.support.AppConstant;
import com.fastbase.support.ConnectivityReceiver;
import com.fastbase.support.MyApplication;
import com.fastbase.support.PrefManager;
import com.fastbase.support.SupportUtil;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ListView list_time;
    PeriodeAdapter periodeAdapter;

    ListView list_web;
    WebListAdapter webListAdapter;
    ArrayList<WebList> webLists;
    WebList web;

    Dialog dialog;
    ArrayList<HashMap<String, String>> leadsList;

    ImageView iv_down;
    ImageView iv_lead, iv_hot, iv_ad, iv_total, iv_email;

    LinearLayout lin_menu, lin_duration, lin_property, lin_weblist, lin_password, lin_details;
    LinearLayout lin_logout;

    RelativeLayout rl_setting, rl_web, rl_hot, rl_ad, rl_total, rl_bg, rl_lock, lin_profile, rl_menu, rl_menu_open;
    EditText edt_search;
    TextView tv_property, tv_periode, tv_totallead, tv_count, tv_name, tv_plan;
    TextView tv_webleads, tv_hot, tv_ad, tv_total, tv_email;
    TextView tv_webleads_count, tv_hot_count, tv_ad_count, tv_total_count;

    CircleImageView iv_profile;
    ImageView iv_loading;

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    SupportUtil support;
    PrefManager manager;
    TelephonyManager tele_manager;
    SharedPreferences pref;

    OkHttpClient client;
    Request request;

    String str_success, str_error, str_pid, str_aid, str_duration, str_leadtype, str_token, str_id;
    String str_query, str_pageno, str_pagesize;
    boolean exit = false;
    boolean isConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(this);
        tele_manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        manager = new PrefManager(this);
        leadsList = new ArrayList<>();

        pref = getSharedPreferences(manager.PREF_NAME, 0);
        str_token = pref.getString(manager.Pm_token, null);

        webLists = new ArrayList<WebList>();
        web = new WebList("All Websites", "0", "0");
        webLists.add(web);

        str_id = tele_manager.getDeviceId();
        str_pid = "0";
        str_aid = "0";
        str_duration = "0";
        str_query = "0";
        str_pageno = "1";
        str_pagesize = "20";
        str_leadtype = "WebLeads";

        list_time = (ListView) findViewById(R.id.list_time);
        list_web = (ListView) findViewById(R.id.list_web);

        lin_menu = (LinearLayout) findViewById(R.id.lin_menu);
        lin_duration = (LinearLayout) findViewById(R.id.lin_duration);
        lin_property = (LinearLayout) findViewById(R.id.lin_property);
        lin_weblist = (LinearLayout) findViewById(R.id.lin_weblist);
        lin_password = (LinearLayout) findViewById(R.id.lin_password);
        lin_details = (LinearLayout) findViewById(R.id.lin_details);
        lin_logout = (LinearLayout) findViewById(R.id.lin_logout);

        rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
        rl_lock = (RelativeLayout) findViewById(R.id.rl_lock);

        lin_profile = (RelativeLayout) findViewById(R.id.lin_profile);
        rl_setting = (RelativeLayout) findViewById(R.id.rl_setting);
        rl_web = (RelativeLayout) findViewById(R.id.rl_web);
        rl_hot = (RelativeLayout) findViewById(R.id.rl_hot);
        rl_ad = (RelativeLayout) findViewById(R.id.rl_ad);
        rl_total = (RelativeLayout) findViewById(R.id.rl_total);

        rl_menu = (RelativeLayout) findViewById(R.id.rl_menu);
        rl_menu_open = (RelativeLayout) findViewById(R.id.rl_menu_open);
        iv_down = (ImageView) findViewById(R.id.iv_down);

        iv_lead = (ImageView) findViewById(R.id.iv_lead);
        iv_hot = (ImageView) findViewById(R.id.iv_hot);
        iv_ad = (ImageView) findViewById(R.id.iv_ad);
        iv_total = (ImageView) findViewById(R.id.iv_total);
        iv_email = (ImageView) findViewById(R.id.iv_email);
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        iv_profile = (CircleImageView) findViewById(R.id.iv_profile);

        edt_search = (EditText) findViewById(R.id.edt_search);

        tv_periode = (TextView) findViewById(R.id.tv_periode);
        tv_property = (TextView) findViewById(R.id.tv_property);
        tv_totallead = (TextView) findViewById(R.id.tv_totallead);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_plan = (TextView) findViewById(R.id.tv_plan);

        tv_webleads = (TextView) findViewById(R.id.tv_webleads);
        tv_hot = (TextView) findViewById(R.id.tv_hot);
        tv_ad = (TextView) findViewById(R.id.tv_ad);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_email = (TextView) findViewById(R.id.tv_email);

        tv_webleads_count = (TextView) findViewById(R.id.tv_web_count);
        tv_hot_count = (TextView) findViewById(R.id.tv_hot_count);
        tv_ad_count = (TextView) findViewById(R.id.tv_ad_count);
        tv_total_count = (TextView) findViewById(R.id.tv_total_count);

        rl_menu.setOnClickListener(this);
        rl_menu_open.setOnClickListener(this);
        lin_duration.setOnClickListener(this);
        lin_weblist.setOnClickListener(this);
        lin_profile.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        lin_logout.setOnClickListener(this);

        rl_web.setOnClickListener(this);
        rl_hot.setOnClickListener(this);
        rl_ad.setOnClickListener(this);
        rl_total.setOnClickListener(this);
        //rl_email.setOnClickListener(this);

        isConnect = ConnectivityReceiver.isConnected();
        showSnack(isConnect);

        final List<String> fruits_list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.duration)));
        periodeAdapter = new PeriodeAdapter(MainActivity.this, fruits_list);
        list_time.setAdapter(periodeAdapter);

        periodeAdapter.selectItem(0);
        str_duration = "0";

        list_time.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (support.checkInternetConnectivity()) {
                    periodeAdapter.selectItem(i);
                    list_time.setVisibility(View.GONE);
                    String str = ((TextView) view.findViewById(R.id.tv_time)).getText().toString();

                    if (i == 0) {
                        str_duration = "0";
                    } else if (i == 1) {
                        str_duration = "1";
                    } else if (i == 2) {
                        str_duration = "7";
                    } else if (i == 3) {
                        str_duration = "90";
                    } else if (i == 4) {
                        str_duration = "150";
                    }
                    tv_periode.setText(str);

                    rl_lock.setVisibility(View.GONE);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_loading.setVisibility(View.VISIBLE);
                            tv_count.setVisibility(View.GONE);
                            new WebLeads().execute();
                            new GetLeadsCounts().execute();
                        }
                    });
                } else {
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
        });

        list_web.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (support.checkInternetConnectivity()) {
                    webListAdapter.selectItem(i);
                    lin_property.setVisibility(View.GONE);
                    String str = ((TextView) view.findViewById(R.id.tv_time)).getText().toString();
                    str_aid = ((TextView) view.findViewById(R.id.tv_aid)).getText().toString();
                    str_pid = ((TextView) view.findViewById(R.id.tv_pid)).getText().toString();

                    tv_property.setText(str);

                    rl_lock.setVisibility(View.GONE);
                    edt_search.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_loading.setVisibility(View.VISIBLE);
                            tv_count.setVisibility(View.GONE);
                            new WebLeads().execute();
                            new GetLeadsCounts().execute();
                        }
                    });
                } else {
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
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    MainActivity.this.webListAdapter.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showSnack(boolean isConnect) {
        if (isConnect) {
            if (str_token == null) {
                str_id = tele_manager.getDeviceId();
                new LoginDetails().execute();
            } else {
                str_token = pref.getString(manager.Pm_token, null);
                new WebLeads().execute();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Weblist().execute();
                    }
                });

                tv_name.setText(pref.getString(manager.Pm_UserName, null));
                tv_plan.setText(pref.getString(manager.Pm_UserType, null));

                final String str_profile = pref.getString(manager.Pm_UserImage, null);
                if (str_profile.isEmpty()) {
                    iv_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(this)
                            .load(str_profile)
                            .into(iv_profile);
                }
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Please Check your Internet Connection", Snackbar.LENGTH_LONG)
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
    public void onClick(View view) {
        if (view == rl_menu) {
            if (support.checkInternetConnectivity()) {
                if (lin_menu.getVisibility() == View.GONE) {
                    lin_menu.setVisibility(View.VISIBLE);
                    lin_menu.bringToFront();
                    overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_right);
                    rl_bg.setVisibility(View.VISIBLE);
                    rl_bg.bringToFront();
                    lin_property.setVisibility(View.GONE);
                    list_time.setVisibility(View.GONE);
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connectiion.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Setting", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(
                                        new Intent(Settings.ACTION_SETTINGS));
                            }
                        }).show();
            }
        } else if (view == rl_menu_open) {
            if (lin_menu.getVisibility() == View.VISIBLE) {
                lin_menu.setVisibility(View.GONE);
                overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
                rl_bg.setVisibility(View.GONE);
            }
        } else if (view == lin_duration) {
            if (list_time.getVisibility() == View.GONE) {
                list_time.setVisibility(View.VISIBLE);
                list_time.bringToFront();
                rl_lock.setVisibility(View.VISIBLE);
                rl_lock.bringToFront();
                lin_property.setVisibility(View.GONE);
            } else {
                list_time.setVisibility(View.GONE);
                rl_lock.setVisibility(View.GONE);
            }
        } else if (view == lin_weblist) {
            if (lin_property.getVisibility() == View.GONE) {
                lin_property.setVisibility(View.VISIBLE);
                lin_property.bringToFront();
                rl_lock.setVisibility(View.VISIBLE);
                rl_lock.bringToFront();
                list_time.setVisibility(View.GONE);
            } else {
                lin_property.setVisibility(View.GONE);
                rl_lock.setVisibility(View.GONE);
            }
        } else if (view == lin_profile) {
            if (rl_setting.getVisibility() == View.GONE) {
                rl_setting.setVisibility(View.VISIBLE);
                rl_setting.bringToFront();
                iv_down.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
            } else {
                rl_setting.setVisibility(View.GONE);
                iv_down.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
            }
        } else if (view == lin_logout) {
            rl_setting.setVisibility(View.GONE);
            dialog = new Dialog(this);
            Window window = dialog.getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_logout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iv_loading.setVisibility(View.VISIBLE);
                    new logout().execute();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if (view == rl_web) {
            iv_lead.setImageDrawable(getResources().getDrawable(R.drawable.ic_webleads_active));
            iv_hot.setImageDrawable(getResources().getDrawable(R.drawable.ic_hot));
            iv_ad.setImageDrawable(getResources().getDrawable(R.drawable.ic_ad));
            iv_total.setImageDrawable(getResources().getDrawable(R.drawable.ic_total));
            iv_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_track));

            rl_web.setBackgroundColor(getResources().getColor(R.color.menu_bg));
            rl_hot.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_ad.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_total.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            //  rl_email.setBackgroundColor(getResources().getColor(R.color.Header_bg));

            tv_webleads.setTextColor(getResources().getColor(R.color.menu_text));
            tv_hot.setTextColor(getResources().getColor(R.color.text));
            tv_ad.setTextColor(getResources().getColor(R.color.text));
            tv_total.setTextColor(getResources().getColor(R.color.text));
            tv_email.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setTextColor(getResources().getColor(R.color.white));
            tv_hot_count.setTextColor(getResources().getColor(R.color.text));
            tv_ad_count.setTextColor(getResources().getColor(R.color.text));
            tv_total_count.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setBackground(getResources().getDrawable(R.drawable.menu_text_fill));
            tv_hot_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_ad_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_total_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));

            str_leadtype = "WebLeads";

            tv_totallead.setText(str_leadtype);
            tv_count.setText(tv_webleads_count.getText().toString());

            new WebLeads().execute();

            lin_menu.setVisibility(View.GONE);
            overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            rl_bg.setVisibility(View.GONE);

        } else if (view == rl_hot) {
            iv_lead.setImageDrawable(getResources().getDrawable(R.drawable.ic_webleads));
            iv_hot.setImageDrawable(getResources().getDrawable(R.drawable.ic_hot_active));
            iv_ad.setImageDrawable(getResources().getDrawable(R.drawable.ic_ad));
            iv_total.setImageDrawable(getResources().getDrawable(R.drawable.ic_total));
            iv_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_track));

            rl_web.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_hot.setBackgroundColor(getResources().getColor(R.color.menu_bg));
            rl_ad.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_total.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            //rl_email.setBackgroundColor(getResources().getColor(R.color.Header_bg));

            tv_webleads.setTextColor(getResources().getColor(R.color.text));
            tv_hot.setTextColor(getResources().getColor(R.color.menu_text));
            tv_ad.setTextColor(getResources().getColor(R.color.text));
            tv_total.setTextColor(getResources().getColor(R.color.text));
            tv_email.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setTextColor(getResources().getColor(R.color.text));
            tv_hot_count.setTextColor(getResources().getColor(R.color.white));
            tv_ad_count.setTextColor(getResources().getColor(R.color.text));
            tv_total_count.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_hot_count.setBackground(getResources().getDrawable(R.drawable.menu_text_fill));
            tv_ad_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_total_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));

            str_leadtype = "HotLeads";

            tv_totallead.setText("Hot Leads");
            tv_count.setText(tv_hot_count.getText().toString());

            new WebLeads().execute();

            lin_menu.setVisibility(View.GONE);
            overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            rl_bg.setVisibility(View.GONE);

        } else if (view == rl_ad) {
            iv_lead.setImageDrawable(getResources().getDrawable(R.drawable.ic_webleads));
            iv_hot.setImageDrawable(getResources().getDrawable(R.drawable.ic_hot));
            iv_ad.setImageDrawable(getResources().getDrawable(R.drawable.ic_ad_active));
            iv_total.setImageDrawable(getResources().getDrawable(R.drawable.ic_total));
            iv_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_track));

            rl_web.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_hot.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_ad.setBackgroundColor(getResources().getColor(R.color.menu_bg));
            rl_total.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            // rl_email.setBackgroundColor(getResources().getColor(R.color.Header_bg));

            tv_webleads.setTextColor(getResources().getColor(R.color.text));
            tv_hot.setTextColor(getResources().getColor(R.color.text));
            tv_ad.setTextColor(getResources().getColor(R.color.menu_text));
            tv_total.setTextColor(getResources().getColor(R.color.text));
            tv_email.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setTextColor(getResources().getColor(R.color.text));
            tv_hot_count.setTextColor(getResources().getColor(R.color.text));
            tv_ad_count.setTextColor(getResources().getColor(R.color.white));
            tv_total_count.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_hot_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_ad_count.setBackground(getResources().getDrawable(R.drawable.menu_text_fill));
            tv_total_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));

            str_leadtype = "AdWordsLeads";

            tv_totallead.setText("AdWords Leads");
            tv_count.setText(tv_ad_count.getText().toString());

            new WebLeads().execute();

            lin_menu.setVisibility(View.GONE);
            overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            rl_bg.setVisibility(View.GONE);

        } else if (view == rl_total) {
            iv_lead.setImageDrawable(getResources().getDrawable(R.drawable.ic_webleads));
            iv_hot.setImageDrawable(getResources().getDrawable(R.drawable.ic_hot));
            iv_ad.setImageDrawable(getResources().getDrawable(R.drawable.ic_ad));
            iv_total.setImageDrawable(getResources().getDrawable(R.drawable.ic_total_active));
            iv_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_track));

            rl_web.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_hot.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_ad.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_total.setBackgroundColor(getResources().getColor(R.color.menu_bg));
            // rl_email.setBackgroundColor(getResources().getColor(R.color.Header_bg));

            tv_webleads.setTextColor(getResources().getColor(R.color.text));
            tv_hot.setTextColor(getResources().getColor(R.color.text));
            tv_ad.setTextColor(getResources().getColor(R.color.text));
            tv_total.setTextColor(getResources().getColor(R.color.menu_text));
            tv_email.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setTextColor(getResources().getColor(R.color.text));
            tv_hot_count.setTextColor(getResources().getColor(R.color.text));
            tv_ad_count.setTextColor(getResources().getColor(R.color.text));
            tv_total_count.setTextColor(getResources().getColor(R.color.white));

            tv_webleads_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_hot_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_ad_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_total_count.setBackground(getResources().getDrawable(R.drawable.menu_text_fill));

            str_leadtype = "WebVisitors";

            tv_totallead.setText("Web Visitors");
            tv_count.setText(tv_total_count.getText().toString());

            new WebLeads().execute();

            lin_menu.setVisibility(View.GONE);
            overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            rl_bg.setVisibility(View.GONE);

        } /*else if (view == rl_email) {
            iv_lead.setImageDrawable(getResources().getDrawable(R.drawable.ic_webleads));
            iv_hot.setImageDrawable(getResources().getDrawable(R.drawable.ic_hot));
            iv_ad.setImageDrawable(getResources().getDrawable(R.drawable.ic_ad));
            iv_total.setImageDrawable(getResources().getDrawable(R.drawable.ic_total));
            iv_email.setImageDrawable(getResources().getDrawable(R.drawable.ic_track_active));

            rl_web.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_hot.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_ad.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_total.setBackgroundColor(getResources().getColor(R.color.Header_bg));
            rl_email.setBackgroundColor(getResources().getColor(R.color.menu_bg));

            tv_webleads.setTextColor(getResources().getColor(R.color.text));
            tv_hot.setTextColor(getResources().getColor(R.color.text));
            tv_ad.setTextColor(getResources().getColor(R.color.text));
            tv_total.setTextColor(getResources().getColor(R.color.text));
            tv_email.setTextColor(getResources().getColor(R.color.menu_text));

            tv_webleads_count.setTextColor(getResources().getColor(R.color.text));
            tv_hot_count.setTextColor(getResources().getColor(R.color.text));
            tv_ad_count.setTextColor(getResources().getColor(R.color.text));
            tv_total_count.setTextColor(getResources().getColor(R.color.text));

            tv_webleads_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_hot_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_ad_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));
            tv_total_count.setBackground(getResources().getDrawable(R.drawable.menu_text_bg));

            str_leadtype = "WebLeads";

            new WebLeads().execute();

            lin_menu.setVisibility(View.GONE);
            overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            rl_bg.setVisibility(View.GONE);

        }*/
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private class LoginDetails extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Getting User Details..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.WebLoginCheck
                                + "Key=" + str_id)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Logon Details :", response.body().string());

                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd.isShowing()) {
                pd.dismiss();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);
                                        str_token = object.getString(AppConstant.Tag_Token);

                                        if (str_success.equals("1") || str_error.equals("0")) {
                                            JSONArray jsonArray = object.getJSONArray(AppConstant.Tag_UserProfile);
                                            for (int u = 0; u < jsonArray.length(); u++) {
                                                JSONObject uobj = jsonArray.getJSONObject(u);

                                                manager.saveLogin(str_token,
                                                        uobj.getString(AppConstant.Tag_UserName),
                                                        uobj.getString(AppConstant.Tag_UserType),
                                                        uobj.getString(AppConstant.Tag_UserEmail),
                                                        uobj.getString(AppConstant.Tag_UserImage));
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv_name.setText(pref.getString(manager.Pm_UserName, null));
                                                    tv_plan.setText(pref.getString(manager.Pm_UserType, null));

                                                    final String str_profile = pref.getString(manager.Pm_UserImage, null);
                                                    if (str_profile.isEmpty()) {
                                                        iv_profile.setImageResource(R.mipmap.ic_launcher);
                                                    } else {
                                                        Glide.with(MainActivity.this)
                                                                .load(str_profile)
                                                                .into(iv_profile);
                                                    }

                                                    new WebLeads().execute();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Weblist().execute();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    } catch (final JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private class Weblist extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.FetchWebsite
                                + "Token=" + str_token)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("WebLists :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);

                                        if (str_success.equals("1") || str_error.equals("0")) {
                                            JSONArray array = object.getJSONArray(AppConstant.Tag_WebsitesList);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = array.getJSONObject(i);

                                                web = new WebList(obj.getString(AppConstant.Tag_Url),
                                                        obj.getString(AppConstant.Tag_ProfileId)
                                                        , obj.getString(AppConstant.Tag_AccountId));
                                                webLists.add(web);
                                            }
                                            webListAdapter = new WebListAdapter(MainActivity.this, webLists);
                                            list_web.setAdapter(webListAdapter);
                                            list_web.setTextFilterEnabled(true);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new GetLeadsCounts().execute();
                                                }
                                            });
                                        }
                                    } catch (final JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WebLeads extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Getting WebLeads...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.FetchLeads
                                + "Query=" + str_query
                                + "&LeadType=" + str_leadtype
                                + "&ProfileId=" + str_pid
                                + "&AccountId=" + str_aid
                                + "&Duration=" + str_duration
                                + "&PageNo=" + str_pageno
                                + "&PageSize=" + str_pagesize
                                + "&Token=" + str_token)
                        .get()
                        .build();

                Log.e("Leads URL :", (AppConstant.FetchLeads
                        + "Query=" + str_query
                        + "&LeadType=" + str_leadtype
                        + "&ProfileId=" + str_pid
                        + "&AccountId=" + str_aid
                        + "&Duration=" + str_duration
                        + "&PageNo=" + str_pageno
                        + "&PageSize=" + str_pagesize
                        + "&Token=" + str_token));

                Response response = client.newCall(request).execute();
                Log.e("Leads List :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            Snackbar.make(findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        leadsList.clear();
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);

                                        if (str_success.equals("1") || str_error.equals("0")) {

                                            JSONArray array = object.getJSONArray(AppConstant.Tag_WebLeads);

                                            if (array.length() == 0) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                                                        alertDialogBuilder.setTitle("Currently Leads Unavailable for this Website.");

                                                        alertDialogBuilder
                                                                .setCancelable(true)
                                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.show();
                                                    }
                                                });
                                            } else {
                                                for (int i = 0; i < array.length(); i++) {
                                                    JSONObject obj = array.getJSONObject(i);

                                                    HashMap<String, String> leads = new HashMap<String, String>();
                                                    leads.put(AppConstant.Tag_LeadId, obj.getString(AppConstant.Tag_LeadId));
                                                    leads.put(AppConstant.Tag_CompanyName, obj.getString(AppConstant.Tag_CompanyName));
                                                    leads.put(AppConstant.Tag_CountryCode, obj.getString(AppConstant.Tag_CountryCode));
                                                    leads.put(AppConstant.Tag_Website, obj.getString(AppConstant.Tag_Website));
                                                    leads.put(AppConstant.Tag_VisitedDate, obj.getString(AppConstant.Tag_VisitedDate));
                                                    leads.put(AppConstant.Tag_SpendTime, obj.getString(AppConstant.Tag_SpendTime));
                                                    leads.put(AppConstant.Tag_Faviconl, obj.getString(AppConstant.Tag_Faviconl));
                                                    leads.put(AppConstant.Tag_CountryFlag, obj.getString(AppConstant.Tag_CountryFlag));

                                                    leadsList.add(leads);
                                                }

                                                Fragment fragment = new Home_Fragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("pid", str_pid);
                                                bundle.putString("aid", str_aid);
                                                bundle.putString("duration", str_duration);
                                                bundle.putString("leadtype", str_leadtype);
                                                bundle.putString("token", str_token);
                                                bundle.putSerializable("LeadsList", leadsList);
                                                fragment.setArguments(bundle);

                                                if (fragment != null) {
                                                    fragmentManager = getSupportFragmentManager();
                                                    fragmentTransaction = fragmentManager.beginTransaction();
                                                    fragmentTransaction.add(R.id.container, fragment);
                                                    fragmentTransaction.commit();
                                                }
                                            }
                                        } else {
                                            str_error = object.getString("ErrorMessage");
                                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                                            alertDialogBuilder.setTitle(str_error);

                                            alertDialogBuilder
                                                    .setCancelable(true)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                        }
                                                    });
                                            AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
                                        }
                                    } catch (final JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
        }
    }

    private class GetLeadsCounts extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.GetWebsiteLeadCount
                                + "ProfileId=" + str_pid + "&"
                                + "AccountId=" + str_aid + "&"
                                + "Duration=" + str_duration + "&"
                                + "Token=" + str_token)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("WebLists Counts :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            Snackbar.make(findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response != null) {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);

                                        if (str_success.equals("1") || str_error.equals("0")) {
                                            JSONArray array = object.getJSONArray(AppConstant.Tag_LeadsCount);

                                            iv_loading.setVisibility(View.GONE);
                                            tv_count.setVisibility(View.VISIBLE);

                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = array.getJSONObject(i);

                                                tv_count.setText(obj.getString(AppConstant.Tag_WebLeads));
                                                tv_webleads_count.setText(obj.getString(AppConstant.Tag_WebLeads));
                                                tv_hot_count.setText(obj.getString(AppConstant.Tag_HotLeads));
                                                tv_ad_count.setText(obj.getString(AppConstant.Tag_AdwardsLeads));
                                                tv_total_count.setText(obj.getString(AppConstant.Tag_WebVisitors));
                                            }
                                        }
                                    } catch (final JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class logout extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.Logout
                                + "Key=" + str_id + "&"
                                + "Token=" + str_token)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Logout :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            Snackbar.make(findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);

                                        if (str_success.equals("1") || str_error.equals("0")) {
                                            manager.logOut();
                                            iv_loading.setVisibility(View.GONE);
                                        }
                                    } catch (final JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {

        if (rl_bg.getVisibility() == View.VISIBLE ||
                rl_lock.getVisibility() == View.VISIBLE ||
                lin_menu.getVisibility() == View.VISIBLE ||
                lin_property.getVisibility() == View.VISIBLE ||
                list_time.getVisibility() == View.VISIBLE) {

            rl_bg.setVisibility(View.GONE);
            rl_lock.setVisibility(View.GONE);
            lin_menu.setVisibility(View.GONE);
            lin_property.setVisibility(View.GONE);
            list_time.setVisibility(View.GONE);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!exit) {
            this.exit = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            MyApplication.getInstance().setConnectivityListener(this);
            return;
        }
    }
}
