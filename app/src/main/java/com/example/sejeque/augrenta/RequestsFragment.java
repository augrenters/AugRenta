package com.example.sejeque.augrenta;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SejeQue on 3/29/2018.
 *
 * This class is used to display multiple list
 */



public class  RequestsFragment extends Fragment {

    private static DatabaseReference requestDatabase;

    private FirebaseAuth mAuth;
    private  static FirebaseUser currentUser;

    private RecyclerView requestVisitList;
    View requestsView;

     static String user_id;

    private static Context mContext;


    private RequestAdapter requestAdpater;

    List<RequestVisitData> requestList = new ArrayList<>();

    LinearLayout ll;

    public RequestsFragment(){
        //empty constructor
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        requestsView = inflater.inflate(R.layout.requests_fragment, container, false);
        mContext = inflater.getContext();

        View v = inflater.inflate(R.layout.seekerrequests_layout, container, false);
        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            user_id = currentUser.getUid();
        }else{
            Toast.makeText(mContext, " No user here", Toast.LENGTH_SHORT).show();
        }

        ll = v.findViewById(R.id.requestsLayout);
        //ll.setLayoutParams(params);


        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");
        requestAdpater = new RequestAdapter(requestList);
        requestVisitList = requestsView.findViewById(R.id.requestsRecycleView);
        requestVisitList.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setReverseLayout(true);
        requestVisitList.setLayoutManager(linearLayoutManager);
        requestVisitList.setAdapter(requestAdpater);
        return requestsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        requestDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requestList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot sender: dataSnapshot.getChildren()){
                        //Toast.makeText(mContext, "" + sender.getKey(), Toast.LENGTH_SHORT).show();
                        for(DataSnapshot reqProp : sender.getChildren()){
                            //Toast.makeText(mContext, "" + properties, Toast.LENGTH_SHORT).show();
                            Log.d("Properties", ""+reqProp.getValue());


                            RequestVisit req = reqProp.getValue(RequestVisit.class);
                            RequestVisitData requestVisitData = new RequestVisitData
                                    (reqProp.getKey(), sender.getKey(), req.getSender(), req.getDate(), req.getTime(), req.getType(), req.getPropertyName(), req.isAccepted());

                            if(req.isAccepted()){
                                ViewGroup.LayoutParams params = ll.getLayoutParams();
                                params.height=0;
                                params.width= 0;

                                ll.setVisibility(View.GONE);
                                ll.setLayoutParams(params);
                            }
                            requestList.add(requestVisitData);
                            requestAdpater.notifyDataSetChanged();
                            Log.d("New Requests", ""+requestList.toString());
                            //Toast.makeText(mContext, "" + requestList, Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{  Log.d("DataSnapshot", "does not exists");}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        requestAdpater.notifyDataSetChanged();
    }






     /*
    *  UTILITES
    *
    *
    */



}
