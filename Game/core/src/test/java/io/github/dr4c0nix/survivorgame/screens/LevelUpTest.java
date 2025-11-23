package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import io.github.dr4c0nix.survivorgame.weapon.Weapon;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link LevelUp}.
 */
public class LevelUpTest {

    @Mock
    private Gameplay mockGameplay;
    @Mock
    private Player mockPlayer;
    @Mock
    private Weapon mockWeapon;
    @Mock
    private Input mockInput;
    @Mock
    private Stage mockStage;
    @Mock
    private Texture mockRectTex;
    @Mock
    private Texture mockContourTex;
    @Mock
    private BitmapFont mockFont;
    @Mock
    private BitmapFont mockTitleFont;
    @Mock
    private Graphics mockGraphics;

    private LevelUp levelUp;

    /**
     * Prépare les mocks et crée une instance de LevelUp pour les tests.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Gdx.input = mockInput;
        Gdx.graphics = mockGraphics;
        when(mockGraphics.getWidth()).thenReturn(1920);
        when(mockGraphics.getHeight()).thenReturn(1080);

        when(mockGameplay.getPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getCurrentWeapon()).thenReturn(mockWeapon);

        levelUp = new LevelUp(mockGameplay);
    }

    /**
     * Vérifie que la méthode privée allPossibleUpgrades retourne une liste non vide contenant des options attendues.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testAllPossibleUpgrades_ReturnsPopulatedList() throws Exception {
        Method method = LevelUp.class.getDeclaredMethod("allPossibleUpgrades");
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<UpgradeOption> upgrades = (List<UpgradeOption>) method.invoke(levelUp);

        assertNotNull("La liste des upgrades ne doit pas être null", upgrades);
        assertFalse("La liste des upgrades ne doit pas être vide", upgrades.isEmpty());

        boolean hasSpeed = upgrades.stream().anyMatch(u -> u.getDisplayName().equals("Speed"));
        boolean hasArmor = upgrades.stream().anyMatch(u -> u.getDisplayName().equals("Armor"));

        assertTrue("La liste doit contenir 'Speed'", hasSpeed);
        assertTrue("La liste doit contenir 'Armor'", hasArmor);
    }

    /**
     * Exécute la génération aléatoire interne et vérifie que trois options uniques sont sélectionnées.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testGenerateRandomUpgrades_SelectsThreeUniqueOptions() throws Exception {
        Method generateMethod = LevelUp.class.getDeclaredMethod("generateRandomUpgrades");
        generateMethod.setAccessible(true);
        generateMethod.invoke(levelUp);

        Field field = LevelUp.class.getDeclaredField("upgradeTotal");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<UpgradeOption> selectedUpgrades = (List<UpgradeOption>) field.get(levelUp);

        assertNotNull("La liste générée ne doit pas être null", selectedUpgrades);
        assertEquals("Il doit y avoir exactement 3 upgrades sélectionnés", 3, selectedUpgrades.size());

        UpgradeOption u1 = selectedUpgrades.get(0);
        UpgradeOption u2 = selectedUpgrades.get(1);
        UpgradeOption u3 = selectedUpgrades.get(2);

        assertNotEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertNotEquals(u2, u3);
    }

    /**
     * Méthode utilitaire pour appeler la méthode privée applyUpgradeToPlayer via réflexion.
     *
     * @param option option d'amélioration à appliquer
     * @throws Exception si la réflexion échoue
     */
    private void invokeApplyUpgrade(UpgradeOption option) throws Exception {
        Method method = LevelUp.class.getDeclaredMethod("applyUpgradeToPlayer", UpgradeOption.class);
        method.setAccessible(true);
        method.invoke(levelUp, option);
    }

