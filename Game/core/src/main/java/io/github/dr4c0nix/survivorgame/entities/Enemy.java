package io.github.dr4c0nix.survivorgame.entities;

import java.util.Vector;

import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends LivingEntity {
    protected float xpDrop;


 public Enemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float xpDrop, int hp, int armor, float force) {
        super(spawnPoint, hitboxWidth, hitboxHeight, hp, armor, force);
        this.xpDrop = xpDrop;
    }

    public void update (float delta){
        if (!target.isAlive) return;

    };

    public void reset(){


    };

}