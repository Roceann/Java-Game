package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Orbe d'expérience.
 * taille = ORB_SIZE = 12 pixels par défaut
 * 
 */
public class OrbXp extends Entity implements Poolable {
    private int xpValue;
    private float orbSize;
    private static final float DEFAULT_ORB_SIZE = 12f;

    //ADD LATER AFTER MVP
    // protected Player target;
    // protected boolean isSeeking = false;
    // protected float seekSpeed = 300.0f;

    /*
     * Constructeur de l'orbe d'expérience.
     * @param xpValue la valeur d'expérience que l'orbe confère lorsqu'elle est ramassée
     */
    public OrbXp(int xpValue) {
        this(xpValue, DEFAULT_ORB_SIZE);
    }

    /**
     * Crée une orbe avec valeur et taille spécifiées.
     *
     * @param xpValue valeur d'expérience
     * @param size taille visuelle (pixels)
     */
    public OrbXp(int xpValue, float size) {
        super(new Vector2(0, 0), size, size, "xporb.png");
        this.xpValue = xpValue;
        this.orbSize = size;
        this.setAlive(false);
    }

    /** Définit la valeur d'XP. */
    public void setXpValue(int xpValue) {
        this.xpValue = xpValue;
    }

    // ADD LATER AFTER MVP (seeking behavior towards player)
    // public void checkDistanceToTarget() {
    //     // Implementation here
    // }

    /*
     * Obtient la valeur d'expérience de l'orbe.
     */
    public int getXpValue() {
        return this.xpValue;
    }

    /** Retourne la taille de l'orbe. */
    public float getOrbSize() {
        return this.orbSize;
    }

    /** Taille par défaut. */
    public static float getDefaultOrbSize() {
        return DEFAULT_ORB_SIZE;
    }

    /** Définit la taille et met à jour la hitbox si existante. */
    public void setSize(float size) {
        this.orbSize = size;
        if (getHitbox() != null) {
            getHitbox().setSize(size, size);
        }
    }

    /** Active/désactive l'orbe. */
    public void setAlive(boolean v) {
        super.setAlive(v);
    }

    /** Dessine l'orbe si active. */
    @Override
    public void update(float delta) {}

    /**
     * Réinitialise l'orbe pour retour au pool : valeur à 0, non active et position remise.
     */
    @Override
    public void reset() {
        setXpValue(0);
        setAlive(false);
        setPosition(new Vector2(0, 0));
        // target = null;
        // isSeeking = false;
    }
}