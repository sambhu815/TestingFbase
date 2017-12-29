package com.fastbase.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastbase.R;
import com.fastbase.support.SupportUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Swapnil.Patel on 06-11-2017.
 */

public class Company_Fragment extends Fragment {
    public static final String TAG = Company_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    String str_phone, str_add, str_summary, str_success, str_error, str_leadId;

    TextView tv_phone, tv_add, tv_sammary;

    SupportUtil support;
    OkHttpClient client;
    Request request;

    public Company_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str_add = getArguments().getString("add", str_add);
        str_phone = getArguments().getString("phone", str_phone);
        str_summary = getArguments().getString("samary", str_summary);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);

        tv_add = (TextView) view.findViewById(R.id.tv_add);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_sammary = (TextView) view.findViewById(R.id.tv_summary);

        tv_add.setText(str_add);
        tv_phone.setText(str_phone);
        tv_sammary.setText(str_summary);
        return view;
    }
}
