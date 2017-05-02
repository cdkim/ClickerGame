package com.example.ceon.clickergame;

/**
 * Created by Ceon on 11/8/16.
 */

public class Enemy {
    private String name;
    private int health;
    private int iconID;
    private double gold_yield;

    public String getName() {
        return name;
    }

    public Enemy(String name, int health, int iconID, double gold_yield) {
        this.name = name;
        this.health = health;
        this.iconID = iconID;
        this.gold_yield = gold_yield;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getGold_yield() {
        return gold_yield;
    }

    public void setGold_yield(double gold_yield) {
        this.gold_yield = gold_yield;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public int getIconID() {
        return iconID;
    }
}
