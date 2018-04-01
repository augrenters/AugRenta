package com.example.sejeque.augrenta;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by SejeQue on 3/29/2018.
 *
 * This class is used to display multiple list
 * Refer to CustomListAdaptor class, to display both image and text on on the List
 */

public class AcceptedFragment extends Fragment {

    String[] itemname ={
            "Cita's Place",
            "Honora's Crib",
    };

    Integer[] imgid={
            R.drawable.sample_image,
            R.drawable.ic_magnify
    };

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accepted_fragment, container, false);

        ///

        CustomListAdaptor adapter=new CustomListAdaptor(getActivity(), itemname, imgid);
        ListView list=(ListView) view.findViewById(R.id.requestsView);
        list.setAdapter(adapter);


        return view;
    }
}
