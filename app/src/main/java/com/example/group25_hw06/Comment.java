package com.example.group25_hw06;

/**
 * Assignment #: HW06
 * File Name: Group25_HW06 Comment.java
 * Full Name: Kristin Pflug
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    String posterName;
    String commentText;
    String datePosted;
    String posterID;
    String commentID;

    public Comment() {
        this.posterName = "Username";
        this.commentText = "Lorem ipsum";
        this.datePosted = new SimpleDateFormat().format(new Date());
        this.posterID = "0";
        this.commentID = "1";
    }

    public Comment(String posterName, String commentText, String datePosted, String posterID, String commentID) {
        this.posterName = posterName;
        this.commentText = commentText;
        this.datePosted = datePosted;
        this.posterID = posterID;
        this.commentID = commentID;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getPosterID() {
        return posterID;
    }

    public void setPosterID(String posterID) {
        this.posterID = posterID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
