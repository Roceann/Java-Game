package io.github.dr4c0nix.survivorgame.entities.enemy;

// import java.util.Vector;
import com.badlogic.gdx.math.Vector2;

import io.github.dr4c0nix.survivorgame.entities.LivingEntity;
import io.github.dr4c0nix.survivorgame.entities.OrbXp;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;

/**
 * Classe de base pour tous les ennemis du jeu.
 * Contient la logique de déplacement, séparation (pour éviter la superposition)
 * et l'orbe d'XP associée à l'ennemi.
 */
public abstract class Enemy extends LivingEntity {
    protected OrbXp xpDrop;
    protected Gameplay gameplay;

    private final Vector2 tmpVector = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Vector2 separation = new Vector2();

    /**
     * Constructeur.
     *
     * @param spawnPoint   position d'apparition initiale
     * @param hitboxWidth  largeur de la hitbox
     * @param hitboxHeight hauteur de la hitbox
     * @param xpDrop       valeur d'XP fournie par cet ennemi
     * @param hp           points de vie
     * @param armor        armure
     * @param force        force de l'ennemi (dégâts)
     * @param texturePath  chemin de la texture
     */
    public Enemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop, float hp, int armor, float force, String texturePath) {
        super(spawnPoint, hitboxWidth, hitboxHeight, hp, armor, force, texturePath);
        this.xpDrop = new OrbXp(xpDrop);
    }

    /**
     * Associe l'objet Gameplay pour permettre l'accès au joueur, collisions et pathfinding.
     *
     * @param gameplay instance de Gameplay
     */
    public void setGameplay(Gameplay gameplay) {
        this.gameplay = gameplay;
    }

    /**
     * Valeur d'XP que donne cet ennemi.
     *
     * @return valeur d'XP
     */
    public int getXpValue() {
        return xpDrop.getXpValue();
    }

    /**
     * Récupère l'objet OrbXp associé (pour spawn d'orbes).
     *
     * @return l'orbe d'XP
     */
    public OrbXp getXpDrop() {
        return xpDrop;
    }

    /**
     * Mise à jour par frame de l'ennemi.
     * Gère : pathfinding/direct vers le joueur, séparation entre ennemis, mouvement avec glissement,
     * et tick d'immunité.
     *
     * @param delta temps écoulé depuis la dernière frame (en secondes)
     */
    @Override
    public void update(float delta) {
        if (!isAlive() || gameplay == null) return;
        
        Player player = gameplay.getPlayer();
        if (player == null) return;

        float centerX = getPosition().x + getHitbox().width / 2;
        float centerY = getPosition().y + getHitbox().height / 2;

        // 1. Obtenir la direction idéale (via Pathfinding ou direct vers joueur)
        Vector2 desiredDir = gameplay.getDirection((int) centerX, (int) centerY);
        
        if (desiredDir == null) {
            tmpVector.set(player.getPosition()).add(player.getHitbox().width/2, player.getHitbox().height/2);
            tmpVector.sub(centerX, centerY).nor();
            velocity.set(tmpVector);
        } else {
            velocity.set(desiredDir);
        }

        // 2. Force de séparation : Évite que les ennemis se superposent trop
        calculateSeparationForce(separation);
        velocity.add(separation).nor(); 

        // 3. Appliquer le mouvement avec glissement sur les murs
        float moveDist = getMovementSpeed() * delta;
        moveAndSlide(velocity.x * moveDist, velocity.y * moveDist, moveDist);
        tickImmunity(delta);
    }

    /**
     * Calcule une force de séparation pour éviter la superposition des ennemis proches.
     * Remplit outSeparation avec le vecteur de séparation (somme des forces).
     *
     * @param outSeparation vecteur de sortie modifié par la méthode
     */
    private void calculateSeparationForce(Vector2 outSeparation) {
        outSeparation.set(0, 0);
        int count = 0;
        float separationRadius = getHitbox().width;

        // Note: ceci ne prend en compte que les ClassicEnemy pour la séparation.
        // Pour inclure les Boss, il faudra une liste plus générique dans Gameplay.
        for (ClassicEnemy other : gameplay.getActiveClassicEnemies()) {
            if (other == this || !other.isAlive()) continue;

            float dst2 = getPosition().dst2(other.getPosition());
            if (dst2 > 0 && dst2 < separationRadius * separationRadius) {
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
     * Déplace l'entité sur les axes X puis Y pour permettre de glisser le long des murs.
     * Annule les déplacements qui provoquent une collision et tente de compléter le mouvement
     * pour conserver la distance totale souhaitée.
     *
     * @param dx       déplacement souhaité sur X
     * @param dy       déplacement souhaité sur Y
     * @param moveDist distance totale à parcourir (|dx| ou |dy| au max)
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