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
import com.badlogic.gdx.graphics.Color; 
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import io.github.dr4c0nix.survivorgame.weapon.Weapon;

/**
 * Représente le joueur contrôlable.
 * Contient les statistiques, gestion d'input, animation, arme et progression (XP/level).
 */
public class Player extends LivingEntity {
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
    private enum Direction {up, down, left, right}
    private static final float feetHeight = 10f;

    /**
     * Constructeur du Player.
     *
     * Initialise les stats de base, les valeurs de progression et les frames d'animation.
     *
     * @param spawnPoint point d'apparition (coordonnées x,y)
     */
    public Player(Vector2 spawnPoint) {
        super(spawnPoint, 32, 32 * 1.3f, 100f, 10, 1.0f, "Entity/Player/static1.png");
        this.description = "Le Romz";
        this.level = 1;
        this.movementSpeed = 3f;
        this.experienceToNextLevel = 100;
        this.xpactual = 0;
        this.regenHP = 5f; // 5hp toutes les 10 secondes
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
     * Teste si le joueur peut se déplacer d'un pas (dx,dy) en vérifiant uniquement la zone des pieds.
     *
     * @param dx déplacement normalisé sur X (-1,0,1)
     * @param dy déplacement normalisé sur Y (-1,0,1)
     * @return true si la case pieds n'est pas en collision
     */
    private boolean canMoveTo(float dx, float dy) {
        if (gameplay == null) return true;
        float newX = this.position.x + dx * this.movementSpeed;
        float newY = this.position.y + dy * this.movementSpeed;
        Rectangle feet = new Rectangle(newX, newY, this.hitbox.width, feetHeight);
        return !gameplay.isColliding(feet);
    }

    /**
     * Lit l'entrée clavier et déplace le joueur (Z/S/Q/D ou les touches configurées).
     * Met à jour la direction courante et le flag isMoving.
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
     * Met à jour l'animation du joueur selon isMoving et la direction.
     * Protège contre l'absence de textures (mode test).
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
     * Demande l'affichage de l'écran de montée de niveau via Gameplay.
     * Ne fait rien si gameplay est null.
     */
    public void levelUp(){
        if (gameplay != null) {
            gameplay.showLevelUpScreen();
        }
    }

    /**
     * Définit la référence vers Gameplay (utilisé par l'écran pour lier le joueur).
     *
     * @param gameplay instance Gameplay à associer
     */
    public void setGameplay(Gameplay gameplay) {
        this.gameplay = gameplay;
    }

    /**
     * Mise à jour par frame : input, animation, arme, régénération et immunité.
     *
     * @param delta temps écoulé depuis la dernière frame (en secondes)
     */
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

    /**
     * Dessine le joueur (animation + clignotement en cas de prise de dégâts).
     *
     * @param batch SpriteBatch utilisé pour le rendu
     */
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

    /**
     * Retourne l'expérience courante du joueur.
     *
     * @return expérience actuelle
     */
    public int getXpactual() {
        return this.xpactual;
    }

    /**
     * Retourne l'expérience requise pour le prochain niveau.
     *
     * @return seuil d'expérience suivant
     */
    public int getXpRequiredForNextLevel() {
        return this.experienceToNextLevel;
    }

    /**
     * Ajoute de l'expérience et gère la montée de niveau (levelUp).
     * Si le seuil est atteint, incrémente level, appelle levelUp() et ajuste l'XP restant.
     *
     * @param value quantité d'expérience à ajouter
     */
    public void addXp(int value) {
        this.xpactual += value;
        if (this.xpactual >= this.experienceToNextLevel) {
            this.level += 1;
            levelUp();
            this.xpactual -= this.experienceToNextLevel;
            this.experienceToNextLevel = (int) (this.experienceToNextLevel * 1.15f);
        }
    }

    /**
     * Applique des dégâts au joueur (hérite de LivingEntity.takeDamage).
     * Si le joueur meurt, notifie Gameplay via onGameOver().
     *
     * @param amount montant de dégâts
     */
    @Override
    public void takeDamage(float amount) {
        if (!isAlive) return;
        super.takeDamage(amount);
        if (!isAlive && gameplay != null) {
            gameplay.onGameOver();
        }
    }

    /**
     * Définit l'arme courante du joueur.
     *
     * @param weapon arme à équiper (peut être null)
     */
    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }
    
    /**
     * Retourne l'arme équipée (ou null).
     *
     * @return arme actuelle
     */
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
    
