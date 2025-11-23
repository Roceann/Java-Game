package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;

/**
 * Classe abstraite pour les ennemis "classiques" (poolable).
 * Fournit un constructeur commun, activation depuis le pool et réinitialisation.
 */
public abstract class ClassicEnemy extends Enemy implements Poolable {
    
    /**
     * Constructeur réutilisable pour ennemis classiques.
     *
     * @param spawnPoint    point d'apparition initial (utilisé par défaut)
     * @param hitboxWidth   largeur de la hitbox
     * @param hitboxHeight  hauteur de la hitbox
     * @param xpDrop        valeur d'XP lâchée
     * @param hp            points de vie
     * @param armor         armure
     * @param force         force (dégâts)
     * @param texturePath   chemin de la texture
     * @param gameplay      référence à Gameplay (peut être null à la construction)
     * @param movementSpeed vitesse de déplacement
     */
    public ClassicEnemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop, float hp, int armor, float force, String texturePath, Gameplay gameplay, float movementSpeed) {
        super(spawnPoint, hitboxWidth, hitboxHeight, xpDrop, hp, armor, force, texturePath);
        this.gameplay = gameplay;
        setMovementSpeed(movementSpeed);
    }

    /**
     * Taille en pixels de l'orbe d'XP lâchée par cet ennemi.
     * Doit être implémentée par chaque sous-classe.
     *
     * @return taille de l'orbe d'XP (en pixels)
     */
    public abstract float getXpOrbSize();

    /**
     * Active l'ennemi (utilisé par le pool/factory) :
     * positionne à spawnPoint, remet les PV au max et met l'état vivant.
     *
     * @param spawnPoint position d'activation
     */
    public void activate(Vector2 spawnPoint) {
        this.setPosition(spawnPoint);
        this.setAlive(true);
        this.setCurrentHp(this.getMaxHp());
    }

    /**
     * Réinitialise l'objet pour le remettre dans le pool :
     * PV remis, état non vivant et position remise à (0,0).
     */
    @Override
    public void reset() {
        this.setCurrentHp(this.getMaxHp());
        this.setAlive(false);
        this.setPosition(new Vector2(0f, 0f));
    }
}