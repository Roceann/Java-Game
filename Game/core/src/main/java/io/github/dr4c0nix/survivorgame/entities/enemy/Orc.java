package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Vector2;

/**
 * Orc: ennemi lent, résistant (tank), avec des dégâts moyens.
 */
public class Orc extends ClassicEnemy {

    private static final String TEX_FRONT = "Entity/Enemy/Orc/orcface.png";

    private final Vector2 lastPos = new Vector2();

    /**
     * Constructeur : initialise les propriétés par défaut de l'Orc.
     */
    public Orc() {
        super(new Vector2(0f, 0f),
                28, 36,
                27,
                120f,
                1,
                6f,
                TEX_FRONT,
                null,
                65f);

        // état initial
        this.isAlive = false;
        this.hp = 120f;
        this.maxHp = 120f;
        this.armor = 1;
        this.force = 6f;

        lastPos.set(position);

        if (this.texture != null) {
            this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
        }
    }

    /**
     * Taille de l'orbe d'XP lâchée par l'Orc.
     *
     * @return taille en pixels
     */
    @Override
    public float getXpOrbSize() {
        return 8.5f;
    }
}
