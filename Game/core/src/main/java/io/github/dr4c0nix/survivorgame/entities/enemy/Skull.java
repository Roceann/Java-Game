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

    private Texture frontTexture;
    private Texture backTexture;
    private Texture leftTexture;
    private Texture rightTexture;

    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction currentDirection = Direction.DOWN;

    private final Vector2 lastPos = new Vector2();

    public Skull() {
        super(
            new Vector2(0f, 0f),
            22, 28,          // small hitbox
            10,              // xp drop
            60f,             // HP
            0,               // armor
            2.0f,            // force (faible dégâts)
            TEX_FRONT,
            new Texture(Gdx.files.internal(TEX_FRONT)),
            null,
            65f              // vitesse moyenne
        );

        this.isAlive = false;
        this.hp = 60f;
        this.maxHp = 60f;
        this.armor = 0;
        this.force = 2.0f;

        frontTexture = new Texture(Gdx.files.internal(TEX_FRONT));
        backTexture  = new Texture(Gdx.files.internal(TEX_BACK));
        leftTexture  = new Texture(Gdx.files.internal(TEX_LEFT));
        rightTexture = new Texture(Gdx.files.internal(TEX_RIGHT));

        this.walkingTexture = frontTexture;
        lastPos.set(position);
    }

    @Override
    public void update(float delta) {
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