    /**
     * Indique si le joueur possède une arme équipée.
     *
     * @return true si une arme est équipée
     */
    public boolean hasWeapon() {
        return currentWeapon != null;
    }

    /**
     * Retourne un vecteur directionnel pointant vers la direction courante.
     *
     * @return vecteur normalisé de direction
     */
    public Vector2 getFacingDirection() {
        switch (currentDirection) {
            case up: return new Vector2(0f, 1f);
            case down: return new Vector2(0f, -1f);
            case left: return new Vector2(-1f, 0f);
            case right: return new Vector2(1f, 0f);
            default: return new Vector2(0f, -1f);
        }
    }

    /**
     * Retourne le niveau courant.
     *
     * @return niveau du joueur
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Retourne l'expérience requise pour le prochain niveau.
     *
     * @return expérience nécessaire
     */
    public int getExperienceToNextLevel() {
        return this.experienceToNextLevel;
    }

    /**
     * Retourne la quantité de HP régénérée tous les REGEN_INTERVAL.
     *
     * @return points de vie régénérés
     */
    public float getRegenHP() {
        return this.regenHP;
    }

    /**
     * Retourne la chance de coup critique (en pourcentage).
     *
     * @return chance critique
     */
    public float getCritChance() {
        return this.critChance;
    }

    /**
     * Retourne la difficulté appliquée au joueur (facteur).
     *
     * @return difficulté
     */
    public float getDifficulter() {
        return this.difficulter;
    }

    /**
     * Retourne le multiplicateur de dégâts critiques.
     *
     * @return dégâts critique
     */
    public float getCritDamage() {
        return this.critDamage;
    }

    /**
     * Retourne la description textuelle du joueur.
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Définit le HP maximum du joueur.
     *
     * @param maxHp nouvelle valeur max HP
     */
    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    /**
     * Définit l'armure du joueur.
     *
     * @param armor nouvelle armure
     */
    public void setArmor(int armor) {
        this.armor = armor;
    }

    /**
     * Définit la force (dégâts) du joueur.
     *
     * @param force nouveau multiplicateur de force
     */
    public void setForce(float force) {
        this.force = force;
    }

    /**
     * Définit la difficulté (facteur) du joueur.
     *
     * @param difficulter nouveau facteur de difficulté
     */
    public void setDifficulter(float difficulter) {
        this.difficulter = difficulter;
    }

    /**
     * Définit la valeur de régénération HP (par tick).
     *
     * @param regenHP points restaurés tous les REGEN_INTERVAL
     */
    public void setRegenHP(float regenHP) {
        this.regenHP = regenHP;
    }

    /**
     * Définit la chance de critique.
     *
     * @param critChance pourcentage de critique
     */
    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    /**
     * Définit le multiplicateur de dégâts critiques.
     *
     * @param critDamage multiplicateur critique
     */
    public void setCritDamage(float critDamage) {
        this.critDamage = critDamage;
    }

    /**
     * Définit les PV courants du joueur.
     *
     * @param amount nouvelle valeur de HP courant
     */
    public void setCurrentHp(float amount){
        this.hp = amount;
    }

    /**
     * Active ou désactive les attaques du joueur.
     *
     * @param enabled true pour autoriser les attaques
     */
    public void setAttacksEnabled(boolean enabled) {
        this.attacksEnabled = enabled;
    }

    /**
     * Indique si les attaques sont activées.
     *
     * @return true si les attaques sont autorisées
     */
    public boolean areAttacksEnabled() {
        return attacksEnabled;
    }

    /**
     * Incrémente le compteur de monstres tués.
     */
    public void incrementMobKilled() {
        this.mobKilled++;
    }

    /**
     * Retourne le nombre de monstres tués.
     *
     * @return compteur de mobs tués
     */
    public int getMobKilled() {
        return this.mobKilled;
    }
    
    /**
     * Gère la régénération périodique de HP.
     * Toutes les REGEN_INTERVAL secondes, restaure regenHP (clampé à maxHp).
     *
     * @param delta temps écoulé depuis la dernière frame (en secondes)
     */
    private void tickRegen(float delta) {
        if (!isAlive) return;
        regenTimer += delta;
        if (regenTimer >= REGEN_INTERVAL) {
            regenTimer -= REGEN_INTERVAL;
            this.hp = Math.min(this.maxHp, this.hp + regenHP);
        }
    }
}