package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour la classe Projectile.
 * Les tests se concentrent sur la logique d'initialisation, de mise à jour,
 * de portée et de réinitialisation pour le pooling.
 */
public class ProjectileTest {

    @Mock
    private SpriteBatch mockSpriteBatch;
    @Mock
    private LivingEntity mockSource;
    @Mock
    private Texture mockTexture;

    private Projectile projectile;

    /**
     * Met en place l'environnement de test avant chaque test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Empêche le constructeur de créer une vraie texture
        Gdx.files = null;
        projectile = new Projectile("test.png", 10, 20);
        // Assigner une frame pour que les tests de dessin ne lèvent pas d'exception
        projectile.setCurrentFrame(new TextureRegion(mockTexture));
    }

    /**
     * Teste si le constructeur initialise correctement le projectile dans un état inactif.
     */
    @Test
    public void testConstructor_InitialStateIsInactive() {
        assertFalse("Le projectile doit être inactif après sa construction", projectile.isAlive());
        assertEquals("La largeur de base est incorrecte", 10, projectile.getBaseWidth(), 0.01);
        assertEquals("La hauteur de base est incorrecte", 20, projectile.getBaseHeight(), 0.01);
        assertEquals("La position initiale doit être (0,0)", 0, projectile.getPosition().x, 0.01);
    }

    /**
     * Teste si la méthode `init` configure correctement toutes les propriétés du projectile.
     */
    @Test
    public void testInit_CorrectlyInitializesProjectile() {
        Vector2 spawn = new Vector2(100, 100);
        Vector2 direction = new Vector2(1, 1);
        
        projectile.init(spawn, direction, 200, 500, 50, 1.5f, mockSource);

        assertTrue("Le projectile doit être actif après init", projectile.isAlive());
        assertEquals("La vitesse est incorrecte", 200, projectile.getSpeed(), 0.01);
        assertEquals("La portée maximale est incorrecte", 500, projectile.getMaxRange(), 0.01);
        assertEquals("Les dégâts sont incorrects", 50, projectile.getDamage());
        assertEquals("La source est incorrecte", mockSource, projectile.getSource());
        
        // Vérifie que la direction est normalisée
        assertEquals("La direction n'est pas normalisée", 1, projectile.getDirection().len(), 0.001);
        
        // Vérifie que la vélocité est correcte (direction * vitesse)
        Vector2 normalizedDirection = direction.cpy().nor();
        assertEquals("La vélocité X est incorrecte", normalizedDirection.x * 200, projectile.getVelocity().x, 0.01);
        assertEquals("La vélocité Y est incorrecte", normalizedDirection.y * 200, projectile.getVelocity().y, 0.01);

        // Vérifie le calcul de la position de départ (centrée)
        float expectedWidth = 10 * 1.5f;
        float expectedHeight = 20 * 1.5f;
        assertEquals("La position X est mal centrée", 100 - expectedWidth / 2, projectile.getPosition().x, 0.01);
        assertEquals("La position Y est mal centrée", 100 - expectedHeight / 2, projectile.getPosition().y, 0.01);
        assertEquals("La largeur de la hitbox est incorrecte", expectedWidth, projectile.getHitbox().width, 0.01);
        assertEquals("La hauteur de la hitbox est incorrecte", expectedHeight, projectile.getHitbox().height, 0.01);
    }

    /**
     * Teste si la méthode `update` déplace correctement le projectile.
     */
    @Test
    public void testUpdate_MovesProjectileAndIncreasesDistance() {
        projectile.init(new Vector2(0, 0), new Vector2(1, 0), 100, 1000, 10, 1f, mockSource);
        
        projectile.update(0.5f); // 0.5 secondes, déplacement de 100 * 0.5 = 50 sur X

        // La nouvelle position X est -5 (départ) + 50 (déplacement) = 45.
        assertEquals("La position X devrait avoir changé", 45, projectile.getPosition().x, 0.01);
        // La position Y ne change pas, elle reste à -10.
        assertEquals("La position Y ne devrait pas avoir changé", -10, projectile.getPosition().y, 0.01);
        assertEquals("La distance parcourue est incorrecte", 50, projectile.getDistanceTraveled(), 0.01);
    }

    /**
     * Teste si le projectile devient inactif lorsque sa portée maximale est atteinte.
     */
    @Test
    public void testUpdate_WhenRangeExceeded_BecomesInactive() {
        projectile.init(new Vector2(0, 0), new Vector2(1, 0), 100, 200, 10, 1f, mockSource);
        
        projectile.update(2.1f); // Distance parcourue = 100 * 2.1 = 210, ce qui est > 200

        assertFalse("Le projectile doit devenir inactif après avoir dépassé sa portée", projectile.isAlive());
    }

    /**
     * Teste si la méthode `reset` réinitialise correctement l'état du projectile pour le pooling.
     */
    @Test
    public void testReset_ResetsAllPropertiesToDefault() {
        projectile.init(new Vector2(100, 100), new Vector2(1, 1), 200, 500, 50, 1.5f, mockSource);
        
        projectile.reset();

        assertFalse("isAlive doit être false après reset", projectile.isAlive());
        assertEquals("damage doit être 0 après reset", 0, projectile.getDamage());
        assertEquals("maxRange doit être 0 après reset", 0, projectile.getMaxRange(), 0.01);
        assertNull("source doit être null après reset", projectile.getSource());
        assertEquals("speed doit être 0 après reset", 0, projectile.getSpeed(), 0.01);
        assertEquals("distanceTraveled doit être 0 après reset", 0, projectile.getDistanceTraveled(), 0.01);
        assertTrue("velocity doit être un vecteur nul après reset", projectile.getVelocity().isZero());
        assertEquals("La taille de la hitbox doit être réinitialisée à la taille de base", 10, projectile.getHitbox().width, 0.01);
    }

    /**
     * Teste que la méthode `draw` appelle bien la méthode de dessin du SpriteBatch avec les bons paramètres.
     */
    @Test
    public void testDraw_WhenAlive_CallsBatchDrawWithRotation() {
        projectile.init(new Vector2(100, 100), new Vector2(0, 1), 100, 200, 10, 1f, mockSource);
        
        projectile.draw(mockSpriteBatch);

        // L'angle de direction (0, 1) est 90°. L'angle de rotation est (90 + 90) % 360 = 180°.
        verify(mockSpriteBatch, times(1)).draw(
            any(TextureRegion.class),
            eq(projectile.getPosition().x),
            eq(projectile.getPosition().y),
            eq(projectile.getHitbox().width * 0.5f),
            eq(projectile.getHitbox().height * 0.5f),
            eq(projectile.getHitbox().width),
            eq(projectile.getHitbox().height),
            eq(1f),
            eq(1f),
            eq(180f)
        );
    }
}