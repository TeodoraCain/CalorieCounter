package com.example.caloriecounter.model.dataHolder;

import com.example.caloriecounter.model.DAO.UserDetails;

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
