package com.example.sejeque.augrenta;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * Created by SejeQue on 5/18/2018.
 */

public class GlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);

//        Register FirebaseImageLoader to handle StorageReference
//        registry.append(StorageReference.class, InputStream.class,
//                new FirebaseImageLoader.Factory());
    }
}
