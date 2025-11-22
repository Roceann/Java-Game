package io.github.dr4c0nix.survivorgame.entities.player;

import java.util.ArrayList;
import java.util.List;
import io.github.dr4c0nix.survivorgame.entities.LivingEntity;
import io.github.dr4c0nix.survivorgame.GameOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color; // Nécessaire pour l'effet visuel
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Nécessaire pour draw
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import io.github.dr4c0nix.survivorgame.weapon.Weapon;

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
    protected float regenHP;
    protected float critChance;
    protected float critDamage;
    protected float difficulter;
    protected String description;
    protected Weapon currentWeapon;
    protected Gameplay gameplay;
    protected int mobKilled;

    private TextureRegion[] staticFrames;
    private TextureRegion[] downFrames;
    private TextureRegion[] upFrames;
    private TextureRegion[] rightFrames;
    private TextureRegion[] leftFrames;
    private float animationTimer = 0f;
    private static final float staticFrameDuration = 0.5f;
    private static final float walkFrameDuration = 0.3f;
    private Direction currentDirection = Direction.down;
    private boolean isMoving = false;
    private boolean attacksEnabled = false;
    // Regen HP every X seconds
    private float regenTimer = 0f;
    private static final float REGEN_INTERVAL = 10f; // seconds
    private static final float REGEN_AMOUNT = 5f; // hp per interval
    private enum Direction {up, down, left, right}
    private static final float feetHeight = 10f;

    /**
     * Constructeur du Player.
     *
     * Initialise les stats de base (hp, armure, force, etc...),les valeurs de progression et les frames d'animations.
     *
     * @param spawnPoint point d'apparition (coordonnées x,y)
     * @param baseHp points de vie de base (max et courant)
     * @param baseArmor armure de base
     * @param baseForce multiplicateur de dégâts de base
     * @param texturePath chemin vers la texture principale (fichier)
     * @param walkingTexture texture de marche / animation
     * @param description description textuelle du joueur
     */
    public Player(Vector2 spawnPoint) {
        super(spawnPoint, 32, 32 * 1.3f, 100f, 10, 1.0f, "Entity/Player/static1.png");
        this.description = "Le Romz";
        this.level = 1;
        this.movementSpeed = 5f;
        this.experienceToNextLevel = 100;
        this.xpactual = 0;
        this.regenHP = 0.01f; // 1%
        this.critChance = 50.0f;
        this.difficulter = 1.0f;
        this.critDamage = 1.5f;
        this.currentWeapon = null;
        this.mobKilled = 0;
        
        // On vérifie si le contexte graphique existe avant de charger les textures.
        // Cela empêche le crash dans les tests unitaires.
        if (Gdx.graphics != null) {
            Texture static1 = new Texture(Gdx.files.internal("Entity/Player/static1.png"));
            Texture static2 = new Texture(Gdx.files.internal("Entity/Player/static2.png"));
            Texture dwalk1 = new Texture(Gdx.files.internal("Entity/Player/dwalk1.png"));
            Texture dwalk2 = new Texture(Gdx.files.internal("Entity/Player/dwalk2.png"));
            Texture uwalk1 = new Texture(Gdx.files.internal("Entity/Player/uwalk1.png"));
            Texture uwalk2 = new Texture(Gdx.files.internal("Entity/Player/uwalk2.png"));
            Texture rwalk1 = new Texture(Gdx.files.internal("Entity/Player/rwalk1.png"));
            Texture rwalk2 = new Texture(Gdx.files.internal("Entity/Player/rwalk2.png"));
            Texture lwalk1 = new Texture(Gdx.files.internal("Entity/Player/lwalk1.png"));
            Texture lwalk2 = new Texture(Gdx.files.internal("Entity/Player/lwalk2.png"));
            
            staticFrames = new TextureRegion[] {
                new TextureRegion(static1),
                new TextureRegion(static2)
            };
            downFrames = new TextureRegion[] {
                new TextureRegion(dwalk1),
                new TextureRegion(dwalk2)
            };
            upFrames = new TextureRegion[] {
                new TextureRegion(uwalk1),
                new TextureRegion(uwalk2)
            };
            rightFrames = new TextureRegion[] {
                new TextureRegion(rwalk1),
                new TextureRegion(rwalk2)
            };
            leftFrames = new TextureRegion[] {
                new TextureRegion(lwalk1),
                new TextureRegion(lwalk2)
            };

            currentFrame = staticFrames[0];
        }
    }

    /**
     * Teste si le joueur peut se déplacer de (dx,dy) en testant uniquement les pieds.
     */
    private boolean canMoveTo(float dx, float dy) {
        if (gameplay == null) return true;
        float newX = this.position.x + dx * this.movementSpeed;
        float newY = this.position.y + dy * this.movementSpeed;
        Rectangle feet = new Rectangle(newX, newY, this.hitbox.width, feetHeight);
        return !gameplay.isColliding(feet);
    }

    /**
     * Gère l'entrée utilisateur pour déplacer le joueur.
     *
     * Utilise Z/S/Q/D pour monter/descendre/gauche/droite et appelle moveBy.
     */
    public void handleInput() {
        GameOptions options = GameOptions.getInstance();
        isMoving = false;
        
        if (Gdx.input.isKeyPressed(options.getKeyUp())) {
            if (canMoveTo(0, 1)) {
                moveBy(0, 1);
            }
            currentDirection = Direction.up;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(options.getKeyDown())) {
            if (canMoveTo(0, -1)) {
                moveBy(0, -1);
            }
            currentDirection = Direction.down;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(options.getKeyLeft())) {
            if (canMoveTo(-1, 0)) {
                moveBy(-1, 0);
            }
            currentDirection = Direction.left;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(options.getKeyRight())) {
            if (canMoveTo(1, 0)) {
                moveBy(1, 0);
            }
            currentDirection = Direction.right;
            isMoving = true;
        }

        if (!isMoving) {
            currentDirection = Direction.down;
        }
    }

    /**
     * permet de gérer l'animation de déplacement
     */
    public void animation() {
        // Protection pour ne pas animer si les textures ne sont pas chargées (mode test)
        if (staticFrames == null) return;

        animationTimer += Gdx.graphics.getDeltaTime();

        if (!isMoving) {
            int frameIndex = (int)(animationTimer / staticFrameDuration) % 2;
            currentFrame = staticFrames[frameIndex];
        } else {
            int frameIndex = (int)(animationTimer / walkFrameDuration) % 2;
            switch (currentDirection) {
                case up:
                    currentFrame = upFrames[frameIndex];
                    break;
                case down:
                    currentFrame = downFrames[frameIndex];
                    break;
                case left:
                    currentFrame = leftFrames[frameIndex];
                    break;
                case right:
                    currentFrame = rightFrames[frameIndex];
                    break;
            }
        }
    }

    /**
     * Demande à Gameplay d'afficher l'overlay de montée de niveau.
     * Gameplay centralise l'affichage / gestion d'input et la pause.
     */
    public void levelUp(){
        if (gameplay != null) {
            gameplay.showLevelUpScreen();
        }
    }

    /**
     * Stocke la référence Gameplay (appelé depuis Gameplay lors de l'initialisation).
     */
    public void setGameplay(Gameplay gameplay) {
        this.gameplay = gameplay;
    }

    @Override
    public void update(float delta) {
        handleInput(); 
        animation();  

        if (currentWeapon != null && attacksEnabled) {
            currentWeapon.update(delta, this);
        }
        
        // HP regen tick
        tickRegen(delta);
        
        tickImmunity(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isAlive) return;
        
        if (immunityTimer > 0) {
            if (Math.sin(immunityTimer * 20) > 0) {
                batch.setColor(1f, 0f, 0f, 1f); // Rouge 
            } else {
                batch.setColor(1f, 1f, 1f, 1f); // Normal 
            }
        } else {
            batch.setColor(Color.WHITE);
        }

        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y, hitbox.width, hitbox.height);
        }
        
        batch.setColor(Color.WHITE);
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

    @Override
    public void takeDamage(float amount) {
        if (!isAlive) return;
        super.takeDamage(amount);
        if (!isAlive && gameplay != null) {
            gameplay.onGameOver();
        }
    }

    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }
    
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
    
    public boolean hasWeapon() {
        return currentWeapon != null;
    }

    public Vector2 getFacingDirection() {
        switch (currentDirection) {
            case up: return new Vector2(0f, 1f);
            case down: return new Vector2(0f, -1f);
            case left: return new Vector2(-1f, 0f);
            case right: return new Vector2(1f, 0f);
            default: return new Vector2(0f, -1f);
        }
    }

    public int getLevel() {
        return this.level;
    }

    public int getExperienceToNextLevel() {
        return this.experienceToNextLevel;
    }

    public float getRegenHP() {
        return this.regenHP;
    }

    public float getCritChance() {
        return this.critChance;
    }

    public float getDifficulter() {
        return this.difficulter;
    }

    public float getCritDamage() {
        return this.critDamage;
    }

    public String getDescription() {
        return this.description;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public void setRegenHP(float regenHP) {
        this.regenHP = regenHP;
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public void setCritDamage(float critDamage) {
        this.critDamage = critDamage;
    }

    public void setCUrrentHp(float amount){
        this.hp = amount;
    }

    public void setAttacksEnabled(boolean enabled) {
        this.attacksEnabled = enabled;
    }

    public boolean areAttacksEnabled() {
        return attacksEnabled;
    }

    public void incrementMobKilled() {
        this.mobKilled++;
    }

    public int getMobKilled() {
        return this.mobKilled;
    }
    
    /**
     * Tick la régénération périodique : toutes les REGEN_INTERVAL secondes,
     * restaure REGEN_AMOUNT HP (clampé à maxHp).
     */
    private void tickRegen(float delta) {
        if (!isAlive) return;
        regenTimer += delta;
        if (regenTimer >= REGEN_INTERVAL) {
            regenTimer -= REGEN_INTERVAL;
            this.hp = Math.min(this.maxHp, this.hp + REGEN_AMOUNT);
        }
    }
}