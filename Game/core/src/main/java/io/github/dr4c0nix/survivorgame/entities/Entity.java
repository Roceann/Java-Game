package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe abstraite Entity qui sert de base pour toutes les entités du jeu. (joueurs, ennemis, projectiles, Xp)
 * 
 * Cette classe définit les propriétés et comportements communs à toutes les entités :
 * - Position dans le monde du jeu
 * - Hitbox pour les collisions
 * - Vitesse de déplacement
 * - État de vie
 * - Gestion des textures et animations
 * 
 * @author Abdelkader1900
 * @author Roceann (relecture, corrections et documentation)
 * @version 1.0
 */
public abstract class Entity {
    protected Vector2 position;
    protected Rectangle hitbox;
    protected float movementSpeed;
    protected boolean isAlive = true;
    protected Texture texture;
    protected TextureRegion currentFrame;

    /**
     * Constructeur de l'entité.
     * 
     * Initialise une nouvelle entité avec sa position de départ, sa hitbox
     * et sa texture. La hitbox est utilisée pour la détection des collisions.
     * 
     * @param spawnPoint Point de départ de l'entité dans le monde
     * @param hitboxWidth Largeur de la hitbox de collision
     * @param hitboxHeight Hauteur de la hitbox de collision
     * @param texturePath Chemin vers le fichier de texture de l'entité
     */
    public Entity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, String texturePath) {
        this.position = new Vector2(spawnPoint);
        this.hitbox = new Rectangle(spawnPoint.x, spawnPoint.y, hitboxWidth, hitboxHeight);
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.currentFrame = new TextureRegion(texture);
    }

    /**
     * Dessine l'entité à l'écran.
     * 
     * Utilise le SpriteBatch fourni pour dessiner la frame courante de l'entité
     * à sa position actuelle dans le monde.
     * 
     * @param batch Le SpriteBatch utilisé pour le rendu
     */
    public void draw(SpriteBatch batch) {
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }
    }

    /**
     * Met à jour l'état de l'entité.
     * 
     * Cette méthode abstraite doit être implémentée par les classes filles
     * pour définir le comportement spécifique de chaque type d'entité.
     * 
     * @param delta Le temps écoulé depuis la dernière mise à jour en secondes
     */
    public abstract void update(float delta);

    /**
     * Récupère la position actuelle de l'entité.
     * @return La position sous forme de Vector2
     */
    public Vector2 getPosition() {
        return this.position;
    }

    /**
     * Définit la nouvelle position de l'entité.
     * @param value La nouvelle position
     */
    public void setPosition(Vector2 value) {
        this.position = value;
    }

    /**
     * Récupère la hitbox de collision de l'entité.
     * @return La hitbox sous forme de Rectangle
     */
    public Rectangle getHitbox() {
        return this.hitbox;
    }

    /**
     * Récupère la vitesse de déplacement de l'entité.
     * @return La vitesse de déplacement
     */
    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    /**
     * Définit la vitesse de déplacement de l'entité.
     * @param value La nouvelle vitesse
     */
    public void setMovementSpeed(float value) {
        this.movementSpeed = value;
    }

    /**
     * Vérifie si l'entité est en vie.
     * @return true si l'entité est vivante, false sinon
     */
    public boolean getIsAlive() {
        return this.isAlive;
    }

    /**
     * Récupère la frame d'animation actuelle.
     * @return La TextureRegion courante
     */
    public TextureRegion getCurrentFrame() {
        return this.currentFrame;
    }

    /**
     * Définit la frame d'animation actuelle.
     * @param value La nouvelle TextureRegion
     */
    public void setCurrentFrame(TextureRegion value) {
        this.currentFrame = value;
    }

    /**
     * Libère les ressources utilisées par l'entité.
     * 
     * Cette méthode doit être appelée quand l'entité n'est plus utilisée
     * pour éviter les fuites de mémoire.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
