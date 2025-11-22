package io.github.dr4c0nix.survivorgame.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.dr4c0nix.survivorgame.Main;

/**
 * Lanceur de l'application de bureau (LWJGL3).
 * 
 * Cette classe est responsable du lancement et de la configuration
 * de l'application SurvivorGame pour la plateforme de bureau.
 * 
 * @author Dr4c0nix
 * @author Abdelkader1900
 * @author Roceann
 * @version 1.0
 */
public class Lwjgl3Launcher {
    /**
     * Point d'entrée principal de l'application.
     * 
     * Cette méthode lance l'application en :
     * - Vérifiant la compatibilité avec macOS et Windows
     * - Créant l'application LibGDX avec la configuration appropriée
     * 
     * @param args Les arguments en ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    /**
     * Crée l'application LibGDX.
     * 
     * Cette méthode instancie l'application principale avec la configuration
     * de la plateforme LWJGL3.
     * 
     * @return L'application LibGDX créée
     */
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    /**
     * Obtient la configuration par défaut de l'application.
     * 
     * Cette méthode configure :
     * - Le titre de la fenêtre
     * - La synchronisation verticale (VSync)
     * - La limite d'images par seconde (FPS)
     * - Le mode plein écran
     * - Les icônes de la fenêtre
     * - L'émulation OpenGL
     * 
     * @return La configuration LWJGL3 complètement configurée
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("SurvivorGameTest");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        // Utiliser OpenGL natif
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2);

        return configuration;
    }
}