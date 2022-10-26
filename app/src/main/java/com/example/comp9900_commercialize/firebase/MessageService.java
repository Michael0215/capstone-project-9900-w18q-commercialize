package com.example.comp9900_commercialize.firebase;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends FirebaseMessagingService {

    // FirebaseMessagingService is a provided class for dealing with Firebase Cloud Message.

    // We planned to use this for implement the function of message notification in real time.
    /* But the message sent by Cloud Message was very difficult and unstable to receive in China even when
     *  we used the VPN provided by UNSW. Thus we finally abandoned this function and left the works here. */

    @Override
    public void onNewToken(@NonNull String token) { super.onNewToken(token); }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}
