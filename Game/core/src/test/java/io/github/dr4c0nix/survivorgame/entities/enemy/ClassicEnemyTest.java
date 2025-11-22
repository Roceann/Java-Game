package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Classe de test pour ClassicEnemy.
 * Utilise une classe interne 'TestableClassicEnemy' pour tester les fonctionnalités
 * de cette classe de base.
 */
public class ClassicEnemyTest {

    private static final float DELTA = 1e-6f;
    private TestableClassicEnemy enemy;
    private Gameplay mockGameplay;

    /**
     * Classe interne concrète pour tester la classe abstraite ClassicEnemy.
     */
    private static class TestableClassicEnemy extends ClassicEnemy {
        public TestableClassicEnemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop, float hp, int armor, float force, String texturePath, Gameplay gameplay, float movementSpeed) {
            super(spawnPoint, hitboxWidth, hitboxHeight, xpDrop, hp, armor, force, texturePath, gameplay, movementSpeed);
        }
    }

    /**
     * Initialise l'environnement de test avant chaque test.
     * Crée un mock de Gameplay et une instance de TestableClassicEnemy.
     */
    @Before
    public void setUp() {
        mockGameplay = Mockito.mock(Gameplay.class);
        enemy = new TestableClassicEnemy(
            new Vector2(0, 0), 10, 10, 5, 100, 10, 5, null, mockGameplay, 50f
        );
        enemy.setAlive(false);
    }

    /**
     * Teste si le constructeur initialise correctement les propriétés de l'ennemi.
     */
    @Test
    public void testConstructor() {
        assertEquals("La vitesse de déplacement doit être définie par le constructeur", 50f, enemy.getMovementSpeed(), DELTA);
        assertEquals("La valeur d'XP doit être définie par le constructeur", 5, enemy.getXpValue());
        assertEquals("Les HP max doivent être définis par le constructeur", 100f, enemy.getMaxHp(), DELTA);
    }

    /**
     * Teste la méthode activate() pour s'assurer qu'elle réinitialise correctement l'état de l'ennemi.
     */
    @Test
    public void testActivate() {
        assertFalse("L'ennemi doit être inactif avant l'activation", enemy.isAlive());
        enemy.setCurrentHp(0);
        Vector2 spawnPoint = new Vector2(150, 250);

        enemy.activate(spawnPoint);

        assertTrue("activate() doit définir isAlive à true", enemy.isAlive());
        assertEquals("activate() doit définir la position au point d'apparition", spawnPoint, enemy.getPosition());
        assertEquals("activate() doit réinitialiser les HP actuels aux HP max", 100f, enemy.getHp(), DELTA);
    }

    /**
     * Teste la méthode reset() pour s'assurer qu'elle désactive et réinitialise l'ennemi.
     */
    @Test
    public void testReset() {
        // Active l'ennemi pour lui donner un état non-défaut.
        enemy.activate(new Vector2(100, 100));
        enemy.takeDamage(20);
        assertTrue("L'ennemi doit être en vie avant le reset", enemy.isAlive());
        assertNotEquals("Les HP doivent être inférieurs au max avant le reset", enemy.getMaxHp(), enemy.getHp(), DELTA);

        enemy.reset();

        assertFalse("reset() doit définir isAlive à false", enemy.isAlive());
        assertEquals("reset() doit réinitialiser la position à (0,0)", new Vector2(0, 0), enemy.getPosition());
        assertEquals("reset() doit restaurer les HP actuels aux HP max", 100f, enemy.getHp(), DELTA);
    }
}