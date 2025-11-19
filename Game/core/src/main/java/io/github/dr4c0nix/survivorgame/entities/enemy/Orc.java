package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;

/**
 * Orc: exemple concret de ClassicEnemy avec constructeur sans-arg.
 * - lent (movementspeed faible)
 * - beaucoup de vie (hp élevé)
 * - armure modérée
 * - peu de dégâts (force faible)
 */
public class Orc extends ClassicEnemy {

    private static final String texture_pincipale = "personages/Jhonny/Jhonny-boss/Jhonny-boss.png";

    /**
     * Constructeur sans argument pour la pool.
     * L'EntityFactory appellera setGameplay(...) et activate(...) après obtention.
     */
    public Orc() {
        super(new Vector2(0f, 0f),
                28, 36,
                12, 160, 8, 4f,
                texture_pincipale,
                new Texture(Gdx.files.internal(texture_pincipale)),
                null,
                60f);

        this.isAlive = false;;
        this.hp = 160;
        this.maxHp = 160;
        this.armor = 8;
        this.force = 4f;
        this.walkingTexture = new Texture(Gdx.files.internal(texture_pincipale)); 
    }
}
