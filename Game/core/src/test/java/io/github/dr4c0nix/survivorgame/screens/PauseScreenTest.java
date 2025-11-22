package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.dr4c0nix.survivorgame.Main;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link PauseScreen}.
 */
@RunWith(JUnit4.class)
public class PauseScreenTest {

    private PauseScreen pauseScreen;

    @Mock private Main mockMain;
    @Mock private Gameplay mockGameplay;
    @Mock private Player mockPlayer;
    @Mock private Stage mockStage;
    @Mock private Texture mockOverlayTex;
    @Mock private Texture mockPanelTex;
    @Mock private BitmapFont mockFont;
    @Mock private Label mockLabel;

    /**
     * Prépare les mocks et injette les dépendances nécessaires.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        pauseScreen = mock(PauseScreen.class);

        try {
            injectField("main", mockMain);
            injectField("gameplay", mockGameplay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie que updateStatsText remplit correctement un Label avec les statistiques du joueur.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testUpdateStatsText_PopulatesLabel() throws Exception {
        when(mockGameplay.getPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getLevel()).thenReturn(5);
        when(mockPlayer.getXpactual()).thenReturn(100);
        when(mockPlayer.getExperienceToNextLevel()).thenReturn(500);
        when(mockPlayer.getHp()).thenReturn(50f);
        when(mockPlayer.getMaxHp()).thenReturn(100f);
        when(mockPlayer.getArmor()).thenReturn(10);
        when(mockPlayer.getForce()).thenReturn(1.5f);
        when(mockPlayer.getCritChance()).thenReturn(0.1f);
        when(mockPlayer.getCritDamage()).thenReturn(2.0f);
        when(mockPlayer.getRegenHP()).thenReturn(1.0f);
        when(mockPlayer.getMobKilled()).thenReturn(0);
        when(mockPlayer.getCurrentWeapon()).thenReturn(null);

        PauseScreen realPauseScreen = new PauseScreen(mockMain, mockGameplay);
        realPauseScreen.updateStatsText(mockLabel);

        verify(mockLabel).setText(argThat(argument -> {
            String text = argument.toString();
            String normalized = text.replace(',', '.');
            return normalized.contains("XP: 100 / 500") &&
                   normalized.contains("HP: 50 / 100") &&
                   normalized.contains("Armor: 10") &&
                   normalized.contains("Crit Chance: 0.1%") &&
                   normalized.contains("Crit Damage: x2.00") &&
                   normalized.contains("HP Regen: 1.0 hp/10sec") &&
                   normalized.contains("Mobs Killed: 0") &&
                   normalized.contains("Equipped Weapon: none");
        }));
    }

    /**
     * Vérifie que updateStatsText ne modifie pas le label si le joueur est null.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testUpdateStatsText_HandlesNullPlayer() throws Exception {
        when(mockGameplay.getPlayer()).thenReturn(null);
        PauseScreen realPauseScreen = new PauseScreen(mockMain, mockGameplay);

        Method method = PauseScreen.class.getDeclaredMethod("updateStatsText", Label.class);
        method.setAccessible(true);
        method.invoke(realPauseScreen, mockLabel);

        verify(mockLabel, never()).setText(anyString());
    }

    /**
     * Vérifie que dispose libère correctement les ressources graphiques.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testDispose_CleansUpResources() throws Exception {
        injectField("stage", mockStage);
        injectField("overlayTex", mockOverlayTex);
        injectField("panelTex", mockPanelTex);
        injectField("font", mockFont);

        doCallRealMethod().when(pauseScreen).dispose();

        pauseScreen.dispose();

        verify(mockStage).dispose();
        verify(mockOverlayTex).dispose();
        verify(mockPanelTex).dispose();
        verify(mockFont).dispose();
    }

    /**
     * Injecte un champ privé via réflexion dans PauseScreen (utilisé pour les tests).
     *
     * @param fieldName nom du champ
     * @param value     valeur à injecter
     * @throws Exception si la réflexion échoue
     */
    private void injectField(String fieldName, Object value) throws Exception {
        Field field = PauseScreen.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(pauseScreen, value);
    }
}