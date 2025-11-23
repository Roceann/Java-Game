package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

/**
 * Classe abstraite représentant une entité vivante du jeu (joueur, ennemi).
 * <p>
 * Cette classe étend {@link Entity} et ajoute des propriétés liées à la
 * survie et au combat : points de vie (hp), armure, force (multiplicateur de dégâts), 
 * vitesse de déplacement par défaut, ainsi que des attributs pour les états
 * (ralentissement, textures de marche, etc.).
 *
 * Elle fournit des méthodes utilitaires communes : infliger des dégâts
 * ({@link #takeDamage(float)}), déplacer l'entité ({@link #moveBy(float, float)})
 * et getters pour consulter l'état.
 *
 */
public abstract class LivingEntity extends Entity {
    protected float hp;
    protected float maxHp;
    protected int armor;
    protected float force = 1.0f;
    protected float slowChance;
    protected float slowPower;
    protected float slowDuration;
    protected float immunityTimer = 0f;
    private static final float IMMUNITY_TIME = 0.2f;

    /**
     * Constructeur : initialise PV, armure et force.
     *
     * @param spawnPoint point d'apparition
     * @param hitboxWidth largeur de la hitbox
     * @param hitboxHeight hauteur de la hitbox
     * @param hp points de vie initiaux (et max)
     * @param armor valeur d'armure
     * @param force valeur de dégâts
     * @param texturePath chemin de texture
     */
    public LivingEntity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float hp, int armor, float force, String texturePath) {
        super(spawnPoint, hitboxWidth, hitboxHeight, texturePath);
        this.hp = hp;
        this.maxHp = hp;
        this.armor = armor;
        this.force = force;
    }

    /**
     * Applique des dégâts en tenant compte de l'armure.
     * Ignore l'appel si l'entité est morte ou en période d'immunité.
     *
     * @param amount dégâts bruts entrants
     */
    public void takeDamage(float amount) {
        if (immunityTimer > 0 || !isAlive()) return;
        float effectiveDamage = amount * (100f / (100f + armor));
        float damage = Math.max(1f, effectiveDamage);
        hp -= damage;
        if (hp <= 0f) {
            hp = 0f;
            setAlive(false);
        }
        immunityTimer = IMMUNITY_TIME;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isAlive()) return;
        
        if (immunityTimer > 0) {
            if ((int)(immunityTimer * 20) > 0 ) {
                batch.setColor(Color.RED);
            } else {
                batch.setColor(1, 1, 1, 1);
            }
        } else {
            batch.setColor(Color.WHITE);
        }

        super.draw(batch);

        batch.setColor(Color.WHITE);
    }

    /**
     * Déplace l'entité en ajoutant (dx, dy) à sa position.
     * <p>
     * Les valeurs dx et dy peuvent être négatives. La translation est
     * multipliée par la propriété {@code movementSpeed} pour respecter la
     * vitesse de l'entité.
     *
     * Exemple : {@code moveBy(1, 0)} avance de {@code movementSpeed} vers la droite.
     *
     * @param dx déplacement en X (peut être négatif)
     * @param dy déplacement en Y (peut être négatif)
     */
    protected void moveBy(float dx, float dy) {
        if (!isAlive()) return;
        position.add(dx * movementSpeed, dy * movementSpeed);
        hitbox.setPosition(position.x, position.y);
    }


    /** Retourne les points de vie actuels. */
    public float getHp() {
        return hp;
    }

    /** Retourne les points de vie maximum. */
    public float getMaxHp() {
        return maxHp;
    }

    /** Retourne la valeur d'armure. */
    public int getArmor() {
        return armor;
    }

    /** Retourne la force de l'entité. */
    public float getForce() {
        return force;
    }

    /** Définit le HP max et ajuste hp courant si besoin. */
    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
        this.hp = Math.min(this.hp, this.maxHp);
    }

    /** Définit les PV courants (clamp à max). */
    public void setCurrentHp(float amount){
        this.hp = amount;
        this.hp = Math.min(this.hp, this.maxHp);
    }

    /** Définit l'armure. */
    public void setArmor(int armor) {
        this.armor = armor;
    }

    /** Définit la force. */
    public void setForce(float force) {
        this.force = force;
    }

    /** Retourne le timer d'immunité restant. */
    public float getImmunityTimer() {
        return immunityTimer;
    }

    /**
     * Décrémente le timer d'immunité (appelé chaque frame).
     *
     * @param delta temps écoulé depuis la dernière frame (secondes)
     */
    protected void tickImmunity(float delta) {
        if (immunityTimer <= 0f) return;
        immunityTimer -= delta;
        if (immunityTimer < 0f) {
            immunityTimer = 0f;
        }
    }
}