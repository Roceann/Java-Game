package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Vector2;

/**
 * Skull : ennemi lambda, équilibré.
 */
public class Skull extends ClassicEnemy {

    private static final String TEX_FRONT = "Entity/Enemy/Skull/skullface.png";

    private final Vector2 lastPos = new Vector2();

    /**
     * Constructeur : initialise les stats par défaut et la hitbox si la texture est chargée.
     */
    public Skull() {
        super(
            new Vector2(0f, 0f),
            22, 28,
            30,
            200f,
            4,
            3.0f,
            TEX_FRONT,
            null,
            60f
        );

        // état initial
        this.isAlive = false;
        this.hp = 200f;
        this.maxHp = 200f;
        this.armor = 4;
        this.force = 3.0f;

        lastPos.set(position);

        if (this.texture != null) {
            this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
        }
    }

    /**
     * Taille de l'orbe d'XP lâchée par le Skull.
     *
     * @return taille en pixels
     */
    @Override
    public float getXpOrbSize() {
        return 10.5f;
    }
}
