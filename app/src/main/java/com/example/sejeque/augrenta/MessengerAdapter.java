package com.example.sejeque.augrenta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by SejeQue on 5/18/2018.
 */

public class MessengerAdapter extends BaseAdapter  {

    private DatabaseReference messageDatabase;

    //instantiate firebase auth and user
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    List<HashMap<String, String>> message_user;
    Context context;

    private LayoutInflater mlayoutInflater;

    public MessengerAdapter(Context context, List<HashMap<String, String>> message_user) {
        this.message_user = message_user;
        this.context = context;
    }

    @Override
    public int getCount() {
        return message_user.size();
    }

    @Override
    public Object getItem(int i) {
        return message_user.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        final int pos = i;

        if(view==null){

            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.list_item_chat, viewGroup, false);
            holder.text = (TextView) view.findViewById(R.id.textUploadItem);
            holder.text1 = (TextView) view.findViewById(R.id.textSubItem);
            holder.delImg = view.findViewById(R.id.delete_button);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.text.setText(message_user.get(i).get("Sender"));
        holder.text1.setText(message_user.get(i).get("Prop_name"));

        holder.delImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "New "+pos, Toast.LENGTH_SHORT).show();
                showDeleteDialog(pos);
            }
        });

        return view;
    }

    public void showDeleteDialog(int pos){

        final int position = pos;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this conversation? All conversation will be lost");


        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String ownerId, propertyId;
                ownerId = message_user.get(position).get("SenderId");
                propertyId = message_user.get(position).get("Property ID");
                //Toast.makeText(context, ownerId+ " and " + propertyId , Toast.LENGTH_SHORT).show();
                nowDeleteMessageData(ownerId, propertyId);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        AlertDialog levelDialog = builder.create();
        levelDialog.show();
    }

    public void nowDeleteMessageData(String ownerId, String propertyId){

        //get user information
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        messageDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        messageDatabase.child(currentUser.getUid()).child(ownerId).child(propertyId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Conversation has been deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public class ViewHolder {
        TextView text, text1;
        ImageButton delImg;
    }

}