    /**
     * Test de l'application d'une amélioration de vitesse.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_Vitesse() throws Exception {
        // Utilise la clé anglaise "Speed"
        UpgradeOption option = new UpgradeOption("Speed", 0.5f, 0.5f, UpgradeOption.StatType.FLOAT);
        setOptionValue(option, 0.5f);

        when(mockPlayer.getMovementSpeed()).thenReturn(1.0f);

        invokeApplyUpgrade(option);

        verify(mockPlayer).setMovementSpeed(1.5f);
        verify(mockGameplay).setIsPaused(false);
    }

    /**
     * Test de l'application d'une augmentation du HP maximum.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_MaxHP() throws Exception {
        UpgradeOption option = new UpgradeOption("Max Health", 10f, 10f, UpgradeOption.StatType.FLOAT);
        setOptionValue(option, 10f);

        when(mockPlayer.getHp()).thenReturn(50f);
        when(mockPlayer.getMaxHp()).thenReturn(100f);

        invokeApplyUpgrade(option);

        verify(mockPlayer).setMaxHp(110f);
        verify(mockPlayer).setCurrentHp(60f);
    }

    /**
     * Test de l'application d'une amélioration d'armure.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_Armure() throws Exception {
        UpgradeOption option = new UpgradeOption("Armor", 5, 5, UpgradeOption.StatType.INT);
        setOptionValue(option, 5f);

        when(mockPlayer.getArmor()).thenReturn(10);

        invokeApplyUpgrade(option);

        verify(mockPlayer).setArmor(15);
    }

    /**
     * Test de l'application d'une amélioration de force (difficulty).
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_Force() throws Exception {
        UpgradeOption option = new UpgradeOption("Difficulty", 0.2f, 0.2f, UpgradeOption.StatType.FLOAT);
        setOptionValue(option, 0.2f);
        when(mockPlayer.getDifficulter()).thenReturn(1.0f);
        invokeApplyUpgrade(option);
        verify(mockPlayer).setDifficulter(1.2f);
    }

    /**
     * Test de l'application d'une amélioration de régénération HP.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_RegenHP() throws Exception {
        UpgradeOption option = new UpgradeOption("HP Regeneration", 1.0f, 1.0f, UpgradeOption.StatType.FLOAT);
        setOptionValue(option, 1.0f);

        when(mockPlayer.getRegenHP()).thenReturn(0.5f);

        invokeApplyUpgrade(option);

        verify(mockPlayer).setRegenHP(1.5f);
    }

    /**
     * Test de l'application d'une augmentation de chance de critique.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_CritChance() throws Exception {
        UpgradeOption option = new UpgradeOption("Critical Chance", 5f, 5f, UpgradeOption.StatType.FLOAT);
        setOptionValue(option, 5f);
        when(mockPlayer.getCritChance()).thenReturn(10f);
        invokeApplyUpgrade(option);
        verify(mockPlayer).setCritChance(15f);
    }

    /**
     * Test de l'application d'une augmentation des dégâts critiques.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_CritDamage() throws Exception {
        UpgradeOption option = new UpgradeOption("Critical Damage", 0.5f, 0.5f, UpgradeOption.StatType.FLOAT);
        setOptionValue(option, 0.5f);

        when(mockPlayer.getCritDamage()).thenReturn(1.5f);

        invokeApplyUpgrade(option);

        verify(mockPlayer).setCritDamage(2.0f);
    }

    /**
     * Test de l'augmentation du niveau d'arme.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_WeaponLevel() throws Exception {
        UpgradeOption option = new UpgradeOption("Weapon Level", 1, 1, UpgradeOption.StatType.INT);
        setOptionValue(option, 1f);

        invokeApplyUpgrade(option);

        verify(mockWeapon).increaseWeaponLevel();
    }

    /**
     * Vérifie que l'augmentation du niveau d'arme est ignorée si aucune arme équipée.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testApplyUpgrade_WeaponLevel_NoWeapon() throws Exception {
        when(mockPlayer.getCurrentWeapon()).thenReturn(null);

        UpgradeOption option = new UpgradeOption("Weapon Level", 1, 1, UpgradeOption.StatType.INT);
        setOptionValue(option, 1f);

        invokeApplyUpgrade(option);

        verify(mockWeapon, never()).increaseWeaponLevel();
    }

    /**
     * Vérifie que hide libère correctement les ressources et remet l'input processor à null.
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testHide_DisposesResourcesAndResetsInput() throws Exception {
        injectField("stage", mockStage);
        injectField("rectTex", mockRectTex);
        injectField("contourTex", mockContourTex);
        injectField("font", mockFont);
        injectField("titleFont", mockTitleFont);

        when(mockInput.getInputProcessor()).thenReturn(mockStage);

        levelUp.hide();

        verify(mockInput).setInputProcessor(null);
        verify(mockStage).clear();
        verify(mockStage).dispose();
        verify(mockRectTex).dispose();
        verify(mockContourTex).dispose();
        verify(mockFont).dispose();
        verify(mockTitleFont).dispose();

        verify(mockGameplay).setIsPaused(false);

        assertNull(getField("stage"));
        assertNull(getField("rectTex"));
        assertNull(getField("contourTex"));
    }

    /**
     * Vérifie que hide ne lève pas d'exception lorsque stage est null et que le jeu est repris.
     *
     * @throws Exception si la réflexion échoue
     */
    @Test
    public void testHide_HandlesNullStageGracefully() throws Exception {
        levelUp.hide();

        verify(mockGameplay, atLeastOnce()).setIsPaused(false);
    }

    /**
     * Vérifie que dispose appelle la libération des ressources (hide/dispose du stage).
     *
     * @throws Exception si l'injection de champs échoue
     */
    @Test
    public void testDispose_CallsHide() throws Exception {
        injectField("stage", mockStage);

        levelUp.dispose();

        verify(mockStage).dispose();
    }

    /**
     * Utilitaire de test : modifie la valeur privée 'value' d'une UpgradeOption via réflexion.
     *
     * @param option option à modifier
     * @param value  valeur à définir
     * @throws Exception si la réflexion échoue
     */
    private void setOptionValue(UpgradeOption option, float value) throws Exception {
        try {
            Field f = UpgradeOption.class.getDeclaredField("value");
            f.setAccessible(true);
            f.set(option, value);
        } catch (NoSuchFieldException ignored) {
        }
    }

    /**
     * Injecte un champ privé dans l'instance LevelUp via réflexion.
     *
     * @param fieldName nom du champ
     * @param value     valeur à injecter
     * @throws Exception si la réflexion échoue
     */
    private void injectField(String fieldName, Object value) throws Exception {
        Field field = LevelUp.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(levelUp, value);
    }

    /**
     * Récupère la valeur d'un champ privé dans l'instance LevelUp via réflexion.
     *
     * @param fieldName nom du champ
     * @return valeur du champ
     * @throws Exception si la réflexion échoue
     */
    private Object getField(String fieldName) throws Exception {
        Field field = LevelUp.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(levelUp);
    }
}