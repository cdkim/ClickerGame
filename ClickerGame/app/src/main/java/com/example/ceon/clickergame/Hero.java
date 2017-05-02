package com.example.ceon.clickergame;

import java.io.Serializable;

/**
 * Created by Ceon on 11/8/16.
 */

public class Hero implements Serializable{

    private String name;
    private String description;
    private int iconID;
    private double cost;
    private double baseDps;
    private double totalDps;
    private int level;

    public Hero(String name, String description, int iconID, double cost, double baseDps, double totalDps) {
        super();
        this.name = name;
        this.description = description;
        this.iconID = iconID;
        this.cost = cost;
        this.baseDps = baseDps;
        this.totalDps = totalDps;
        this.level = 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public int getIconID() {
        return iconID;
    }

    public double getBaseDps() {
        return baseDps;
    }

    public void setBaseDps(double baseDps) {
        this.baseDps = baseDps;
    }

    public double getTotalDps() {
        return totalDps;
    }

    public void setTotalDps(double totalDps) {
        this.totalDps = totalDps;
    }

    public int getLevel() {
        return level;
    }

    public void incLevel() {
        this.level += 1;
    }
}

