package com.example.sejeque.augrenta;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by SejeQue on 5/12/2018.
 */

public class AppFCMService extends FirebaseMessagingService {
    private final static String TAG="FCM Message";
    private String title, body;

    Intent intent;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " );
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage){

        title=remoteMessage.getData().get("title");
        body=remoteMessage.getData().get("body");

        //String click_action = remoteMessage.getNotification().getClickAction();
        String response_user = remoteMessage.getData().get("response");

        String property_id = remoteMessage.getData().get("property");
        String owner_id = remoteMessage.getData().get("from_userID");

        if(response_user !=null && response_user.equals("accept")){
            intent = new Intent(this, Main2Activity.class);
            intent.putExtra("propertyId", property_id);
            intent.putExtra("ownerId", owner_id);
            showNotificationBuilder();

        }else if (response_user.equals("message")){
            intent = new Intent(this, ChatMessage.class);
            intent.putExtra("propertyId", property_id);
            intent.putExtra("ownerId", owner_id);
            showNotificationBuilder();

        }else if(response_user.equals("isHere")){
            intent = new Intent(this, Main2Activity.class);
            intent.putExtra("propertyId", property_id);
            intent.putExtra("ownerId", owner_id);
            showNotificationBuilder();

        }else if (response_user.equals("rate")){
            intent = new Intent(this, Main2Activity.class);
            intent.putExtra("propertyId", property_id);
            showNotificationBuilder();
        }


//        if (click_action.equals("com.example.sejeque.augrenta_CHAT_TARGET_NOTIFICATION") ||
//                click_action.equals("com.example.sejeque.augrenta_MAIN_TARGET_NOTIFICATION")){
//            Log.d(TAG, "NotificationClicke: " + property_id);
//
//
//        }else{
//            intent = new Intent(click_action);
//            showNotificationBuilder();
//        }
    }

    private void showNotificationBuilder(){

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap notifyImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_black_24dp);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setLargeIcon(notifyImage)
                .setColor(Color.parseColor("#FFE74C3C"))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);





        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
