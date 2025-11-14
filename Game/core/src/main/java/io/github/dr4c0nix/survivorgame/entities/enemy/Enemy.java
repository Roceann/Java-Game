package io.github.dr4c0nix.survivorgame.entities.enemy;

// import java.util.Vector;
import com.badlogic.gdx.math.Vector2;

import io.github.dr4c0nix.survivorgame.entities.LivingEntity;

import com.badlogic.gdx.graphics.Texture;

public abstract class Enemy extends LivingEntity {
    protected float xpDrop;


    public Enemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float xpDrop,
        int hp, int armor, float force, String texturePath, Texture walkingTexture) {
        super(spawnPoint, hitboxWidth, hitboxHeight, hp, armor, force, texturePath, walkingTexture);
        this.xpDrop = xpDrop;
    }

    public void update (float delta){
        
    };

    public void reset(){


    };

}