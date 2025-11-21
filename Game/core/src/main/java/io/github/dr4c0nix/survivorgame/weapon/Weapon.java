package io.github.dr4c0nix.survivorgame.weapon;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

/**
 * Weapon de base — garde les stats d'arme, le cooldown et fournit quelques utilitaires.
 */
public abstract class Weapon {

    protected EntityFactory entityFactory;

    protected int level;
    protected int damage;
    protected float shotDelay;
    protected float cooldown;
    protected int range; // portée du projectile (l'arme n'a pas de portée propre car elle tire des projectiles)
    protected float projectileSpeed; // vitesse du projectile
    protected float projectileSize; // scale factor pour la taille du projectile (1 -> 32x8)
    protected float projectileBaseWidth;
    protected float projectileBaseHeight;
    protected String projectileTexturePath;
    protected String description;
    protected String iconPath;

    public Weapon(EntityFactory factory, int damage, float shotDelay, int range, float projectileSpeed, float projectileSize, float projectileBaseWidth, float projectileBaseHeight, String projectileTexturePath, String description, String iconPath) {
        this.level = 1;
        this.entityFactory = factory;
        this.damage = damage;
        this.shotDelay = shotDelay;
        this.cooldown = 0f;
        this.range = range;
        this.projectileSpeed = projectileSpeed;
        this.projectileSize = projectileSize;
        this.projectileBaseWidth = projectileBaseWidth;
        this.projectileBaseHeight = projectileBaseHeight;
        this.projectileTexturePath = projectileTexturePath;
        this.description = description;
        this.iconPath = iconPath;
    }

    public abstract void update(float delta, Player player);

    protected boolean canShoot() {
        return cooldown <= 0f;
    }

    protected void resetCooldown() {
        this.cooldown = this.shotDelay;
    }

    protected void cooldownTick(float delta) {
        if (this.cooldown > 0f) {
            this.cooldown -= delta;
            if (this.cooldown < 0f) this.cooldown = 0f;
        }
    }

    public void levelUp() {
        level += 1;
    }

    public String getIconPath() {
        return iconPath;
    }

    public int getLevel() { return level; }
    public int getDamage() { return damage; }
    public float getShotDelay() { return shotDelay; }
    public int getRange() { return range; }
    public float getProjectileSpeed() { return projectileSpeed; }
    public float getProjectileSize() { return projectileSize; }
    public float getProjectileBaseWidth() { return projectileBaseWidth; }
    public float getProjectileBaseHeight() { return projectileBaseHeight; }
    public String getProjectileTexturePath() { return projectileTexturePath; }
    public String getDescription() { return description; }
}
