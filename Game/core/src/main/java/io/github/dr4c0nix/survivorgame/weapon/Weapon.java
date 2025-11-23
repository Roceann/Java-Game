package io.github.dr4c0nix.survivorgame.weapon;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

/**
 * Weapon de base — garde les stats d'arme, le cooldown et fournit quelques utilitaires.
 */
public abstract class Weapon {

    protected EntityFactory entityFactory;

    protected int level;
    // weapon level for upgrades (1..6)
    protected int weaponLevel = 1;
    protected static final int MAX_WEAPON_LEVEL = 6;
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

    /**
     * Constructeur principal d'une arme.
     *
     * @param factory factory utilisée pour créer les projectiles
     * @param damage dégâts de base de l'arme
     * @param shotDelay délai entre deux tirs (secondes)
     * @param range portée du projectile (pixels)
     * @param projectileSpeed vitesse du projectile (pixels/sec)
     * @param projectileSize multiplicateur de taille du projectile
     * @param projectileBaseWidth largeur de base du projectile
     * @param projectileBaseHeight hauteur de base du projectile
     * @param projectileTexturePath chemin de la texture du projectile
     * @param description description courte de l'arme
     * @param iconPath chemin de l'icône de l'arme
     */
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

    /**
     * Méthode d'update appelée chaque frame par le joueur.
     * Doit gérer le cooldown et la logique de tir.
     *
     * @param delta temps écoulé depuis la dernière frame (secondes)
     * @param player référence vers le joueur utilisant l'arme
     */
    public abstract void update(float delta, Player player);

    /**
     * Indique si l'arme peut tirer (cooldown expiré).
     *
     * @return true si prêt à tirer
     */
    protected boolean canShoot() {
        return cooldown <= 0f;
    }

    /**
     * Remet le cooldown à la valeur effective (tient compte du niveau d'arme).
     */
    protected void resetCooldown() {
        // Use effective shot delay after weapon level modifiers
        this.cooldown = getEffectiveShotDelay();
    }

    /**
     * Décrémente le cooldown en fonction du delta.
     *
     * @param delta temps écoulé depuis la dernière frame (secondes)
     */
    protected void cooldownTick(float delta) {
        if (this.cooldown > 0f) {
            this.cooldown -= delta;
            if (this.cooldown < 0f) this.cooldown = 0f;
        }
    }

    /**
     * Augmente le niveau logique de l'arme (utilisé pour upgrades de l'arme).
     */
    public void levelUp() {
        level += 1;
    }

    /**
     * Retourne le chemin de l'icône de l'arme.
     *
     * @return chemin icône
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Retourne le niveau d'upgrade de l'arme (non le level de la classe).
     *
     * @return niveau d'arme
     */
    public int getLevel() { return level; }

    /**
     * Retourne les dégâts de base.
     *
     * @return dégâts
     */
    public int getDamage() { return damage; }

    /**
     * Retourne le délai de tir de base.
     *
     * @return shot delay (s)
     */
    public float getShotDelay() { return shotDelay; }

    /**
     * Retourne la portée des projectiles de l'arme.
     *
     * @return portée (pixels)
     */
    public int getRange() { return range; }

    /**
     * Retourne la vitesse de projectile.
     *
     * @return vitesse (px/s)
     */
    public float getProjectileSpeed() { return projectileSpeed; }

    /**
     * Retourne le multiplicateur de taille du projectile (non ajusté par level).
     *
     * @return multiplicateur de taille
     */
    public float getProjectileSize() { return projectileSize; }

    /**
     * Retourne la largeur de base du projectile.
     *
     * @return largeur (px)
     */
    public float getProjectileBaseWidth() { return projectileBaseWidth; }

    /**
     * Retourne la hauteur de base du projectile.
     *
     * @return hauteur (px)
     */
    public float getProjectileBaseHeight() { return projectileBaseHeight; }

    /**
     * Retourne le chemin de la texture utilisée pour le projectile.
     *
     * @return chemin texture
     */
    public String getProjectileTexturePath() { return projectileTexturePath; }

    /**
     * Retourne la description courte de l'arme.
     *
     * @return description
     */
    public String getDescription() { return description; }

    /**
     * Retourne le niveau actuel de l'arme (upgrade tier).
     *
     * @return weapon level (1..MAX_WEAPON_LEVEL)
     */
    public int getWeaponLevel() { return weaponLevel; }
    
    /**
     * Calcule la taille effective du projectile en tenant compte du niveau d'arme.
     *
     * @return multiplicateur de taille effectif
     */
    public float getEffectiveProjectileSize() {
        return projectileSize * (1f + 0.20f * (weaponLevel - 1));
    }
    
    /**
     * Calcule les dégâts effectifs suite aux upgrades.
     *
     * @return dégâts arrondis effectifs
     */
    public int getEffectiveDamage() {
        float mul = 1f + 0.20f * (weaponLevel - 1);
        return Math.round(damage * mul);
    }

    /**
     * Calcule le délai effectif entre tirs en tenant compte du niveau.
     *
     * @return shot delay effectif (s)
     */
    public float getEffectiveShotDelay() {
        float factor = 1f - 0.10f * (weaponLevel - 1);
        if (factor < 0.05f) factor = 0.05f;
        return shotDelay * factor;
    }

    /**
     * Définit le niveau d'arme (clampé entre 1 et MAX_WEAPON_LEVEL).
     *
     * @param lvl niveau souhaité
     */
    public void setWeaponLevel(int lvl) {
        weaponLevel = Math.max(1, Math.min(MAX_WEAPON_LEVEL, lvl));
    }
    
    /**
     * Incrémente le niveau d'arme d'un cran (si possible).
     */
    public void increaseWeaponLevel() {
        if (weaponLevel < MAX_WEAPON_LEVEL) weaponLevel++;
    }
}
