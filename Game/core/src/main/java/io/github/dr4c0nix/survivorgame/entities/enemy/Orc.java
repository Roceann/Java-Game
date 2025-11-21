package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Orc: ennemi lent, tanky, peu de dégâts.
 */
public class Orc extends ClassicEnemy {

    private static final String TEX_FRONT = "Entity/Enemy/Orc/orcface.png";
    private static final String TEX_BACK  = "Entity/Enemy/Orc/orcdos.png";
    private static final String TEX_LEFT  = "Entity/Enemy/Orc/orcleft.png";
    private static final String TEX_RIGHT = "Entity/Enemy/Orc/orcright.png";

    private final Vector2 lastPos = new Vector2();

    public Orc() {
        super(new Vector2(0f, 0f),
                28, 36,
                12, 160f, 0, 4f,
                TEX_FRONT,
                null,
                60f);

        this.isAlive = false;
        this.hp = 160f;
        this.maxHp = 160f;
        this.armor = 0;
        this.force = 4f;

        lastPos.set(position);
    }
}
