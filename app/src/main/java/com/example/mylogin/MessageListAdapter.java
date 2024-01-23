package com.example.mylogin;

// MessageListAdapter.java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MessageListAdapter extends ArrayAdapter<Message> {

    public MessageListAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }
    // Add a method to update messages
    public void updateMessages(List<Message> messages) {
        clear(); // Clear existing data
        addAll(messages); // Add new messages
        notifyDataSetChanged(); // Notify adapter of data change
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Message message = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_thread, parent, false);
        }

        // Lookup view for data population
        TextView bodyTextView = convertView.findViewById(R.id.latestMessageTextView);
        TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);
        TextView agentIdTextView = convertView.findViewById(R.id.agentIdTextView);
        TextView userIdTextView = convertView.findViewById(R.id.userIdTextView);

        // Populate the data into the template view using the data object
        if (message != null) {
            bodyTextView.setText(message.getBody());
            timestampTextView.setText(message.getTimestamp());
            agentIdTextView.setText("Agent ID: " + message.getAgentId());
            userIdTextView.setText("User ID: " + message.getUserId());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the ConversationActivity when the item is clicked
                    Intent intent = new Intent(getContext(), ConversationActivity.class);
                    Message clickedMessage = getItem(position);
                    if (clickedMessage != null) {
                        intent.putExtra("thread_id", clickedMessage.getThreadId());
                        getContext().startActivity(intent);
                    }
                }
            });
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
