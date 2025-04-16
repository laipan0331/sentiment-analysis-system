package com.lmz.sentiment_analysis.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
//This entity represents a comment in the application. It stores the comment content,
// sentiment analysis results (sentiment, emoji, sentiment score, background color),
//creation timestamp, and the identifier of the user who posted the comment.
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Comment cannot be blank")
    @Column(nullable = false)
    private String content;

    private String sentiment;
    private String emoji;
    private double sentimentScore;
    private String backgroundColor;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Long userId;

    //Default constructor required by JPA.
    public Comment() {}

    // Constructor to create a Comment with only content.
    public Comment(String content) {
        this.content = content;
    }

    // Getter & Setter methods for all properties
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getSentiment() {
        return sentiment;
    }
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getEmoji() {
        return emoji;
    }
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }
    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
