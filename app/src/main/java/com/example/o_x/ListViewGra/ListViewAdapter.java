package com.example.o_x.ListViewGra;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.o_x.Glowna;
import com.example.o_x.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Marcin on 2016-02-03.
 */
public class ListViewAdapter extends ArrayAdapter<ItemGra> {
    Context context;
    int resource;
    ItemGra objects[] = null;

    public ListViewAdapter(Context context, int resource, ItemGra[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Glowna) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }
        ItemGra item = objects[position];

        TextView tvLogin = (TextView) convertView.findViewById(R.id.tvLogin);
        TextView tvProcent = (TextView) convertView.findViewById(R.id.tvProcent);
        CircleImageView ivLogo = (CircleImageView) convertView.findViewById(R.id.ivLogoStare);

        byte[] logo = item.getGra().getGospodarz().getLogo();
        ivLogo.setImageBitmap(BitmapFactory.decodeByteArray(logo, 0, logo.length));
        tvLogin.setText(item.getLoginGospodarza());
        tvProcent.setText(item.getProcentZakonczonychGier());
        return convertView;
    }
}
