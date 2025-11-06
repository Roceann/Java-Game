package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import io.github.dr4c0nix.survivorgame.screens.Gameplay;
import io.github.dr4c0nix.survivorgame.screens.Menu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    Menu menu;
    Gameplay gameplay;

    @Override
    public void create() {
        this.menu = new Menu();
        this.gameplay = new Gameplay();
        setScreen(menu);
    }

    public static void changeScreen(String screenName) {
        Main mainInstance = ((Main) Gdx.app.getApplicationListener());
        switch (screenName) {
            case "Menu":
                mainInstance.setScreen(mainInstance.menu);
                break;
            case "Gameplay":
                mainInstance.gameplay.dispose();
                mainInstance.gameplay = new Gameplay();
                mainInstance.setScreen(mainInstance.gameplay);
                break;
            default:
                Gdx.app.log("Main", "Unknown screen: " + screenName);
                break;
        }
    }
}