package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link WoodStick}.
 *
 * Même structure que {@link SwordTest} : vérifie la position de spawn du projectile
 * et la direction par défaut lorsque la direction du joueur est nulle.
 * Utilise un {@link EntityFactory} mocké pour capturer la création des projectiles.
 */
public class WoodStickTest {

    @Mock
    private EntityFactory mockEntityFactory;
    @Mock
    private Player mockPlayer;

    private WoodStick woodStick;

    /**
     * Initialise les mocks et configure un joueur factice avant chaque test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        woodStick = new WoodStick(mockEntityFactory);

        Rectangle playerHitbox = new Rectangle(100, 100, 32, 32);
        when(mockPlayer.getHitbox()).thenReturn(playerHitbox);
        when(mockPlayer.getForce()).thenReturn(1.0f);
    }

    /**
     * Vérifie la position de spawn du projectile lorsque le joueur fait face à droite.
     */
    @Test
    public void testUpdate_ProjectileSpawnPosition_FacingRight() {
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(1, 0));

        woodStick.update(2f, mockPlayer); // delta > shotDelay pour forcer le tir

        ArgumentCaptor<Vector2> spawnPosCaptor = ArgumentCaptor.forClass(Vector2.class);
        verify(mockEntityFactory).obtainProjectile(spawnPosCaptor.capture(), any(), anyFloat(), anyFloat(), anyInt(), anyFloat(), anyFloat(), anyFloat(), anyString(), any());

        Vector2 capturedPos = spawnPosCaptor.getValue();
        assertEquals(132f, capturedPos.x, 0.001f);
        assertEquals(116f, capturedPos.y, 0.001f);
    }

    /**
     * Vérifie que la direction par défaut du projectile est vers le bas si la direction du joueur est nulle.
     */
    @Test
    public void testUpdate_FacingDirectionIsZero_DefaultsToDown() {
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(0, 0));

        woodStick.update(2f, mockPlayer);

        ArgumentCaptor<Vector2> directionCaptor = ArgumentCaptor.forClass(Vector2.class);
        verify(mockEntityFactory).obtainProjectile(any(), directionCaptor.capture(), anyFloat(), anyFloat(), anyInt(), anyFloat(), anyFloat(), anyFloat(), anyString(), any());

        Vector2 capturedDir = directionCaptor.getValue();
        assertEquals(0f, capturedDir.x, 0.001f);
        assertEquals(-1f, capturedDir.y, 0.001f);
    }
}