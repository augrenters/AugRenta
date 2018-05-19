package com.example.sejeque.augrenta;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by SejeQue on 5/19/2018.
 */

public class ImagePager extends PagerAdapter {
    private List<Uri> imagerUris;
    private Context context;
    private LayoutInflater mlayoutInflater;

    public ImagePager() {
    }

    public ImagePager(Context applicationContext, List<Uri> imagerUris) {
        this.imagerUris = imagerUris;
        this.context = applicationContext;
    }

    @Override
    public int getCount() {
        return imagerUris.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        View itemView = LayoutInflater.from(context).inflate(R.layout.imageviewpager, container, false);
        ImageView imageView = itemView.findViewById(R.id.propertyImages);

        Glide.with(context)
                .load(imagerUris.get(position))
                .into(imageView);
        //Toast.makeText(context, ""+imagerUris, Toast.LENGTH_SHORT).show();
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {

    }
}
