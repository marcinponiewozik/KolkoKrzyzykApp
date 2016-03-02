package com.example.o_x.ListViewGraDB;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.o_x.MojeGry;
import com.example.o_x.OsobaActivity;
import com.example.o_x.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Marcin on 2016-02-03.
 */
public class ListViewAdapterDB extends ArrayAdapter<ItemGraDB> {
    Context context;
    int resource;
    ItemGraDB objects[] = null;

    public ListViewAdapterDB(Context context, int resource, ItemGraDB[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((MojeGry) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }
        final ItemGraDB item = objects[position];

        TextView tvLoginGospodarz = (TextView) convertView.findViewById(R.id.tvLoginGospodarz);
        TextView tvLoginPrzeciwnik = (TextView) convertView.findViewById(R.id.tvLoginPrzeciwnik);
        TextView tvData = (TextView) convertView.findViewById(R.id.tvData);
        CircleImageView ivGospodarz = (CircleImageView) convertView.findViewById(R.id.ivGospodarz);
        CircleImageView ivPrzeciwnik = (CircleImageView) convertView.findViewById(R.id.ivPrzeciwnik);

        tvData.setText(item.data);
        byte[] logoGospodarz = item.getGraDB().getGospodarz().getLogo();
        Bitmap bitmapGospodarz = BitmapFactory.decodeByteArray(logoGospodarz, 0, logoGospodarz.length);
        ivGospodarz.setImageBitmap(bitmapGospodarz);
        tvLoginGospodarz.setText(item.getLoginGospodarza());
        ivGospodarz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OsobaActivity.class);
                intent.putExtra("idOsoba", item.getGraDB().getGospodarz().getId());
                context.startActivity(intent);
            }
        });
        if (item.getGraDB().getPrzeciwnik() != null) {
            byte[] logoPrzeciwnik = item.getGraDB().getPrzeciwnik().getLogo();
            Bitmap bitmapPrzeciwnik = BitmapFactory.decodeByteArray(logoPrzeciwnik, 0, logoPrzeciwnik.length);
            ivPrzeciwnik.setImageBitmap(bitmapPrzeciwnik);
            tvLoginPrzeciwnik.setText(item.getGraDB().getPrzeciwnik().getLogin());
        }

        return convertView;
    }
}
