package io.github.dr4c0nix.survivorgame.entities;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import io.github.dr4c0nix.survivorgame.entities.enemy.Enemy;

/**
 * Projectile lancé par une arme.
 * Contient direction, vitesse, portée, dégâts et source ; réutilisable via pool.
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

    /**
     * Constructeur : initialise la taille de base et marque le projectile inactif.
     *
     * @param texturePath chemin de texture
     * @param width largeur de base
     * @param height hauteur de base
     */
    public Projectile(String texturePath, float width, float height) {
        super(new Vector2(0, 0), width, height, texturePath);
        this.baseWidth = width;
        this.baseHeight = height;
        this.setAlive(false);
    }

    /**
     * Initialise le projectile prêt à être mis à jour/rendu.
     *
     * @param spawnCenter position centrale d'apparition
     * @param direction direction normalisée de tir (sera normalisée si nécessaire)
     * @param speed vitesse en pixels/sec
     * @param maxRange portée maximale
     * @param damage dégâts infligés
     * @param projectileSize multiplicateur de taille (1.0 = base)
     * @param source entité ayant tiré (peut être null)
     */
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

    /**
     * Met à jour la position du projectile et marque mort si portée dépassée.
     *
     * @param delta temps écoulé depuis la dernière frame (secondes)
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

    /** Réinitialise l'état pour retour au pool. */
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

    /** Retourne les dégâts du projectile. */
    public int getDamage() {
        return damage;
    }

    /** Dessine le projectile avec rotation. */
    @Override
    public void draw(SpriteBatch batch) {
        if (!isAlive()) return;
        batch.draw(getCurrentFrame(), getPosition().x, getPosition().y, getHitbox().width * 0.5f, getHitbox().height * 0.5f, getHitbox().width, getHitbox().height, 1f, 1f, rotationAngle);
    }

    /** Définit les dégâts du projectile. */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /** Retourne la cible du projectile. */
    public Enemy getTarget() {
        return target;
    }

    /** Définit la cible du projectile. */
    public void setTarget(Enemy target) {
        this.target = target;
    }

    /** Retourne la portée maximale du projectile. */
    public float getMaxRange() {
        return maxRange;
    }

    /** Définit la portée maximale du projectile. */
    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    /** Retourne l'entité source du projectile. */
    public LivingEntity getSource() {
        return source;
    }

    /** Définit l'entité source du projectile. */
    public void setSource(LivingEntity source) {
        this.source = source;
    }

    /** Retourne la vitesse du projectile. */
    public float getSpeed() {
        return speed;
    }

    /** Définit la vitesse du projectile. */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /** Retourne la direction du projectile. */
    public Vector2 getDirection() {
        return direction;
    }

    /** Retourne la vélocité du projectile. */
    public Vector2 getVelocity() {
        return velocity;
    }

    /** Retourne la distance parcourue par le projectile. */
    public float getDistanceTraveled() {
        return distanceTraveled;
    }

    /** Définit la distance parcourue par le projectile. */
    public void setDistanceTraveled(float distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    /** Retourne la largeur de base du projectile. */
    public float getBaseWidth() {
        return baseWidth;
    }

    /** Retourne la hauteur de base du projectile. */
    public float getBaseHeight() {
        return baseHeight;
    }

    /** Retourne l'angle de rotation du projectile. */
    public float getRotationAngle() {
        return rotationAngle;
    }

    /** Définit l'angle de rotation du projectile. */
    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}
