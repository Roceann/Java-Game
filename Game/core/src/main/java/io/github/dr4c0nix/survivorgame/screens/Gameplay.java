package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;

import io.github.dr4c0nix.survivorgame.Main;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.OrbXp;

import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Gameplay implements Screen {
    Main main;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private SpriteBatch batch;
    private Player player;
    private boolean isPaused = false;
    private BitmapFont font;
    private LevelUp levelUpOverlay;
    private InputProcessor previousInputProcessor;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Vector2 spawnPoint;
    // private List<Class<? extends Enemy>> enemies; 
    private EntityFactory entityFactory;

    public Gameplay() {
        this.main = (Main) Gdx.app.getApplicationListener();
        initCameras();
        initGraphics();
        initPlayer();
        this.entityFactory = new EntityFactory();
    }

    public boolean getIsPaused() {
        return this.isPaused;
    }

    public void setIsPaused(boolean value) {
        this.isPaused = value;
    }

    private void initCameras() {
        this.camera = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camera);
        this.camera.zoom = 0.5f;
        this.camera.update();
    }

    private void initGraphics() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(2.0f);
        this.map = new TmxMapLoader().load("Map/map.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        MapLayer spawnLayer = map.getLayers().get("spawn");
        if (spawnLayer != null) {
            MapObjects objects = spawnLayer.getObjects();
            MapObject spawnObj = objects.get("spawnpoint");
            if (spawnObj != null) {
                float x = spawnObj.getProperties().get("x", Float.class);
                float y = spawnObj.getProperties().get("y", Float.class);
                this.spawnPoint = new Vector2(x, y);
            } else {
                this.spawnPoint = new Vector2(0, 0);
            }
        } else {
            this.spawnPoint = new Vector2(0, 0);
        }
    }

    private void initPlayer() {
        Texture playerTexture = new Texture(Gdx.files.internal("Entity/Player/static1.png"));
        this.player = new Player(spawnPoint, 100, 10, 1.0f, "Entity/Player/static1.png", playerTexture, "Jhonny Player") {
            @Override
            public void animation() {
                super.animation();
            }
        };
        player.setGameplay(this);
    }

    @Override
    public void show() {
        this.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        updateCamera();
        viewport.apply();
        mapRenderer.setView(camera);
        mapRenderer.render();
        player.update(delta);

        for (OrbXp orb : entityFactory.getActiveOrbs()) {
            orb.update(delta);
            if(player.getHitbox().overlaps(orb.getHitbox())) {
                player.addXp(orb.getXpValue());
                entityFactory.releaseOrbXp(orb);
            }
        }
        drawScene();
        drawOverlayIfActive(delta);
        handleGlobalInput();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void updateCamera() {
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
    }

    private void drawScene() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        entityFactory.drawActiveOrbs(batch);
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

    public void showLevelUpScreen() {
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
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
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
        batch.dispose();
        if (player != null) {
            try {
                player.dispose();
            } catch (Exception ignored) {
            }
        }
        font.dispose();
        if (map != null) {
            map.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
    }
}
