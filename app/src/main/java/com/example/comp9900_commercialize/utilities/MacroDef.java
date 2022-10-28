package com.example.comp9900_commercialize.utilities;

import com.example.comp9900_commercialize.bean.Ingredient;
import com.example.comp9900_commercialize.bean.Recipe;

import java.util.HashMap;
import java.util.List;

public class MacroDef {

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_EMAIL = "E-mail";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_RECIPE_DESCRIPTION = "recipe_description";
    public static final String KEY_RECIPE_NAME = "recipe_name";
    public static final String KEY_RECIPE_DIFFICULTY = "recipe_difficulty";
    public static final String KEY_RECIPE_SCHEDULED_TIME = "recipe_scheduled_time";
    public static final String KEY_RECIPE_ID = "recipe_id";
    public static final String KEY_MODE_CREATE = "recipe_mode_create";
    public static final String KEY_CONTRIBUTOR_EMAIL = "contributor_email";
    public static final String KEY_SEARCH_CONTENT = "search_content";
    public static final String KEY_SEARCH_TYPE = "search_type";
    public static final String KEY_SEARCH_MODE = "search_mode";
    //Live_Chat
    public static final String KEY_KEY_LIVECHATTABLE = "users";
    public static final String KEY_LIVECHATNAME = "Name";
    public static final String KEY_LIVECHATAVATAR = "Avatar";
    public static final String KEY_LIVECHATTYPE = "Type";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_EMAIL = "senderEmail";
    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    //
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA7iD5f4g:APA91bG1BSq3epZtKC7sqftqqL4O-LBgU2WiIGPi-RM46KuUEh7vOTlkvJzPlBn_JDz6vxopn6mbBBEnlCCHbwO5L5X__iwyV05ehb4zofxngIJqABlD7NN95Ry0vXqwLwUckm0mlyxM"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
