package com.fastbase.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fastbase.R;
import com.fastbase.adapter.VisitorAdapter;
import com.fastbase.support.SupportUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Swapnil.Patel on 06-11-2017.
 */

public class Visitor_Fragment extends Fragment {
    public static final String TAG = Visitor_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    ListView list_visitor;
    VisitorAdapter visitorAdapter;
    ArrayList<HashMap<String, String>> visitorList;

    SupportUtil support;

    public Visitor_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visitorList = (ArrayList<HashMap<String, String>>)
                getArguments().getSerializable("visitor");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor, container, false);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);

        list_visitor = (ListView) view.findViewById(R.id.list_visitor);

        if (visitorList.size() == 0) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

            alertDialogBuilder.setTitle("Information about visitor interaction is currently unavailable for this company.");

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            visitorAdapter = new VisitorAdapter(activity, visitorList);
            list_visitor.setAdapter(visitorAdapter);
        }
        return view;
    }
}
