package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;

/**
 * Refactorisé : mêmes comportements, moins de duplication.
 */
public abstract class ClassicEnemy extends Enemy implements Poolable {
    protected Gameplay gameplay;
    
    // Vecteurs temporaires pour éviter les allocations mémoire (new Vector2) dans la boucle update
    private final Vector2 tmpVector = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Vector2 separation = new Vector2();

    public ClassicEnemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop, float hp, int armor, float force, String texturePath, Texture walkingTexture, Gameplay gameplay, float movementSpeed) {
        super(spawnPoint, hitboxWidth, hitboxHeight, xpDrop, hp, armor, force, texturePath, walkingTexture);
        this.gameplay = gameplay;
        setMovementSpeed(movementSpeed);
    }

    /**
     * Activate enemy (used by the factory/pool).
     * Reinitialises state and places it at spawn.
     */
    public void activate(Vector2 spawnPoint) {
        this.setPosition(spawnPoint);
        this.setAlive(true);
        this.setCurrentHp(this.getMaxHp());
    }

    public void setGameplay(Gameplay gameplay) {
        this.gameplay = gameplay;
    }

    @Override
    public void reset() {
        this.setCurrentHp(this.getMaxHp());
        this.setAlive(false);
        this.setPosition(new Vector2(0f, 0f));
    }

    @Override
    public void update(float delta) {
        if (gameplay == null) return;
        update(delta, gameplay.getPlayer());
    }

    public void update(float delta, Player player) {
        if (!isAlive() || player == null) return;

        float centerX = getPosition().x + getHitbox().width / 2;
        float centerY = getPosition().y + getHitbox().height / 2;

        // 1. Obtenir la direction idéale (via Pathfinding ou direct vers joueur)
        Vector2 desiredDir = gameplay.getDirection((int) centerX, (int) centerY);
        
        if (desiredDir == null) {
            // Fallback : ligne droite vers le joueur
            tmpVector.set(player.getPosition()).add(player.getHitbox().width/2, player.getHitbox().height/2);
            tmpVector.sub(centerX, centerY).nor();
            velocity.set(tmpVector);
        } else {
            velocity.set(desiredDir);
        }

        // 2. Force de séparation (Boids) : Évite que les ennemis se superposent trop
        // Au lieu de bloquer le mouvement, on ajoute juste une petite force qui pousse ailleurs
        calculateSeparationForce(separation);
        velocity.add(separation).nor(); // On combine direction + évitement et on normalise

        // 3. Appliquer le mouvement avec glissement sur les murs
        float moveDist = getMovementSpeed() * delta;
        moveAndSlide(velocity.x * moveDist, velocity.y * moveDist, moveDist);
        tickImmunity(delta);
    }

    /**
     * Calcule une force pour repousser cet ennemi des autres ennemis proches.
     * Cela remplace la méthode complexe isOverlappingOtherEnemies.
     */
    private void calculateSeparationForce(Vector2 outSeparation) {
        outSeparation.set(0, 0);
        int count = 0;
        float separationRadius = getHitbox().width;

        for (ClassicEnemy other : gameplay.getActiveClassicEnemies()) {
            if (other == this || !other.isAlive()) continue;

            float dst2 = getPosition().dst2(other.getPosition());
            if (dst2 < separationRadius * separationRadius) {
                // Vecteur qui fuit l'autre ennemi
                tmpVector.set(getPosition()).sub(other.getPosition()).nor();
                // Plus on est proche, plus la force est grande
                tmpVector.scl(1f / (float)Math.sqrt(dst2)); 
                outSeparation.add(tmpVector);
                count++;
                // On ne vérifie pas tous les ennemis, 5 suffisent pour l'effet de foule
                if (count > 5) break; 
            }
        }
        if (count > 0) {
            outSeparation.scl(1.5f); 
        }
    }

    /**
     * Déplace l'entité axe par axe pour permettre de glisser le long des murs.
     */
    private void moveAndSlide(float dx, float dy, float moveDist) {
        // Essai mouvement X
        if (dx != 0) {
            getPosition().x += dx;
            getHitbox().x = getPosition().x;
            if (gameplay.isColliding(getHitbox())) {
                getPosition().x -= dx;
                getHitbox().x = getPosition().x;
                dx = 0f;
            }
        }

        // Essai mouvement Y
        if (dy != 0) {
            getPosition().y += dy;
            getHitbox().y = getPosition().y;
            if (gameplay.isColliding(getHitbox())) {
                getPosition().y -= dy; // Annuler si collision
                getHitbox().y = getPosition().y;
                dy = 0f;
            }
        }
        
        if (dx == 0f && dy != 0f) {
            float fullY = Math.signum(dy) * moveDist;
            float extra = fullY - dy; 
            if (Math.abs(extra) > 0.0001f) {
                getPosition().y += extra;
                getHitbox().y = getPosition().y;
                if (gameplay.isColliding(getHitbox())) {
                    getPosition().y -= extra;
                    getHitbox().y = getPosition().y;
                }
            }
        } else if (dy == 0f && dx != 0f) {
            float fullX = Math.signum(dx) * moveDist;
            float extra = fullX - dx;
            if (Math.abs(extra) > 0.0001f) {
                getPosition().x += extra;
                getHitbox().x = getPosition().x;
                if (gameplay.isColliding(getHitbox())) {
                    getPosition().x -= extra;
                    getHitbox().x = getPosition().x;
                }
            }
        }
    }
}