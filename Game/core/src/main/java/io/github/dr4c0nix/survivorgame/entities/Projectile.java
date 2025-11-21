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

    protected final Vector2 velocity = new Vector2();
    protected final Vector2 direction = new Vector2();
    protected float distanceTraveled;
    protected float speed;
    protected final float baseWidth;
    protected final float baseHeight;
    protected float rotationAngle;

    public Projectile(String texturePath, float width, float height) {
        super(new Vector2(0, 0), width, height, texturePath);
        this.baseWidth = width;
        this.baseHeight = height;
        this.setAlive(false);
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
        
        Vector2 newPos = new Vector2(spawnCenter.x - halfWidth, spawnCenter.y - halfHeight);
        this.setPosition(newPos);
        this.getHitbox().setSize(scaledWidth, scaledHeight);
        this.setAlive(true);
    }

    /*
     * Met à jour l'état du projectile chaque frame
     * Réduit la durée de vie et vérifie si le projectile doit être détruit
     */
    @Override
    public void update(float delta) {
        if (!isAlive()) return;
        float frameDistance = speed * delta;
        getPosition().mulAdd(velocity, delta);
        getHitbox().setPosition(getPosition().x, getPosition().y);
        distanceTraveled += frameDistance;
        if (distanceTraveled >= maxRange) {
            setAlive(false);
        }
    }

    /*
     * Réinitialise l'état du projectile pour réutilisation dans le pool
     */
    @Override
    public void reset() {
        setDamage(0);
        setTarget(null);
        setMaxRange(0f);
        setSource(null);
        setSpeed(0f);
        setDistanceTraveled(0f);
        velocity.setZero();
        direction.setZero();
        setAlive(false);
        setPosition(new Vector2(0, 0));
        getHitbox().setSize(baseWidth, baseHeight);
        setRotationAngle(0f);
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isAlive()) return;
        batch.draw(getCurrentFrame(), getPosition().x, getPosition().y, getHitbox().width * 0.5f, getHitbox().height * 0.5f, getHitbox().width, getHitbox().height, 1f, 1f, rotationAngle);
    }

    // --- Getters for testing and external access ---

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Enemy getTarget() {
        return target;
    }

    public void setTarget(Enemy target) {
        this.target = target;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    public LivingEntity getSource() {
        return source;
    }

    public void setSource(LivingEntity source) {
        this.source = source;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(float distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public float getBaseWidth() {
        return baseWidth;
    }

    public float getBaseHeight() {
        return baseHeight;
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}
