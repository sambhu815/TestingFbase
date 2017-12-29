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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fastbase.R;
import com.fastbase.model.Email;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.fastbase.model.Contact;
import com.fastbase.support.AppConstant;
import com.fastbase.support.SupportUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Swapnil.Patel on 06-11-2017.
 */

public class ProfileTab_Fragment extends Fragment implements View.OnClickListener {
    public static final String TAG = ProfileTab_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    TextView tv_name, tv_code, tv_url, tv_profile, tv_visitor, tv_contact;
    View view_profile, view_visitor, view_contact;
    ImageView iv_flag, iv_profile, iv_visitor, iv_contact, iv_url;
    RelativeLayout rl_profile, rl_visitor, rl_contact;

    String str_cname, str_code, str_url, str_flag, str_fav, str_leadId, str_token;
    String str_phone, str_add, str_summary, str_success, str_error;

    SupportUtil support;

    Fragment fragment;
    OkHttpClient client;
    Request request;

    ArrayList<HashMap<String, String>> visitorList;
    List<Email> emailList;
    List<Contact> contactList;
    Contact contact;
    Email email;

    public ProfileTab_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str_leadId = getArguments().getString("leadid");
        str_cname = getArguments().getString("name");
        str_code = getArguments().getString("code");
        str_url = getArguments().getString("url");
        str_fav = getArguments().getString("fav");
        str_flag = getArguments().getString("flag");
        str_token = getArguments().getString("token");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lead_profile, container, false);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);
        visitorList = new ArrayList<>();
        contactList = new ArrayList<Contact>();
        emailList = new ArrayList<>();

        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_code = (TextView) view.findViewById(R.id.tv_code);
        tv_url = (TextView) view.findViewById(R.id.tv_url);
        tv_profile = (TextView) view.findViewById(R.id.tv_profile);
        tv_visitor = (TextView) view.findViewById(R.id.tv_visitor);
        tv_contact = (TextView) view.findViewById(R.id.tv_contact);

        view_profile = (View) view.findViewById(R.id.view_profile);
        view_visitor = (View) view.findViewById(R.id.view_visitor);
        view_contact = (View) view.findViewById(R.id.view_contact);

        iv_flag = (ImageView) view.findViewById(R.id.iv_flag);
        iv_profile = (ImageView) view.findViewById(R.id.iv_profile);
        iv_visitor = (ImageView) view.findViewById(R.id.iv_visitor);
        iv_contact = (ImageView) view.findViewById(R.id.iv_contact);
        iv_url = (ImageView) view.findViewById(R.id.iv_url);

        rl_profile = (RelativeLayout) view.findViewById(R.id.rl_profile);
        rl_visitor = (RelativeLayout) view.findViewById(R.id.rl_visitor);
        rl_contact = (RelativeLayout) view.findViewById(R.id.rl_contact);

        rl_profile.setOnClickListener(this);
        rl_visitor.setOnClickListener(this);
        rl_contact.setOnClickListener(this);

        tv_name.setText(str_cname);
        tv_code.setText(str_code);
        tv_url.setText(str_url);

        if (str_fav.isEmpty()) {
            iv_url.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(activity)
                    .load(str_fav)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(iv_url, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(str_fav)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_url)
                                    .into(iv_url);
                        }
                    });
        }

        if (str_flag.isEmpty()) {
            iv_flag.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(activity)
                    .load(str_flag)
                    .into(iv_flag);
        }

        if (support.checkInternetConnectivity()) {
            new CompanyProfile().execute();
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

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == rl_profile) {
            tv_profile.setTextColor(activity.getResources().getColor(R.color.blue));
            tv_visitor.setTextColor(activity.getResources().getColor(R.color.text));
            tv_contact.setTextColor(activity.getResources().getColor(R.color.text));

            iv_profile.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_buildingmark));
            iv_visitor.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_visitor));
            iv_contact.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_contact));

            view_profile.setVisibility(View.VISIBLE);
            view_visitor.setVisibility(View.INVISIBLE);
            view_contact.setVisibility(View.INVISIBLE);

            fragment = new Company_Fragment();
            Bundle profile = new Bundle();
            profile.putString("add", str_add);
            profile.putString("phone", str_phone);
            profile.putString("samary", str_summary);
            fragment.setArguments(profile);

            if (fragment != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_profile, fragment);
                fragmentTransaction.commit();
            }
        } else if (view == rl_visitor) {
            tv_profile.setTextColor(activity.getResources().getColor(R.color.text));
            tv_visitor.setTextColor(activity.getResources().getColor(R.color.blue));
            tv_contact.setTextColor(activity.getResources().getColor(R.color.text));

            iv_profile.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_building));
            iv_visitor.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_visitormark));
            iv_contact.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_contact));

            view_profile.setVisibility(View.INVISIBLE);
            view_visitor.setVisibility(View.VISIBLE);
            view_contact.setVisibility(View.INVISIBLE);

            fragment = new Visitor_Fragment();
            Bundle visitor = new Bundle();
            visitor.putSerializable("visitor", visitorList);
            fragment.setArguments(visitor);

            if (fragment != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_profile, fragment);
                fragmentTransaction.commit();
            }
        } else if (view == rl_contact) {
            tv_profile.setTextColor(activity.getResources().getColor(R.color.text));
            tv_visitor.setTextColor(activity.getResources().getColor(R.color.text));
            tv_contact.setTextColor(activity.getResources().getColor(R.color.blue));

            iv_profile.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_building));
            iv_visitor.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_visitor));
            iv_contact.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_contactmark));

            view_profile.setVisibility(View.INVISIBLE);
            view_visitor.setVisibility(View.INVISIBLE);
            view_contact.setVisibility(View.VISIBLE);

            fragment = new Contact_Fragment();
            Bundle contact = new Bundle();
            contact.putSerializable("email", (Serializable) emailList);
            contact.putSerializable("contact", (Serializable) contactList);
            fragment.setArguments(contact);

            if (fragment != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_profile, fragment);
                fragmentTransaction.commit();
            }
        }
    }

    private class CompanyProfile extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(activity);
            pd.setMessage("Loading..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();

                request = new Request.Builder()
                        .url(AppConstant.FetchProfile
                                + "LeadId=" + str_leadId
                                + "&Token=" + str_token)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                Log.e("Company Profile :", response.body().string());

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

                                            JSONArray array = object.getJSONArray(AppConstant.Tag_CompanyInfo);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = array.getJSONObject(i);

                                                str_phone = obj.getString(AppConstant.Tag_Phone);
                                                str_add = obj.getString(AppConstant.Tag_Address);
                                                str_summary = obj.getString(AppConstant.Tag_Summary);
                                            }

                                            JSONArray array_visitor = object.getJSONArray(AppConstant.Tag_Interaction);
                                            for (int i = 0; i < array_visitor.length(); i++) {
                                                JSONObject obj = array_visitor.getJSONObject(i);
                                                JSONArray visitorArray = obj.getJSONArray(AppConstant.Tag_VisitedPages);

                                                HashMap<String, String> visitor = new HashMap<String, String>();
                                                visitor.put(AppConstant.Tag_Date, obj.getString(AppConstant.Tag_Date));
                                                visitor.put(AppConstant.Tag_ClientImg, obj.getString(AppConstant.Tag_ClientImg));
                                                visitor.put(AppConstant.Tag_Referrer, obj.getString(AppConstant.Tag_Referrer));
                                                visitor.put(AppConstant.Tag_PageViews, obj.getString(AppConstant.Tag_PageViews));
                                                visitor.put(AppConstant.Tag_TimeSpent, obj.getString(AppConstant.Tag_TimeSpent));
                                                visitor.put(AppConstant.Tag_VisitedPages, visitorArray.toString());

                                                visitorList.add(visitor);
                                            }

                                            JSONArray array_emp = object.getJSONArray(AppConstant.Tag_Employees);
                                            for (int i = 0; i < array_emp.length(); i++) {
                                                JSONObject obj = array_emp.getJSONObject(i);

                                                contact = new Contact(
                                                        obj.getString(AppConstant.Tag_Name),
                                                        obj.getString(AppConstant.Tag_Designation),
                                                        obj.getString(AppConstant.Tag_Img),
                                                        obj.getString(AppConstant.Tag_Current),
                                                        obj.getString(AppConstant.Tag_Previous),
                                                        obj.getString(AppConstant.Tag_Education),
                                                        obj.getString(AppConstant.Tag_LinkedInUrl));

                                                contactList.add(contact);
                                            }

                                            JSONArray array_email = object.getJSONArray(AppConstant.Tag_Emails);
                                            for (int i = 0; i < array_email.length(); i++) {
                                                JSONObject obj = array_email.getJSONObject(i);

                                                email = new Email(obj.getString(AppConstant.Tag_Name),
                                                        obj.getString(AppConstant.Tag_Email));

                                                emailList.add(email);
                                            }

                                            fragment = new Company_Fragment();
                                            Bundle profile = new Bundle();
                                            profile.putString("add", str_add);
                                            profile.putString("phone", str_phone);
                                            profile.putString("samary", str_summary);
                                            fragment.setArguments(profile);

                                            if (fragment != null) {
                                                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.add(R.id.container_profile, fragment);
                                                fragmentTransaction.commit();
                                            }
                                        } else {
                                            str_error = object.getString(AppConstant.Tag_ErrorMessage);
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
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
