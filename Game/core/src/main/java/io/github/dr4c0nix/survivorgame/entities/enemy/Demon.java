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
    private Texture frontTexture;
    private Texture backTexture;
    private Texture leftTexture;
    private Texture rightTexture;

    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction currentDirection = Direction.DOWN;

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
            new Texture(Gdx.files.internal(TEX_FRONT)),
            null,
            100f             
        );

        this.isAlive = false;
        this.hp = 80f;
        this.maxHp = 80f;
        this.armor = 0;
        this.force = 3.0f;

        frontTexture = new Texture(Gdx.files.internal(TEX_FRONT));
        backTexture  = new Texture(Gdx.files.internal(TEX_BACK));
        leftTexture  = new Texture(Gdx.files.internal(TEX_LEFT));
        rightTexture = new Texture(Gdx.files.internal(TEX_RIGHT));

        this.walkingTexture = frontTexture;
        lastPos.set(position);
    }

    @Override
    public void update(float delta) {
        // IA / pathfinding / collisions gérés par ClassicEnemy
        super.update(delta);

        Vector2 deltaPos = new Vector2(position).sub(lastPos);

        if (!deltaPos.isZero()) {
            if (Math.abs(deltaPos.x) > Math.abs(deltaPos.y)) {
                currentDirection = (deltaPos.x > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                currentDirection = (deltaPos.y > 0) ? Direction.UP : Direction.DOWN;
            }
        }

        lastPos.set(position);

        switch (currentDirection) {
            case UP:
                walkingTexture = backTexture;
                break;
            case DOWN:
                walkingTexture = frontTexture;
                break;
            case LEFT:
                walkingTexture = leftTexture;
                break;
            case RIGHT:
                walkingTexture = rightTexture;
                break;
        }
    }
}
