package org.sxy.frontier.dto;

public class RoomUserDetails {
    private String userId;
    private String username;

    public RoomUserDetails(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
