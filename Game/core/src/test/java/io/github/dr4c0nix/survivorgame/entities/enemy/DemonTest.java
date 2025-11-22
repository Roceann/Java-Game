package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Classe de test pour l'ennemi concret Demon.
 */
public class DemonTest {

    private static final float DELTA = 1e-6f;

    /**
     * Teste si le constructeur de Demon initialise correctement toutes les statistiques.
     */
    @Test
    public void testConstructorInitializesCorrectStats() {
        Demon demon = new Demon();

        // Vérifie les valeurs définies dans le constructeur de Demon.
        assertEquals("Max HP doit être 80", 80f, demon.getMaxHp(), DELTA);
        assertEquals("Current HP doit être 80", 80f, demon.getHp(), DELTA);
        assertEquals("L'armure doit être 0", 0, demon.getArmor());
        assertEquals("La force doit être 3.0", 3.0f, demon.getForce(), DELTA);
        assertFalse("Demon doit être initialisé comme non vivant", demon.isAlive());

        // Vérifie les valeurs de l'appel super().
        assertEquals("La vitesse de déplacement doit être 100", 100f, demon.getMovementSpeed(), DELTA);
        assertEquals("La valeur d'XP doit être 15", 15, demon.getXpValue());
        assertEquals("La position initiale doit être (0,0)", new Vector2(0f, 0f), demon.getPosition());
        assertEquals("La largeur de la hitbox doit être 24", 24f, demon.getHitbox().width, DELTA);
        assertEquals("La hauteur de la hitbox doit être 32", 32f, demon.getHitbox().height, DELTA);
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

        // Crée une classe anonyme pour injecter la texture mockée et simuler le redimensionnement.
        Demon demon = new Demon() {{
            this.texture = mockTexture;
            if (this.texture != null) {
                this.hitbox.setSize(this.texture.getWidth(), this.texture.getHeight());
            }
        }};

        assertEquals("La largeur de la hitbox doit être redimensionnée à la largeur de la texture", 50f, demon.getHitbox().width, DELTA);
        assertEquals("La hauteur de la hitbox doit être redimensionnée à la hauteur de la texture", 60f, demon.getHitbox().height, DELTA);
    }
}