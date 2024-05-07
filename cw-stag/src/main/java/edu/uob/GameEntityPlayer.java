package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GameEntityPlayer extends GameEntity {
    public static final int MAXHEALTH = 3;
    private int health;
    public GameEntityPlayer(String name) {
        super(name, "A player with name '" + name + "'");
        health = MAXHEALTH;
    }

    public void setHealth(int health) {
        if (health > MAXHEALTH) {
            this.health = MAXHEALTH;
        }
        else if (health < 0) {
            this.health = 0;
        }
        else {
            this.health = health;
        }
    }

    public int getHealth() {
        return health;
    }
}
