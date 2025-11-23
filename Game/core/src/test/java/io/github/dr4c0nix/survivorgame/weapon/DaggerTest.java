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
 * Tests unitaires pour {@link Dagger}.
 *
 * Ces tests reprennent le même schéma que {@link SwordTest} :
 * - création d'un joueur factice avec hitbox et direction,
 * - appel de update(...) avec un delta suffisant pour déclencher un tir,
 * - capture des arguments passés à {@link EntityFactory#obtainProjectile}
 *   et vérification du positionnement et de la direction du projectile.
 */
public class DaggerTest {

    @Mock
    private EntityFactory mockEntityFactory;
    @Mock
    private Player mockPlayer;

    private Dagger dagger;

    /**
     * Initialise les mocks et crée une instance de Dagger avant chaque test.
     * Configure également un hitbox de joueur de 32x32 positionné en (100,100).
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dagger = new Dagger(mockEntityFactory);

        Rectangle playerHitbox = new Rectangle(100, 100, 32, 32);
        when(mockPlayer.getHitbox()).thenReturn(playerHitbox);
        when(mockPlayer.getForce()).thenReturn(1.0f);
    }

    /**
     * Vérifie que la position de spawn du projectile est correcte lorsque le
     * joueur regarde vers la droite.
     *
     * Logique attendue :
     * - centre du joueur = (100 + 16, 100 + 16) = (116,116)
     * - offset X = 16 => spawn X = 116 + 16 = 132
     */
    @Test
    public void testUpdate_ProjectileSpawnPosition_FacingRight() {
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(1, 0));

        dagger.update(2f, mockPlayer); // delta large pour forcer le tir

        ArgumentCaptor<Vector2> spawnPosCaptor = ArgumentCaptor.forClass(Vector2.class);
        verify(mockEntityFactory).obtainProjectile(spawnPosCaptor.capture(), any(), anyFloat(), anyFloat(), anyInt(), anyFloat(), anyFloat(), anyFloat(), anyString(), any());

        Vector2 capturedPos = spawnPosCaptor.getValue();
        // Centre du joueur: (116,116), offsetX = 16 -> spawn (132,116)
        assertEquals(132f, capturedPos.x, 0.001f);
        assertEquals(116f, capturedPos.y, 0.001f);
    }

    /**
     * Vérifie que si la direction du joueur est le vecteur nul, la direction
     * du projectile par défaut est vers le bas (0, -1).
     */
    @Test
    public void testUpdate_FacingDirectionIsZero_DefaultsToDown() {
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(0, 0));

        dagger.update(2f, mockPlayer);

        ArgumentCaptor<Vector2> directionCaptor = ArgumentCaptor.forClass(Vector2.class);
        verify(mockEntityFactory).obtainProjectile(any(), directionCaptor.capture(), anyFloat(), anyFloat(), anyInt(), anyFloat(), anyFloat(), anyFloat(), anyString(), any());

        Vector2 capturedDir = directionCaptor.getValue();
        assertEquals(0f, capturedDir.x, 0.001f);
        assertEquals(-1f, capturedDir.y, 0.001f);
    }
}