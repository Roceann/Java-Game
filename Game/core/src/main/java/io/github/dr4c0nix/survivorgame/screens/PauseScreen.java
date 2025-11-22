package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class PauseScreen implements Screen {

    private final Main main;
    private final Gameplay gameplay;

    private Stage stage;
    private BitmapFont font;
    private Texture overlayTex;   
    private Texture panelTex;     // fond des panneaux

    public PauseScreen(Main main, Gameplay gameplay) {
        this.main = main;
        this.gameplay = gameplay;
    }

    @Override
    public void show() {
        if (gameplay != null) gameplay.setIsPaused(true);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(1.2f);

        createTextures();
        buildUI();
    }

    private void createTextures() {
        // Fond noir sensé être transparent
        Pixmap p1 = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p1.setColor(0f, 0f, 0f, 0.35f); 
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
        Table leftPanel = new Table();
        leftPanel.defaults().pad(8f).width(220f).height(55f);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(panelTex));
        TextButton resumeBtn = new TextButton("Reprendre", btnStyle);
        TextButton menuBtn   = new TextButton("Menu principal", btnStyle);
        TextButton quitBtn   = new TextButton("Quitter", btnStyle);

        leftPanel.add(resumeBtn).row();
        leftPanel.add(menuBtn).row();
        leftPanel.add(quitBtn).row();


        Table rightPanel = new Table();
        rightPanel.defaults().pad(4f);
        rightPanel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTex)));
        rightPanel.pad(12f);
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label.LabelStyle textStyle  = new Label.LabelStyle(font, Color.WHITE);
        Label statsTitle = new Label("STATISTIQUES", titleStyle);
        statsTitle.setAlignment(Align.center);
        final Label statsLabel = new Label("", textStyle);
        statsLabel.setAlignment(Align.topLeft);
        updateStatsText(statsLabel);

        rightPanel.add(statsTitle).padBottom(10f).row();
        rightPanel.add(statsLabel).width(260f).left().top();
        root.add(leftPanel).top().left().pad(20f);
        root.add(rightPanel).top().right().pad(20f);


        resumeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                gameplay.setIsPaused(false);
                main.setScreen(gameplay);
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                gameplay.stopMusics();
                gameplay.setIsPaused(false);
                Main.changeScreen("Menu");
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(root);
    }

    private void updateStatsText(Label statsLabel) {
        Player p = gameplay.getPlayer();
        if (p == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Niveau : ").append(p.getLevel()).append("\n");
        sb.append("XP : ").append(p.getXpactual())
            .append(" / ").append(p.getExperienceToNextLevel()).append("\n\n");

        sb.append("HP : ").append((int)p.getHp())
            .append(" / ").append((int)p.getMaxHp()).append("\n");
        sb.append("Armure : ").append(p.getArmor()).append("\n");
        sb.append("Force : ").append(p.getForce()).append("\n\n");

        sb.append("Crit chance : ").append(p.getCritChance()).append("\n");
        sb.append("Crit dmg : x").append(p.getCritDamage()).append("\n");
        sb.append("Regen HP : ").append(p.getRegenHP()).append("\n");

        statsLabel.setText(sb.toString());
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (overlayTex != null) overlayTex.dispose();
        if (panelTex != null) panelTex.dispose();
        if (font != null) font.dispose();
    }
}
