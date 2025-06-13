package com.relioww.moviematch.friends;

public class FriendItem {
    private int friendshipId;
    private int userId;
    private String username;
    private boolean selected;

    public FriendItem(int friendshipId, int userId, String username) {
        this.friendshipId = friendshipId;
        this.userId = userId;
        this.username = username;
        this.selected = false;
    }

    public int getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(int friendshipId) {
        this.friendshipId = friendshipId;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
