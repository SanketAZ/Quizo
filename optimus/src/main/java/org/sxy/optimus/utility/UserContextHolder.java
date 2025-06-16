package org.sxy.optimus.utility;

import org.sxy.optimus.dto.UserPrinciple;

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
