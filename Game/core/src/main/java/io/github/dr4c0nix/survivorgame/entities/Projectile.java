package io.github.dr4c0nix.survivorgame.entities;


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
    protected float damage;
    protected float lifespan;
    protected Enemy target;
    protected int maxRange;
    protected LivingEntity source;

    public Projectile(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float damage, float lifespan, Enemy target, LivingEntity source, String pathFIle) {
        super(spawnPoint, hitboxWidth, hitboxHeight, pathFIle);
        this.damage = damage;
        this.lifespan = lifespan;
        this.target = target;
        this.maxRange = 100; 
        this.source = source;
    }

    /*
     * Met à jour l'état du projectile chaque frame
     * Réduit la durée de vie et vérifie si le projectile doit être détruit
     */
    @Override
    public void update(float delta) {
        if (!isAlive) return;

        lifespan -= delta;
        if (lifespan <= 0) {
                isAlive = false;
                return;
        }
    }

    /*
     * Réinitialise l'état du projectile pour réutilisation dans le pool
     */
    @Override
    public void reset() {
        damage = 0;
        lifespan = 0;
        target = null;
        maxRange = 0;
        source = null;
        isAlive = false;
        position.set(0, 0);
    }
}
