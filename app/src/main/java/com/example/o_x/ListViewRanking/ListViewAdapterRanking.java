package com.example.o_x.ListViewRanking;

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
public class ListViewAdapterRanking extends ArrayAdapter<ItemOsoba> {
    Context context;
    int resource;
    ItemOsoba objects[] = null;
    int miejsce = 1;

    public ListViewAdapterRanking(Context context, int resource, ItemOsoba[] objects) {
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
        ItemOsoba item = objects[position];

        TextView tvLogin = (TextView) convertView.findViewById(R.id.tvLogin);
        TextView tvPkt = (TextView) convertView.findViewById(R.id.tvPkt);
        TextView tvMiejsce = (TextView) convertView.findViewById(R.id.tvMiejsce);
        CircleImageView ivLogo = (CircleImageView) convertView.findViewById(R.id.ivLogoStare);
        byte[] logo = item.getOsoba().getLogo();
        ivLogo.setImageBitmap(BitmapFactory.decodeByteArray(logo, 0, logo.length));
        tvLogin.setText(item.getOsoba().getLogin());
        tvPkt.setText(String.valueOf(item.getOsoba().getPkt()));
        tvMiejsce.setText(String.valueOf(position + 1));
        miejsce++;
        return convertView;
    }
}
