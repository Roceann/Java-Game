package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour la classe abstraite LivingEntity.
 * Les tests se concentrent sur la logique non-graphique comme la gestion des points de vie,
 * les dégâts, l'armure et l'immunité.
 * Utilise JUnit 4 et Mockito.
 */
public class LivingEntityTest {

    // Classe interne pour permettre l'instanciation de LivingEntity.
    private static class TestableLivingEntity extends LivingEntity {
        public TestableLivingEntity(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, float hp, int armor, float force) {
            // On passe un chemin de texture nul pour éviter la création de texture réelle.
            super(spawnPoint, hitboxWidth, hitboxHeight, hp, armor, force, null);
        }

        @Override
        public void update(float delta) {
            // Appelle la logique d'immunité pour les tests
            tickImmunity(delta);
        }
    }

    @Mock
    private SpriteBatch mockSpriteBatch;
    @Mock
    private Texture mockTexture;

    private TestableLivingEntity testEntity;

    /**
     * Met en place l'environnement de test avant chaque test.
     * Initialise les mocks et une instance de TestableLivingEntity.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Vector2 spawn = new Vector2(100, 100);
        testEntity = new TestableLivingEntity(spawn, 32, 32, 100, 100, 1.0f);
        testEntity.setCurrentFrame(new com.badlogic.gdx.graphics.g2d.TextureRegion(mockTexture));
    }

    /**
     * Teste si le constructeur initialise correctement toutes les propriétés
     * de LivingEntity.
     */
    @Test
    public void testConstructor_Initialization() {
        assertEquals("Les HP initiaux sont incorrects", 100, testEntity.getHp(), 0.01);
        assertEquals("Les HP max sont incorrects", 100, testEntity.getMaxHp(), 0.01);
        assertEquals("L'armure est incorrecte", 100, testEntity.getArmor());
        assertEquals("La force est incorrecte", 1.0f, testEntity.getForce(), 0.01);
        assertTrue("L'entité doit être vivante par défaut", testEntity.isAlive());
    }

    /**
     * Teste la prise de dégâts avec une armure qui réduit les dégâts de moitié.
     * (100 d'armure -> 100 / (100 + 100) = 0.5)
     */
    @Test
    public void testTakeDamage_WithArmorReduction() {
        testEntity.takeDamage(50); // Dégâts effectifs = 50 * 0.5 = 25
        assertEquals("Les HP après dégâts sont incorrects", 75, testEntity.getHp(), 0.01);
        assertTrue("L'entité doit rester en vie", testEntity.isAlive());
    }

    /**
     * Teste que les dégâts infligés sont d'au moins 1, même si le calcul
     * donne un résultat inférieur.
     */
    @Test
    public void testTakeDamage_MinimumDamageIsOne() {
        testEntity.takeDamage(1); // Dégâts effectifs = 1 * 0.5 = 0.5, mais plafonné à 1
        assertEquals("Les HP doivent être réduits de 1 (dégât minimum)", 99, testEntity.getHp(), 0.01);
    }

    /**
     * Teste que l'entité meurt (isAlive = false) et que ses HP sont à 0
     * lorsqu'elle subit des dégâts mortels.
     */
    @Test
    public void testTakeDamage_LethalDamage() {
        testEntity.takeDamage(200); // Dégâts effectifs = 200 * 0.5 = 100
        assertEquals("Les HP doivent être à 0 après des dégâts mortels", 0, testEntity.getHp(), 0.01);
        assertFalse("L'entité ne doit plus être en vie", testEntity.isAlive());
    }

    /**
     * Teste qu'une entité morte ne peut plus subir de dégâts.
     */
    @Test
    public void testTakeDamage_WhenNotAlive_ShouldNotTakeDamage() {
        testEntity.setAlive(false);
        testEntity.setCurrentHp(50);
        testEntity.takeDamage(20);
        assertEquals("Une entité morte ne doit pas perdre de HP", 50, testEntity.getHp(), 0.01);
    }

    /**
     * Teste que l'entité ne subit pas de dégâts pendant sa période d'immunité.
     */
    @Test
    public void testTakeDamage_DuringImmunity_ShouldNotTakeDamage() {
        testEntity.takeDamage(10);
        assertEquals("Les HP doivent être réduits par la première attaque", 95, testEntity.getHp(), 0.01);
        
        testEntity.takeDamage(10); 
        assertEquals("Les HP ne doivent pas changer pendant l'immunité", 95, testEntity.getHp(), 0.01);
    }

    /**
     * Teste que le timer d'immunité est correctement décrémenté par la méthode update.
     */
    @Test
    public void testTickImmunity_DecrementsTimer() {
        testEntity.takeDamage(10); 
        assertTrue("Le timer d'immunité doit être positif après avoir subi des dégâts", testEntity.getImmunityTimer() > 0);

        testEntity.update(0.1f); 
        assertTrue("Le timer doit encore être positif", testEntity.getImmunityTimer() > 0);
        assertEquals("Le timer est mal décrémenté", 0.1f, testEntity.getImmunityTimer(), 0.01);

        testEntity.update(0.1f);
        assertEquals("Le timer doit être à 0", 0, testEntity.getImmunityTimer(), 0.01);
    }

    /**
     * Teste que la méthode `moveBy` déplace correctement l'entité en fonction de sa vitesse.
     */
    @Test
    public void testMoveBy_ShouldUpdatePosition() {
        testEntity.setMovementSpeed(100);
        testEntity.moveBy(0.1f, 0.05f); 
        
        assertEquals("La position X est incorrecte", 110, testEntity.getPosition().x, 0.01);
        assertEquals("La position Y est incorrecte", 105, testEntity.getPosition().y, 0.01);
        assertEquals("La position X de la hitbox est incorrecte", 110, testEntity.getHitbox().x, 0.01);
        assertEquals("La position Y de la hitbox est incorrecte", 105, testEntity.getHitbox().y, 0.01);
    }

    /**
     * Teste que la méthode `draw` ne fait rien si l'entité n'est pas en vie.
     * La méthode `draw` de LivingEntity doit retourner au début si !isAlive.
     */
    @Test
    public void testDraw_WhenNotAlive_ShouldReturnEarly() {
        testEntity.setAlive(false);
        testEntity.draw(mockSpriteBatch);
        verify(mockSpriteBatch, never()).setColor(any(Color.class));
        verify(mockSpriteBatch, never()).draw(any(com.badlogic.gdx.graphics.g2d.TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }
}