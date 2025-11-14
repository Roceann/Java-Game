package io.github.dr4c0nix.survivorgame.weapons;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;


public abstract class Weapon {

    protected EntityFactory factory;

    protected int level = 1;
    protected float shotDelay = 0.0f;

    protected float damage;
    protected float cadence;
    protected float critChance;
    protected float projectileSpeed;
    protected float projectileCount;
    protected int range;
    protected float critDamage;
    protected float duration;
    protected int pierce;
    protected String description;
    protected EffectType effectType = EffectType.NONE;

    public Weapon(EntityFactory factory) {
        this.factory = factory;
    }

    public abstract void update(float delta, Player owner);

    public void levelUp() {
        level += 1;
    }

}
