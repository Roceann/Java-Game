package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

import io.github.dr4c0nix.survivorgame.Main;


public class PauseScreen implements Screen {

    private final Main main;
    private final Gameplay gameplay;

    private Stage stage;
    private Texture overlay;
    private Texture btnUp, btnDown;
    private BitmapFont font;

    public PauseScreen(Main main, Gameplay gameplay) {
        this.main = main;
        this.gameplay = gameplay;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(1.2f);

        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(0f, 0f, 0f, 0.6f); 
        p.fill();
        overlay = new Texture(p);
        p.dispose();

        Pixmap pb = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pb.setColor(0.15f, 0.15f, 0.15f, 0.95f);
        pb.fill();
        btnUp = new Texture(pb);
        pb.dispose();

        // Bouton pressed (encore plus sombre)
        Pixmap pbd = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pbd.setColor(0.2f, 0.05f, 0.05f, 0.95f);
        pbd.fill();
        btnDown = new Texture(pbd);
        pbd.dispose();

        buildUI();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(overlay)));
        stage.addActor(root);

        // ========= COLONNE GAUCHE : TITRE + BOUTONS =========
        Table leftCol = new Table();
        leftCol.defaults().pad(8f);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.WHITE);
        Label title = new Label("PAUSE", titleStyle);
        title.setFontScale(2f);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up   = new TextureRegionDrawable(new TextureRegion(btnUp));
        btnStyle.down = new TextureRegionDrawable(new TextureRegion(btnDown));

        TextButton resumeBtn = new TextButton("Reprendre", btnStyle);
        TextButton menuBtn   = new TextButton("Menu principal", btnStyle);
        TextButton quitBtn   = new TextButton("Quitter", btnStyle);

        // Listeners
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Retour au gameplay en cours
                main.setScreen(gameplay);
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Retour au menu principal
                main.setScreen(new Menu());
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        leftCol.add(title).padBottom(20f).row();
        leftCol.add(resumeBtn).width(260f).height(60f).row();
        leftCol.add(menuBtn).width(260f).height(60f).row();
        leftCol.add(quitBtn).width(260f).height(60f).row();

        Table rightCol = new Table();
        rightCol.defaults().pad(4f).left();

        if (gameplay != null && gameplay.getPlayer() != null) {
            Player p = gameplay.getPlayer();

            Label.LabelStyle statsStyle = new Label.LabelStyle(font, Color.WHITE);

            Label statsTitle = new Label("Stats du personnage", statsStyle);
            statsTitle.setFontScale(1.4f);

            Label hpLabel    = new Label("HP : " + (int)p.getHp() + " / " + (int)p.getMaxHp(), statsStyle);
            Label armorLabel = new Label("Armure : " + p.getArmor(), statsStyle);
            Label forceLabel = new Label("Force : " + p.getForce(), statsStyle);
            Label lvlLabel   = new Label("Niveau : " + p.getLevel(), statsStyle);
            Label xpLabel    = new Label("XP : " + p.getXpactual() + " / " + p.getExperienceToNextLevel(), statsStyle);
            Label killLabel  = new Label("Mobs tués : " + p.getMobKilled(), statsStyle);

            rightCol.add(statsTitle).padBottom(10f).row();
            rightCol.add(hpLabel).row();
            rightCol.add(armorLabel).row();
            rightCol.add(forceLabel).row();
            rightCol.add(lvlLabel).row();
            rightCol.add(xpLabel).row();
            rightCol.add(killLabel).row();
        }

        root.center();
        root.add(leftCol).padRight(60f);
        root.add(rightCol).padLeft(60f);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (overlay != null) overlay.dispose();
        if (btnUp != null) btnUp.dispose();
        if (btnDown != null) btnDown.dispose();
        if (font != null) font.dispose();
    }
}
