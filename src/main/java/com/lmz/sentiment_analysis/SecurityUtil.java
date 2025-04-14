package com.lmz.sentiment_analysis.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.lmz.sentiment_analysis.security.CustomUserDetails;

public class SecurityUtil {

    /**
     * 获取当前登录用户的ID，如果未认证则返回 null（或者你可以抛出异常）
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getId();
        }
        return null;
    }
}

