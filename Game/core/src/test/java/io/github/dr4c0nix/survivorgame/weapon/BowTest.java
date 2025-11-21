package io.github.dr4c0nix.survivorgame.weapon;

import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Tests unitaires spécifiques à la classe {@link Bow}.
 */
public class BowTest {

    @Mock
    private EntityFactory mockEntityFactory;

    private Bow bow;

    /**
     * Initialise les mocks et l'instance de Bow avant chaque test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bow = new Bow(mockEntityFactory);
    }

    /**
     * Teste que la méthode update de l'arc déclenche bien un tir et
     * réinitialise le cooldown lorsque c'est possible.
     */
    @Test
    public void testUpdate_FiresAndResetsCooldown() throws NoSuchFieldException, IllegalAccessException {
        Player mockPlayer = mock(Player.class);
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);

        // S'assurer que le cooldown est à 0 pour pouvoir tirer
        cooldownField.set(bow, 0f);

        // Action
        bow.update(0.1f, mockPlayer);

        // Vérification
        // Le cooldown doit être réinitialisé à la valeur de shotDelay
        float cooldownAfterShot = (float) cooldownField.get(bow);
        assertEquals("Le cooldown doit être réinitialisé après le tir", bow.getShotDelay(), cooldownAfterShot, 0.001f);
        assertTrue("Le cooldown doit être supérieur à 0 après le tir", cooldownAfterShot > 0);
    }

    /**
     * Teste que la méthode update ne fait rien (à part décrémenter le cooldown)
     * si l'arme est en rechargement.
     */
    @Test
    public void testUpdate_DoesNothingWhenOnCooldown() throws NoSuchFieldException, IllegalAccessException {
        Player mockPlayer = mock(Player.class);
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);

        // Mettre l'arme en cooldown
        bow.resetCooldown();
        float initialCooldown = (float) cooldownField.get(bow);

        // Action
        float delta = 0.1f;
        bow.update(delta, mockPlayer);

        // Vérification
        float cooldownAfterTick = (float) cooldownField.get(bow);
        assertEquals("Le cooldown doit avoir diminué", initialCooldown - delta, cooldownAfterTick, 0.001f);
    }
}