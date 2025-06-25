package com.example.studymate;

public class User {
    private String username;
    private int number, points;

    public User(String username, int points, int number) {
        setUsername(username);
        setPoints(points);
        setNumber(number);
    }

    public String getUsername() {
        return username;
    }
    public int getPoints() {
        return points;
    }
    public int getNumber() {
        return number;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void setNumber(int number) {
        this.number = number;
    }
}
