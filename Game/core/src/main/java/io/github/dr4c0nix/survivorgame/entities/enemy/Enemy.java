package io.github.dr4c0nix.survivorgame.entities.enemy;

// import java.util.Vector;
import com.badlogic.gdx.math.Vector2;

import io.github.dr4c0nix.survivorgame.entities.LivingEntity;
import io.github.dr4c0nix.survivorgame.entities.OrbXp;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

import com.badlogic.gdx.graphics.Texture;

public abstract class Enemy extends LivingEntity {
    protected OrbXp xpDrop;

    public Enemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop,
        int hp, int armor, float force, String texturePath, Texture walkingTexture) {
        super(spawnPoint, hitboxWidth, hitboxHeight, hp, armor, force, texturePath, walkingTexture);
        this.xpDrop = new OrbXp(xpDrop);
    }

    public int getXpValue() {
        return xpDrop.getXpValue();
    }

    // Surcharge utilitaire: par défaut, délègue à update(delta)
    public void update(float delta, Player player) {
    }
}