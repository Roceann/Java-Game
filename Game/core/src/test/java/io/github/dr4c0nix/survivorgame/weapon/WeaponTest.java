package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe abstraite {@link Weapon}.
 * <p>
 * Étant donné que {@link Weapon} est abstraite, les tests sont effectués
 * à travers une de ses implémentations concrètes, {@link Sword}.
 * Cela permet de tester la logique non-abstraite de la classe de base.
 */
public class WeaponTest {

    @Mock
    private EntityFactory mockEntityFactory;
    @Mock
    private Player mockPlayer;

    private Sword sword; // Utilisation d'une classe concrète pour tester la classe abstraite

    /**
     * Prépare l'environnement de test avant chaque exécution de test.
     * Initialise les mocks et crée une instance de l'arme à tester.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sword = new Sword(mockEntityFactory);

        // Configuration par défaut pour le mockPlayer pour éviter les NullPointerExceptions
        when(mockPlayer.getHitbox()).thenReturn(new Rectangle(0, 0, 32, 32));
        when(mockPlayer.getFacingDirection()).thenReturn(new Vector2(1, 0));
        when(mockPlayer.getForce()).thenReturn(1.0f);
    }

    /**
     * Teste si le constructeur de Weapon initialise correctement tous les champs
     * avec les valeurs attendues et si le niveau par défaut est bien 1.
     */
    @Test
    public void testConstructorInitialisation() {
        assertEquals("Le niveau initial doit être 1", 1, sword.getLevel());
        assertEquals("Les dégâts ne correspondent pas", 100, sword.getDamage());
        assertEquals("Le délai de tir ne correspond pas", 0.5f, sword.getShotDelay(), 0.001f);
        assertEquals("La portée ne correspond pas", 150, sword.getRange());
        assertEquals("La chance de critique ne correspond pas", 0.10f, sword.getCritChance(), 0.001f);
        assertEquals("La vitesse du projectile ne correspond pas", 100f, sword.getProjectileSpeed(), 0.001f);
        assertEquals("Le nombre de projectiles ne correspond pas", 1, sword.getProjectileCount());
        assertEquals("La taille du projectile ne correspond pas", 2f, sword.getProjectileSize(), 0.001f);
        assertEquals("Le chemin de l'icône est incorrect", "Weapon/Sword/sword.png", sword.getIconPath());
        assertEquals("La description est incorrecte", "Equipez vous d'une épée pour tabasser les triple monstres !", sword.getDescription());
        assertEquals("Le chemin de la texture du projectile est incorrect", "Weapon/Sword/sword-effect.png", sword.getProjectileTexturePath());

        // Le cooldown initial doit être 0
        try {
            Field cooldownField = Weapon.class.getDeclaredField("cooldown");
            cooldownField.setAccessible(true);
            float cooldownValue = (float) cooldownField.get(sword);
            assertEquals("Le cooldown initial doit être 0", 0f, cooldownValue, 0.001f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Impossible d'accéder au champ 'cooldown' pour le test : " + e.getMessage());
        }
    }

    /**
     * Vérifie que la méthode levelUp() incrémente correctement le niveau de l'arme.
     */
    @Test
    public void testLevelUp() {
        assertEquals("Niveau initial incorrect", 1, sword.getLevel());
        sword.levelUp();
        assertEquals("Le niveau après un levelUp doit être 2", 2, sword.getLevel());
        sword.levelUp();
        sword.levelUp();
        assertEquals("Le niveau après plusieurs levelUp est incorrect", 4, sword.getLevel());
    }

    /**
     * Teste la logique de décrémentation du cooldown via la méthode cooldownTick.
     * Vérifie également que le cooldown ne peut pas devenir négatif.
     */
    @Test
    public void testCooldownTick() throws NoSuchFieldException, IllegalAccessException {
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);

        // Test de la décrémentation normale
        cooldownField.set(sword, 1.0f);
        sword.cooldownTick(0.2f);
        assertEquals("Le cooldown devrait être décrémenté", 0.8f, (float) cooldownField.get(sword), 0.001f);

        // Test pour s'assurer que le cooldown ne devient pas négatif
        sword.cooldownTick(1.0f); // Le cooldown devrait passer à -0.2, puis être ramené à 0
        assertEquals("Le cooldown ne doit pas être négatif", 0f, (float) cooldownField.get(sword), 0.001f);

        // Test quand le cooldown est déjà à zéro
        sword.cooldownTick(0.5f);
        assertEquals("Le cooldown à 0 doit rester à 0", 0f, (float) cooldownField.get(sword), 0.001f);
    }

