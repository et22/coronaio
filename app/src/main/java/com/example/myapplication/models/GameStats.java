package com.example.myapplication.models;

public class GameStats {
    int gamesWon, gamesLost;
    String gameType;

    public GameStats(int gamesWon, int gamesLost, String type){
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gameType = type;
    }

    public GameStats(){

    }

    public int getGamesWon(){
        return this.gamesWon;
    }

    public int getGamesLost(){
        return this.gamesLost;
    }

    public String getGameType() {
        return this.gameType;
    }
}
