package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.enemy.ClassicEnemy;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Classe de test pour EntityFactory.
 * Les tests vérifient la logique de création, gestion des pools d'objets
 * et de libération des différentes entités (ennemis, orbes, projectiles).
 */
public class EntityFactoryTest {

    @Mock
    private Gameplay mockGameplay;

    private EntityFactory entityFactory;

    /**
     * Met en place l'environnement de test avant chaque test.
     * Simule l'environnement LibGDX pour que la factory puisse charger des textures mockées.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Gdx.files = null;
        
        entityFactory = new EntityFactory(mockGameplay);
    }

    @After
    public void tearDown() {
        // Nettoyer le mock statique
        Gdx.files = null;
    }

    /**
     * Teste si la factory peut obtenir un ennemi à partir du pool, l'activer
     * et l'ajouter à la liste des ennemis actifs.
     */
    @Test
    public void testObtainAndReleaseEnemy() {
        assertEquals("La liste des ennemis actifs doit être vide au début", 0, entityFactory.getActiveEnemies().size());
        
        ClassicEnemy enemy = entityFactory.obtainEnemy("Orc", new Vector2(100, 100));

        assertNotNull("L'ennemi obtenu ne doit pas être nul", enemy);
        assertTrue("L'ennemi doit être dans la liste des actifs", entityFactory.getActiveEnemies().contains(enemy));
        assertEquals("Il doit y avoir 1 ennemi actif", 1, entityFactory.getActiveEnemies().size());
        assertTrue("L'ennemi doit être actif", enemy.isAlive());
        assertEquals("La position de l'ennemi est incorrecte", 100, enemy.getPosition().x, 0.01);

        entityFactory.releaseEnemy(enemy);
        assertFalse("L'ennemi ne doit plus être dans la liste des actifs", entityFactory.getActiveEnemies().contains(enemy));
        assertEquals("La liste des ennemis actifs doit être vide après libération", 0, entityFactory.getActiveEnemies().size());
    }

    /**
     * Teste si la factory peut obtenir une orbe d'XP, l'activer et l'ajouter
     * à la liste des orbes actives.
     */
    @Test
    public void testObtainAndReleaseOrbXp() {
        assertEquals("La liste des orbes actives doit être vide au début", 0, entityFactory.getActiveOrbs().size());

        OrbXp orb = entityFactory.obtainOrbXp(new Vector2(50, 50), 20, OrbXp.getDefaultOrbSize());
    
        assertNotNull("L'orbe obtenue ne doit pas être nulle", orb);
        assertTrue("L'orbe doit être dans la liste des actives", entityFactory.getActiveOrbs().contains(orb));
        assertEquals("Il doit y avoir 1 orbe active", 1, entityFactory.getActiveOrbs().size());
        assertTrue("L'orbe doit être active", orb.isAlive());
        assertEquals("La valeur d'XP de l'orbe est incorrecte", 20, orb.getXpValue());

        entityFactory.releaseOrbXp(orb);
        assertFalse("L'orbe ne doit plus être dans la liste des actives", entityFactory.getActiveOrbs().contains(orb));
        assertEquals("La liste des orbes actives doit être vide après libération", 0, entityFactory.getActiveOrbs().size());
    }

    /**
     * Teste si la factory peut obtenir un projectile, l'initialiser et l'ajouter
     * à la liste des projectiles actifs.
     */
    @Test
    public void testObtainAndReleaseProjectile() {
        assertEquals("La liste des projectiles actifs doit être vide au début", 0, entityFactory.getActiveProjectiles().size());
        LivingEntity mockSource = mock(LivingEntity.class);

        Projectile p = entityFactory.obtainProjectile(new Vector2(0,0), new Vector2(1,0), 100, 200, 10, 1f, 16, 16, "proj.png", mockSource);

        assertNotNull("Le projectile obtenu ne doit pas être nul", p);
        assertTrue("Le projectile doit être dans la liste des actifs", entityFactory.getActiveProjectiles().contains(p));
        assertEquals("Il doit y avoir 1 projectile actif", 1, entityFactory.getActiveProjectiles().size());
        assertTrue("Le projectile doit être actif", p.isAlive());

        entityFactory.releaseProjectile(p);
        assertFalse("Le projectile ne doit plus être dans la liste des actifs", entityFactory.getActiveProjectiles().contains(p));
        assertEquals("La liste des projectiles actifs doit être vide après libération", 0, entityFactory.getActiveProjectiles().size());
    }

    /**
     * Teste que la méthode `updateProjectiles` retire bien les projectiles inactifs.
     */
    @Test
    public void testUpdateProjectiles_RemovesInactiveProjectiles() {
        LivingEntity mockSource = mock(LivingEntity.class);
        entityFactory.obtainProjectile(new Vector2(0,0), new Vector2(1,0), 100, 50, 10, 1f, 16, 16, "proj.png", mockSource);
        
        assertEquals("Il doit y avoir 1 projectile actif", 1, entityFactory.getActiveProjectiles().size());

        // Mettre à jour jusqu'à ce que le projectile dépasse sa portée
        entityFactory.updateProjectiles(0.6f);

        assertEquals("Le projectile inactif aurait dû être retiré", 0, entityFactory.getActiveProjectiles().size());
    }

    /**
     * Vérifie que les types d'ennemis disponibles et les tailles de hitbox sont exposés.
     */
    @Test
    public void testGetAvailableEnemyTypesAndHitboxSize() {
        ArrayList<String> types = entityFactory.getAvailableEnemyTypes();
        // initializePools crée Orc, Demon, Skull keys
        assertTrue("Doit contenir au moins le type Orc", types.contains("Orc"));

        // Force la création d'un prototype dans la pool afin que registerEnemyPrototype ait été appelé
        ClassicEnemy prototype = entityFactory.obtainEnemy("Orc", new Vector2(0f, 0f));
        // libérer immédiatement
        entityFactory.releaseEnemy(prototype);

        Vector2 size = entityFactory.getEnemyHitboxSize("Orc");
        assertNotNull("La taille de hitbox pour Orc ne doit pas être nulle", size);
        assertTrue("Largeur positive attendue", size.x > 0);
        assertTrue("Hauteur positive attendue", size.y > 0);
    }

    /**
     * Vérifie que dispose nettoie les pools et rend getAvailableEnemyTypes vide.
     */
    @Test
    public void testDispose_ClearsPoolsAndCreated() {
        ArrayList<String> before = entityFactory.getAvailableEnemyTypes();
        assertFalse("Doit y avoir des types disponibles avant dispose", before.isEmpty());

        entityFactory.dispose();

        ArrayList<String> after = entityFactory.getAvailableEnemyTypes();
        assertTrue("Après dispose, il ne doit plus y avoir de types disponibles", after.isEmpty());
    }
}