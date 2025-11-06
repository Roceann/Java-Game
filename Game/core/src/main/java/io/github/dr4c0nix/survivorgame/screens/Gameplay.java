package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import io.github.dr4c0nix.survivorgame.Main;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;

public class Gameplay implements Screen {
    Main main;
    public Gameplay() {
        this.main = (Main) Gdx.app.getApplicationListener();
    }

    @Override
    public void show() {
        Gdx.app.log("Gameplay", "Screen shown");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Main.changeScreen("Menu");
        }
    }

    @Override
    public void resize(int width, int height) {
        // Implementation here
    }

    @Override
    public void pause() {
        // Implementation here
    }

    @Override
    public void resume() {
        // Implementation here
    }

    @Override
    public void hide() {
        // Implementation here
    }

    @Override
    public void dispose() {
        // Implementation here
    }
    
}
