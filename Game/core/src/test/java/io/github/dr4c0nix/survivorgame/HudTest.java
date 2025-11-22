package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class HudTest {

    private Hud hud;

    @Mock private ShapeRenderer mockShape;
    @Mock private BitmapFont mockFont;
    @Mock private Player mockPlayer;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hud = mock(Hud.class);
    }

    @Test
    public void testUpdateHealth() throws Exception {
        doCallRealMethod().when(hud).updateHealth(anyFloat(), anyFloat());

        hud.updateHealth(50f, 100f);

        // Ici on attend des float, donc on cast en Float
        assertEquals(50f, (float) getField("hpCur"), 0.001f);
        assertEquals(100f, (float) getField("hpMax"), 0.001f);
    }

    @Test
    public void testSetPlayer() throws Exception {
        doCallRealMethod().when(hud).setPlayer(any(Player.class));
        
        hud.setPlayer(mockPlayer);
        
        // Ici on attend un objet Player, pas besoin de cast en float
        assertEquals(mockPlayer, getField("player"));
    }

    @Test
    public void testDispose() throws Exception {
        injectField("shape", mockShape);
        injectField("font", mockFont);

        doCallRealMethod().when(hud).dispose();

        hud.dispose();

        verify(mockShape).dispose();
        verify(mockFont).dispose();
    }

    private void injectField(String name, Object value) throws Exception {
        Field field = Hud.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(hud, value);
    }

    // CORRECTION ICI : Retourne Object au lieu de float pour être générique
    private Object getField(String name) throws Exception {
        Field field = Hud.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(hud);
    }
}