package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.dr4c0nix.survivorgame.Main;

public class VictoryScreen implements Screen {

    private final Main main;
    private final int kills;
    private final int level;
    private final float timeSurvived;

    private Stage stage;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture panelTex;
    private Texture buttonTex;
    private Texture buttonDownTex;

    public VictoryScreen(Main main, int kills, int level, float timeSurvived) {
        this.main = main;
        this.kills = kills;
        this.level = level;
        this.timeSurvived = timeSurvived;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(Color.GOLD);

        // Petit rectangle pour fond de panneau
        Pixmap panelPix = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        panelPix.setColor(0f, 0f, 0f, 0.7f);
        panelPix.fill();
        panelTex = new Texture(panelPix);
        panelPix.dispose();

        // Boutons
        Pixmap btnPix = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        btnPix.setColor(0.15f, 0.5f, 0.15f, 0.95f);
        btnPix.fill();
        buttonTex = new Texture(btnPix);
        btnPix.dispose();

        Pixmap btnDownPix = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        btnDownPix.setColor(0.05f, 0.3f, 0.05f, 0.95f);
        btnDownPix.fill();
        buttonDownTex = new Texture(btnDownPix);
        btnDownPix.dispose();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTex));
        btnStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDownTex));

        buildUI(btnStyle);
    }

    private void buildUI(TextButton.TextButtonStyle btnStyle) {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTex)));
        panel.pad(24f);

        Label title = new Label("VICTOIRE !", new Label.LabelStyle(titleFont, Color.GOLD));
        panel.add(title).padBottom(20f).row();

        String timeText = "Temps survécu : " + Gameplay.formatTime(timeSurvived);
        Label stats1 = new Label(timeText, new Label.LabelStyle(font, Color.WHITE));
        Label stats2 = new Label("Niveau atteint : " + level, new Label.LabelStyle(font, Color.WHITE));
        Label stats3 = new Label("Ennemis vaincus : " + kills, new Label.LabelStyle(font, Color.WHITE));

        panel.add(stats1).pad(4f).row();
        panel.add(stats2).pad(4f).row();
        panel.add(stats3).pad(12f).row();

        TextButton retryBtn = new TextButton("Rejouer", btnStyle);
        TextButton menuBtn  = new TextButton("Menu principal", btnStyle);
        TextButton quitBtn  = new TextButton("Quitter", btnStyle);

        retryBtn.getLabel().setFontScale(1.1f);
        menuBtn.getLabel().setFontScale(1.1f);
        quitBtn.getLabel().setFontScale(1.1f);

        retryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new Gameplay()); // relance une partie
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

        panel.add(retryBtn).width(260f).height(60f).pad(4f).row();
        panel.add(menuBtn).width(260f).height(60f).pad(4f).row();
        panel.add(quitBtn).width(260f).height(60f).pad(4f).row();

        root.add(panel);
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
    }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (panelTex != null) panelTex.dispose();
        if (buttonTex != null) buttonTex.dispose();
        if (buttonDownTex != null) buttonDownTex.dispose();
    }
}
