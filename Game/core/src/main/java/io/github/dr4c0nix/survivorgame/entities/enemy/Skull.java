package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Skull : ennemi lambda
 * - HP faibles
 * - Armure nulle
 * - Dégâts faibles
 * - Vitesse moyenne
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
            10,
            60f,
            0,
            2.0f,
            TEX_FRONT,
            null,
            65f
        );

        this.isAlive = false;
        this.hp = 60f;
        this.maxHp = 60f;
        this.armor = 0;
        this.force = 2.0f;

        lastPos.set(position);
        
        if (this.texture != null) {
            this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
        }
    }
}
