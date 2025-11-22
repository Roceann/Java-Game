package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;

/**
 * Refactorisé : mêmes comportements, moins de duplication.
 */
public abstract class ClassicEnemy extends Enemy implements Poolable {
    
    public ClassicEnemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop, float hp, int armor, float force, String texturePath, Gameplay gameplay, float movementSpeed) {
        super(spawnPoint, hitboxWidth, hitboxHeight, xpDrop, hp, armor, force, texturePath);
        this.gameplay = gameplay;
        setMovementSpeed(movementSpeed);
    }

    /**
     * Activate enemy (used by the factory/pool).
     * Reinitialises state and places it at spawn.
     */
    public void activate(Vector2 spawnPoint) {
        this.setPosition(spawnPoint);
        this.setAlive(true);
        this.setCurrentHp(this.getMaxHp());
    }

    @Override
    public void reset() {
        this.setCurrentHp(this.getMaxHp());
        this.setAlive(false);
        this.setPosition(new Vector2(0f, 0f));
    }
}