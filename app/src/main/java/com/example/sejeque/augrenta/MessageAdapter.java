package com.example.sejeque.augrenta;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by SejeQue on 4/25/2018.
 */



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Message> userMessagesList;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    public MessageAdapter(List<Message> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatmessage_layout, parent, false);


        //get user information

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        Message message = userMessagesList.get(position);

        String message_sender_id = currentUser.getDisplayName();
        String fromUserID = message.getSender();

        Log.d(" Message and Sender", message_sender_id+ " " + message.getSender());

        if (fromUserID.equals(message_sender_id)) {
            // My own message
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            holder.ll.setLayoutParams(params);

            holder.messageText.setLayoutParams(params);
            holder.messageText.setBackgroundResource(R.drawable.message_shapesender);
            holder.sender.setLayoutParams(params);
            holder.messageDate.setLayoutParams(params);

            holder.messageText.setText(message.getMessage());
            holder.sender.setText(message.getSender());
            holder.messageDate.setText(message.getDate());

            holder.messageText.setGravity(Gravity.END);

        } else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            holder.ll.setLayoutParams(params);


            holder.messageText.setLayoutParams(params);
            holder.messageText.setBackgroundResource(R.drawable.message_shapereceiver);
            holder.sender.setLayoutParams(params);
            holder.messageDate.setLayoutParams(params);

            holder.messageText.setText(message.getMessage());
            holder.sender.setText(message.getSender());
            holder.messageDate.setText(message.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText, sender, messageDate;
        public LinearLayout ll;
        public MessageViewHolder(View itemView) {
            super(itemView);

            ll = itemView.findViewById(R.id.chatlayout);
            messageText = itemView.findViewById(R.id.messageText);
            sender = itemView.findViewById(R.id.usernameText);
            messageDate = itemView.findViewById(R.id.messageTime);
        }
    }
}

