package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;

/*  
 * Factory pour créer et gérer les entités du jeu avec des pools d'objets
 * Permet de réutiliser les instances d'entités pour optimiser les performances
 * Gère les orbes XP (pour l'instant)
 */
public class EntityFactory {
    private AssetManager assetManager;
    private Pool<OrbXp> orbXpPool;
    private Pool<Projectile> swordProjectilePool;
    private ArrayList<OrbXp> activeOrbs;
    private ArrayList<OrbXp> createdOrbs;
    private ArrayList<Projectile> activeProjectiles;
    private ArrayList<Projectile> createdProjectiles;
    private String swordProjectileTexturePath;
    private float swordProjectileWidth;
    private float swordProjectileHeight;

    private static final int INITIAL_POOL_SIZE = 50;
    private static final int MAX_POOL_SIZE = 500;
    private static final int INITIAL_PROJECTILE_POOL_SIZE = 32;
    private static final int MAX_PROJECTILE_POOL_SIZE = 256;

    /**
     * Constructeur de la factory avec initialisation des pools
     */
    public EntityFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
        initializePools();
    }

    public EntityFactory() {
        this.assetManager = null;
        initializePools();
    }
    /**
     * Initialise tous les pools avec des capacités appropriées
     */
    private void initializePools() {
        this.activeOrbs = new ArrayList<>();
        this.createdOrbs = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();
        this.createdProjectiles = new ArrayList<>();
        this.orbXpPool = new Pool<OrbXp>(INITIAL_POOL_SIZE, MAX_POOL_SIZE) {
            @Override
            protected OrbXp newObject() {
                OrbXp orb = new OrbXp(0);
                createdOrbs.add(orb);
                return orb;
            }
        };
    }

    private void ensureSwordProjectilePool(String texturePath, float width, float height) {
        if (swordProjectilePool != null) {
            return;
        }
        this.swordProjectileTexturePath = texturePath;
        this.swordProjectileWidth = width;
        this.swordProjectileHeight = height;
        this.swordProjectilePool = new Pool<Projectile>(INITIAL_PROJECTILE_POOL_SIZE, MAX_PROJECTILE_POOL_SIZE) {
            @Override
            protected Projectile newObject() {
                Projectile projectile = new Projectile(swordProjectileTexturePath, swordProjectileWidth, swordProjectileHeight);
                createdProjectiles.add(projectile);
                return projectile;
            }
        };
    }

    /**
     * Obtient une orbe XP du pool, l'initialise et l'ajoute à la liste active
     */
    public OrbXp obtainOrbXp(Vector2 position, int xpValue) {
        OrbXp orb = orbXpPool.obtain();
        orb.setPosition(position);
        orb.setXpValue(xpValue);
        orb.setAlive(true);
        activeOrbs.add(orb);
        return orb;
    }

    /**
     * Libère une orbe XP dans le pool et l'enlève de la liste active
     */
    public void releaseOrbXp(OrbXp orb) {
        if (orb == null) {
            return;
        }
        activeOrbs.remove(orb);
        orb.reset();
        orbXpPool.free(orb);
    }

    /**
     * Dessine toutes les orbes actives
     */
    public void drawActiveOrbs(SpriteBatch batch) {
        for (OrbXp orb : activeOrbs) {
            orb.draw(batch);
        }
    }

    public Projectile obtainSwordProjectile(Vector2 position, Vector2 direction, float speed, float range, float damage, float projectileSize, float projectileBaseWidth, float projectileBaseHeight, String texturePath, LivingEntity source) {
        ensureSwordProjectilePool(texturePath, projectileBaseWidth, projectileBaseHeight);
        Projectile projectile = swordProjectilePool.obtain();
        projectile.init(new Vector2(position), new Vector2(direction), speed, range, damage, projectileSize, source);
        activeProjectiles.add(projectile);
        return projectile;
    }

    public void updateProjectiles(float delta) {
        for (int i = activeProjectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = activeProjectiles.get(i);
            projectile.update(delta);
            if (!projectile.getIsAlive()) {
                removeProjectileAt(i);
            }
        }
    }

    public void drawActiveProjectiles(SpriteBatch batch) {
        for (Projectile projectile : activeProjectiles) {
            projectile.draw(batch);
        }
    }

    public void releaseProjectile(Projectile projectile) {
        if (projectile == null) return;
        int index = activeProjectiles.indexOf(projectile);
        if (index >= 0) {
            removeProjectileAt(index);
        } else {
            projectile.reset();
            swordProjectilePool.free(projectile);
        }
    }

    private void removeProjectileAt(int index) {
        Projectile projectile = activeProjectiles.remove(index);
        projectile.reset();
        swordProjectilePool.free(projectile);
    }

    public ArrayList<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    /**
     * VIde le pool et dispose les ressources créées (textures des orbes)
     */
    public void dispose() {
        for (OrbXp orb : new ArrayList<>(activeOrbs)) {
            try {
                orb.setAlive(false);
                releaseOrbXp(orb);
            } catch (Exception e) {
                Gdx.app.error("EntityFactory", "Error disposing active orb: " + e.getMessage());
            }
        }
        for (OrbXp orb : createdOrbs) {
            try {
                orb.dispose();
            } catch (Exception e) {
                Gdx.app.error("EntityFactory", "Error disposing created orb: " + e.getMessage());
            }
        }
        createdOrbs.clear();
        orbXpPool.clear();
        activeOrbs.clear();

        for (Projectile projectile : new ArrayList<>(activeProjectiles)) {
            try {
                releaseProjectile(projectile);
            } catch (Exception e) {
                Gdx.app.error("EntityFactory", "Error disposing active projectile: " + e.getMessage());
            }
        }
        for (Projectile projectile : createdProjectiles) {
            try {
                projectile.dispose();
            } catch (Exception e) {
                Gdx.app.error("EntityFactory", "Error disposing created projectile: " + e.getMessage());
            }
        }
        createdProjectiles.clear();
        if (swordProjectilePool != null) {
            swordProjectilePool.clear();
            swordProjectilePool = null;
        }
    }

    public Pool<OrbXp> getOrbXpPool() {
        return orbXpPool;
    }

    public ArrayList<OrbXp> getActiveOrbs() {
        return activeOrbs;
    }
}
