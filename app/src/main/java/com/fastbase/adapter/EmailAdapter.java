package com.fastbase.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.fastbase.R;
import com.fastbase.model.Email;
import com.fastbase.model.WebList;
import com.fastbase.support.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Swapnil.Patel on 23-11-2017.
 */

public class EmailAdapter extends BaseAdapter implements Filterable {
    Activity activity;
    List<Email> emailList;
    List<Email> filteremail;
    LayoutInflater inflater;

    public EmailAdapter(Activity activity, List<Email> emailList) {
        this.activity = activity;
        this.emailList = emailList;
        inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return emailList.size();
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
        view = inflater.inflate(R.layout.row_email, viewGroup, false);

        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_email = (TextView) view.findViewById(R.id.tv_email);

        final Email email = emailList.get(i);

        tv_name.setText(email.getName());
        tv_email.setText(email.getEmail());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Email email = emailList.get(i);

                String str_email = email.getEmail();

                Intent in = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", str_email, null));
                in.putExtra(Intent.EXTRA_SUBJECT, "Can You Point Me To The Right Direction in Your Company?");
                in.putExtra(Intent.EXTRA_TEXT, "Hi there,\n" +
                        "\n" +
                        "Can you point me in the right direction? I am looking for the person in your company responsible for [ex online marketing and sales]. \n" +
                        "I would like to introduce [my company] and discuss what [company name] is doing in this area. \n" +
                        "\n" +
                        "Thank you in advance and best regards,\n" +
                        "\n" +
                        "[your signature]");
                activity.startActivity(Intent.createChooser(in, "Send email..."));
            }
        });

        return view;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Email> results = new ArrayList<Email>();
                if (filteremail == null)
                    filteremail = emailList;
                if (constraint != null) {
                    if (filteremail != null && filteremail.size() > 0) {
                        for (final Email g : filteremail) {
                            if (g.getName().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                emailList = (ArrayList<Email>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
