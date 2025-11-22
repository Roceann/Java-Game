package io.github.dr4c0nix.survivorgame.screens;

import java.util.Random;
import java.util.Locale; // Import nécessaire pour Locale.US

/**
 * Représente une option d'amélioration proposée au niveau supérieur.
 *
 * Chaque option possède un nom affiché,
 * une plage de valeurs min/max et un type (INT ou FLOAT). La méthode generateRandomValue
 * calcule une valeur aléatoire dans l'intervalle en fonction du type.
 * @author Roceann
 * @version 1.0
 */
public class UpgradeOption {
    public enum StatType { INT, FLOAT }
    
    private final String displayName;
    private final float minValue;
    private final float maxValue;
    private final StatType type;
    private float value;

    /**
     * Crée une nouvelle option d'amélioration.
     *
     * @param displayName nom affiché dans l'UI
     * @param minValue valeur minimale possible (incluse)
     * @param maxValue valeur maximale possible (incluse)
     * @param type type de la statistique (INT ou FLOAT) qui influence la génération
     */
    public UpgradeOption(String displayName, float minValue, float maxValue, StatType type) {
        this.displayName = displayName;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.type = type;
    }

    /**
     * Génère une valeur aléatoire pour cette option en utilisant l'instance Random fournie.
     *
     * Si le type est INT, la valeur générée est arrondie en int. Si FLOAT, la valeur est un float
     * dans l'intervalle [minValue, maxValue)
     */
    public void generateRandomValue(Random random) {
        if (type == StatType.INT) {
            value = Math.round(minValue + random.nextFloat() * (maxValue - minValue));
        } else {
            value = minValue + random.nextFloat() * (maxValue - minValue);
        }
    }

    /**
     * Retourne la valeur formatée pour affichage selon le type.
     *
     * - INT -> format "%d"
     * - FLOAT -> format "%.2f"
     * 
     * Utilise Locale.US pour garantir l'utilisation du point comme séparateur décimal.
     */
    public String getFormattedValue() {
        if (type == StatType.INT) {
            return String.format(Locale.US, "%d", (int) this.value);
        } else {
            return String.format(Locale.US, "%.2f", value);
        }
    }

    /**
     * Retourne le nom affiché de l'option.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Retourne la valeur numérique générée pour l'option.
     */
    public float getValue(){
        return this.value;
    }
}