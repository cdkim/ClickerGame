package com.example.ceon.clickergame;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Created by Ceon on 11/9/16.
 */

public class Player {

    private int money;
    private double click_damage;
    private double follower_damage;
    private List<Hero> ownedHeroes = new ArrayList<Hero>();

    public Player(int money, double click_damage, double follower_damage, List<Hero> ownedHeroes) {
        this.money = money;
        this.click_damage = click_damage;
        this.follower_damage = follower_damage;
        this.ownedHeroes = ownedHeroes;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public double getDamage() {
        return click_damage;
    }

    public void setDamage(double damage) {
        this.click_damage = damage;
    }

    public double getFollowerDPS() {
        return follower_damage;
    }

    public void setFollowerDPS(double follower_damage) {
        this.follower_damage = follower_damage;
    }

    public List<Hero> getOwnedHeroes() {
        return ownedHeroes;
    }

    public void setOwnedHeroes(List<Hero> ownedHeroes) {
        this.ownedHeroes = ownedHeroes;
    }

    public void attackEnemy(Enemy enemy) {
        int enemyHealth = enemy.getHealth();
        enemyHealth-= click_damage;
        enemy.setHealth(enemyHealth);
    }

    public void buyHero(Hero hero) {
        this.money -= hero.getCost();
        hero.incLevel();
        hero.setCost(floor(hero.getCost() * 1.07));
        hero.setTotalDps(floor(hero.getTotalDps() + hero.getBaseDps()));
        this.follower_damage += hero.getBaseDps();
    }

    public void getEnemyGold(Enemy enemy) {
        this.money += enemy.getGold_yield();
    }
}
