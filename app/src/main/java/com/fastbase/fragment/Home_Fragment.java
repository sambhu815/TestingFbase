package com.fastbase.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fastbase.R;
import com.fastbase.adapter.WebLeadsAdapter;
import com.fastbase.support.AppConstant;
import com.fastbase.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Swapnil.Patel on 06-11-2017.
 */

public class Home_Fragment extends Fragment {
    public static final String TAG = Home_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    ListView list_lead;
    SwipeRefreshLayout swipeRefreshLayout;
    WebLeadsAdapter webLeadsAdapter;
    ArrayList<HashMap<String, String>> leadsList;
    ArrayList<HashMap<String, String>> leadsListsearch;

    EditText edt_search;

    SupportUtil support;

    OkHttpClient client;
    Request request;

    String str_success, str_error, str_query, str_pid, str_aid, str_duration, str_token, str_pageno, str_pagesize;
    public static String str_type;
    public static int page = 1;

    boolean userScrolled = false;

    public Home_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str_type = getArguments().getString("leadtype");
        str_pid = getArguments().getString("pid");
        str_aid = getArguments().getString("aid");
        str_duration = getArguments().getString("duration");
        str_token = getArguments().getString("token");

        leadsList = (ArrayList<HashMap<String, String>>)
                getArguments().getSerializable("LeadsList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_webleads, container, false);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        leadsListsearch = new ArrayList<>();

        str_query = "0";
        str_pageno = "1";
        str_pagesize = "20";

        list_lead = (ListView) view.findViewById(R.id.list_lead);
        edt_search = (EditText) view.findViewById(R.id.edt_search);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(activity.getResources().getColor(R.color.orange));

        webLeadsAdapter = new WebLeadsAdapter(activity, leadsList, str_token);
        list_lead.setAdapter(webLeadsAdapter);

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((i == EditorInfo.IME_ACTION_DONE) || (i == EditorInfo.IME_ACTION_NEXT) || (i == EditorInfo.IME_ACTION_SEND)) {
                    if (support.checkInternetConnectivity()) {
                        str_query = edt_search.getText().toString().trim();
                        new WebLeads().execute();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } else {
                        Snackbar.make(activity.findViewById(android.R.id.content), "Please Check your Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Setting", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(
                                                new Intent(Settings.ACTION_SETTINGS));
                                    }
                                }).show();
                    }
                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (support.checkInternetConnectivity()) {
                    RefreshWebLeads();
                } else {
                    Snackbar.make(activity.findViewById(android.R.id.content), "Please Check Your Internet Connection..", Snackbar.LENGTH_INDEFINITE)
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

        implementScrollListener();
        return view;
    }

    private void implementScrollListener() {
        list_lead.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                    userScrolled = false;
                    new WebLeadsLoadMore().execute();
                }
            }
        });
    }

    private void RefreshWebLeads() {
        try {
            client = new OkHttpClient();

            request = new Request.Builder()
                    .url(AppConstant.FetchLeads
                            + "Query=" + str_query
                            + "&LeadType=" + str_type
                            + "&ProfileId=" + str_pid
                            + "&AccountId=" + str_aid
                            + "&Duration=" + str_duration
                            + "&PageNo=" + str_pageno
                            + "&PageSize=" + str_pagesize
                            + "&Token=" + str_token)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            Log.e("Leads List :", response.body().string());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (e instanceof SocketTimeoutException) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(activity.findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {

                    if (response != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String list = null;
                                try {
                                    leadsListsearch.clear();
                                    list_lead.invalidate();
                                    list_lead.refreshDrawableState();

                                    list = response.body().string();
                                    JSONObject object = new JSONObject(list);

                                    str_success = object.getString(AppConstant.Tag_IsSuccess);
                                    str_error = object.getString(AppConstant.Tag_IsError);

                                    if (str_success.equals("1") || str_error.equals("0")) {

                                        JSONArray array = object.getJSONArray(AppConstant.Tag_WebLeads);

                                        if (array.length() == 0) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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

                                                leadsListsearch.add(leads);
                                            }
                                        }
                                        webLeadsAdapter = new WebLeadsAdapter(activity, leadsListsearch, str_token);
                                        list_lead.setAdapter(webLeadsAdapter);
                                        swipeRefreshLayout.setRefreshing(false);

                                        int currentposition = list_lead.getFirstVisiblePosition();
                                        list_lead.setSelectionFromTop(currentposition + 1, 0);
                                    } else {
                                        str_error = object.getString("ErrorMessage");
                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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
    }

    private class WebLeads extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(activity);
            pd.setMessage("Getting WebLeads");
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
                                + "&LeadType=" + str_type
                                + "&ProfileId=" + str_pid
                                + "&AccountId=" + str_aid
                                + "&Duration=" + str_duration
                                + "&PageNo=" + str_pageno
                                + "&PageSize=" + str_pagesize
                                + "&Token=" + str_token)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Leads List :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        leadsListsearch.clear();
                                        list_lead.invalidate();
                                        list_lead.refreshDrawableState();

                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);

                                        if (str_success.equals("1") || str_error.equals("0")) {

                                            JSONArray array = object.getJSONArray(AppConstant.Tag_WebLeads);

                                            if (array.length() == 0) {
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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

                                                    leadsListsearch.add(leads);
                                                }
                                            }
                                            webLeadsAdapter = new WebLeadsAdapter(activity, leadsListsearch, str_token);
                                            list_lead.setAdapter(webLeadsAdapter);
                                        } else {
                                            str_error = object.getString("ErrorMessage");
                                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
        }
    }

    private class WebLeadsLoadMore extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                str_pageno = "" + page++;
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.FetchLeads
                                + "Query=" + str_query
                                + "&LeadType=" + str_type
                                + "&ProfileId=" + str_pid
                                + "&AccountId=" + str_aid
                                + "&Duration=" + str_duration
                                + "&PageNo=" + str_pageno
                                + "&PageSize=" + str_pagesize
                                + "&Token=" + str_token)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Leads List :", response.body().string());

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(activity.findViewById(android.R.id.content), "Error when connecting to server. Please try again.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {

                        if (response != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String list = null;
                                    try {
                                        list = response.body().string();
                                        JSONObject object = new JSONObject(list);

                                        str_success = object.getString(AppConstant.Tag_IsSuccess);
                                        str_error = object.getString(AppConstant.Tag_IsError);

                                        if (str_success.equals("1") || str_error.equals("0")) {

                                            JSONArray array = object.getJSONArray(AppConstant.Tag_WebLeads);

                                            if (array.length() == 0) {
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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
                                            }
                                            webLeadsAdapter = new WebLeadsAdapter(activity, leadsList, str_token);
                                            list_lead.setAdapter(webLeadsAdapter);
                                        } else {
                                            str_error = object.getString("ErrorMessage");
                                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

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
    }
}
