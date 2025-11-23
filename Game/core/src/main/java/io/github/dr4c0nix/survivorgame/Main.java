package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import io.github.dr4c0nix.survivorgame.screens.Menu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    Menu menu;
    Gameplay gameplay;

    /**
     * Initialisation du jeu : crée les écrans et affiche le menu.
     * Méthode appelée par LibGDX au démarrage.
     */
    @Override
    public void create() {
        this.menu = new Menu();
        this.gameplay = new Gameplay();
        setScreen(menu);
    }

    /**
     * Change l'écran courant de l'application.
     * Dispose l'ancienne instance pour libérer les ressources si nécessaire,
     * puis instancie et affiche le nouvel écran demandé.
     *
     * @param screenName nom de l'écran cible ("Menu" ou "Gameplay")
     */
    public static void changeScreen(String screenName) {
        Main mainInstance = ((Main) Gdx.app.getApplicationListener());
        switch (screenName) {
            case "Menu":
                if (mainInstance.menu != null) {
                    mainInstance.menu.dispose();
                }
                mainInstance.menu = new Menu();
                mainInstance.setScreen(mainInstance.menu);
                break;
            case "Gameplay":
                if (mainInstance.gameplay != null) {
                    mainInstance.gameplay.dispose();
                }
                mainInstance.gameplay = new Gameplay();
                mainInstance.setScreen(mainInstance.gameplay);
                break;
            default:
                Gdx.app.log("Main", "Unknown screen: " + screenName);
                break;
        }
    }
}