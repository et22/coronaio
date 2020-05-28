package com.example.myapplication.models;

public class Player {
    public float x;
    public float y;
    public float scale;
    public float alpha;
    public float speed;
    public int score;
    public int cellType;
    public long spawnTime;

    public Player() {
        scale = 1;
        alpha = 1;
        speed = 1;
        score = 0;
        cellType = 0;
        spawnTime = 0;
    }
}
