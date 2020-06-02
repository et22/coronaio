package com.example.myapplication.models;

public class GameStats {
    int gamesWon, gamesLost;

    public GameStats(int gamesWon, int gamesLost){
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
    }

    public GameStats(){

    }

    public int getGamesWon(){
        return this.gamesWon;
    }

    public int getGamesLost(){
        return this.gamesLost;
    }
}
