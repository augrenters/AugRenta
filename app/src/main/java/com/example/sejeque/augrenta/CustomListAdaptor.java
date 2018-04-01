package com.example.sejeque.augrenta;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

/**
 * Created by SejeQue on 3/29/2018.
 *
 *
 */

public class CustomListAdaptor extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public CustomListAdaptor(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.seekerrequests_layout, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.seekerrequests_layout, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.seekerRequestPlace);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.seekerRequestImage);


        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);

        return rowView;

    };
}
