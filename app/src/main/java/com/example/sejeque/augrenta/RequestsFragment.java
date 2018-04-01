package com.example.sejeque.augrenta;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SejeQue on 3/29/2018.
 *
 * This class is used to display multiple list
 */



public class RequestsFragment extends Fragment {

    String[] itemname ={
            "Cita's Place",
            "Honora's Crib",
            "Faith's Flat",
            "Kwene's Loft",
            "Damo's Boarding House",

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requests_fragment, container, false);

        ListView listView = (ListView) view.findViewById(R.id.requestsView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.seekerrequests_layout,
                R.id.seekerRequestPlace,
                itemname
        );
        listView.setAdapter(adapter);

        return view;


    }

}
