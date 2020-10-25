package com.example.android.miwok;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private int recorse;
    private ArrayList<Words> word;

    private int color;

    public MyAdapter(Context context, int recorse, ArrayList<Words> word, int color) {
        this.context = context;
        this.recorse = recorse;
        this.word = word;
        this.color = color;
    }

    @Override
    public int getCount() {
        return word.size();
    }

    @Override
    public Words getItem(int position) {
        return word.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Words words = getItem(position);
        View v = convertView;
        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.list_view_templet, parent, false);
        }

        TextView tv_miwok = v.findViewById(R.id.lang_miok);
        TextView tv_english = v.findViewById(R.id.lang_eng);
        ImageView img = v.findViewById(R.id.img_photo);
        View layout = v.findViewById(R.id.lay_text);
        layout.setBackgroundColor(ContextCompat.getColor(context, color));


        tv_miwok.setText(words.getMiwok());
        tv_english.setText(words.getEnglish());

        if (words.hasImage())
            img.setImageResource(words.getImg());
        else
            img.setVisibility(View.GONE);


        return v;
    }
}
