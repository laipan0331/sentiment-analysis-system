package com.lmz.sentiment_analysis.service;
import java.util.List;
import com.lmz.sentiment_analysis.model.Comment;
import java.util.Map;

//This interface defines the contract for comment-related operations.
//It declares methods to add a comment, retrieve all comments, and compute a global distribution of sentiment values from the comments.
public interface CommentService {
    Comment addComment(String content);
    List<Comment> getAllComments();
    Map<String, Long> getGlobalSentimentDistribution();
}


