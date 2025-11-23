package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.dr4c0nix.survivorgame.Main;

public class VictoryScreen implements Screen {

    private final Main main;
    private final int kills;
    private final int level;
    private final float timeSurvived;

    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture overlayTex;
    private Texture panelTex;
    private Texture victoryBg;

    private float scaleX = 1f;
    private float scaleY = 1f;

    public VictoryScreen(Main main, int kills, int level, float timeSurvived) {
        this.main = main;
        this.kills = kills;
        this.level = level;
        this.timeSurvived = timeSurvived;

        victoryBg = new Texture(Gdx.files.internal("background/victorybg.png"));
        updateScale();
    }

    private void updateScale() {
        this.scaleX = (Gdx.graphics.getWidth() / 1920f) * 1.2f;
        this.scaleY = (Gdx.graphics.getHeight() / 1080f) * 1.2f;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        createFonts();
        createTextures();
        buildUI();
    }

    private void createFonts() {
        font = new BitmapFont();
        font.getData().setScale(1.4f * Math.min(scaleX, scaleY));

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.4f * Math.min(scaleX, scaleY));
        titleFont.setColor(Color.GREEN);
    }

    private void createTextures() {
        Pixmap p1 = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p1.setColor(0f, 0f, 0f, 0.7f);
        p1.fill();
        overlayTex = new Texture(p1);
        p1.dispose();

        Pixmap p2 = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p2.setColor(0.15f, 0.15f, 0.15f, 0.9f);
        p2.fill();
        panelTex = new Texture(p2);
        p2.dispose();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(overlayTex)));

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTex)));
        panel.pad(20 * Math.min(scaleX, scaleY));

        Label title = new Label("VICTOIRE !", new Label.LabelStyle(titleFont, Color.GREEN));
        title.setAlignment(Align.center);

        String timeString = Gameplay.formatTime(timeSurvived);

        Label stats = new Label(
            "Kills : " + kills + "\nNiveau : " + level + "\nTemps survécu : " + timeString,
            new Label.LabelStyle(font, Color.WHITE)
        );
        stats.setAlignment(Align.center);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(panelTex));

        TextButton retryBtn = new TextButton("Rejouer", btnStyle);
        TextButton menuBtn = new TextButton("Menu principal", btnStyle);
        TextButton quitBtn = new TextButton("Quitter", btnStyle);

        retryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new Gameplay());
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

        panel.add(title).padBottom(20 * Math.min(scaleX, scaleY)).row();
        panel.add(stats).padBottom(20 * Math.min(scaleX, scaleY)).row();
        panel.add(retryBtn).width(250 * scaleX).height(60 * scaleY).pad(5 * Math.min(scaleX, scaleY)).row();
        panel.add(menuBtn).width(250 * scaleX).height(60 * scaleY).pad(5 * Math.min(scaleX, scaleY)).row();
        panel.add(quitBtn).width(250 * scaleX).height(60 * scaleY).pad(5 * Math.min(scaleX, scaleY)).row();

        root.add(panel).center();
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(victoryBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        updateScale();
        stage.getViewport().update(w, h, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        overlayTex.dispose();
        panelTex.dispose();
        font.dispose();
        titleFont.dispose();
        victoryBg.dispose();
    }
}
