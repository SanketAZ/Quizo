package org.sxy.frontier.utility;


import org.sxy.frontier.dto.UserPrinciple;

public class UserContextHolder {

    private static final ThreadLocal<UserPrinciple> userHolder = new ThreadLocal<>();

    public static void setUser(UserPrinciple user) {
        userHolder.set(user);
    }

    public static UserPrinciple getUser() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }
}
