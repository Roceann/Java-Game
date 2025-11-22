package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link GameOverScreen}.
 */
@RunWith(JUnit4.class)
public class GameOverScreenTest {

    private GameOverScreen gameOverScreen;

    @Mock private Stage mockStage;
    @Mock private SpriteBatch mockBatch;
    @Mock private BitmapFont mockFont;
    @Mock private BitmapFont mockTitleFont;
    @Mock private Texture mockOverlayTex;
    @Mock private Texture mockPanelTex;
    @Mock private Texture mockBgTex;

    /**
     * Initialise les mocks et crée un mock de GameOverScreen pour les tests.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gameOverScreen = mock(GameOverScreen.class);
    } 

    /**
     * Vérifie que dispose libère toutes les ressources graphiques attendues.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testDispose_CleansUpAllResources() throws Exception {
        injectField("stage", mockStage);
        injectField("batch", mockBatch);
        injectField("font", mockFont);
        injectField("titleFont", mockTitleFont);
        injectField("overlayTex", mockOverlayTex);
        injectField("panelTex", mockPanelTex);
        injectField("gameOverBg", mockBgTex);

        doCallRealMethod().when(gameOverScreen).dispose();

        gameOverScreen.dispose();

        verify(mockStage).dispose();
        verify(mockBatch).dispose();
        verify(mockFont).dispose();
        verify(mockTitleFont).dispose();
        verify(mockOverlayTex).dispose();
        verify(mockPanelTex).dispose();
        verify(mockBgTex).dispose();
    }

    /**
     * Injecte un champ private dans GameOverScreen via réflexion.
     *
     * @param name  nom du champ
     * @param value valeur à injecter
     * @throws Exception si la réflexion échoue
     */
    private void injectField(String name, Object value) throws Exception {
        Field field = GameOverScreen.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(gameOverScreen, value);
    }
}