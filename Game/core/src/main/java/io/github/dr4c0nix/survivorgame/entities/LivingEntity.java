package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
    protected int hp;
    protected int maxHp;
    protected int armor;
    protected float force = 1.0f;
    protected boolean isAlive = true;

    // protected float tickDmgChance;
    // protected float tickDmgDamage;
    // protected float tickDmgDuration;
    // protected TickDmgType tickDmgType

    protected Texture walkingTexture;
    protected float slowChance;
    protected float slowPower;
    protected float slowDuration;
    private float immunityTimer = 0f;
    private static final float immun_time = 0.2f; 

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
     * @param walkingTexture texture utilisée pour l'animation de marche
     */
    public LivingEntity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int hp,
        int armor, float force, String texturePath, Texture walkingTexture) {
        super(spawnPoint, hitboxWidth, hitboxHeight, texturePath);
        this.hp = hp;
        this.maxHp = hp;
        this.armor = armor;
        this.force = force;
        this.walkingTexture = walkingTexture;
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
        if (immunityTimer > 0) {
            return;
        }

        if (!isAlive) {
            return;
        }
        float effectiveDamage = amount * (100f / (100f + armor));
        // Assure au moins 1 dégât si un coup passe
        int damage = Math.max(1, Math.round(effectiveDamage));
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            isAlive = false;
        }
        immunityTimer = immun_time;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isAlive) return;
        
        if (immunityTimer > 0) {
            if (Math.sin(immunityTimer * 20) > 0) {
                batch.setColor(0f, 0f, 0f, 1f); // blanc 
            } else {
                batch.setColor(1f, 1f, 1f, 1f); // Normal 
            }
        } else {
            batch.setColor(Color.WHITE);
        }
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
        if (!isAlive) return;
        position.add(dx * movementSpeed, dy * movementSpeed);
        hitbox.setPosition(position.x, position.y);
    }


    /** Retourne les points de vie actuels. */
    public int getHp() {
        return hp;
    }

    /** Retourne les points de vie maximum. */
    public int getMaxHp() {
        return maxHp;
    }

    /** Retourne la valeur d'armure. */
    public int getArmor() {
        return armor;
    }

    /** Indique si l'entité est en vie. */
    public boolean isAlive() {
        return isAlive;
    }

    /** Retourne la force de l'entité. */
    public float getForce() {
        return force;
    }
}