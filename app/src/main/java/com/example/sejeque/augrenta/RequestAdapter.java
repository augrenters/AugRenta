package com.example.sejeque.augrenta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by SejeQue on 5/15/2018.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestAdapterHolder> {

    private List<RequestVisitData> userRequestList;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    List<Property> properties;

    private DatabaseReference requestDatabase, propDatabase, notifDatabase;
    View v;
    Context mContext;

    private List<HashMap<String,String>> prop_name;

    public RequestAdapter(List<RequestVisitData> userRequestList) {
        this.userRequestList = userRequestList;

    }

    @NonNull
    @Override
    public RequestAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seekerrequests_layout, parent, false);

        mContext = parent.getContext();
        //get user information

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");
        propDatabase = FirebaseDatabase.getInstance().getReference().child("Property");
        notifDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notifDatabase.keepSynced(true);

        prop_name = new ArrayList<>();
        properties = new ArrayList<>();

//        propDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for(final DataSnapshot propKey: dataSnapshot.getChildren()){
//                    propDatabase.child(propKey.getKey()).child("propertyName").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            final HashMap<String, String> propResult = new HashMap<>();
//                            propResult.put("propertyId", propKey.getKey());
//                            propResult.put("propertyname", dataSnapshot.getValue().toString());
//                            prop_name.add(propResult);
//
//                            Toast.makeText(mContext, ""+prop_name, Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {}});
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
        //Toast.makeText(mContext, ""+prop_name, Toast.LENGTH_SHORT).show();

        //Toast.makeText(mContext, ""+properties, Toast.LENGTH_SHORT).show();
        return new RequestAdapterHolder(v);
    }

    public RequestAdapter() {
        super();
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestAdapterHolder holder, final int position) {

        RequestVisitData request = userRequestList.get(position);

        if(request.getType().equals("sender")){
            ViewGroup.LayoutParams params = holder.ll.getLayoutParams();
            params.height=0;
            params.width= 0;

            holder.ll.setVisibility(View.GONE);
            holder.ll.setLayoutParams(params);

        }else if(request.getType().equals("receiver")){

            if(!request.isAccepted()){
                holder.sender_content.setText(request.getSender());
                holder.date_content.setText(request.getDate());
                holder.time_content.setText(request.getTime());

                holder.propertyName.setText(request.getPropertyName());

                holder.accept_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.goToAcceptRequest(position);
                        userRequestList.remove(position);
                        ViewGroup.LayoutParams params = holder.ll.getLayoutParams();
                        params.height=0;
                        params.width= 0;

                        holder.ll.setVisibility(View.GONE);
                        holder.ll.setLayoutParams(params);
                    }
                });

                holder.decline_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.goToDeclineRequest(position);
                        userRequestList.remove(position);
                        ViewGroup.LayoutParams params = holder.ll.getLayoutParams();
                        params.height=0;
                        params.width= 0;

                        holder.ll.setVisibility(View.GONE);
                        holder.ll.setLayoutParams(params);
                    }
                });

            }else{
                ViewGroup.LayoutParams params = holder.ll.getLayoutParams();
                params.height=0;
                params.width= 0;

                holder.ll.setVisibility(View.GONE);
                holder.ll.setLayoutParams(params);
            }
        }

    }

    @Override
    public int getItemCount() { return userRequestList.size();}

    public class RequestAdapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View mView;
        TextView sender_content, time_content, date_content, propertyName;
        Button chat_user, accept_request, decline_request;
        LinearLayout ll;

        public RequestAdapterHolder(View itemView) {
            super(itemView);
            mView = itemView;

            sender_content = mView.findViewById(R.id.senderName);
            time_content = mView.findViewById(R.id.requestTime);
            date_content = mView.findViewById(R.id.requestDate);
            propertyName = mView.findViewById(R.id.requestedPropertyName);

            ll = mView.findViewById(R.id.requestsLayout);
            //ll.setLayoutParams(params);
            chat_user = mView.findViewById(R.id.chat_user);
            accept_request = mView.findViewById(R.id.accept_request);
            decline_request = mView.findViewById(R.id.decline_request);

            chat_user.setOnClickListener(this);
            //accept_request.setOnClickListener(this);
            //decline_request.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.chat_user) {
                int adapterPos = getAdapterPosition();
                //Toast.makeText(view.getContext(), "Accept User" + userRequestList.get(adapterPos).getSender(), Toast.LENGTH_SHORT).show();
                goToChat(adapterPos);

            }else if(view.getId() == R.id.accept_request){
                int adapterPos = getAdapterPosition();
                Toast.makeText(view.getContext(), "Accept User" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                goToAcceptRequest(adapterPos);

                notifyItemRemoved(adapterPos);
                notifyItemChanged(adapterPos, userRequestList.size());
                notifyDataSetChanged();


            }else if(view.getId() == R.id.decline_request){
                Toast.makeText(view.getContext(), "Decline User" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(view.getContext(), "No buttons Found", Toast.LENGTH_SHORT).show();
            }
        }


        private void goToChat(int adapterPos) {
            //Toast.makeText(mContext, ""+request_user.get(adapterPos).get("Owner ID"), Toast.LENGTH_SHORT).show();
            Intent chatIntent = new Intent(mContext, ChatMessage.class);
            chatIntent.putExtra("ownerId", userRequestList.get(adapterPos).getSenderId());
            chatIntent.putExtra("propertyId", userRequestList.get(adapterPos).getPropertyId());
            mContext.startActivity(chatIntent);
        }

        private void goToAcceptRequest(final int adapterPos) {
            int pos = adapterPos;
            final String prop_Id = userRequestList.get(pos).getPropertyId();

            requestDatabase.child(currentUser.getUid())
                    .child(userRequestList.get(adapterPos).getSenderId())
                    .child(prop_Id)
                    .child("accepted")
                    .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(mContext, "You accepted the Request ", Toast.LENGTH_SHORT).show();
                        requestDatabase
                                .child(userRequestList.get(adapterPos).getSenderId())
                                .child(currentUser.getUid())
                                .child(prop_Id)
                                .child("accepted")
                                .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    HashMap<String, String> notificationData = new HashMap<String, String>();
                                    notificationData.put("fromName", currentUser.getDisplayName());
                                    notificationData.put("fromID", currentUser.getUid());
                                    notificationData.put("type", "receiver");
                                    notificationData.put("response", "accept");
                                    notificationData.put("propertyId", prop_Id);

                                    notifDatabase.child(userRequestList.get(adapterPos).getSenderId()).push().setValue(notificationData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Log.d("Updated Req/Sender End", "Completed");
                                                    }
                                                }
                                            });


                                    Log.d("Updated Req/Sender End", "Completed");
                                    Toast.makeText(mContext, "You accepted the Request " + prop_Id, Toast.LENGTH_SHORT).show();
                                    notifyItemRemoved(adapterPos);
                                    notifyItemChanged(adapterPos, userRequestList.size());
                                    notifyItemRangeChanged(adapterPos, userRequestList.size());
                                    notifyDataSetChanged();
                                }
                            }
                        });

                    }else{
                        Log.d("Accept Request Error", "There is error accepting request " + task.getResult());
                    }
                }
            });
        }

        private void goToDeclineRequest(final int adapterPos) {
            int pos = adapterPos;
            final String prop_Id = userRequestList.get(pos).getPropertyId();
            final String senderId = userRequestList.get(adapterPos).getSenderId();

            requestDatabase.child(currentUser.getUid())
                    .child(userRequestList.get(adapterPos).getSenderId())
                    .child(prop_Id)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //Toast.makeText(mContext, "You accepted the Request " + userRequestList.get(adapterPos).getPropertyId(), Toast.LENGTH_SHORT).show();
                        requestDatabase
                                .child(senderId)
                                .child(currentUser.getUid())
                                .child(prop_Id)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    HashMap<String, String> notificationData = new HashMap<String, String>();
                                    notificationData.put("fromName", currentUser.getUid());
                                    notificationData.put("fromID", currentUser.getDisplayName() );
                                    notificationData.put("type", "receiver");
                                    notificationData.put("response", "declined");
                                    notificationData.put("propertyId", prop_Id);

                                    notifDatabase.child(senderId).push().setValue(notificationData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Log.d("Updated Req/Sender End", "Completed");
                                                    }
                                                }
                                            });
                                    Log.d("Updated Req/Sender End", "Completed");
                                    Toast.makeText(mContext, "You Declined the Request ", Toast.LENGTH_SHORT).show();
                                    notifyItemRemoved(adapterPos);
                                    notifyItemChanged(adapterPos, userRequestList.size());
                                    notifyItemRangeChanged(adapterPos, userRequestList.size());
                                    notifyDataSetChanged();
                                }
                            }
                        });

                    }else{
                        Log.d("Accept Request Error", "There is error accepting request " + task.getResult());
                    }
                }
            });
        }
    }



}
