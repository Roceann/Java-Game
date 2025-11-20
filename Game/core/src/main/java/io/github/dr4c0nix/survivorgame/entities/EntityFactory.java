package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import io.github.dr4c0nix.survivorgame.entities.enemy.ClassicEnemy;
import io.github.dr4c0nix.survivorgame.entities.enemy.Orc; 
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Factory pour créer et gérer les entités du jeu avec des pools d'objets
 * Gère les orbes XP et les ennemis "ClassicEnemy" (et ses sous-classes).
 */
public class EntityFactory {
    private Gameplay gameplay; 

    private Pool<OrbXp> orbXpPool;
    private final ArrayList<OrbXp> activeOrbs = new ArrayList<>();
    private final ArrayList<OrbXp> createdOrbs = new ArrayList<>();

    private final ArrayList<ClassicEnemy> activeEnemies = new ArrayList<>();
    private final ArrayList<ClassicEnemy> createdEnemies = new ArrayList<>();
    private Pool<Projectile> swordProjectilePool;
    private final ArrayList<Projectile> activeProjectiles = new ArrayList<>();
    private final ArrayList<Projectile> createdProjectiles = new ArrayList<>();
    private String swordProjectileTexturePath;
    private float swordProjectileWidth;
    private float swordProjectileHeight;
    
    // pools par type (ajoute manuellement chaque pool dans initializePools)
    private final Map<String, Pool<ClassicEnemy>> enemyPools = new HashMap<>();
    private final Map<ClassicEnemy, String> instanceToType = new HashMap<>();
    private final Map<String, Vector2> enemyHitboxSizes = new HashMap<>();
    
    private static final int INITIAL_POOL_SIZE = 64;
    private static final int MAX_POOL_SIZE = 512;
    private static final int INITIAL_PROJECTILE_POOL_SIZE = 32;
    private static final int MAX_PROJECTILE_POOL_SIZE = 256;
    private static final String CLASSIC_TEXTURE = "personages/Jhonny/Jhonny-boss/Jhonny-boss.png";
    private Texture classicEnemyTexture; 

    public EntityFactory(Gameplay gameplay) {
        this.gameplay = gameplay;
        initializePools();
    }

    private void initializePools() {
        if (classicEnemyTexture == null) {
            classicEnemyTexture = new Texture(Gdx.files.internal(CLASSIC_TEXTURE));
        }

        // Pool Orbs
        this.orbXpPool = new Pool<OrbXp>(INITIAL_POOL_SIZE, MAX_POOL_SIZE) {
            @Override
            protected OrbXp newObject() {
                OrbXp orb = new OrbXp(0);
                createdOrbs.add(orb);
                return orb;
            }
        };

        // --- Ajouter manuellement une pool pas enfant de classic enemy (ex avec orc factice) ---
        Pool<ClassicEnemy> orcPoolLocal = new Pool<ClassicEnemy>(INITIAL_POOL_SIZE, MAX_POOL_SIZE) {
            @Override
            protected ClassicEnemy newObject() {
                ClassicEnemy e = new Orc(); 
                createdEnemies.add(e);
                registerEnemyPrototype("Orc", e);
                return e;
            }
        };
        enemyPools.put("Orc", orcPoolLocal);

        // Pré-crée un prototype pour chaque pool afin d'avoir la taille d'ennemi dispo
    }


    /**
     * Obtient un ennemi du type donné et le place à la position.
     * Le type doit être ajouté manuellement dans initializePools.
     */
    public ClassicEnemy obtainEnemy(String type, Vector2 position) {
        if (type == null) return obtainEnemy(position); 

        Pool<ClassicEnemy> pool = enemyPools.get(type);

        ClassicEnemy enemy = pool.obtain();
        if (!activeEnemies.contains(enemy)) {
            activeEnemies.add(enemy);
        }
        instanceToType.put(enemy, type);

        enemy.setGameplay(this.gameplay);
        enemy.activate(new Vector2(position));
        return enemy;
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
     * Old fallback (compatible) : si aucun type précisé, on utilise le premier type enregistré.
     * Retourne null si aucun type enregistré.
     */
    public ClassicEnemy obtainEnemy(Vector2 position) {
        String firstType = enemyPools.keySet().iterator().next();
        return obtainEnemy(firstType, position);
    }

    /**
     * Release global — on retrouve la pool via instanceToType.
     */
    public void releaseEnemy(ClassicEnemy enemy) {
        if (enemy == null) return;
        activeEnemies.remove(enemy);
        String type = instanceToType.remove(enemy);
        if (type == null) {
            for (Pool<ClassicEnemy> p : enemyPools.values()) {
                try { 
                    p.free(enemy); 
                    break; 
                } catch (Exception e) {
                    Gdx.app.log("EntityFactory", "Enemy type not found in pool during release. error message : " + e.getMessage());
                }
            }
            return;
        }
        Pool<ClassicEnemy> pool = enemyPools.get(type);
        if (pool != null) pool.free(enemy);
    }

    // Orbes XP
    public OrbXp obtainOrbXp(Vector2 position, int xpValue) {
        OrbXp orb = orbXpPool.obtain();
        if (!activeOrbs.contains(orb)) {
            activeOrbs.add(orb);
        }
        orb.setXpValue(xpValue);
        orb.setAlive(true);
        orb.setPosition(new Vector2(position)); // place l’orbe
        return orb;
    }

    public void releaseOrbXp(OrbXp orb) {
        if (orb == null) return;
        activeOrbs.remove(orb);
        orbXpPool.free(orb);
    }

    public void drawActiveOrbs(SpriteBatch batch) {
        for (int i = 0; i < activeOrbs.size(); i++) {
            OrbXp orb = activeOrbs.get(i);
            if (orb.getIsAlive()) {
                orb.draw(batch);
            } 
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

        if (classicEnemyTexture != null) {
            classicEnemyTexture.dispose();
        }

        for (Pool<ClassicEnemy> pool : enemyPools.values()) {
            pool.clear();
        }
        enemyPools.clear();
        for (ClassicEnemy enemy : createdEnemies) {
            enemy.dispose();
        }
        createdEnemies.clear();
        enemyHitboxSizes.clear();
    }

    public Pool<OrbXp> getOrbXpPool() {
        return orbXpPool;
    }

    public ArrayList<OrbXp> getActiveOrbs() {
        return activeOrbs;
    }

    public void drawActiveEnemies(SpriteBatch batch) {
        for (int i = 0; i < activeEnemies.size(); i++) {
            ClassicEnemy e = activeEnemies.get(i);
            if (e.isAlive()) {
                e.draw(batch);
            }
        }
    }

    public ArrayList<ClassicEnemy> getActiveEnemies() {
        return activeEnemies;
    }

    private void registerEnemyPrototype(String type, ClassicEnemy enemy) {
        if (type == null || enemy == null || enemy.getHitbox() == null) return;
        enemyHitboxSizes.put(type, new Vector2(enemy.getHitbox().width, enemy.getHitbox().height));
    }

    public ArrayList<String> getAvailableEnemyTypes() {
        return new ArrayList<>(enemyPools.keySet());
    }

    public Vector2 getEnemyHitboxSize(String type) {
        Vector2 size = enemyHitboxSizes.get(type);
        return size == null ? null : new Vector2(size);
    }
}
