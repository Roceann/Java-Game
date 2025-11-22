package io.github.dr4c0nix.survivorgame.entities.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Classe de test pour la classe abstraite Enemy.
 * Utilise une classe interne 'TestableEnemy' pour permettre l'instanciation et
 * le test des comportements de la classe de base.
 */
public class EnemyTest {

    private static final float DELTA = 1e-6f;
    private TestableEnemy enemy;
    private Gameplay mockGameplay;
    private Player mockPlayer;

    /**
     * Classe interne concrète pour tester la classe abstraite Enemy.
     */
    private static class TestableEnemy extends Enemy {
        public TestableEnemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, int xpDrop, float hp, int armor, float force, String texturePath) {
            super(spawnPoint, hitboxWidth, hitboxHeight, xpDrop, hp, armor, force, texturePath);
        }
    }

    /**
     * Initialise l'environnement de test avant chaque exécution de test.
     * Crée des mocks pour Gameplay et Player, et configure une instance de TestableEnemy
     * avec des valeurs par défaut.
     */
    @Before
    public void setUp() {
        mockGameplay = Mockito.mock(Gameplay.class);
        mockPlayer = Mockito.mock(Player.class);

        enemy = new TestableEnemy(new Vector2(100, 100), 10, 10, 5, 100, 0, 1, null);
        enemy.setMovementSpeed(50f);
        enemy.setGameplay(mockGameplay);
        enemy.setAlive(true);

        when(mockGameplay.getPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getPosition()).thenReturn(new Vector2(200, 100));
        when(mockPlayer.getHitbox()).thenReturn(new Rectangle(200, 100, 10, 10));
        when(mockGameplay.isColliding(any())).thenReturn(false);
        when(mockGameplay.getActiveClassicEnemies()).thenReturn(new ArrayList<>());
    }

    /**
     * Teste si le constructeur de Enemy initialise correctement les attributs.
     */
    @Test
    public void testConstructor() {
        // Crée un nouvel ennemi et vérifie que ses propriétés sont correctement définies.
        TestableEnemy newEnemy = new TestableEnemy(new Vector2(10, 20), 30, 40, 50, 60, 70, 80, null);
        assertEquals("La position doit être définie", new Vector2(10, 20), newEnemy.getPosition());
        assertEquals("La hitbox doit être définie", new Rectangle(10, 20, 30, 40), newEnemy.getHitbox());
        assertEquals("Les HP max doivent être définis", 60f, newEnemy.getMaxHp(), DELTA);
        assertNotNull("L'objet xpDrop doit être instancié", newEnemy.getXpDrop());
        assertEquals("La valeur d'XP doit être définie", 50, newEnemy.getXpValue());
    }

    /**
     * Vérifie que la méthode update() ne modifie pas la position de l'ennemi s'il n'est pas en vie.
     */
    @Test
    public void testUpdateDoesNothingWhenNotAlive() {
        enemy.setAlive(false);
        Vector2 initialPos = new Vector2(enemy.getPosition());
        enemy.update(0.1f);
        assertEquals("La position ne doit pas changer si l'ennemi n'est pas en vie", initialPos, enemy.getPosition());
    }

    /**
     * Vérifie que la méthode update() ne fait rien si l'objet Gameplay est nul.
     */
    @Test
    public void testUpdateDoesNothingWhenGameplayIsNull() {
        enemy.setGameplay(null);
        Vector2 initialPos = new Vector2(enemy.getPosition());
        enemy.update(0.1f);
        assertEquals("La position ne doit pas changer si gameplay est nul", initialPos, enemy.getPosition());
    }

    /**
     * Vérifie que la méthode update() ne fait rien si le joueur est nul.
     */
    @Test
    public void testUpdateDoesNothingWhenPlayerIsNull() {
        when(mockGameplay.getPlayer()).thenReturn(null);
        Vector2 initialPos = new Vector2(enemy.getPosition());
        enemy.update(0.1f);
        assertEquals("La position ne doit pas changer si le joueur est nul", initialPos, enemy.getPosition());
    }

    /**
     * Teste si l'ennemi se déplace vers le joueur lorsque le pathfinding n'est pas disponible.
     */
    @Test
    public void testUpdateMovesTowardPlayerOnFallback() {
        // Le pathfinding est forcé à retourner null pour tester le déplacement de secours.
        when(mockGameplay.getDirection(anyInt(), anyInt())).thenReturn(null);
        Vector2 initialPos = new Vector2(enemy.getPosition());

        enemy.update(1.0f); // delta = 1 seconde

        // L'ennemi à (100,100) et le joueur à (200,100) doivent provoquer un déplacement vers la droite.
        float expectedX = initialPos.x + enemy.getMovementSpeed() * 1.0f;
        assertTrue("L'ennemi doit se déplacer vers le joueur sur l'axe X", enemy.getPosition().x > initialPos.x);
        assertEquals("L'ennemi doit se déplacer de la bonne distance", expectedX, enemy.getPosition().x, DELTA);
        assertEquals("L'ennemi ne doit pas se déplacer sur l'axe Y", initialPos.y, enemy.getPosition().y, DELTA);
    }

    /**
     * Teste si l'ennemi s'arrête de bouger lorsqu'il rencontre un mur.
     */
    @Test
    public void testUpdateStopsAtWall() {
        // Simule une collision avec un mur.
        when(mockGameplay.isColliding(any())).thenReturn(true);
        Vector2 initialPos = new Vector2(enemy.getPosition());

        enemy.update(0.1f);

        assertEquals("La position de l'ennemi ne doit pas changer en cas de collision", initialPos, enemy.getPosition());
    }

    /**
     * Teste si la force de séparation est correctement appliquée pour éviter les superpositions d'ennemis.
     */
    @Test
    public void testUpdateAppliesSeparationForce() {
        // Un autre ennemi est placé très près de notre ennemi de test.
        Skull otherEnemy = new Skull();
        otherEnemy.setPosition(new Vector2(101, 101));
        otherEnemy.setAlive(true);

        ArrayList<ClassicEnemy> enemyList = new ArrayList<>();
        enemyList.add(otherEnemy);
        when(mockGameplay.getActiveClassicEnemies()).thenReturn(enemyList);

        // Le joueur est placé loin pour que la force de séparation soit dominante.
        when(mockPlayer.getPosition()).thenReturn(new Vector2(1000, 1000));
        Vector2 initialPos = new Vector2(enemy.getPosition());

        enemy.update(0.1f);

        // L'autre ennemi à (101, 101) doit repousser notre ennemi à (100, 100) vers le bas et la gauche.
        assertTrue("L'ennemi doit être repoussé sur l'axe X", enemy.getPosition().x < initialPos.x);
        assertTrue("L'ennemi doit être repoussé sur l'axe Y", enemy.getPosition().y < initialPos.y);
    }
}