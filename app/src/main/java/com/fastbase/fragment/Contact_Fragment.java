package com.fastbase.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fastbase.R;
import com.fastbase.adapter.ContactAdapter;
import com.fastbase.adapter.EmailAdapter;
import com.fastbase.model.Contact;
import com.fastbase.model.Email;
import com.fastbase.support.NonScrollListView;
import com.fastbase.support.NonScrollRecyclerView;
import com.fastbase.support.SupportUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Swapnil.Patel on 06-11-2017.
 */

public class Contact_Fragment extends Fragment implements View.OnClickListener {
    public static final String TAG = Contact_Fragment.class.getSimpleName();
    private AppCompatActivity activity;

    NonScrollRecyclerView list_contact;
    ContactAdapter contactAdapter;
    List<Contact> contactList;

    NonScrollListView list_email;
    EmailAdapter emailAdapter;
    List<Email> emailList;

    LinearLayout lin_contact, lin_email;
    RelativeLayout lin_root;
    ImageView iv_contact, iv_email;
    EditText edt_search;
    TextView tv_search;

    SupportUtil support;
    public static String str_tab = "contact";

    public Contact_Fragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailList = (List<Email>) getArguments().getSerializable("email");
        contactList = (List<Contact>) getArguments().getSerializable("contact");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        support = new SupportUtil(activity);

        lin_contact = (LinearLayout) view.findViewById(R.id.lin_contact);
        lin_email = (LinearLayout) view.findViewById(R.id.lin_email);
        lin_root = (RelativeLayout) view.findViewById(R.id.lin_root);

        iv_contact = (ImageView) view.findViewById(R.id.iv_contact);
        iv_email = (ImageView) view.findViewById(R.id.iv_email);

        edt_search = (EditText) view.findViewById(R.id.edt_search);
        tv_search = (TextView) view.findViewById(R.id.tv_search);

        lin_contact.setOnClickListener(this);
        lin_email.setOnClickListener(this);

        list_email = (NonScrollListView) view.findViewById(R.id.list_email);

        list_contact = (NonScrollRecyclerView) view.findViewById(R.id.list_contact);
        list_contact.setHasFixedSize(true);
        list_contact.setLayoutManager(new LinearLayoutManager(activity));
        list_contact.setItemAnimator(new DefaultItemAnimator());
        list_contact.setNestedScrollingEnabled(false);

        if (contactList.size() == 0) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

            alertDialogBuilder.setTitle("Information about key employees is currently unavailable for this company.");

            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            contactAdapter = new ContactAdapter(activity, contactList);
            list_contact.setAdapter(contactAdapter);
        }

        setListViewHeightBasedOnChildren(list_email);

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_search = edt_search.getText().toString().trim();

                if (str_tab.equals("contact")) {
                    contactAdapter.getFilter().filter(str_search);
                } else {
                    emailAdapter.getFilter().filter(str_search);
                }

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {

        if (view == lin_contact) {
            str_tab = "contact";
            iv_contact.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mark));
            iv_email.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_unmark));

            list_contact.setVisibility(View.VISIBLE);
            list_email.setVisibility(View.GONE);

            if (contactList.size() == 0) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                alertDialogBuilder.setTitle("Information about key employees is currently unavailable for this company.");

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                contactAdapter = new ContactAdapter(activity, contactList);
                list_contact.setAdapter(contactAdapter);
            }
        } else if (view == lin_email) {
            str_tab = "email";

            iv_contact.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_unmark));
            iv_email.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mark));

            list_email.setVisibility(View.VISIBLE);
            list_contact.setVisibility(View.GONE);

            if (emailList.size() == 0) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                alertDialogBuilder.setTitle("Email data is not available for this company.");

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                emailAdapter = new EmailAdapter(activity, emailList);
                list_email.setAdapter(emailAdapter);
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
