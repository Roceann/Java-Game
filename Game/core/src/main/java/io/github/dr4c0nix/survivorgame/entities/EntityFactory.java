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
    private ArrayList<OrbXp> activeOrbs;
    private ArrayList<OrbXp> createdOrbs;

    private static final int INITIAL_POOL_SIZE = 50;
    private static final int MAX_POOL_SIZE = 500;

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
        this.orbXpPool = new Pool<OrbXp>(INITIAL_POOL_SIZE, MAX_POOL_SIZE) {
            @Override
            protected OrbXp newObject() {
                OrbXp orb = new OrbXp(0);
                createdOrbs.add(orb);
                return orb;
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
    }

    public Pool<OrbXp> getOrbXpPool() {
        return orbXpPool;
    }

    public ArrayList<OrbXp> getActiveOrbs() {
        return activeOrbs;
    }
}
