package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.Entity;
import com.badlogic.gdx.math.Rectangle;

public abstract class LivingEntity extends Entity{
    protected int hp;
    protected int maxHp;
    protected int armor;
    protected float force = 1.0f; 
    protected boolean isAlive = true;

    protected float tickDmgChance;
    protected float tickDmgDamage;
    protected float tickDmgDuration;

    //protected TickDmgType tickDmgType

    protected float slowChance;
    protected float slowPower;
    protected float slowDuration;

    public LivingEntity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int hp, 
    int armor, float force) {
        super(spawnPoint, hitboxWidth, hitboxHeight);
        this.hp = hp;
        this.maxHp = hp;
        this.armor = armor;
        this.force = force;
    }

    public void takeDamage(float amount) {
        float damageReduced = amount * (100f / (100f + armor));
        if (!isAlive){
        return;
        }

        if (hp <= 0) {
            hp = 0;
            isAlive = false;
        }
        if (hp <= maxHp && hp >= 0) {
            hp -= (int)damageReduced;
        }
        }

    public void heal(float amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public int getHp() { 
        return hp; 
    }
    public int getMaxHp() { 
        return maxHp; 
    }
    public int getArmor() { 
        return armor; 
    }
    public boolean isAlive() { 
        return isAlive; 
    }
    public float getForce() { 
        return force; 
    }
}
    