package com.lmz.sentiment_analysis.controller;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
public class CommentViewController {

    @Autowired
    private CommentService commentService;

    // 处理评论提交，返回分析结果页面 result.html
    @PostMapping("/submit")
    public String submitComment(@RequestParam("content") String content, Model model) {
        // 调用 Service 分析和保存评论
        Comment savedComment = commentService.addComment(content);
        // 将评论数据放入 Model 中
        model.addAttribute("emoji", savedComment.getEmoji());
        model.addAttribute("sentiment", savedComment.getSentiment());
        model.addAttribute("score", savedComment.getSentimentScore());
        model.addAttribute("bgColor", savedComment.getBackgroundColor());
        // 可选：将评论内容也传入
        model.addAttribute("content", savedComment.getContent());
        // 返回 Thymeleaf 模板名称（result.html）
        return "result";
    }
}

