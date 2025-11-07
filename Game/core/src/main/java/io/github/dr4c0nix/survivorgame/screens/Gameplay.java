package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import io.github.dr4c0nix.survivorgame.Main;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Gameplay implements Screen {
    Main main;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;

    public Gameplay() {
        this.main = (Main) Gdx.app.getApplicationListener();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = 0.5f;
        this.batch = new SpriteBatch();
        Texture playerTexture = new Texture(Gdx.files.internal("personages/Jhonny/Jhonny-3/Jhonny-3.png"));
        this.player = new Player(100, 10, 1.0f, "personages/Jhonny/Jhonny-3/Jhonny-3.png", playerTexture, "Jhonny Player");
    }

    @Override
    public void show() {
        Gdx.app.log("Gameplay", "Screen shown");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        player.update(delta);
        
        camera.position.set(0,0, 0);
        camera.update();
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();
        
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
        batch.dispose();
        player.dispose();
    }
    
}
