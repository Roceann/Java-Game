package io.github.dr4c0nix.survivorgame.entities.player;

import io.github.dr4c0nix.survivorgame.entities.LivingEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe abstraite représentant un joueur dans le jeu.
 *
 * @author Roceann
 * @version 1.0
 */
public class Player extends LivingEntity {
    private int xpactual;
    private int level;
    private int experienceToNextLevel;
    private float nbprojectil;
    private float chance;
    private float regenHP;
    private float lifeSteal;
    private float critChance;
    private int pickUpRange;
    private float difficulter;
    private float critDamage;
    private float durationEffect;
    private String description;
    // protected List<Weapon> weapon;

    public Player(int baseHp, int baseArmor, float baseForce, String texturePath, Texture walkingTexture,
            String description) {
        super(new Vector2(0, 0), 0, 0, baseHp, baseArmor, baseForce, texturePath, walkingTexture);
        this.description = description;
        this.level = 1;
        this.experienceToNextLevel = 100;
        this.xpactual = 0;
        this.nbprojectil = 1.0f;
        this.chance = 0.0f;
        this.regenHP = 0.0f;
        this.lifeSteal = 0.0f;
        this.critChance = 0.0f;
        this.pickUpRange = 15;
        this.difficulter = 1.0f;
        this.critDamage = 1.5f;
        this.durationEffect = 1.0f;
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            moveBy(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveBy(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            moveBy(-1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveBy(1, 0);
        }
    }

    @Override
    public void update(float delta) {
        // TODO Auto-generated method stub
        handleInput();
    }

    public int getXpactual() {
        return this.xpactual;
    }

    public void addXp(int value) {
        this.xpactual += value;
        if (this.xpactual >= this.experienceToNextLevel) {
            this.level += 1;
            this.xpactual -= this.experienceToNextLevel;
            this.experienceToNextLevel = (int) (this.experienceToNextLevel * 1.25);
        }
    }

    public int getLevel() {
        return this.level;
    }

    public int getExperienceToNextLevel() {
        return this.experienceToNextLevel;
    }

    public float getNbprojectil() {
        return this.nbprojectil;
    }

    public float getChance() {
        return this.chance;
    }

    public float getRegenHP() {
        return this.regenHP;
    }

    public float getLifeSteal() {
        return this.lifeSteal;
    }

    public float getCritChance() {
        return this.critChance;
    }

    public int getPickUpRange() {
        return this.pickUpRange;
    }

    public float getDifficulter() {
        return this.difficulter;
    }

    public float getCritDamage() {
        return this.critDamage;
    }

    public float getDurationEffect() {
        return this.durationEffect;
    }

    public String getDescription() {
        return this.description;
    }
}
