package com.example.group25_hw06;

/**
 * Assignment #: HW06
 * File Name: Group25_HW06 Forum.java
 * Full Name: Kristin Pflug
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Forum {

    String title;
    String author;
    String description;
    String timeCreated;
    String userID;
    String forumID;
    ArrayList<String> userLikes;


    public Forum() {
        this.title = "Forum Title";
        this.author = "Forum Author";
        this.description = "Forum description";
        this.timeCreated = new SimpleDateFormat().format(new Date());
        this.userID = "1";
        this.forumID = "0";
        this.userLikes = new ArrayList<>();
    }

    public Forum(String title, String author, String description, String timeCreated, String userID, String forumID, ArrayList<String> userLikes) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.timeCreated = timeCreated;
        this.userID = userID;
        this.forumID = forumID;
        this.userLikes = userLikes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getForumID() {
        return forumID;
    }

    public void setForumID(String forumID) {
        this.forumID = forumID;
    }

    public ArrayList<String> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(ArrayList<String> userLikes) {
        this.userLikes = userLikes;
    }
}

