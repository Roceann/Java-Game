package io.github.dr4c0nix.survivorgame.screens;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour la classe {@link UpgradeOption}.
 */
@RunWith(JUnit4.class)
public class UpgradeOptionTest {

    /**
     * Vérifie le constructeur et les accesseurs basiques.
     */
    @Test
    public void testConstructorAndGetters() {
        UpgradeOption option = new UpgradeOption("Test Option", 10f, 20f, UpgradeOption.StatType.FLOAT);
        assertEquals("Test Option", option.getDisplayName());
        assertEquals(0f, option.getValue(), 0.001f);
    }

    /**
     * Teste la génération aléatoire pour un type FLOAT avec un Random contrôlé.
     */
    @Test
    public void testGenerateRandomValue_Float() {
        UpgradeOption option = new UpgradeOption("Speed", 1.0f, 2.0f, UpgradeOption.StatType.FLOAT);

        Random fixedRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.5f;
            }
        };

        option.generateRandomValue(fixedRandom);

        assertEquals(1.5f, option.getValue(), 0.001f);
        assertEquals("1.50", option.getFormattedValue());
    }

    /**
     * Teste la génération aléatoire pour un type INT avec arrondi attendu.
     */
    @Test
    public void testGenerateRandomValue_Int() {
        UpgradeOption option = new UpgradeOption("Armor", 10f, 20f, UpgradeOption.StatType.INT);

        Random fixedRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.9f;
            }
        };

        option.generateRandomValue(fixedRandom);

        assertEquals(19f, option.getValue(), 0.001f);
        assertEquals("19", option.getFormattedValue());
    }

    /**
     * Vérifie le comportement d'arrondi pour INT avec fraction intermédiaire.
     */
    @Test
    public void testGenerateRandomValue_Int_Rounding() {
        UpgradeOption option = new UpgradeOption("Armor", 0f, 10f, UpgradeOption.StatType.INT);

        Random fixedRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.55f;
            }
        };

        option.generateRandomValue(fixedRandom);

        assertEquals(6f, option.getValue(), 0.001f);
    }

    /**
     * Vérifie la précision du formatage pour les valeurs FLOAT.
     */
    @Test
    public void testGetFormattedValue_FloatPrecision() {
        UpgradeOption option = new UpgradeOption("Crit", 0f, 10f, UpgradeOption.StatType.FLOAT);

        Random fixedRandom = new Random() {
            @Override
            public float nextFloat() {
                return 0.12345f;
            }
        };

        option.generateRandomValue(fixedRandom);

        assertEquals("1.23", option.getFormattedValue());
    }
}