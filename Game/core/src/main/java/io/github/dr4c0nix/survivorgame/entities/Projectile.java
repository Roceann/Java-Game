package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Projectile extends Entity implements Poolable {
    protected float damage;
    protected float lifespan;
    protected Enemy target;
    protected int maxRange;
    protected LivingEntity source;

    // ADD projectiles sprite and logic later
    public Projectile(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float damage, float lifespan, Enemy target, LivingEntity source) {
        super(spawnPoint, hitboxWidth, hitboxHeight, "");  // ADD TEXTURE PATH LATER
        this.damage = damage;
        this.lifespan = lifespan;
        this.target = target;
        this.maxRange = 100; //Change value later
        this.source = source;
    }

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        lifespan -= delta;
        if (lifespan <= 0) {
                isAlive = false;
                return;
        }
    }

    @Override
    public void reset() {
        damage = 0;
        lifespan = 0;
        target = null;
        maxRange = 0;
        source = null;
        isAlive = false;
        position.set(0, 0);
    }
}
