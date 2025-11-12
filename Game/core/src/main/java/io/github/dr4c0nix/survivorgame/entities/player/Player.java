package io.github.dr4c0nix.survivorgame.entities.player;

import io.github.dr4c0nix.survivorgame.entities.LivingEntity;
import io.github.dr4c0nix.survivorgame.GameOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.screens.LevelUp;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;

/**
 * Classe abstraite représentant un joueur dans le jeu.
 *
 * @author Roceann
 * @version 1.0
 */
public abstract class Player extends LivingEntity {
    protected int xpactual;
    protected int level;
    protected int experienceToNextLevel;
    protected float nbprojectil;
    protected float chance;
    protected float regenHP;
    protected float lifeSteal;
    protected float critChance;
    protected int pickUpRange;
    protected float difficulter;
    protected float critDamage;
    protected float durationEffect;
    protected String description;
    // protected List<Weapon> weapon;
    protected LevelUp levelUpOverlay;
    /**
     * Constructeur du Player.
     *
     * Initialise les stats de base (hp, armure, force, etc.) et les valeurs de progression.
     *
     * @param baseHp points de vie de base (max et courant)
     * @param baseArmor armure de base
     * @param baseForce multiplicateur de dégâts de base
     * @param texturePath chemin vers la texture principale (transmis à la superclasse)
     * @param walkingTexture texture de marche / animation
     * @param description description textuelle du joueur
     */
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

    /**
     * Gère l'entrée utilisateur pour déplacer le joueur.
     *
     * Utilise Z/S/Q/D pour monter/descendre/gauche/droite et appelle moveBy.
     */
    public void handleInput() {
        GameOptions options = GameOptions.getInstance();
        
        if (Gdx.input.isKeyPressed(options.getKeyUp())) {
            moveBy(0, 1);
        }
        if (Gdx.input.isKeyPressed(options.getKeyDown())) {
            moveBy(0, -1);
        }
        if (Gdx.input.isKeyPressed(options.getKeyLeft())) {
            moveBy(-1, 0);
        }
        if (Gdx.input.isKeyPressed(options.getKeyRight())) {
            moveBy(1, 0);
        }
    }

    /**
     * permet de gérer l'animation de déplacement
     */
    public abstract void animation() ;

    /**
     * Ouvre l'overlay de montée de niveau pré-configuré dans levelUp.
     *
     * Doit être précédé par setGameplay(...) pour que levelUpOverlay soit initialisé.
     */
    public void levelUp(){
        levelUpOverlay.show();
    }

    /**
     * Initialise le LevelUp overlay depuis Gameplay.
     * Appeler ceci depuis Gameplay (ou tout code d'initialisation) avant d'appeler levelUp().
     */
    public void setGameplay(Gameplay gameplay) {
        this.levelUpOverlay = new LevelUp(gameplay);
    }

    /**
     * Mise à jour par frame du Player.
     *
     * Appelé depuis la boucle de rendu. Pour l'instant, ne fait que gérer l'entrée.
     */
    @Override
    public void update(float delta) {
        handleInput();
    }

    public int getXpactual() {
        return this.xpactual;
    }

    /**
     * Ajoute de l'expérience au joueur et gère la montée de niveau.
     *
     * Si xp >= experienceToNextLevel, augmente le level, appelle levelUp(),
     * décrémente l'expérience utilisée et augmente le seuil suivant.
     *
     */
    public void addXp(int value) {
        this.xpactual += value;
        if (this.xpactual >= this.experienceToNextLevel) {
            this.level += 1;
            levelUp();
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

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public void setNbProjectil(float nbprojectil) {
        this.nbprojectil = nbprojectil;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setRegenHP(float regenHP) {
        this.regenHP = regenHP;
    }

    public void setLifeSteal(float lifeSteal) {
        this.lifeSteal = lifeSteal;
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public void setPickUpRange(int pickUpRange) {
        this.pickUpRange = pickUpRange;
    }

    public void setCritDamage(float critDamage) {
        this.critDamage = critDamage;
    }

    public void setDurationEffect(float durationEffect) {
        this.durationEffect = durationEffect;
    }
}
