package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link GameOptions}.
 */
@RunWith(JUnit4.class)
public class GameOptionsTest {

    @Mock private Application mockApp;
    @Mock private Graphics mockGraphics;
    @Mock private Preferences mockPrefs;

    /**
     * Initialise les mocks et réinitialise le singleton GameOptions pour les tests.
     *
     * @throws Exception si la réflexion échoue
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Gdx.app = mockApp;
        Gdx.graphics = mockGraphics;

        when(mockApp.getPreferences(anyString())).thenReturn(mockPrefs);

        when(mockPrefs.getInteger(anyString(), anyInt())).thenAnswer(inv -> inv.getArgument(1));
        when(mockPrefs.getBoolean(anyString(), anyBoolean())).thenAnswer(inv -> inv.getArgument(1));

        resetSingleton();
    }

    /**
     * Réinitialise l'instance singleton de GameOptions entre les tests.
     *
     * @throws Exception si la réflexion échoue
     */
    private void resetSingleton() throws Exception {
        Field instance = GameOptions.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Vérifie que getInstance crée et retourne une instance unique et interagit avec Preferences.
     */
    @Test
    public void testGetInstance_CreatesInstance() {
        GameOptions opts = GameOptions.getInstance();
        assertNotNull(opts);
        verify(mockApp).getPreferences("game-options");
    }

    /**
     * Vérifie que setMusicVolume clamp les valeurs en [0,100] et sauvegarde dans les Preferences.
     */
    @Test
    public void testSetMusicVolume_ClampsValues() {
        GameOptions opts = GameOptions.getInstance();

        opts.setMusicVolume(50);
        assertEquals(50, opts.getMusicVolume());
        verify(mockPrefs).putInteger("musicVolume", 50);

        opts.setMusicVolume(150);
        assertEquals(100, opts.getMusicVolume());

        opts.setMusicVolume(-10);
        assertEquals(0, opts.getMusicVolume());
    }

    /**
     * Vérifie que resetToDefault restaure les valeurs par défaut et flush des prefs.
     */
    @Test
    public void testResetToDefault() {
        GameOptions opts = GameOptions.getInstance();

        opts.setKeyUp(Keys.A);
        opts.setMusicVolume(0);
        opts.setFullscreen(false);

        opts.resetToDefault();

        assertEquals(Keys.Z, opts.getKeyUp());
        assertEquals(100, opts.getMusicVolume());
        assertTrue(opts.isFullscreen());

        verify(mockPrefs, atLeastOnce()).flush();
    }

    /**
     * Vérifie que la synchronisation de l'état plein écran met à jour les prefs si nécessaire.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testSyncFullscreenState_UpdatesIfDifferent() throws Exception {
        when(mockGraphics.isFullscreen()).thenReturn(false);

        GameOptions opts = GameOptions.getInstance();

        assertFalse(opts.isFullscreen());
        verify(mockPrefs).putBoolean("fullscreen", false);
    }

    /**
     * Vérifie le mapping d'une touche en nom lisible (ex : Z, Space).
     */
    @Test
    public void testGetKeyName() {
        assertEquals("Z", GameOptions.getKeyName(Keys.Z));
        assertEquals("Space", GameOptions.getKeyName(Keys.SPACE));
    }
}