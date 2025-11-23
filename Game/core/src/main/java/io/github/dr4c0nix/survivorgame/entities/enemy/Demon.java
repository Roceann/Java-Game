package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Vector2;

/**
 * Demon: ennemi très rapide mais fragile.
 */
public class Demon extends ClassicEnemy {

    private static final String TEX_FRONT = "Entity/Enemy/DemonSimple/demonface.png";

    private final Vector2 lastPos = new Vector2();

    /**
     * Constructeur : initialise les statistiques et la hitbox si la texture est chargée.
     */
    public Demon() {
        super(
            new Vector2(0f, 0f),
            24, 32,          
            25,
            50f,
            0,
            1.2f,
            TEX_FRONT,
            null,
            100f
        );

        // état initial
        this.isAlive = false;
        this.hp = 50f;
        this.maxHp = 50f;
        this.armor = 0;
        this.force = 1.2f;

        lastPos.set(position);

        if (this.texture != null) {
            this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
        }
    }

    /**
     * Taille de l'orbe d'XP lâchée par le Demon.
     *
     * @return taille en pixels
     */
    @Override
    public float getXpOrbSize() {
        return 7.0f;
    }
}