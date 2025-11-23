package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Skull : ennemi lambda
 */
public class Skull extends ClassicEnemy {

    private static final String TEX_FRONT = "Entity/Enemy/Skull/skullface.png";
    private static final String TEX_BACK  = "Entity/Enemy/Skull/skulldos.png";
    private static final String TEX_LEFT  = "Entity/Enemy/Skull/skullleft.png";
    private static final String TEX_RIGHT = "Entity/Enemy/Skull/skullright.png";

    private final Vector2 lastPos = new Vector2();

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

    @Override
    public float getXpOrbSize() {
        return 10.5f;
    }
}
