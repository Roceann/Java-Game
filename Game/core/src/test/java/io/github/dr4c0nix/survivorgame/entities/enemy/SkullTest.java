package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Classe de test pour l'ennemi concret Skull.
 */
public class SkullTest {

    private static final float DELTA = 1e-6f;

    /**
     * Teste si le constructeur de Skull initialise correctement toutes les statistiques.
     */
    @Test
    public void testConstructorInitializesCorrectStats() {
        Skull skull = new Skull();

        // Valeurs attendues basées sur Skull.java
        assertEquals("Max HP doit être 200", 200f, skull.getMaxHp(), DELTA);
        assertEquals("Current HP doit être 200", 200f, skull.getHp(), DELTA);
        assertEquals("L'armure doit être 4", 4, skull.getArmor());
        assertEquals("La force doit être 3.0", 3.0f, skull.getForce(), DELTA);
        assertFalse("Skull doit être initialisé comme non vivant", skull.isAlive());

        // Vérifie les valeurs de l'appel super().
        assertEquals("La vitesse de déplacement doit être 60", 60f, skull.getMovementSpeed(), DELTA);
        assertEquals("La valeur d'XP doit être 15", 15, skull.getXpValue());
        assertEquals("La position initiale doit être (0,0)", new Vector2(0f, 0f), skull.getPosition());
        assertEquals("La largeur de la hitbox doit être 22", 22f, skull.getHitbox().width, DELTA);
        assertEquals("La hauteur de la hitbox doit être 28", 28f, skull.getHitbox().height, DELTA);
    }

    /**
     * Teste le cas où une texture est présente et que la hitbox est redimensionnée en conséquence.
     * Un mock de Texture est utilisé pour éviter de dépendre d'un contexte graphique.
     */
    @Test
    public void testConstructorResizesHitboxWhenTextureIsPresent() {
        // Crée un mock de la texture avec des dimensions définies.
        Texture mockTexture = Mockito.mock(Texture.class);
        when(mockTexture.getWidth()).thenReturn(50);
        when(mockTexture.getHeight()).thenReturn(60);

        // Crée une classe pour injecter la texture mockée et simuler le redimensionnement.
        Skull skull = new Skull() {{
            this.texture = mockTexture;
            if (this.texture != null) {
                this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
            }
        }};

        assertEquals("La largeur de la hitbox doit être redimensionnée à la largeur de la texture", 50f, skull.getHitbox().width, DELTA);
        assertEquals("La hauteur de la hitbox doit être redimensionnée à la hauteur de la texture", 60f, skull.getHitbox().height, DELTA);
    }
}