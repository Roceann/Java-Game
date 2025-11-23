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
 * Tests unitaires spécifiques à la classe {@link Sword}.
 * Ces tests valident la logique propre à l'arme Sword, comme le positionnement de l'attaque.
 */
public class SwordTest {

    @Mock
    private EntityFactory mockEntityFactory;
    @Mock
    private Player mockPlayer;

    private Sword sword;

    /**
     * Initialise les mocks et l'instance de Sword avant chaque test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sword = new Sword(mockEntityFactory);

        // Configuration d'un joueur fictif pour les tests
        Rectangle playerHitbox = new Rectangle(100, 100, 32, 32);
        when(mockPlayer.getHitbox()).thenReturn(playerHitbox);
        when(mockPlayer.getForce()).thenReturn(1.0f);
    }

    /**
     * Teste que la position de spawn du projectile de l'épée est correcte
     * lorsque le joueur fait face à droite.
     */
    @Test
    public void testUpdate_ProjectileSpawnPosition_FacingRight() {
        // Le joueur regarde à droite
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(1, 0));

        sword.update(1f, mockPlayer); // delta > shotDelay pour assurer le tir

        // Capture l'argument de position passé à la factory
        ArgumentCaptor<Vector2> spawnPosCaptor = ArgumentCaptor.forClass(Vector2.class);
        verify(mockEntityFactory).obtainProjectile(spawnPosCaptor.capture(), any(), anyFloat(), anyFloat(), anyInt(), anyFloat(), anyFloat(), anyFloat(), anyString(), any());

        Vector2 capturedPos = spawnPosCaptor.getValue();
        // Centre du joueur: (100 + 16, 100 + 16) = (116, 116)
        // Offset X: 1 * (32 * 0.5) = 16
        // Position de spawn attendue: (116 + 16, 116) = (132, 116)
        assertEquals("La position X du projectile est incorrecte", 132, capturedPos.x, 0.001f);
        assertEquals("La position Y du projectile est incorrecte", 116, capturedPos.y, 0.001f);
    }

    /**
     * Teste que si le joueur n'a pas de direction (vecteur nul),
     * l'attaque est dirigée par défaut vers le bas.
     */
    @Test
    public void testUpdate_FacingDirectionIsZero_DefaultsToDown() {
        // Le joueur est immobile, direction nulle
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(0, 0));

        sword.update(1f, mockPlayer);

        // Capture la direction passée à la factory
        ArgumentCaptor<Vector2> directionCaptor = ArgumentCaptor.forClass(Vector2.class);
        verify(mockEntityFactory).obtainProjectile(any(), directionCaptor.capture(), anyFloat(), anyFloat(), anyInt(), anyFloat(), anyFloat(), anyFloat(), anyString(), any());

        Vector2 capturedDir = directionCaptor.getValue();
        assertEquals("La direction par défaut doit être vers le bas (X=0)", 0f, capturedDir.x, 0.001f);
        assertEquals("La direction par défaut doit être vers le bas (Y=-1)", -1f, capturedDir.y, 0.001f);
    }
}