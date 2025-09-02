package org.sxy.frontier.dto;

public class UserPrinciple {
    private String id;
    private String userName;
    private String email;

    public UserPrinciple(String email, String userName, String id) {
        this.email = email;
        this.userName = userName;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
