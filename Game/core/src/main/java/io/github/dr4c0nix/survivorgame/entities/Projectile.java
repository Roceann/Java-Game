package io.github.dr4c0nix.survivorgame.entities;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import io.github.dr4c0nix.survivorgame.entities.enemy.Enemy;
/*
 * Classe représentant un projectile lancé par une arme
 * Contient les informations sur les dégâts, la durée de vie, la cible, etc.
 * Implémente Poolable pour réutilisation via un pool d'objets
 * 
 * @author Dr4c0nix, Abdelkader1900
 * @version 1.0
 */
public class Projectile extends Entity implements Poolable {
    protected int damage;
    protected Enemy target;
    protected float maxRange;
    protected LivingEntity source;

    private final Vector2 velocity = new Vector2();
    private final Vector2 direction = new Vector2();
    private float distanceTraveled;
    private float speed;
    private final float baseWidth;
    private final float baseHeight;
    private float rotationAngle;

    public Projectile(String texturePath, float width, float height) {
        super(new Vector2(0, 0), width, height, texturePath);
        this.baseWidth = width;
        this.baseHeight = height;
        this.isAlive = false;
    }

    public void init(Vector2 spawnCenter, Vector2 direction, float speed, float maxRange, int damage, float projectileSize, LivingEntity source) {
        this.direction.set(direction).nor();
        if (this.direction.isZero(0.0001f)) {
            this.direction.set(0f, -1f);
        }
        this.speed = speed;
        this.velocity.set(this.direction).scl(speed);
        this.maxRange = maxRange;
        this.damage = damage;
        this.source = source;
        this.distanceTraveled = 0f;
        this.rotationAngle = (this.direction.angleDeg() + 90f) % 360f;
        float scaledWidth = baseWidth * projectileSize;
        float scaledHeight = baseHeight * projectileSize;
        float halfWidth = scaledWidth * 0.5f;
        float halfHeight = scaledHeight * 0.5f;
        this.position.set(spawnCenter.x - halfWidth, spawnCenter.y - halfHeight);
        this.hitbox.setSize(scaledWidth, scaledHeight);
        this.hitbox.setPosition(position.x, position.y);
        this.isAlive = true;
    }

    /*
     * Met à jour l'état du projectile chaque frame
     * Réduit la durée de vie et vérifie si le projectile doit être détruit
     */
    @Override
    public void update(float delta) {
        if (!isAlive) return;
        float frameDistance = speed * delta;
        position.mulAdd(velocity, delta);
        hitbox.setPosition(position.x, position.y);
        distanceTraveled += frameDistance;
        if (distanceTraveled >= maxRange) {
            isAlive = false;
        }
    }

    /*
     * Réinitialise l'état du projectile pour réutilisation dans le pool
     */
    @Override
    public void reset() {
        damage = 0;
        target = null;
        maxRange = 0f;
        source = null;
        speed = 0f;
        distanceTraveled = 0f;
        velocity.setZero();
        direction.setZero();
        isAlive = false;
        position.set(0, 0);
        hitbox.setPosition(0, 0);
        hitbox.setSize(baseWidth, baseHeight);
        rotationAngle = 0f;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isAlive) return;
        batch.draw(currentFrame, position.x, position.y, hitbox.width * 0.5f, hitbox.height * 0.5f, hitbox.width, hitbox.height, 1f, 1f, rotationAngle);
    }
}
