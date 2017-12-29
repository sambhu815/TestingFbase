package com.fastbase.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fastbase.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.fastbase.fragment.ProfileTab_Fragment;
import com.fastbase.support.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Swapnil.Patel on 14-11-2017.
 */

public class WebLeadsAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<HashMap<String, String>> leadsList;
    LayoutInflater inflater;
    HashMap<String, String> resultp = new HashMap<String, String>();
    String str_token;

    public WebLeadsAdapter(Activity activity, ArrayList<HashMap<String, String>> leadsList, String str_token) {
        this.activity = activity;
        this.leadsList = leadsList;
        this.str_token = str_token;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return leadsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.row_lead, viewGroup, false);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_country = (TextView) view.findViewById(R.id.tv_country);
        TextView tv_url = (TextView) view.findViewById(R.id.tv_url);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_date = (TextView) view.findViewById(R.id.tv_date);

        final ImageView iv_flag = (ImageView) view.findViewById(R.id.iv_flag);
        final CircleImageView iv_web_logpo = (CircleImageView) view.findViewById(R.id.iv_web_logo);

        resultp = leadsList.get(i);

        tv_title.setText(resultp.get(AppConstant.Tag_CompanyName));
        tv_country.setText(resultp.get(AppConstant.Tag_CountryCode));
        tv_url.setText(resultp.get(AppConstant.Tag_Website));
        tv_time.setText(resultp.get(AppConstant.Tag_SpendTime));
        tv_date.setText(resultp.get(AppConstant.Tag_VisitedDate));

        final String str_favi = resultp.get(AppConstant.Tag_Faviconl);
        if (str_favi.isEmpty()) {
            iv_web_logpo.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(activity)
                    .load(str_favi)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_favicons)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(iv_web_logpo, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(str_favi)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_favicons)
                                    .into(iv_web_logpo);
                        }
                    });
        }

        final String str_flag = resultp.get(AppConstant.Tag_CountryFlag);
        if (str_flag.isEmpty()) {
            iv_flag.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(activity)
                    .load(str_flag)
                    .into(iv_flag);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultp = leadsList.get(i);

                String name = resultp.get(AppConstant.Tag_CompanyName);
                String url = resultp.get(AppConstant.Tag_Website);
                String code = resultp.get(AppConstant.Tag_CountryCode);
                String leadid = resultp.get(AppConstant.Tag_LeadId);

                ProfileTab_Fragment fragment = new ProfileTab_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("leadid", leadid);
                bundle.putString("name", name);
                bundle.putString("url", url);
                bundle.putString("code", code);
                bundle.putString("fav", str_favi);
                bundle.putString("flag", str_flag);
                bundle.putString("token", str_token);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(ProfileTab_Fragment.TAG);
                fragmentTransaction.commit();

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        return view;
    }
}
