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
import io.github.dr4c0nix.survivorgame.weapon.Weapon;

public class PauseScreen implements Screen {

    private final Main main;
    private final Gameplay gameplay;

    private Stage stage;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture overlayTex;   
    private Texture panelTex;
    private Texture contourTex;
    
    private float scaleX = 1f;
    private float scaleY = 1f;

    public PauseScreen(Main main, Gameplay gameplay) {
        this.main = main;
        this.gameplay = gameplay;
        updateScale();
    }

    @Override
    public void show() {
        if (gameplay != null) gameplay.setIsPaused(true);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createFonts();
        createTextures();
        buildUI();
    }

    private void updateScale() {
        this.scaleX = (Gdx.graphics.getWidth() / 1920f) * 1.2f;
        this.scaleY = (Gdx.graphics.getHeight() / 1080f) * 1.2f;
    }

    private void createFonts() {
        font = new BitmapFont();
        font.getData().setScale(1.3f * Math.min(scaleX, scaleY));
        
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.3f * Math.min(scaleX, scaleY));
        titleFont.setColor(Color.WHITE);
    }

    private void createTextures() {
        Pixmap p1 = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p1.setColor(0.35f, 0.35f, 0.35f, 1f);
        p1.fill();
        panelTex = new Texture(p1);
        p1.dispose();

        Pixmap p2 = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        p2.setColor(0.18f, 0.18f, 0.18f, 1f);
        p2.fill();
        contourTex = new Texture(p2);
        p2.dispose();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Table visuelle = new Table();
        visuelle.setBackground(new TextureRegionDrawable(new TextureRegion(contourTex)));
        visuelle.pad(18 * Math.min(scaleX, scaleY));
        visuelle.center();

        Label title = new Label("PAUSED", new Label.LabelStyle(titleFont, Color.WHITE));
        title.setAlignment(Align.center);
        visuelle.add(title).colspan(2).padBottom(12 * Math.min(scaleX, scaleY)).row();

        Table leftPanel = new Table();
        float btnWidth = 220f * scaleX;
        float btnHeight = 55f * scaleY;
        leftPanel.defaults().pad(8f * Math.min(scaleX, scaleY)).width(btnWidth).height(btnHeight);
        
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(panelTex));
        
        TextButton resumeBtn = new TextButton("Resume", btnStyle);
        TextButton menuBtn   = new TextButton("Main Menu", btnStyle);
        TextButton quitBtn   = new TextButton("Quit", btnStyle);

        leftPanel.add(resumeBtn).expandY().top().row();
        leftPanel.add(menuBtn).expandY().center().row();
        leftPanel.add(quitBtn).expandY().bottom().row();

        Table rightPanel = new Table();
        rightPanel.defaults().pad(4f * Math.min(scaleX, scaleY));
        rightPanel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTex)));
        rightPanel.pad(12f * Math.min(scaleX, scaleY));
        
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label.LabelStyle textStyle  = new Label.LabelStyle(font, Color.WHITE);
        
        Label timeLabel = new Label("Time: " + Gameplay.formatTime(gameplay.getElapsedTime()), textStyle);
        timeLabel.setAlignment(Align.center);
        Label statsTitle = new Label("STATISTICS", titleStyle);
        statsTitle.setAlignment(Align.center);
        final Label statsLabel = new Label("", textStyle);
        statsLabel.setAlignment(Align.topLeft);
        updateStatsText(statsLabel);

        rightPanel.add(timeLabel).padBottom(10f * Math.min(scaleX, scaleY)).row();
        rightPanel.add(statsTitle).padBottom(10f * Math.min(scaleX, scaleY)).row();
        rightPanel.add(statsLabel).width(260f * scaleX).left().top();

        visuelle.add(leftPanel).top().left().pad(20f * Math.min(scaleX, scaleY));
        visuelle.add(rightPanel).top().right().pad(20f * Math.min(scaleX, scaleY));

        root.add(visuelle).center();
        stage.addActor(root);

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
    }

    // Mise en public pour les tests
    public void updateStatsText(Label statsLabel) {
        Player p = gameplay.getPlayer();
        if (p == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("XP: ").append(p.getXpactual()).append(" / ").append(p.getExperienceToNextLevel()).append("\n");
        sb.append("HP: ").append((int) p.getHp()).append(" / ").append((int) p.getMaxHp()).append("\n");
        sb.append("Armor: ").append(p.getArmor()).append("\n");
        sb.append("Speed: ").append(String.format("%.2f", p.getMovementSpeed())).append("\n");
        sb.append("Crit Chance: ").append(String.format("%.1f%%", p.getCritChance())).append("\n");
        sb.append("Crit Damage: x").append(String.format("%.2f", p.getCritDamage())).append("\n");
        sb.append("HP Regen: ").append(String.format("%.1f hp/10sec", p.getRegenHP())).append("\n\n");
        sb.append("Mobs Killed: ").append(p.getMobKilled()).append("\n\n");

        Weapon weapon = p.getCurrentWeapon();
        if (weapon == null) {
            sb.append("Equipped Weapon: none");
        } else {
            sb.append("Equipped Weapon: ").append(weapon.getClass().getSimpleName()).append("\n");
            sb.append("Weapon Level: ").append(weapon.getWeaponLevel()).append("\n");
            sb.append("Damage: ").append(weapon.getEffectiveDamage()).append("\n");
            sb.append("Rate: ").append(String.format("%.2f seconds", weapon.getEffectiveShotDelay())).append("\n");
            sb.append("Projectile Size: ").append(String.format("%.2f", weapon.getEffectiveProjectileSize()));
        }

        statsLabel.setText(sb.toString());
    }

    @Override
    public void render(float delta) {
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
        if (stage != null) stage.dispose();
        if (overlayTex != null) overlayTex.dispose();
        if (panelTex != null) panelTex.dispose();
        if (contourTex != null) contourTex.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
    }
}
