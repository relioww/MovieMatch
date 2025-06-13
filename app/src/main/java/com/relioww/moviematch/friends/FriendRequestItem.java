package com.relioww.moviematch.friends;

public class FriendRequestItem {
    private int requestId;
    private int userId;
    private String username;


    public FriendRequestItem(int requestId, int userId, String username) {
        this.requestId = requestId;
        this.userId = userId;
        this.username = username;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
