package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GameEntityPlayer extends GameEntity {
    private static final int maxHealth = 3;
    private int health;
    public GameEntityPlayer(String name) {
        super(name, "A player");
        health = maxHealth;
    }

    public void setHealth(int health) {
        if (health > maxHealth) {
            this.health = maxHealth;
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