    /**
     * Vérifie que la méthode resetCooldown() réinitialise correctement le cooldown
     * à la valeur de shotDelay.
     */
    @Test
    public void testResetCooldown() throws NoSuchFieldException, IllegalAccessException {
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);

        cooldownField.set(sword, 0f); // S'assurer que le cooldown est à 0
        sword.resetCooldown();

        float expectedCooldown = sword.getShotDelay();
        float actualCooldown = (float) cooldownField.get(sword);
        assertEquals("Le cooldown doit être réinitialisé à la valeur de shotDelay", expectedCooldown, actualCooldown, 0.001f);
    }

    /**
     * Teste la méthode canShoot() dans différentes conditions de cooldown.
     */
    @Test
    public void testCanShoot() throws NoSuchFieldException, IllegalAccessException {
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);

        // Quand le cooldown est à 0, on peut tirer
        cooldownField.set(sword, 0f);
        assertTrue("Doit pouvoir tirer quand le cooldown est à 0", sword.canShoot());

        // Quand le cooldown est positif, on ne peut pas tirer
        cooldownField.set(sword, 0.1f);
        assertFalse("Ne doit pas pouvoir tirer quand le cooldown est positif", sword.canShoot());

        // Cas limite : quand le cooldown est négatif, on doit pouvoir tirer
        cooldownField.set(sword, -0.1f);
        assertTrue("Doit pouvoir tirer quand le cooldown est négatif", sword.canShoot());
    }

    /**
     * Teste le comportement de la méthode update() de Sword lorsque l'arme peut tirer.
     * Vérifie qu'un projectile est créé et que le cooldown est réinitialisé.
     */
    @Test
    public void testUpdate_WhenCanShoot_ShouldFireAndResetCooldown() throws IllegalAccessException, NoSuchFieldException {
        // S'assurer que l'arme peut tirer
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);
        cooldownField.set(sword, 0f);

        // Action
        sword.update(0.1f, mockPlayer);

        // Assertions
        // Vérifie que la factory a bien été appelée pour créer un projectile
        // On attend des float pour range, baseWidth, baseHeight
        verify(mockEntityFactory, times(1)).obtainSwordProjectile(
            any(Vector2.class), any(Vector2.class), anyFloat(), anyFloat(), anyInt(),
            anyFloat(), anyFloat(), anyFloat(), anyString(), eq(mockPlayer)
        );

        // Vérifie que le cooldown a été réinitialisé
        float cooldownAfterShot = (float) cooldownField.get(sword);
        assertEquals("Le cooldown doit être réinitialisé après un tir", sword.getShotDelay(), cooldownAfterShot, 0.001f);
    }

    /**
     * Teste le comportement de la méthode update() de Sword lorsque l'arme est en cooldown.
     * Vérifie qu'aucun projectile n'est créé et que le cooldown diminue.
     */
    @Test
    public void testUpdate_WhenOnCooldown_ShouldNotFireAndTickCooldown() throws NoSuchFieldException, IllegalAccessException {
        // Mettre l'arme en cooldown
        sword.resetCooldown();
        float initialCooldown = sword.getShotDelay();

        // Action
        float delta = 0.1f;
        sword.update(delta, mockPlayer);

        // Assertions
        // Vérifie que la factory n'a PAS été appelée
        verify(mockEntityFactory, never()).obtainSwordProjectile(
            any(), any(), anyFloat(), anyFloat(), anyInt(),
            anyFloat(), anyFloat(), anyFloat(), anyString(), any()
        );

        // Vérifie que le cooldown a diminué
        Field cooldownField = Weapon.class.getDeclaredField("cooldown");
        cooldownField.setAccessible(true);
        float cooldownAfterTick = (float) cooldownField.get(sword);
        assertEquals("Le cooldown doit diminuer", initialCooldown - delta, cooldownAfterTick, 0.001f);
    }
}
