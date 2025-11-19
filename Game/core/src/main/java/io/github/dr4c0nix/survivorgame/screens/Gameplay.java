package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import box2dLight.RayHandler;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    private Texture pauseOverlayTexture;
    // private List<Class<? extends Enemy>> enemies; 
    private EntityFactory entityFactory;
    private ArrayList<Rectangle> collisionRectangles;
    private Rectangle triggerRect;
    private boolean wasInTrigger = false;
    private float targetZoom = 0.5f;
    private World lightWorld;
    private RayHandler rayHandler;
    private PointLight playerLight;
    private float currentLightRadius;
    private float targetLightRadius;
    private static final float minLightRadius = 60f;
    private static final float maxLightRadius = 180f;
    private ArrayList<PointLight> torchLights;
    private Rectangle lightTogglerRect;
    private boolean wasInLightToggler = false;
    private boolean lightsEnabled = true;
    private Rectangle comingFromR1;
    private Rectangle comingFromR2;
    private boolean ccomingFromR1 = true;
    private boolean ccomingFromR2 = false;
    private float targetAmbient = 0.5f;
    private float currentAmbient = 0.5f;
    private boolean isPauseMenuVisible = false;
    private GlyphLayout pauseLayout = new GlyphLayout();
    private Stage pauseStage;
    private TextButton.TextButtonStyle pauseButtonStyle;
    private Texture pauseButtonTexture;
    private Texture pauseButtonTextureDown;

    private float survivalTime = 0f;
    private int killCount = 0;
    

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

    private void buildPauseUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("PAUSE", titleStyle);
        titleLabel.setFontScale(2f);
        root.add(titleLabel).padBottom(30f).row();
        TextButton resumeBtn = new TextButton("Reprendre", pauseButtonStyle);
        TextButton menuBtn   = new TextButton("Menu principal", pauseButtonStyle);
        TextButton quitBtn   = new TextButton("Quitter", pauseButtonStyle);

        resumeBtn.getLabel().setFontScale(1.3f);
        menuBtn.getLabel().setFontScale(1.3f);
        quitBtn.getLabel().setFontScale(1.3f);

        resumeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    isPaused = false;
                    isPauseMenuVisible = false;
                    Gdx.input.setInputProcessor(null);
                }
            });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen("Menu");
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        root.add(resumeBtn).width(260f).height(60f).pad(5f).row();
        root.add(menuBtn).width(260f).height(60f).pad(5f).row();
        root.add(quitBtn).width(260f).height(60f).pad(5f).row();

        pauseStage.addActor(root);
    }

    private void initCameras() {
        this.camera = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camera);
        this.camera.zoom = 0.5f;
        this.targetZoom = 0.5f;
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

        MapLayer collisionsLayer = map.getLayers().get("collisions");
        collisionRectangles = new ArrayList<>();
        if (collisionsLayer != null) {
            MapObjects objects = collisionsLayer.getObjects();
            for (MapObject obj : objects) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle r = ((RectangleMapObject) obj).getRectangle();
                    collisionRectangles.add(new Rectangle(r));
                }
            }
        }

        MapLayer triggerLayer = map.getLayers().get("trigger");
        if (triggerLayer != null) {
            MapObjects objects = triggerLayer.getObjects();
            MapObject triggerObj = objects.get("trigger");
            if (triggerObj instanceof RectangleMapObject) {
                triggerRect = ((RectangleMapObject) triggerObj).getRectangle();
            }
        }

        currentLightRadius = minLightRadius;
        targetLightRadius = minLightRadius;

        lightWorld = new World(new Vector2(0, 0), true);
        RayHandler.setGammaCorrection(true);
        rayHandler = new RayHandler(lightWorld);
        rayHandler.setAmbientLight(0.5f);
        torchLights = new ArrayList<>();
        MapLayer lightsLayer = map.getLayers().get("lights");
        if (lightsLayer != null) {
            MapObjects lightObjects = lightsLayer.getObjects();
            for (MapObject obj : lightObjects) {
                float x = obj.getProperties().get("x", Float.class);
                float y = obj.getProperties().get("y", Float.class);
                PointLight torch = new PointLight(rayHandler, 64, new Color(1f, 0.6f, 0.1f, 0.6f), 130f, x, y);
                torch.setSoft(true);
                torchLights.add(torch);
            }
        }

        MapLayer togglerLayer = map.getLayers().get("lightstoggler");
        if (togglerLayer != null) {
            MapObjects togglerObjs = togglerLayer.getObjects();
            for (MapObject obj : togglerObjs) {
                if (obj instanceof RectangleMapObject && obj.getName().equals("lightstoggler")) {
                    lightTogglerRect = ((RectangleMapObject) obj).getRectangle();
                }
                if (obj instanceof RectangleMapObject && obj.getName().equals("comingfromr1")) {
                    comingFromR1 = ((RectangleMapObject) obj).getRectangle();
                }
                if (obj instanceof RectangleMapObject && obj.getName().equals("comingfromr2")) {
                    comingFromR2 = ((RectangleMapObject) obj).getRectangle();
                }
            }
        }

            Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            overlayPixmap.setColor(0f, 0f, 0f, 0.6f); 
            overlayPixmap.fill();
            pauseOverlayTexture = new Texture(overlayPixmap);
            overlayPixmap.dispose();

            Pixmap btnPix = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
            btnPix.setColor(0.15f, 0.15f, 0.15f, 0.9f);
            btnPix.fill();
            pauseButtonTexture = new Texture(btnPix);

            Pixmap btnDownPix = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
            btnDownPix.setColor(0.05f, 0.05f, 0.05f, 0.9f);
            btnDownPix.fill();
            pauseButtonTextureDown = new Texture(btnDownPix);

            btnPix.dispose();
            btnDownPix.dispose();
            pauseButtonStyle = new TextButton.TextButtonStyle();
            pauseButtonStyle.font = font;
            pauseButtonStyle.fontColor = Color.WHITE;
            pauseButtonStyle.up = new TextureRegionDrawable(new TextureRegion(pauseButtonTexture));
            pauseButtonStyle.down = new TextureRegionDrawable(new TextureRegion(pauseButtonTextureDown));
            pauseStage = new Stage(new StretchViewport(800, 600));
            buildPauseUI();

    }

    /**
     * Vérifie si une hitbox intersecte la couche collisions (rectangle).
     */
    public boolean isColliding(Rectangle rect) {
        if (collisionRectangles == null || collisionRectangles.isEmpty()) {
            return false;
        }

        for (Rectangle r : collisionRectangles) {
            if (rect.overlaps(r)) {
                return true;
            }
        }

        return false;
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

        if (rayHandler != null && playerLight == null) {
            playerLight = new PointLight(rayHandler, 128, null, currentLightRadius, player.getPosition().x + player.getHitbox().width / 2f, player.getPosition().y + player.getHitbox().height / 2f);
            playerLight.setSoft(true);
        }
    }

    @Override
    public void show() {
        this.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }


    @Override
    public void render(float delta) {
        clearScreen();


        if (!isPaused) {
            survivalTime += delta;
        }

        updateCamera();
        viewport.apply();
        mapRenderer.setView(camera);
        mapRenderer.render();

        if (!isPaused) {
            player.update(delta);

            if (triggerRect != null) {
                Rectangle head = new Rectangle(
                    player.getPosition().x,
                    player.getPosition().y + player.getHitbox().height - 2f,
                    player.getHitbox().width,
                    2f
                );
                boolean isInTrigger = head.overlaps(triggerRect);
                if (isInTrigger) wasInTrigger = true;
                if (wasInTrigger && !isInTrigger) {
                    targetZoom = 1.0f;
                    targetLightRadius = maxLightRadius;
                    collisionRectangles.add(triggerRect);
                    triggerRect = null;
                    for (PointLight torch : torchLights) {
                        torch.remove();
                    }
                    torchLights.clear();
                    wasInTrigger = false;
                }
            }

            if (lightTogglerRect != null) {
                Rectangle feet = new Rectangle(
                    player.getPosition().x,
                    player.getPosition().y,
                    player.getHitbox().width,
                    5f
                );
                boolean isInLightToggler = feet.overlaps(lightTogglerRect);
                if (isInLightToggler) wasInLightToggler = true;
                if (!isInLightToggler && wasInLightToggler) {
                    if ((ccomingFromR1 && feet.overlaps(comingFromR2)) ||
                        (ccomingFromR2 && feet.overlaps(comingFromR1))) {

                        lightsEnabled = !lightsEnabled;
                        if (!lightsEnabled) {
                            targetAmbient = 1.0f;
                            targetLightRadius = 0f;
                        } else {
                            targetAmbient = 0.5f;
                            targetLightRadius = maxLightRadius;
                        }

                        if (ccomingFromR1) {
                            ccomingFromR1 = false;
                            ccomingFromR2 = true;
                        } else {
                            ccomingFromR1 = true;
                            ccomingFromR2 = false;
                        }
                    }
                    wasInLightToggler = false;
                }
            }

           
            for (OrbXp orb : entityFactory.getActiveOrbs()) {
                orb.update(delta);
                if (player.getHitbox().overlaps(orb.getHitbox())) {
                    player.addXp(orb.getXpValue());
                    entityFactory.releaseOrbXp(orb);
                }
            }
        }

        if (playerLight != null) {
            playerLight.setPosition(
                player.getPosition().x + player.getHitbox().width / 2f,
                player.getPosition().y + player.getHitbox().height / 2f
            );
            currentLightRadius = MathUtils.lerp(currentLightRadius, targetLightRadius, 0.05f);
            playerLight.setDistance(currentLightRadius);
            currentAmbient = MathUtils.lerp(currentAmbient, targetAmbient, 0.05f);
            rayHandler.setAmbientLight(currentAmbient);
            rayHandler.setCombinedMatrix(camera);
            rayHandler.updateAndRender();
        }

        drawScene();
        drawOverlayIfActive(delta);
        if (isPauseMenuVisible) {
            if (pauseOverlayTexture != null) {
                float w = viewport.getWorldWidth();
                float h = viewport.getWorldHeight();
                float x = camera.position.x - w / 2f;
                float y = camera.position.y - h / 2f;

                batch.setProjectionMatrix(camera.combined);
                batch.begin();
                batch.setColor(1f, 1f, 1f, 1f);
                batch.draw(pauseOverlayTexture, x, y, w, h);
                batch.end();
            }
            if (pauseStage != null) {
                pauseStage.act(delta);
                pauseStage.draw();
            }
        }
        handleGlobalInput();
}


    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void updateCamera() {
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, 0.05f);
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

    private void drawPauseText() {
        if (!isPaused) return;
        String text = "PAUSE";
        pauseLayout.setText(font, text);
        float x = camera.position.x - pauseLayout.width / 2f;
        float y = camera.position.y + pauseLayout.height / 2f;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, pauseLayout, x, y);
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
            isPauseMenuVisible = !isPauseMenuVisible;
            isPaused = isPauseMenuVisible;  

            if (isPauseMenuVisible) {
                if (pauseStage != null) {
                    Gdx.input.setInputProcessor(pauseStage);
                }
            } else {
                Gdx.input.setInputProcessor(null);
            }
            return;
        }
        if (isPauseMenuVisible) return;
        if (isPaused) return;

        if (Gdx.input.isKeyJustPressed(Keys.P)) {
            Main.changeScreen("Menu");
        }
        if (Gdx.input.isKeyJustPressed(Keys.L)) {
            showLevelUpScreen();
        }
        if (Gdx.input.isKeyJustPressed(Keys.G)) {
            testGameOver();
        }
}



    public void showLevelUpScreen() {
        isPaused = true; // on fige le jeu
        previousInputProcessor = Gdx.input.getInputProcessor();
        if (levelUpOverlay == null) {
            levelUpOverlay = new LevelUp(this);
        }
        levelUpOverlay.show();
        Gdx.input.setInputProcessor(levelUpOverlay.getStage());
    }


    
    private void drawPauseOverlay() {
        if (!isPaused || pauseOverlayTexture == null) return;

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float x = camera.position.x - w / 2f;
        float y = camera.position.y - h / 2f;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(pauseOverlayTexture, x, y, w, h);
        batch.end();
    }

    private void testGameOver() {
        Main main = (Main) Gdx.app.getApplicationListener();

        int kills = player.getKillCount();                    
        int level = player.getLevel();           
        float time = survivalTime;                       

        main.setScreen(new GameOverScreen(main, kills, level, time));
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
        if (rayHandler != null) {
            rayHandler.dispose();
            rayHandler = null;
        }
        if (lightWorld != null) {
            lightWorld.dispose();
            lightWorld = null;
        }
        if (torchLights != null) {
            torchLights.clear();
            torchLights = null;
        }

        if (pauseOverlayTexture != null) {
            pauseOverlayTexture.dispose();
            pauseOverlayTexture = null;
        }
    }


    public void onGameOver() {
        int level = player.getLevel();
        int kills = this.killCount;
        float time = this.survivalTime;
        Main main = (Main) Gdx.app.getApplicationListener();
        main.setScreen(new GameOverScreen(main, kills, level, time));

        dispose();
    }
}
