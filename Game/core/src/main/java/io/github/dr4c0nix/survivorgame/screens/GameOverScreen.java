package io.github.dr4c0nix.survivorgame.screens;

import io.github.dr4c0nix.survivorgame.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class GameOverScreen implements Screen {

    private final Main main;
    private final int kills;
    private final int level;
    private final float timeSurvived;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private Stage stage;

    private BitmapFont titleFont;
    private BitmapFont textFont;

    private Texture buttonTexture;
    private Texture buttonTextureDown;
    private TextButton.TextButtonStyle buttonStyle;

    public GameOverScreen(Main main, int kills, int level, float timeSurvived) {
        this.main = main;
        this.kills = kills;
        this.level = level;
        this.timeSurvived = timeSurvived;

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
    }

    @Override
    public void show() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        createFontsAndStyles();
        buildUI();
    }

    private void createFontsAndStyles() {
        titleFont = new BitmapFont();
        titleFont.setColor(Color.RED);
        titleFont.getData().setScale(7f);

        textFont = new BitmapFont();
        textFont.setColor(Color.WHITE);
        textFont.getData().setScale(1.4f);

        Pixmap pix = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pix.setColor(0.25f, 0.1f, 0.25f, 1f);
        pix.fill();
        pix.setColor(0.3f, 0.1f, 0.3f, 1f);
        pix.drawRectangle(0, 0, 8, 8);
        buttonTexture = new Texture(pix);

        Pixmap pixDown = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pixDown.setColor(0.15f, 0.02f, 0.02f, 1f);
        pixDown.fill();
        pixDown.setColor(0.8f, 0.15f, 0.15f, 1f);
        pixDown.drawRectangle(0, 0, 8, 8);
        buttonTextureDown = new Texture(pixDown);

        pix.dispose();
        pixDown.dispose();

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = textFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTextureDown));
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        Label title = new Label("GAME OVER", new Label.LabelStyle(titleFont, Color.PURPLE));
        title.setAlignment(Align.center);

    
        String timeString = formatTime(timeSurvived);

        Label statsLabel = new Label(
            "Niveau : " + level + "\n" +
            "Kills : " + kills + "\n" +
            "Temps survécu : " + timeString,
            new Label.LabelStyle(textFont, Color.WHITE)
        );
        statsLabel.setAlignment(Align.center);

        
        TextButton restartBtn = new TextButton("Restart", buttonStyle);
        TextButton menuBtn = new TextButton("Menu Principal", buttonStyle);
        TextButton quitBtn = new TextButton("Quitter le jeu", buttonStyle);

        restartBtn.getLabel().setFontScale(1.3f);
        menuBtn.getLabel().setFontScale(1.3f);
        quitBtn.getLabel().setFontScale(1.3f);

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen("Gameplay"); 
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

        root.add(title).padBottom(30f).row();
        root.add(statsLabel).padBottom(30f).row();

        root.add(restartBtn).width(260f).height(60f).pad(5f).row();
        root.add(menuBtn).width(260f).height(60f).pad(5f).row();
        root.add(quitBtn).width(260f).height(60f).pad(5f).row();

        stage.addActor(root);
    }

    private String formatTime(float timeSeconds) {
        int total = (int) timeSeconds;
        int minutes = total / 60;
        int seconds = total % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (titleFont != null) titleFont.dispose();
        if (textFont != null) textFont.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonTextureDown != null) buttonTextureDown.dispose();
    }
}
