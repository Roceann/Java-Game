package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import io.github.dr4c0nix.survivorgame.Main;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Gameplay implements Screen {
    Main main;
    private OrthographicCamera camera;;
    private SpriteBatch batch;
    private Player player;
    private boolean isPaused = false;
    private BitmapFont font;
    private Texture backgroundTexture;
    private LevelUp levelUpOverlay;
    private InputProcessor previousInputProcessor;

    private Texture createBackgroundTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public Gameplay() {
        this.main = (Main) Gdx.app.getApplicationListener();
        initCameras();
        initGraphics();
        initPlayer();
        this.backgroundTexture = createBackgroundTexture();
    }

    public boolean getIsPaused() {
        return this.isPaused;
    }

    public void setIsPaused(boolean value) {
        this.isPaused = value;
    }

    private void initCameras() {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(0, 0, 0);
        this.camera.zoom = 0.5f;
        this.camera.update();
    }

    private void initGraphics() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(2.0f);
    }

    private void initPlayer() {
        Texture playerTexture = new Texture(Gdx.files.internal("personages/Jhonny/Jhonny-3/Jhonny-3.png"));
        this.player = new Player(100, 10, 1.0f, "personages/Jhonny/Jhonny-3/Jhonny-3.png", playerTexture,
                "Jhonny Player");
        player.setGameplay(this);
    }

    @Override
    public void show() {
        this.camera.viewportWidth = Gdx.graphics.getWidth();
        this.camera.viewportHeight = Gdx.graphics.getHeight();
        this.camera.update();
    }

    @Override
    public    private boolean isPaused = false

    void render(float delta) {
        clearScreen();
        updateCamera();
        player.update(delta);
        drawScene();
        drawOverlayIfActive(delta);
        handleGlobalInput();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void updateCamera() {
        camera.position.set(0, 0, 0);
        camera.update();
    }

    private void drawScene() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        batch.end();
    }

    private void drawOverlayIfActive(float delta) {
        if (levelUpOverlay != null && levelUpOverlay.getStage() != null) {
            levelUpOverlay.getStage().act(delta);
            levelUpOverlay.getStage().draw();
        }
    }

    private void handleGlobalInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Main.changeScreen("Menu");
        }
        if (Gdx.input.isKeyJustPressed(Keys.L)) {
            showLevelUpScreen();
        }
    }

    private void showLevelUpScreen() {
        isPaused = true;
        previousInputProcessor = Gdx.input.getInputProcessor();
        if (levelUpOverlay == null) {
            levelUpOverlay = new LevelUp(this);
        }
        levelUpOverlay.show();
        Gdx.input.setInputProcessor(levelUpOverlay.getStage());
    }

    public Player getPlayer() {
        return player;
    }

    public void onLevelUpOverlayClosed() {
        if (previousInputProcessor != null) {
            Gdx.input.setInputProcessor(previousInputProcessor);
            previousInputProcessor = null;
        } else {
            Gdx.input.setInputProcessor(null);
        }
        if (levelUpOverlay != null) {
            levelUpOverlay.hide();
            levelUpOverlay = null;
        }
        isPaused = false;
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (player != null) {
            player.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
