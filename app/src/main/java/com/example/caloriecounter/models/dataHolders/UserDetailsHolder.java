package com.example.caloriecounter.models.dataHolders;

import com.example.caloriecounter.models.dao.UserDetails;

public class UserDetailsHolder {
    private UserDetails userDetails;

    public UserDetails getData() {
        return userDetails;
    }

    public void setData(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    private static final UserDetailsHolder holder = new UserDetailsHolder();

    public static UserDetailsHolder getInstance() {
        return holder;
    }
}
