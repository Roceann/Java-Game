package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class OrbXp extends Entity implements Poolable {
    protected int xpValue;

    //ADD LATER AFTER MVP
    // protected Player target;
    // protected boolean isSeeking = false;
    // protected float seekSpeed = 300.0f;


    // ADD orb sprite and logic later
    public OrbXp(int xpValue) {
        super(new Vector2(0, 0), 5, 5);
        this.xpValue = xpValue;
    }

    // ADD LATER AFTER MVP (seeking behavior towards player)
    // public void checkDistanceToTarget() {
    //     // Implementation here
    // }

    @Override
    public void update(float delta) {}

    @Override
    public void reset() {
        xpValue = 0;
        isAlive = false;
        position.set(0, 0);
        // target = null;
        // isSeeking = false;
    }
}
