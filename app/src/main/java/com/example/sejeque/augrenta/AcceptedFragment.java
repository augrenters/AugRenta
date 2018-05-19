package com.example.sejeque.augrenta;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
 * Refer to CustomListAdaptor class, to display both image and text on on the List
 */

public class AcceptedFragment extends Fragment {


    private DatabaseReference mDatabase, requestDatabase;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String user_id;

    List<HashMap<String, String>> request_user;
    private RecyclerView acceptedVisitList;

    View acceptedView;


    private AcceptAdapter requestAdpater;

    List<RequestVisitData> requestList = new ArrayList<>();

    LinearLayout ll;
    View requestsView;
    Context mContext;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        acceptedView = inflater.inflate(R.layout.accepted_fragment, container, false);

        View v = inflater.inflate(R.layout.seekerrequests_layout, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            proceed();
        }

        user_id = currentUser.getUid();
        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");

//        request_user = new ArrayList<>();
//
//        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Property");
//
//        acceptedVisitList = acceptedView.findViewById(R.id.acceptedRecycleView);
//        acceptedVisitList.setHasFixedSize(true);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        //linearLayoutManager.setReverseLayout(true);
//        acceptedVisitList.setLayoutManager(linearLayoutManager);

        ll = v.findViewById(R.id.requestsLayout);
        //ll.setLayoutParams(params);


        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");
        requestAdpater = new AcceptAdapter(requestList);
        acceptedVisitList = acceptedView.findViewById(R.id.acceptedRecycleView);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setReverseLayout(true);
        acceptedVisitList.setHasFixedSize(true);
        acceptedVisitList.setLayoutManager(linearLayoutManager);
        acceptedVisitList.setAdapter(requestAdpater);

        return acceptedView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //populateRequestUser();


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


                            if(!req.isAccepted()){
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
                }else{
                    //Toast.makeText(getContext(), " No data here", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        requestAdpater.notifyDataSetChanged();

    }

    private void populateRequestUser() {

        FirebaseRecyclerAdapter<RequestVisit, AcceptViewHolder> acceptAdapter =
                new FirebaseRecyclerAdapter<RequestVisit, AcceptViewHolder>(
                        RequestVisit.class,
                        R.layout.seekerrequests_layout,
                        AcceptViewHolder.class,
                        requestDatabase.child(user_id)
                ) {
                    @Override
                    protected void populateViewHolder(final AcceptViewHolder viewHolder, RequestVisit model, int position) {

                        final String key = this.getRef((position)).getKey();

                        requestDatabase.child(user_id).child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for(DataSnapshot propKey : dataSnapshot.getChildren()) {
                                    RequestVisit requestVisit = propKey.getValue(RequestVisit.class);
                                    HashMap<String, String> resultMap = new HashMap<>();

                                    resultMap.put("Property ID", propKey.getKey());
                                    resultMap.put("Sender", requestVisit.getSender());
                                    resultMap.put("Date", requestVisit.getDate());
                                    resultMap.put("Time", requestVisit.getTime());
                                    resultMap.put("Type", requestVisit.getType());
                                    resultMap.put("accepted", String.valueOf(requestVisit.isAccepted()));
                                    resultMap.put("Owner ID", key);
                                    request_user.add(resultMap);

                                    if(requestVisit.getType().equals("sender")){

                                        ViewGroup.LayoutParams params = viewHolder.ll.getLayoutParams();
                                        params.height=0;
                                        params.width= 0;

                                        viewHolder.ll.setVisibility(View.GONE);
                                        viewHolder.ll.setLayoutParams(params);
                                    }
                                    else if (requestVisit.getType().equals("receiver")){
                                        //Toast.makeText(getContext(), ""+requestVisit.isAccepted(), Toast.LENGTH_SHORT).show();
                                        if(!requestVisit.isAccepted()){
                                            ViewGroup.LayoutParams params = viewHolder.ll.getLayoutParams();
                                            params.height=0;
                                            params.width= 0;

                                            viewHolder.ll.setVisibility(View.GONE);
                                            viewHolder.ll.setLayoutParams(params);


                                        }else{
                                            viewHolder.setSender(requestVisit.getSender());
                                            viewHolder.setDate(requestVisit.getDate());
                                            viewHolder.setTime(requestVisit.getTime());

                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
        acceptedVisitList.setAdapter(acceptAdapter);
        Log.d("List Seeker", ""+requestDatabase);
    }

    public static class AcceptViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView sender_content, time_content, date_content, propertyName;
        Button chat_user, accept_request, decline_request;
        LinearLayout ll, buttonsPanel;

        public AcceptViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            ll = mView.findViewById(R.id.requestsLayout);
            buttonsPanel = mView.findViewById(R.id.requestsButtonsPanel);
            buttonsPanel.setVisibility(View.GONE);
            chat_user = mView.findViewById(R.id.chat_user);
            accept_request = mView.findViewById(R.id.accept_request);
            decline_request = mView.findViewById(R.id.decline_request);
        }

        public void setSender(String sender){
            sender_content = mView.findViewById(R.id.senderName);
            sender_content.setText(sender);
        }

        public void setTime(String time) {
            time_content = mView.findViewById(R.id.requestTime);
            time_content.setText(time);
        }

        public void setDate(String date) {
            date_content = mView.findViewById(R.id.requestDate);
            date_content.setText(date);
        }

        public void setProperty(String property) {
            propertyName = mView.findViewById(R.id.requestedPropertyName);
            propertyName.setText(property);
        }
    }






    /*
    *  UTILITES
    *
    *
    */

    //method for going back to login panel
    private void proceed() {
        Intent onReturnView = new Intent(getContext(), MainActivity.class);
        startActivity(onReturnView);
    }

}
