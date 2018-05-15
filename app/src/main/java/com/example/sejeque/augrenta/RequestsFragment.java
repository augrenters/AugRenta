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

    private static DatabaseReference mDatabase, requestDatabase;

    private FirebaseAuth mAuth;
    private  static FirebaseUser currentUser;

    static List<HashMap<String, String>> request_user;
    static List<HashMap<String, String>> requestTemp;

    private RecyclerView requestVisitList;
    View requestsView;

     static String user_id;

    private static Context mContext;

    private static FirebaseRecyclerAdapter<RequestVisit, RequestViewHolder> requestAdapter;

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

//        request_user = new ArrayList<>();
//        requestTemp = new ArrayList<>();;
//
//        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Property");
//
//        requestVisitList = requestsView.findViewById(R.id.requestsRecycleView);
//        requestVisitList.setHasFixedSize(true);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        //linearLayoutManager.setReverseLayout(true);
//        requestVisitList.setLayoutManager(linearLayoutManager);
//        return requestsView;

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

        //request_user.clear();

        //populateRequestUser();


//        requestAdpater.notifyItemRangeRemoved(0, requestList.size());
//        requestVisitList.removeAllViewsInLayout();

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
                }else{
                    //Toast.makeText(mContext, " No data here", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        requestAdpater.notifyDataSetChanged();
    }

    private void populateRequestUser() {

         requestAdapter
                = new FirebaseRecyclerAdapter<RequestVisit, RequestViewHolder>(
                RequestVisit.class,
                R.layout.seekerrequests_layout,
                RequestViewHolder.class,
                requestDatabase.child(user_id)
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, RequestVisit model, int position) {


                final String key = this.getRef((position)).getKey();

                requestDatabase.child(user_id).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
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
                                        viewHolder.setSender(requestVisit.getSender());
                                        viewHolder.setDate(requestVisit.getDate());
                                        viewHolder.setTime(requestVisit.getTime());

                                    }else{
                                        ViewGroup.LayoutParams params = viewHolder.ll.getLayoutParams();
                                        params.height=0;
                                        params.width= 0;

                                        viewHolder.ll.setVisibility(View.GONE);
                                        viewHolder.ll.setLayoutParams(params);
                                    }

                                }
                            }
                        }else{
                            Log.d("Data exists", "No database exists");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        requestVisitList.setAdapter(requestAdapter);
        Log.d("List Seeker", ""+requestDatabase);
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        TextView sender_content, time_content, date_content, propertyName;
        Button chat_user, accept_request, decline_request;
        LinearLayout ll;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            ll = mView.findViewById(R.id.requestsLayout);
            //ll.setLayoutParams(params);
            chat_user = mView.findViewById(R.id.chat_user);
            accept_request = mView.findViewById(R.id.accept_request);
            decline_request = mView.findViewById(R.id.decline_request);

            chat_user.setOnClickListener(this);
            accept_request.setOnClickListener(this);
            decline_request.setOnClickListener(this);

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

        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.chat_user) {
                int adapterPos = getAdapterPosition();
                goToChat(adapterPos);
            }else if(view.getId() == R.id.accept_request){
                //Toast.makeText(view.getContext(), "Accept User" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                goToAcceptRequest();
            }else if(view.getId() == R.id.decline_request){
                //Toast.makeText(view.getContext(), "Decline User" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }else{
               // Toast.makeText(view.getContext(), "No buttons Found", Toast.LENGTH_SHORT).show();
            }
        }

        private void goToChat(int adapterPos) {
            //Toast.makeText(mContext, ""+request_user.get(adapterPos).get("Owner ID"), Toast.LENGTH_SHORT).show();
            Intent chatIntent = new Intent(mContext, ChatMessage.class);
            chatIntent.putExtra("ownerId", request_user.get(adapterPos).get("Owner ID"));
            chatIntent.putExtra("propertyId", request_user.get(adapterPos).get("Property ID"));
            mContext.startActivity(chatIntent);
        }

        private void goToAcceptRequest() {
            requestDatabase.child(user_id)
                            .child(request_user.get(getAdapterPosition()).get("Owner ID"))
                            .child(request_user.get(getAdapterPosition()).get("Property ID"))
                            .child("accepted")
                            .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(mContext, "You accepted the Request", Toast.LENGTH_SHORT).show();
                        requestDatabase
                                .child(request_user.get(getAdapterPosition()).get("Owner ID"))
                                .child(user_id)
                                .child(request_user.get(getAdapterPosition()).get("Property ID"))
                                .child("accepted")
                                .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Updated Req/Sender End", "Completed");
                                }
                            }
                        });

                        requestAdapter.notifyDataSetChanged();
                    }else{
                        Log.d("Accept Request Error", "There is error accepting request " + task.getResult());
                    }
                }
            });
        }

    }


     /*
    *  UTILITES
    *
    *
    */



}
