package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.PathfindingMap;
import io.github.dr4c0nix.survivorgame.entities.SpawnManager;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link Gameplay}.
 */
public class GameplayTest {

    private Gameplay gameplay;

    @Mock
    private Player mockPlayer;
    @Mock
    private SpawnManager mockSpawnManager;
    @Mock
    private PathfindingMap mockPathfindingMap;
    @Mock
    private Input mockInput;
    @Mock
    private LevelUp mockLevelUp;

    /**
     * Configure les mocks et initialise l'instance de gameplay utilisée par les tests.
     * Initialise également Gdx.input.
     *
     * @throws Exception si une réflexion échoue
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Gdx.input = mockInput;

        gameplay = mock(Gameplay.class);

        doCallRealMethod().when(gameplay).setIsPaused(anyBoolean());
        doCallRealMethod().when(gameplay).getIsPaused();
        doCallRealMethod().when(gameplay).isColliding(any(Rectangle.class));
        doCallRealMethod().when(gameplay).updateDifficulty(anyFloat());
        doCallRealMethod().when(gameplay).getDirection(anyInt(), anyInt());
        doCallRealMethod().when(gameplay).onLevelUpOverlayClosed();
        doCallRealMethod().when(gameplay).showLevelUpScreen();

        injectField("player", mockPlayer);
        injectField("spawnManager", mockSpawnManager);
        injectField("pathfindingMap", mockPathfindingMap);
        injectField("levelUpOverlay", mockLevelUp);
    }

    /**
     * Vérifie le formatage du temps en mm:ss.
     */
    @Test
    public void testFormatTime() {
        assertEquals("00:00", Gameplay.formatTime(0));
        assertEquals("00:59", Gameplay.formatTime(59));
        assertEquals("01:00", Gameplay.formatTime(60));
        assertEquals("01:30", Gameplay.formatTime(90));
        assertEquals("10:05", Gameplay.formatTime(605));
    }

    /**
     * Vérifie qu'une collision est détectée quand les rectangles s'intersectent.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testIsColliding_ReturnsTrueOnIntersection() throws Exception {
        ArrayList<Rectangle> collisions = new ArrayList<>();
        collisions.add(new Rectangle(0, 0, 32, 32));
        collisions.add(new Rectangle(100, 100, 32, 32));
        injectField("collisionRectangles", collisions);

        Rectangle playerRect = new Rectangle(10, 10, 10, 10);
        assertTrue("Le joueur devrait être en collision", gameplay.isColliding(playerRect));
    }

    /**
     * Vérifie qu'aucune collision n'est signalée quand l'espace est libre.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testIsColliding_ReturnsFalseOnFreeSpace() throws Exception {
        ArrayList<Rectangle> collisions = new ArrayList<>();
        collisions.add(new Rectangle(0, 0, 32, 32));
        injectField("collisionRectangles", collisions);

        Rectangle playerRect = new Rectangle(50, 50, 10, 10);
        assertFalse("Le joueur ne devrait pas être en collision", gameplay.isColliding(playerRect));
    }

    /**
     * Vérifie que isColliding gère une liste nulle sans lancer d'exception.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testIsColliding_HandlesNullList() throws Exception {
        injectField("collisionRectangles", null);
        Rectangle rect = new Rectangle(0, 0, 10, 10);
        assertFalse(gameplay.isColliding(rect));
    }

    /**
     * Vérifie que la difficulté augmente (ex. appel de setSpawnInterval) selon le temps écoulé.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testUpdateDifficulty_IncreasesSpawnRate() throws Exception {
        injectField("elapsedTime", 60f);
        when(mockPlayer.getDifficulter()).thenReturn(1.0f);

        gameplay.updateDifficulty(0.1f);

        verify(mockSpawnManager).setSpawnInterval(anyFloat());
    }

    /**
     * Vérifie que updateDifficulty fonctionne même pour de grandes valeurs de temps (pas d'exception).
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testUpdateDifficulty_CapsMinimumInterval() throws Exception {
        injectField("elapsedTime", 6000f);
        when(mockPlayer.getDifficulter()).thenReturn(100f);

        gameplay.updateDifficulty(0.1f);

        // Vérifie simplement que ça ne plante pas
    }

    /**
     * Vérifie que getDirection délègue au pathfinding avec la bonne conversion de coordonnées.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testGetDirection_DelegatesToPathfinding() throws Exception {
        Vector2 expectedDir = new Vector2(1, 0);
        when(mockPathfindingMap.getDirection(anyInt(), anyInt())).thenReturn(expectedDir);

        Vector2 result = gameplay.getDirection(64, 32);

        verify(mockPathfindingMap).getDirection(2, 1);
        assertEquals(expectedDir, result);
    }

    /**
     * Vérifie les accesseurs setIsPaused / getIsPaused.
     */
    @Test
    public void testSetIsPaused() {
        gameplay.setIsPaused(true);
        assertTrue(gameplay.getIsPaused());

        gameplay.setIsPaused(false);
        assertFalse(gameplay.getIsPaused());
    }

    /**
     * Vérifie que la fermeture de l'overlay de niveau remet le jeu en marche et réinitialise l'input processor.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testOnLevelUpOverlayClosed_ResumesGame() throws Exception {
        gameplay.setIsPaused(true);

        gameplay.onLevelUpOverlayClosed();

        assertFalse("Le jeu ne devrait plus être en pause", gameplay.getIsPaused());
        verify(mockInput).setInputProcessor(null);
    }

    /**
     * Vérifie que l'affichage de l'écran de level up met le jeu en pause et définit l'input processor.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testShowLevelUpScreen_PausesGame() throws Exception {
        com.badlogic.gdx.scenes.scene2d.Stage mockStage = mock(com.badlogic.gdx.scenes.scene2d.Stage.class);
        when(mockLevelUp.getStage()).thenReturn(mockStage);

        gameplay.showLevelUpScreen();

        assertTrue("Le jeu devrait être en pause", gameplay.getIsPaused());
        verify(mockLevelUp).show();
        verify(mockInput).setInputProcessor(mockStage);
    }

    /**
     * Injecte une valeur privée via réflexion dans la classe Gameplay.
     *
     * @param fieldName nom du champ privé
     * @param value     valeur à assigner
     * @throws Exception si la réflexion échoue
     */
    private void injectField(String fieldName, Object value) throws Exception {
        Field field = Gameplay.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(gameplay, value);
    }
}