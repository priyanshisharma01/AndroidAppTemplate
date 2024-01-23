

package com.example.mylogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageThreadsActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://android-messaging.branch.co/api/";
    private static final String API_MESSAGES_ENDPOINT = "messages";
    private static final String EXTRA_THREAD_ID = "thread_id";
    private static final String AUTH_TOKEN_HEADER = "X-Branch-Auth-Token";

    private ListView messageThreadsListView;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    private List<Message> messageList;
    private MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);

        messageThreadsListView = findViewById(R.id.messageThreadsListView);

        // Initialize the message list
        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(this, messageList);
        messageThreadsListView.setAdapter(messageListAdapter);
        // MessageThreadsActivity.java
// Inside the onCreate method where you set the item click listener for the ListView

        // Inside the onItemClick method or wherever you are handling the click on a message thread
        messageThreadsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Get the selected message thread
                Message selectedMessage = messageList.get(position);

                // Log the selected thread ID for debugging
                Log.d("ThreadID", "Selected Thread ID: " + selectedMessage.getThreadId());

                // Start the ConversationActivity and pass the thread ID
                Intent intent = new Intent(MessageThreadsActivity.this, ConversationActivity.class);
                intent.putExtra("thread_id", selectedMessage.getThreadId());
                startActivity(intent);
            }
        });



        // Make API call to get messages after successful login
        getMessages(-1);
    }


    private void getMessages(int threadId) {
        // Retrieve auth token from SharedPreferences
        String authToken = getAuthToken();

        // Check if auth token is available
        if (authToken != null && !authToken.isEmpty()) {
            // Build the API endpoint URL
            String endpointUrl = (threadId == -1) ? API_BASE_URL + API_MESSAGES_ENDPOINT : API_BASE_URL + API_MESSAGES_ENDPOINT + "?thread_id=" + threadId;

            // Make GET request to the API endpoint
            JsonArrayRequest messagesRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    endpointUrl,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            parseMessagesResponse(response);

                            // Sort messages based on timestamp (most recent first)
                            Collections.sort(messageList, new Comparator<Message>() {
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
                            messageListAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("APIError", "Error fetching messages: " + error.toString());
                            Toast.makeText(MessageThreadsActivity.this, "Error fetching messages", Toast.LENGTH_SHORT).show();
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
    }



    private String getAuthToken() {
        // Retrieve auth token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyLoginApp", Context.MODE_PRIVATE);
        return sharedPreferences.getString("authToken", null);
    }



    private void parseMessagesResponse(JSONArray response) {
        try {
            // Clear existing messages
            messageList.clear();

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

                // Create a Message object and add it to the list
                Message message = new Message(messageId, threadId, userId, agentId, body, timestamp);
                messageList.add(message);
            }

            // Notify the adapter that the data has changed
            messageListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ParsingError", "Error parsing messages response: " + e.getMessage());
            Toast.makeText(MessageThreadsActivity.this, "Error parsing messages response", Toast.LENGTH_SHORT).show();
        }

    }
}

