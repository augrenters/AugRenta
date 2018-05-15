package com.example.sejeque.augrenta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by SejeQue on 5/15/2018.
 */

public class AcceptAdapter extends RecyclerView.Adapter<AcceptAdapter.AcceptAdapterHolder> {

    private List<RequestVisitData> userRequestList;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DatabaseReference requestDatabase;
    View v;
    Context mContext;

    public AcceptAdapter(List<RequestVisitData> userRequestList) {
        this.userRequestList = userRequestList;
    }


    @NonNull
    @Override
    public AcceptAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seekerrequests_layout, parent, false);

        mContext = parent.getContext();
        //get user information

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Requests");

        return new AcceptAdapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptAdapterHolder holder, int position) {

        RequestVisitData request = userRequestList.get(position);

        if(request.getType().equals("sender")){
            ViewGroup.LayoutParams params = holder.ll.getLayoutParams();
            params.height=0;
            params.width= 0;

            holder.ll.setVisibility(View.GONE);
            holder.ll.setLayoutParams(params);

        }else if(request.getType().equals("receiver")){

            if(request.isAccepted()){
                holder.sender_content.setText(request.getSender());
                holder.date_content.setText(request.getDate());
                holder.time_content.setText(request.getTime());
                holder.propertyName.setText(request.getPropertyName());
                //Toast.makeText(mContext, ""+request.getPropertyName(), Toast.LENGTH_SHORT).show();

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
    public int getItemCount() { return userRequestList.size(); }


    public class AcceptAdapterHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView sender_content, time_content, date_content, propertyName;
        Button chat_user, accept_request, decline_request;
        LinearLayout ll, buttonsPanel;

        public AcceptAdapterHolder(View itemView) {
            super(itemView);

            mView = itemView;

            sender_content = mView.findViewById(R.id.senderName);
            time_content = mView.findViewById(R.id.requestTime);
            date_content = mView.findViewById(R.id.requestDate);
            propertyName = mView.findViewById(R.id.requestedPropertyName);
            buttonsPanel = mView.findViewById(R.id.requestsButtonsPanel);
            buttonsPanel.setVisibility(View.GONE);

            ll = mView.findViewById(R.id.requestsLayout);
        }
    }
}
