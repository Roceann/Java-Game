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
        configuration.setTitle("Cavazzinni Survivor");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        // Start the application in fullscreen by default.
        // Use the current display mode; if you prefer a windowed default, replace with setWindowedMode(w,h).
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("icon.png");

        //// This should improve compatibility with Windows machines with buggy OpenGL drivers, Macs
        //// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
        //// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
        //// You can choose to remove the following line and the mentioned dependency if you want; they
        //// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
        // Only enable ANGLE emulation on Windows (avoid EGL errors on Linux / WSL)
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);
        }

        return configuration;
    }
}