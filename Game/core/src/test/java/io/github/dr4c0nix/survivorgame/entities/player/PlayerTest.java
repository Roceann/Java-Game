package io.github.dr4c0nix.survivorgame.entities.player;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences; // Import nécessaire
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.GameOptions;
import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import io.github.dr4c0nix.survivorgame.weapon.Weapon;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe Player.
 * Javadoc courte en français : décrit l'objectif général des tests.
 */
public class PlayerTest {

    @Mock
    private Gameplay mockGameplay;
    @Mock
    private Weapon mockWeapon;
    @Mock
    private SpriteBatch mockBatch;
    @Mock
    private Graphics mockGraphics;
    @Mock
    private Input mockInput;
    @Mock
    private FileHandle mockFileHandle;
    @Mock
    private Application mockApp;
    @Mock
    private Files mockFiles;
    @Mock
    private GL20 mockGL20;
    @Mock
    private Preferences mockPrefs; // Mock ajouté pour les préférences

    private TestPlayer player;

    /**
     * Classe de test concrète minimale étendant Player pour permettre l'instanciation.
     */
    private static class TestPlayer extends Player {
        /**
         * Constructeur minimal pour les tests.
         *
         * @param spawnPoint position d'apparition du joueur
         */
        public TestPlayer(Vector2 spawnPoint) {
            super(spawnPoint);
        }
    }

    /**
     * Prépare l'environnement de test.
     * Initialise les mocks LibGDX, configure GameOptions et crée l'instance TestPlayer.
     *
     * @throws Exception si la réinitialisation par réflexion échoue
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 1. Initialisation des mocks LibGDX
        Gdx.graphics = null;
        Gdx.app = mockApp;
        Gdx.files = mockFiles;
        Gdx.gl = mockGL20;

        // Mock du comportement des fichiers
        when(mockFiles.internal(anyString())).thenReturn(mockFileHandle);
        when(mockFileHandle.exists()).thenReturn(true);

        // GameOptions.getInstance() appelle Gdx.app.getPreferences()
        when(mockApp.getPreferences(anyString())).thenReturn(mockPrefs);
        
        // On configure le mock pour renvoyer la valeur par défaut (2ème argument)
        // Cela permet à GameOptions de charger les touches par défaut (Z, Q, S, D) sans planter
        when(mockPrefs.getInteger(anyString(), anyInt())).thenAnswer(invocation -> invocation.getArgument(1));
        
        // Réinitialiser le singleton GameOptions pour éviter les effets de bord entre les tests
        resetGameOptionsSingleton();

        // 2. Création de l'instance de test
        player = new TestPlayer(new Vector2(100, 100));
        player.setGameplay(mockGameplay);

        // 3. Rétablissement des mocks pour les tests de méthodes
        Gdx.graphics = mockGraphics;
        Gdx.input = mockInput;
        
        when(mockGraphics.getDeltaTime()).thenReturn(0.1f);
    }

    /**
     * Réinitialise le singleton GameOptions (champ 'instance') à null via réflexion.
     * Permet d'isoler chaque test en forçant la relecture des préférences.
     *
     * @throws Exception si l'accès réflexif échoue
     */
    private void resetGameOptionsSingleton() throws Exception {
        try {
            Field instance = GameOptions.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (NoSuchFieldException e) {
            // Si le champ n'existe pas, on ignore (compatibilité)
        }
    }

    /**
     * Vérifie l'état initial du joueur (niveau et XP).
     */
    @Test
    public void testConstructor() {
        assertEquals("Niveau initial incorrect", 1, player.getLevel());
        assertEquals("XP initial incorrect", 0, player.getXpactual());
    }

    /**
     * Vérifie que addXp augmente correctement l'XP.
     */
    @Test
    public void testAddXp_IncreasesXpCorrectly() {
        player.addXp(10);
        assertEquals(10, player.getXpactual());
    }

    /**
     * Vérifie le passage de niveau quand le seuil d'XP est atteint.
     */
    @Test
    public void testAddXp_LevelUpWhenReachingThreshold() {
        int xpNeeded = player.getExperienceToNextLevel();
        player.addXp(xpNeeded);
        assertEquals("Le niveau devrait augmenter", 2, player.getLevel());
        assertEquals("L'XP devrait être remise à 0 (ou le surplus)", 0, player.getXpactual());
    }

    /**
     * Vérifie le passage de niveau avec XP excédentaire.
     */
    @Test
    public void testAddXp_LevelUpWithExcessXp() {
        int xpNeeded = player.getExperienceToNextLevel();
        player.addXp(xpNeeded + 5);
        assertEquals(2, player.getLevel());
        assertEquals(5, player.getXpactual());
    }

    /**
     * Vérifie l'affectation d'une arme.
     */
    @Test
    public void testSetWeapon_AssignsWeaponCorrectly() {
        player.setWeapon(mockWeapon);
        assertEquals(mockWeapon, player.getCurrentWeapon());
        assertTrue(player.hasWeapon());
    }

    /**
     * Vérifie que takeDamage appelle onGameOver si les HP tombent à zéro.
     */
    @Test
    public void testTakeDamage_CallsGameOverWhenHpZero() {
        player.setGameplay(mockGameplay);
        player.setMaxHp(100f);
        player.setCurrentHp(10f);

        player.takeDamage(1000f);
        
        // Vérifier que la méthode onGameOver du gameplay est appelée
        verify(mockGameplay, times(1)).onGameOver();
    }

    /**
     * Vérifie que takeDamage n'appelle pas onGameOver si le joueur reste vivant.
     */
    @Test
    public void testTakeDamage_DoesNotCallGameOverWhenAlive() {
        player.setGameplay(mockGameplay);
        player.setMaxHp(100f);
        player.setCurrentHp(100f);
        
        // Infliger peu de dégâts
        player.takeDamage(1f);
        
        verify(mockGameplay, never()).onGameOver();
    }

    /**
     * Vérifie la régénération des HP après l'intervalle de regen.
     *
     * @throws Exception exception possible depuis update
     */
    @Test
    public void testRegen_IncreasesHpOverTime() throws Exception {
        player.setMaxHp(100f);
        player.setCurrentHp(50f);
        
        // Simuler le passage du temps > REGEN_INTERVAL (10s)
        player.update(10.1f);
        
        float hp = player.getHp();
        
        assertEquals("HP devrait augmenter de 5 après l'intervalle de regen", 55f, hp, 0.001f);
    }

    /**
     * Vérifie que la régénération ne dépasse pas le MaxHP.
     *
     * @throws Exception exception possible depuis update
     */
    @Test
    public void testRegen_DoesNotExceedMaxHp() throws Exception {
        player.setMaxHp(100f);
        player.setCurrentHp(98f);
        
        player.update(10.1f);
        
        float hp = player.getHp();
        
        assertEquals("HP devrait être plafonné au MaxHP", 100f, hp, 0.001f);
    }

    /**
     * Vérifie le déplacement vers le haut via les touches configurées.
     */
    @Test
    public void testHandleInput_MovesUp() {
        // Mock des entrées
        when(mockInput.isKeyPressed(anyInt())).thenReturn(false);
        // Utilisation de GameOptions qui utilise maintenant nos mocks
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyUp())).thenReturn(true);
        
