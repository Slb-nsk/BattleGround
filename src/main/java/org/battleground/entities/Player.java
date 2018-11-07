package org.battleground.entities;

public class Player {
    private String name;
    private int rating, lifeCapasity, damage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getLifeCapasity() {
        return lifeCapasity;
    }

    public void setLifeCapasity(int lifeCapasity) {
        this.lifeCapasity = lifeCapasity;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Player() {
    }

    public Player(String name, int rating, int damage, int lifeCapasity) {
        this.name = name;
        this.rating = rating;
        this.damage = damage;
        this.lifeCapasity = lifeCapasity;

    }
}
