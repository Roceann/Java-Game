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
 * @author Abdelkader1900
 * @author Roceann (relecture, corrections et documentation)
 * @version 1.0
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
     * Crée une nouvelle entité vivante.
     *
     * @param spawnPoint     point d'apparition (coordonnées x,y)
     * @param hitboxWidth    largeur de la hitbox
     * @param hitboxHeight   hauteur de la hitbox
     * @param hp             points de vie initiaux (et maximum)
     * @param armor          valeur d'armure qui réduit les dégâts
     * @param force          force appliquée par l'entité (impact)
     * @param texturePath    chemin vers la texture principale (fichier)
     */
    public LivingEntity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float hp, int armor, float force, String texturePath) {
        super(spawnPoint, hitboxWidth, hitboxHeight, texturePath);
        this.hp = hp;
        this.maxHp = hp;
        this.armor = armor;
        this.force = force;
    }

    /**
     * Inflige des dégâts à l'entité en tenant compte de son armure.
     * <p>
     * La réduction est calculée par la formule :
     * damageReduced = amount * (100 / (100 + armor)).
     * Si l'entité est déjà morte (isAlive == false), l'appel est ignoré.
     * Si les PV passent à 0 ou moins, l'entité est marquée comme morte
     * (isAlive = false) et les PV sont plafonnés à 0.
     *
     * @param amount montant brut des dégâts à appliquer
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
            // Effet de clignotement simple et efficace
            if ((int)(immunityTimer * 30) % 2 == 0) {
                batch.setColor(Color.DARK_GRAY);
            } else {
                batch.setColor(Color.WHITE);
            }
        } else {
            batch.setColor(Color.WHITE);
        }

        super.draw(batch);

        // Réinitialiser la couleur à la fin est crucial pour ne pas affecter les autres dessins
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

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
        this.hp = Math.min(this.hp, this.maxHp);
    }

    public void setCurrentHp(float amount){
        this.hp = amount;
        this.hp = Math.min(this.hp, this.maxHp);
    }


    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public float getImmunityTimer() {
        return immunityTimer;
    }

    protected void tickImmunity(float delta) {
        if (immunityTimer <= 0f) return;
        immunityTimer -= delta;
        if (immunityTimer < 0f) {
            immunityTimer = 0f;
        }
    }
}