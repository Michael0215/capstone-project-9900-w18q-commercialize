package com.example.comp9900_commercialize.firebase;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.comp9900_commercialize.LiveChatActivity;
import com.example.comp9900_commercialize.R;
import com.example.comp9900_commercialize.models.User;
import com.example.comp9900_commercialize.utilities.MacroDef;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessageService extends FirebaseMessagingService {

    // FirebaseMessagingService is a provided class for dealing with Firebase Cloud Message.

    // We planned to use this for implement the function of message notification in real time.
    /* But the message sent by Cloud Message was very difficult and unstable to receive in China even when
     *  we used the VPN provided by UNSW. Thus we finally abandoned this function and left the works here. */

    @Override
    public void onNewToken(@NonNull String token) { super.onNewToken(token); }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        User user = new User();
        user.email = remoteMessage.getData().get(MacroDef.KEY_EMAIL);
        user.name = remoteMessage.getData().get(MacroDef.KEY_USERNAME);
        user.token = remoteMessage.getData().get(MacroDef.KEY_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, LiveChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(MacroDef.KEY_USER, user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //set data for notification page
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(user.name);
        builder.setContentText(remoteMessage.getData().get(MacroDef.KEY_LAST_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(MacroDef.KEY_LAST_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification is used for chat message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
