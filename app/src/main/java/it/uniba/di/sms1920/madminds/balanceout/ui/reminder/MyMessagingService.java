package it.uniba.di.sms1920.madminds.balanceout.ui.reminder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import it.uniba.di.sms1920.madminds.balanceout.R;


public class MyMessagingService extends FirebaseMessagingService {


    private static final String TAG = "balanceOutTracker";



    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "creazione nuovo token = " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    private void sendRegistrationToServer(String token) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("token").child("userToken");

        db.child(token).child("0").setValue("used");
    }





    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i (TAG, "messaggio ricevuto -OnMessageReceived-  from: " + remoteMessage.getFrom());
        //showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }



    public void showNotification (String title, String message){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNotifications")
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.logo)
                .setAutoCancel(true)
                .setContentText(message);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(999, builder.build());
    }



}
