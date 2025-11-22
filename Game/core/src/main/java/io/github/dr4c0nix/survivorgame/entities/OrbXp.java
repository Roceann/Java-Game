package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Orbe d'expérience.
 * taille = ORB_SIZE 
 * 
 * @author Drac0nix
 * @author Roceann (collision, implementation, documentation)
 * @version 1.1
 */
public class OrbXp extends Entity implements Poolable {
    private int xpValue;
    private static final float ORB_SIZE = 12f;

    //ADD LATER AFTER MVP
    // protected Player target;
    // protected boolean isSeeking = false;
    // protected float seekSpeed = 300.0f;

    /*
     * Constructeur de l'orbe d'expérience.
     * @param xpValue la valeur d'expérience que l'orbe confère lorsqu'elle est ramassée
     */
    public OrbXp(int xpValue) {
        super(new Vector2(0, 0), ORB_SIZE, ORB_SIZE, "xporb.png");
        this.xpValue = xpValue;
        this.setAlive(false);
    }

    /*
     * Définit la valeur d'expérience de l'orbe.
     */
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

    /*
     * Obtient la taille de l'orbe.
     */
    public static float getOrbSize() {
        return ORB_SIZE;
    }

    /*
     * Vérifie si l'orbe est active (vivante).
     */
    public void setAlive(boolean v) {
        super.setAlive(v);
    }

    @Override
    public void update(float delta) {}

    /*
     * Réinitialise l'état de l'orbe pour la réutilisation dans le pool.
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