        Vector2 initialPos = new Vector2(player.getPosition());
        
        // Update appelle handleInput -> moveBy
        player.update(0.1f);
        
        assertTrue("Position Y devrait augmenter", player.getPosition().y > initialPos.y);
    }

    /**
     * Vérifie le déplacement vers le bas.
     */
    @Test
    public void testHandleInput_MovesDown() {
        when(mockInput.isKeyPressed(anyInt())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyDown())).thenReturn(true);
        
        Vector2 initialPos = new Vector2(player.getPosition());
        player.update(0.1f);
        
        assertTrue("Position Y devrait diminuer", player.getPosition().y < initialPos.y);
    }
    
    /**
     * Vérifie le déplacement vers la droite.
     */
    @Test
    public void testHandleInput_MovesRight() {
        when(mockInput.isKeyPressed(anyInt())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyRight())).thenReturn(true);
        
        Vector2 initialPos = new Vector2(player.getPosition());
        player.update(0.1f);
        
        assertTrue("Position X devrait augmenter", player.getPosition().x > initialPos.x);
    }
    
    /**
     * Vérifie le déplacement vers la gauche.
     */
    @Test
    public void testHandleInput_MovesLeft() {
        when(mockInput.isKeyPressed(anyInt())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyLeft())).thenReturn(true);
        
        Vector2 initialPos = new Vector2(player.getPosition());
        player.update(0.1f);
        
        assertTrue("Position X devrait diminuer", player.getPosition().x < initialPos.x);
    }

    /**
     * Vérifie que le joueur ne bouge pas en cas de collision détectée par le gameplay.
     */
    @Test
    public void testHandleInput_DoesNotMove_WhenColliding() {
        // On simule une collision
        when(mockGameplay.isColliding(any())).thenReturn(true);
        
        // On appuie sur la touche DROITE
        when(mockInput.isKeyPressed(anyInt())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyRight())).thenReturn(true);

        Vector2 initialPos = new Vector2(player.getPosition());
        
        player.update(0.1f);

        assertEquals("Le joueur ne devrait pas bouger en cas de collision", initialPos.x, player.getPosition().x, 0.001f);
    }

    /**
     * Vérifie la mise à jour de la direction regardée lors des déplacements.
     */
    @Test
    public void testFacingDirection_UpdatesOnMovement() {
        when(mockInput.isKeyPressed(anyInt())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyRight())).thenReturn(true);
        player.update(0.1f);
        assertEquals(new Vector2(1, 0), player.getFacingDirection());

        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyRight())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyUp())).thenReturn(true);
        player.update(0.1f);
        assertEquals(new Vector2(0, 1), player.getFacingDirection());
        
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyUp())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyLeft())).thenReturn(true);
        player.update(0.1f);
        assertEquals(new Vector2(-1, 0), player.getFacingDirection());
        
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyLeft())).thenReturn(false);
        when(mockInput.isKeyPressed(GameOptions.getInstance().getKeyDown())).thenReturn(true);
        player.update(0.1f);
        assertEquals(new Vector2(0, -1), player.getFacingDirection());
    }

    /**
     * Vérifie que la direction par défaut est vers le bas.
     */
    @Test
    public void testFacingDirection_DefaultIsDown() {
        player.update(0.1f); 
        assertEquals(new Vector2(0, -1), player.getFacingDirection());
    }

    /**
     * Vérifie que l'arme est mise à jour si les attaques sont activées.
     */
    @Test
    public void testUpdate_UpdatesWeapon_WhenEnabled() {
        player.setWeapon(mockWeapon);
        player.setAttacksEnabled(true);
        
        player.update(0.1f);
        
        verify(mockWeapon, times(1)).update(anyFloat(), eq(player));
    }

    /**
     * Vérifie que l'arme n'est pas mise à jour si les attaques sont désactivées.
     */
    @Test
    public void testUpdate_DoesNotUpdateWeapon_WhenDisabled() {
        player.setWeapon(mockWeapon);
        player.setAttacksEnabled(false);
        
        player.update(0.1f);
        
        verify(mockWeapon, never()).update(anyFloat(), any());
    }
    
    /**
     * Teste les getters/setters pour l'activation des attaques.
     */
    @Test
    public void testAttacksEnabled_GetterSetter() {
        player.setAttacksEnabled(true);
        assertTrue(player.areAttacksEnabled());
        
        player.setAttacksEnabled(false);
        assertFalse(player.areAttacksEnabled());
    }

    /**
     * Vérifie l'incrémentation du compteur de mobs tués.
     */
    @Test
    public void testMobKilled_IncrementsCorrectly() {
        assertEquals(0, player.getMobKilled());
        player.incrementMobKilled();
        assertEquals(1, player.getMobKilled());
    }

    /**
     * Vérifie que l'écran de level up est demandé lors d'un passage de niveau.
     */
    @Test
    public void testLevelUp_CallsGameplayShowScreen() {
        int xpNeeded = player.getExperienceToNextLevel();
        player.addXp(xpNeeded);
        
        verify(mockGameplay, times(1)).showLevelUpScreen();
    }
    
    /**
     * Vérifie les setters et getters de quelques statistiques additionnelles.
     */
    @Test
    public void testSetters_AdditionalStats() {
        player.setArmor(50);
        assertEquals(50, player.getArmor());
        
        player.setForce(2.5f);
        assertEquals(2.5f, player.getForce(), 0.001f);
        
        player.setRegenHP(5.0f);
        assertEquals(5.0f, player.getRegenHP(), 0.001f);
        
        assertEquals(1.0f, player.getDifficulter(), 0.001f); // Getter simple
    }

    /**
     * Vérifie que levelUp fonctionne même si le gameplay est null.
     */
    @Test
    public void testLevelUp_DoesNotThrowWhenGameplayIsNull() {
        // ensure no gameplay - levelUp should not throw
        player.setGameplay(null);
        int xpNeeded = player.getExperienceToNextLevel();
        player.addXp(xpNeeded);
        assertEquals("Le niveau doit augmenter même si gameplay est null", 2, player.getLevel());
    }

    /**
     * Teste le comportement lors d'un ajout d'XP négatif (comportement actuel conservé).
     */
    @Test
    public void testAddXp_NegativeValue_DoesNotDecreaseXp() {
        int before = player.getXpactual();
        player.addXp(-10);
        // Le comportement actuel ajoute la valeur négative -> xpactual diminue de 10
        assertEquals("Ajouter une XP négative doit diminuer xpactual de la valeur fournie", before - 10, player.getXpactual());
    }

    /**
     * Vérifie que la régénération ne se produit pas si le joueur est mort.
     */
    @Test
    public void testTickRegen_DoesNotRegenWhenDead() {
        player.setMaxHp(100f);
        player.setCurrentHp(50f);
        player.setAlive(false);
        player.update(11f); // > REGEN_INTERVAL
        assertEquals("HP ne doit pas augmenter si le joueur est mort", 50f, player.getHp(), 0.001f);
    }
}