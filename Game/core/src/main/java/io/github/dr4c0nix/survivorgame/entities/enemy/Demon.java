package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Demon: ennemi très rapide mais fragile.
 * - HP faibles
 * - Peu ou pas d'armure
 * - Vitesse de déplacement élevée
 */
public class Demon extends ClassicEnemy {

    private static final String TEX_FRONT = "Entity/Enemy/DemonSimple/demonface.png";
    private static final String TEX_BACK  = "Entity/Enemy/DemonSimple/demondos.png";
    private static final String TEX_LEFT  = "Entity/Enemy/DemonSimple/demonleft.png";
    private static final String TEX_RIGHT = "Entity/Enemy/DemonSimple/demonright.png";

    private final Vector2 lastPos = new Vector2();

    public Demon() {
        super(
            new Vector2(0f, 0f),
            24, 32,          
            15,              
            80f,             
            0,               
            3.0f,            
            TEX_FRONT,
            null,
            100f             
        );

        this.isAlive = false;
        this.hp = 80f;
        this.maxHp = 80f;
        this.armor = 0;
        this.force = 3.0f;

        lastPos.set(position);
        
        if (this.texture != null) {
            this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
        }
    }

    @Override
    public float getXpOrbSize() {
        return 7.0f;
    }
}