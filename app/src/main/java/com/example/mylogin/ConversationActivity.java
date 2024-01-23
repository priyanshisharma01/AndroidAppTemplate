package com.example.mylogin;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://android-messaging.branch.co/api/";
    private static final String API_MESSAGES_ENDPOINT = "messages";
    private static final String AUTH_TOKEN_HEADER = "X-Branch-Auth-Token";
    private static final String THREAD_ID_EXTRA = "thread_id";
    private int selectedThreadId = -1;

    private ListView conversationListView;
    private List<Message> conversationList;
    private MessageListAdapter conversationAdapter;
    private EditText messageInputEditText;
    private Button sendMessageButton;
    // Inside ConversationActivity class
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        conversationListView = findViewById(R.id.conversationListView);
        messageInputEditText = findViewById(R.id.messageInputEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        // Initialize the conversation list
        conversationList = new ArrayList<>();
        conversationAdapter = new MessageListAdapter(this, conversationList);
        conversationListView.setAdapter(conversationAdapter);

        selectedThreadId = getIntent().getIntExtra("thread_id", -1);

        Log.d("ThreadID", "Selected Thread ID in ConversationActivity: " + selectedThreadId);

        // Check if the thread ID is valid
        if (selectedThreadId != -1) {
            // Fetch and display messages for the selected thread
            Log.d("ThreadID", "Selected Thread ID in ConversationActivity: " + selectedThreadId);

            getMessages(selectedThreadId);
            Log.d("ThreadID", "Selected Thread ID in ConversationActivity: " + selectedThreadId);

        } else {
            Toast.makeText(this, "Invalid thread ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the thread ID is invalid
        }

        // Set click listener for the send message button
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ThreadID", "Selected Thread ID send message on click listnere: " + selectedThreadId);

                // Get the typed message
                String typedMessage = messageInputEditText.getText().toString().trim();
//                Log.d("ThreadID", "Selected Thread ID: " + selectedThreadId);

                // Check if the message is not empty
                if (!typedMessage.isEmpty()) {
                    // Send the message
                    sendMessage(selectedThreadId, typedMessage);
                    Log.d("ThreadID", "Selected Thread ID: " + selectedThreadId);


                    // Clear the message input field
                    messageInputEditText.getText().clear();
                }
            }
        });
    }

    private void getMessages(int threadId) {
        // Retrieve auth token from SharedPreferences
        String authToken = getAuthToken();
//        Log.d("ThreadID", "Selected Thread ID getmessage conversation: " + selectedThreadId);

        // Check if auth token is available
        if (authToken != null && !authToken.isEmpty()) {
            // Make GET request to api/messages endpoint for the selected thread
            JsonArrayRequest messagesRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    API_BASE_URL + API_MESSAGES_ENDPOINT + "?thread_id=" + threadId,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            parseMessagesResponse(response);
                            Log.d("APIResponse", "Messages Response: " + response.toString());
                            // Sort messages based on timestamp (most recent first)
                            Collections.sort(conversationList, new Comparator<Message>() {
                                @Override
                                public int compare(Message message1, Message message2) {
                                    try {
                                        Date date1 = sdf.parse(message1.getTimestamp());
                                        Date date2 = sdf.parse(message2.getTimestamp());
                                        return date2.compareTo(date1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
                                }
                            });

                            // Notify the adapter that the data has changed
                            conversationAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("APIError", "Error fetching messages: " + error.toString());
                            Toast.makeText(ConversationActivity.this, "Error fetching messages", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put(AUTH_TOKEN_HEADER, authToken);
                    return headers;
                }
            };

            // Add the request to the Volley request queue
            Volley.newRequestQueue(this).add(messagesRequest);
        }
        Log.d("APIRequest", "Auth Token: " + authToken);
        Log.d("APIRequest", "Thread ID: " + threadId);
//        Log.d("APIRequest", "USER ID: " + useri);

    }


    private void sendMessage(int threadId, String messageBody) {
        // Retrieve auth token from SharedPreferences
        String authToken = getAuthToken();
//        Log.d("ThreadID", "Selected Thread ID: " + selectedThreadId);

        // Check if auth token is available
        if (authToken != null && !authToken.isEmpty()) {
            // Create request body for sending a new message
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("thread_id", threadId);
                requestBody.put("body", messageBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Make POST request to api/messages endpoint to send a new message
            JsonObjectRequest sendMessageRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    API_BASE_URL + API_MESSAGES_ENDPOINT,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Message sent successfully, refresh the conversation
                            getMessages(threadId);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ConversationActivity.this, "Error sending message", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put(AUTH_TOKEN_HEADER, authToken);
                    return headers;
                }
            };

            // Add the request to the Volley request queue
            Volley.newRequestQueue(this).add(sendMessageRequest);
        }
    }

    private void parseMessagesResponse(JSONArray response) {
        try {
            Log.e("APIError", "before clearing: " + response.toString());

            // Clear existing messages
            conversationList.clear();
            Log.e("APIError", "after clearing: " + response.toString());

            // Parse each message object in the response
            for (int i = 0; i < response.length(); i++) {
                JSONObject messageObject = response.getJSONObject(i);

                // Extract message details
                int messageId = messageObject.getInt("id");
                int threadId = messageObject.getInt("thread_id");
                int userId = messageObject.getInt("user_id");
                int agentId;
                if (messageObject.has("agent_id") && !messageObject.isNull("agent_id")) {
                    agentId = messageObject.getInt("agent_id");
                } else {
                    agentId = 0; // or any default value as per your application logic
                }
                String body = messageObject.getString("body");
                String timestamp = messageObject.getString("timestamp");

                // Check if the thread ID matches the selected thread ID
                if (threadId == selectedThreadId || selectedThreadId == -1) {
                    // Create a Message object and add it to the list
                    Message message = new Message(messageId, threadId, userId, agentId, body, timestamp);
                    conversationList.add(message);
                }
            }

            // Notify the adapter that the data has changed
            conversationAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ParsingError", "Error parsing messages response: " + e.getMessage());
            Toast.makeText(ConversationActivity.this, "Error parsing messages response", Toast.LENGTH_SHORT).show();
        }
    }


    private String getAuthToken() {
        // Retrieve auth token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyLoginApp", Context.MODE_PRIVATE);
        return sharedPreferences.getString("authToken", null);
    }
}
