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
    private AssetManager assetManager;
    private Gameplay gameplay; 

    private Pool<OrbXp> orbXpPool;
    private final ArrayList<OrbXp> activeOrbs = new ArrayList<>();
    private final ArrayList<OrbXp> createdOrbs = new ArrayList<>();

    private final ArrayList<ClassicEnemy> activeEnemies = new ArrayList<>();
    private final ArrayList<ClassicEnemy> createdEnemies = new ArrayList<>();

    // pools par type (ajoute manuellement chaque pool dans initializePools)
    private final Map<String, Pool<ClassicEnemy>> enemyPools = new HashMap<>();
    private final Map<ClassicEnemy, String> instanceToType = new HashMap<>();
    private final Map<String, Vector2> enemyHitboxSizes = new HashMap<>();

    private static final int INITIAL_POOL_SIZE = 64;
    private static final int MAX_POOL_SIZE = 512;

    private static final String CLASSIC_TEXTURE = "personages/Jhonny/Jhonny-boss/Jhonny-boss.png";

    private Texture classicEnemyTexture; 

    public EntityFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
        initializePools();
    }

    public EntityFactory(Gameplay gameplay) {
        this.gameplay = gameplay;
        this.assetManager = null;
        initializePools();
    }

    public EntityFactory(AssetManager assetManager, Gameplay gameplay) {
        this.assetManager = assetManager;
        this.gameplay = gameplay;
        initializePools();
    }

    private void initializePools() {
        if (classicEnemyTexture == null) {
            classicEnemyTexture = new Texture(Gdx.files.internal(CLASSIC_TEXTURE));
        }

        // Pool Orbs
        orbXpPool = new Pool<OrbXp>(INITIAL_POOL_SIZE, MAX_POOL_SIZE) {
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
    }

    /**
     * Obtient un ennemi du type donné et le place à la position.
     * Le type doit être ajouté manuellement dans initializePools.
     */
    public ClassicEnemy obtainEnemy(String type, Vector2 position) {
        if (type == null) return obtainEnemy(position); // fallback

        Pool<ClassicEnemy> pool = enemyPools.get(type);

        ClassicEnemy enemy = pool.obtain();
        if (!activeEnemies.contains(enemy)) {
            activeEnemies.add(enemy);
        }
        instanceToType.put(enemy, type);

        // Link gameplay (si applicable)
        enemy.setGameplay(this.gameplay);
        enemy.activate(new Vector2(position));
        return enemy;
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

    public void dispose() {
        if (classicEnemyTexture != null) {
            classicEnemyTexture.dispose();
        }
        if (orbXpPool != null) {
            orbXpPool.clear();
        }
        for (OrbXp orb : createdOrbs) {
            orb.dispose();
        }
        createdOrbs.clear();

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
