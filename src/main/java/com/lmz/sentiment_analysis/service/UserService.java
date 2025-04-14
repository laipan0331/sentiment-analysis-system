package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.User;

public interface UserService {
    User registerUser(String username, String password);
}
