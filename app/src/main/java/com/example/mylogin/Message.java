package com.example.mylogin;

// Message.java
// Message.java
public class Message {
    private int id;
    private int threadId;
    private int userId;
    private Integer agentId; // Use Integer instead of int for nullable values
    private String body;
    private String timestamp;

    public Message(int id, int threadId, int userId, Integer agentId, String body, String timestamp) {
        this.id = id;
        this.threadId = threadId;
        this.userId = userId;
        this.agentId = agentId;
        this.body = body;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public String getBody() {
        return body;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
