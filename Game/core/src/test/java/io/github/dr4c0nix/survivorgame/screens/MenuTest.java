package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.dr4c0nix.survivorgame.GameOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link Menu}.
 */
@RunWith(JUnit4.class)
public class MenuTest {

    private Menu menu;

    @Mock private Stage mockStage;
    @Mock private BitmapFont mockFont;
    @Mock private Texture mockButtonTexture;
    @Mock private Music mockMusic;

    @Mock private Application mockApp;
    @Mock private Preferences mockPreferences;
    @Mock private Graphics mockGraphics;

    /**
     * Prépare l'environnement Gdx et réinitialise GameOptions singleton pour les tests.
     *
     * @throws Exception si la réflexion échoue
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Gdx.app = mockApp;
        Gdx.graphics = mockGraphics;

        when(mockApp.getPreferences(anyString())).thenReturn(mockPreferences);
        when(mockPreferences.getInteger(anyString(), anyInt())).thenAnswer(invocation -> invocation.getArgument(1));
        when(mockPreferences.getBoolean(anyString(), anyBoolean())).thenAnswer(invocation -> invocation.getArgument(1));

        when(mockGraphics.isFullscreen()).thenReturn(false);

        resetGameOptionsSingleton();

        menu = mock(Menu.class);
    }

    /**
     * Réinitialise le singleton GameOptions entre les tests.
     *
     * @throws Exception si la réflexion échoue
     */
    private void resetGameOptionsSingleton() throws Exception {
        Field instanceField = GameOptions.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * Vérifie que la méthode met le volume de la musique du menu selon GameOptions.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testUpdateMenuMusicVolume() throws Exception {
        injectField("menuMusic", mockMusic);

        GameOptions.getInstance().setMusicVolume(50);

        doCallRealMethod().when(menu).updateMenuMusicVolume();

        menu.updateMenuMusicVolume();

        verify(mockMusic).setVolume(0.5f);
    }

    /**
     * Vérifie que dispose nettoie les ressources et arrête la musique.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testDispose_CleansUp() throws Exception {
        injectField("stage", mockStage);
        injectField("font", mockFont);
        injectField("buttonTexture", mockButtonTexture);
        injectField("menuMusic", mockMusic);
        injectField("currentButtons", new ArrayList<TextButton>());

        doCallRealMethod().when(menu).dispose();
        doCallRealMethod().when(menu).clearMenu();

        menu.dispose();

        verify(mockStage).dispose();
        verify(mockFont).dispose();
        verify(mockButtonTexture).dispose();
        verify(mockMusic).stop();
        verify(mockMusic).dispose();
    }

    /**
     * Injecte un champ privé via réflexion dans Menu.
     *
     * @param fieldName nom du champ
     * @param value     valeur à injecter
     * @throws Exception si la réflexion échoue
     */
    private void injectField(String fieldName, Object value) throws Exception {
        Field field = Menu.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(menu, value);
    }
}