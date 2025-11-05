package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Game;

import io.github.dr4c0nix.survivorgame.screens.Menu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new Menu());
    }
}