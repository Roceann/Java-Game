package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Classe de test pour la classe OrbXp.
 * Les tests vérifient l'initialisation, la gestion de l'état et la réinitialisation
 * pour le pooling.
 */
public class OrbXpTest {

    private OrbXp orb;

    /**
     * Met en place l'environnement de test avant chaque test.
     */
    @Before
    public void setUp() {
        // Empêche le constructeur de créer une vraie texture
        Gdx.files = null;
        orb = new OrbXp(100);
    }

    /**
     * Teste si le constructeur initialise correctement l'orbe avec sa valeur d'XP
     * et la définit comme inactive par défaut.
     */
    @Test
    public void testConstructor_InitializesProperties() {
        assertEquals("La valeur d'XP initiale est incorrecte", 100, orb.getXpValue());
        assertFalse("L'orbe doit être inactive après sa construction", orb.isAlive());
        assertEquals("La taille de la hitbox est incorrecte", OrbXp.getOrbSize(), orb.getHitbox().width, 0.01);
    }

    /**
     * Teste si la méthode `setXpValue` met à jour correctement la valeur d'XP.
     */
    @Test
    public void testSetXpValue_UpdatesValue() {
        orb.setXpValue(250);
        assertEquals("La valeur d'XP n'a pas été mise à jour", 250, orb.getXpValue());
    }

    /**
     * Teste si la méthode `setAlive` met à jour correctement l'état de vie de l'orbe.
     */
    @Test
    public void testSetAlive_UpdatesIsAliveState() {
        orb.setAlive(true);
        assertTrue("L'orbe devrait être active", orb.isAlive());

        orb.setAlive(false);
        assertFalse("L'orbe devrait être inactive", orb.isAlive());
    }

    /**
     * Teste si la méthode `reset` réinitialise correctement l'état de l'orbe
     * pour sa réutilisation dans un pool.
     */
    @Test
    public void testReset_ResetsPropertiesToDefault() {
        orb.setAlive(true);
        orb.setPosition(new Vector2(100, 100));
        orb.setXpValue(500);

        orb.reset();

        assertFalse("isAlive doit être false après reset", orb.isAlive());
        assertEquals("xpValue doit être 0 après reset", 0, orb.getXpValue());
        assertEquals("La position doit être réinitialisée à (0,0)", 0, orb.getPosition().x, 0.01);
    }

    /**
     * Teste que la méthode `update` existe mais ne fait rien, comme prévu.
     */
    @Test
    public void testUpdate_DoesNothing() {
        Vector2 initialPosition = orb.getPosition().cpy();
        orb.update(1f);
        assertEquals("La position ne doit pas changer après update", initialPosition, orb.getPosition());
    }
}