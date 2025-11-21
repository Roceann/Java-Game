package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.dr4c0nix.survivorgame.Main;

public class GameOverScreen implements Screen {

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
    private Texture gameOverBg;

    public GameOverScreen(Main main, int kills, int level, float timeSurvived) {
        this.main = main;
        this.kills = kills;
        this.level = level;
        this.timeSurvived = timeSurvived;

        gameOverBg = new Texture(Gdx.files.internal("background/gameoverbg.png"));
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
        font.getData().setScale(1.4f);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.4f);
        titleFont.setColor(Color.RED);
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
        panel.pad(20);

        Label title = new Label("GAME OVER", new Label.LabelStyle(titleFont, Color.RED));
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

        panel.add(title).padBottom(20).row();
        panel.add(stats).padBottom(20).row();
        panel.add(retryBtn).width(250).height(60).pad(5).row();
        panel.add(menuBtn).width(250).height(60).pad(5).row();
        panel.add(quitBtn).width(250).height(60).pad(5).row();

        root.add(panel);
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(gameOverBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) {
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
        gameOverBg.dispose();
    }
}
