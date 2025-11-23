package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour la classe abstraite Entity.
 * Les tests se concentrent sur la logique non-graphique.
 * Mise à jour pour utiliser JUnit 4.
 */
public class EntityTest {

    // Classe interne  pour permettre l'instanciation de Entity pour les tests.
    private static class TestableEntity extends Entity {
        public TestableEntity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight) {
            // On passe un chemin de texture nul pour éviter la création de Texture dans le constructeur
            super(spawnPoint, hitboxWidth, hitboxHeight, null);
        }

        @Override
        public void update(float delta) {}
    }

    @Mock
    private SpriteBatch mockSpriteBatch;
    @Mock
    private Texture mockTexture;

    private TestableEntity testEntity;

    /**
     * Met en place l'environnement de test avant chaque exécution de test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Gdx.files = null;

        Vector2 spawn = new Vector2(100, 200);
        float width = 32;
        float height = 64;
        testEntity = new TestableEntity(spawn, width, height);

        testEntity.setTexture(mockTexture);
        testEntity.setCurrentFrame(new TextureRegion(mockTexture));
    }

    /**
     * Nettoie les ressources après chaque test.
     */
    @After
    public void tearDown() {
        Gdx.files = null;
    }

    /**
     * Teste si la méthode `draw` appelle correctement la méthode `draw` du SpriteBatch
     * lorsque l'entité est vivante et que sa frame actuelle n'est pas nulle.
     */
    @Test
    public void testDraw_WhenAliveAndFrameNotNull_ShouldDraw() {
        testEntity.setAlive(true);
        testEntity.draw(mockSpriteBatch);
        verify(mockSpriteBatch, times(1)).draw(
            testEntity.getCurrentFrame(),
            testEntity.getPosition().x,
            testEntity.getPosition().y,
            testEntity.getHitbox().width,
            testEntity.getHitbox().height
        );
    }

    /**
     * Teste que la méthode `draw` ne fait rien lorsque l'entité n'est pas en vie.
     */
    @Test
    public void testDraw_WhenNotAlive_ShouldNotDraw() {
        testEntity.setAlive(false);
        testEntity.draw(mockSpriteBatch);
        verify(mockSpriteBatch, never()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    /**
     * Teste que la méthode `draw` ne fait rien si la frame actuelle est nulle.
     */
    @Test
    public void testDraw_WhenFrameIsNull_ShouldNotDraw() {
        testEntity.setAlive(true);
        testEntity.setCurrentFrame(null);
        testEntity.draw(mockSpriteBatch);
        verify(mockSpriteBatch, never()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    /**
     * Teste la robustesse de la méthode `draw` en lui passant un SpriteBatch nul.
     */
    @Test(expected = NullPointerException.class)
    public void testDraw_WithNullBatch_ShouldThrowNullPointerException() {
        testEntity.setAlive(true);
        testEntity.draw(null);
    }

    /**
     * Teste si le constructeur initialise correctement les propriétés de base.
     */
    @Test
    public void testConstructor_Initialization() {
        Vector2 spawn = new Vector2(50, 75);
        float width = 10;
        float height = 20;
        TestableEntity entity = new TestableEntity(spawn, width, height);

        assertNotNull("La position ne doit pas être nulle", entity.getPosition());
        assertEquals("La position X est incorrecte", 50, entity.getPosition().x, 0.01);
        assertEquals("La position Y est incorrecte", 75, entity.getPosition().y, 0.01);

        assertNotNull("La hitbox ne doit pas être nulle", entity.getHitbox());
        assertEquals("La position X de la hitbox est incorrecte", 50, entity.getHitbox().x, 0.01);
        assertEquals("La position Y de la hitbox est incorrecte", 75, entity.getHitbox().y, 0.01);
        assertEquals("La largeur de la hitbox est incorrecte", 10, entity.getHitbox().width, 0.01);
        assertEquals("La hauteur de la hitbox est incorrecte", 20, entity.getHitbox().height, 0.01);

        assertTrue("L'entité doit être vivante par défaut", entity.isAlive());
        assertNull("La texture doit être nulle car non chargée en test", entity.getTexture());
        assertNull("La frame courante doit être nulle car non chargée en test", entity.getCurrentFrame());
    }

    /**
     * Teste si la méthode `setPosition` met à jour correctement la position de l'entité et de sa hitbox.
     */
    @Test
    public void testSetPosition_ShouldUpdatePositionAndHitbox() {
        Vector2 newPosition = new Vector2(500, 600);
        testEntity.setPosition(newPosition);

        assertEquals("La position de l'entité n'a pas été mise à jour", newPosition, testEntity.getPosition());
        assertEquals("La position X de la hitbox n'a pas été mise à jour", 500, testEntity.getHitbox().x, 0.01);
        assertEquals("La position Y de la hitbox n'a pas été mise à jour", 600, testEntity.getHitbox().y, 0.01);
    }

    /**
     * Teste le getter et le setter pour la vitesse de déplacement.
     */
    @Test
    public void testSetAndGetMovementSpeed() {
        float speed = 150.5f;
        testEntity.setMovementSpeed(speed);
        assertEquals("La vitesse de déplacement retournée est incorrecte", speed, testEntity.getMovementSpeed(), 0.01);
    }

    /**
     * Teste la méthode `dispose` pour s'assurer qu'elle libère la texture.
     */
    @Test
    public void testDispose_ShouldCallDisposeOnTexture() {
        testEntity.dispose();
        verify(mockTexture, times(1)).dispose();
    }

    /**
     * Teste que la méthode `dispose` ne lève pas d'exception si la texture est nulle.
     */
    @Test
    public void testDispose_WhenTextureIsNull_ShouldNotThrowException() {
        testEntity.setTexture(null);
        try {
            testEntity.dispose();
        } catch (Exception e) {
            fail("dispose() ne devrait pas lancer d'exception si la texture est nulle.");
        }
    }

    /**
     * Vérifie que setHitbox remplace correctement la hitbox interne.
     */
    @Test
    public void testSetHitbox_ReplacesHitbox() {
        com.badlogic.gdx.math.Rectangle newBox = new com.badlogic.gdx.math.Rectangle(10, 20, 5, 6);
        testEntity.setHitbox(newBox);
        assertSame("La hitbox doit être remplacée par l'instance fournie", newBox, testEntity.getHitbox());
    }

    /**
     * Vérifie les getters/setters pour currentFrame et texture.
     */
    @Test
    public void testSetAndGetCurrentFrameAndTexture() {
        com.badlogic.gdx.graphics.g2d.TextureRegion region = new com.badlogic.gdx.graphics.g2d.TextureRegion(mockTexture);
        testEntity.setCurrentFrame(region);
        assertEquals("La frame courante doit être récupérable", region, testEntity.getCurrentFrame());

        testEntity.setTexture(null);
        assertNull("La texture peut être nulle", testEntity.getTexture());
        testEntity.setTexture(mockTexture);
        assertEquals("La texture doit être récupérable", mockTexture, testEntity.getTexture());
    }
}