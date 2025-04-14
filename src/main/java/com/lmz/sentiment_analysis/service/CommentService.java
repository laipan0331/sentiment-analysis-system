package com.lmz.sentiment_analysis.service;

import java.util.List;
import com.lmz.sentiment_analysis.model.Comment;
import java.util.Map;

public interface CommentService {
    Comment addComment(String content);
    // 返回当前登录用户的所有评论
    List<Comment> getAllComments();
    // 新增：获取所有用户评论情感分布统计数据
    Map<String, Long> getGlobalSentimentDistribution();
}


