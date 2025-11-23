package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // ajout
import com.badlogic.gdx.graphics.Texture;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

/* 
 * beta hud.
 * @author Abdelkader1900
 * @version 0.1
 */
public class Hud implements Screen {
    private final ShapeRenderer shape;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;

    private float hpCur = 0f;
    private float hpMax = 1f;
    private float scaleX;
    private float scaleY;

    private String currentIconPath = null;
    private Texture iconTexture = null;

    public Hud(SpriteBatch sharedBatch) {
        this.batch = sharedBatch;
        this.shape = new ShapeRenderer();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.update();
        this.scaleX = (Gdx.graphics.getWidth() / 1920f) * 1.2f;
        this.scaleY = (Gdx.graphics.getHeight() / 1080f) * 1.2f;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void updateHealth(float currentHp, float maxHp) {
        this.hpCur = currentHp;
        this.hpMax = maxHp;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        camera.update();

        float hudX = 20 * scaleX;
        float hudY = Gdx.graphics.getHeight() - 20 * scaleY;

        float hpPercent = 0f;
        float xpPercent = 0f;
        String name = "Player";
        int level = 1;

        if (player != null) {
            hpMax = Math.max(player.getMaxHp(), 1f);
            hpCur = player.getHp();
            hpPercent = hpCur / hpMax;
            xpPercent = (float) player.getXpactual() / Math.max(player.getExperienceToNextLevel(), 1);
            name = player.getDescription();
            level = player.getLevel();

            String newIconPath = player.getCurrentWeapon().getIconPath();
            if (!newIconPath.equals(currentIconPath)) {
                if (iconTexture != null) {
                    iconTexture.dispose();
                }
                iconTexture = new Texture(Gdx.files.internal(newIconPath));
                currentIconPath = newIconPath;
            }
        }

        float avatarCenterX = hudX + 55 * scaleX;
        float avatarCenterY = hudY - 55 * scaleY;
        float avatarRadius = 50 * Math.min(scaleX, scaleY);

        float barsLeftX = hudX + 130 * scaleX;
        float topTextY = hudY - 10 * scaleY;

        float hp_W = 300 * scaleX;
        float hp_H = 20 * scaleY;
        float hp_X = barsLeftX;
        float hp_Y = avatarCenterY + 10 * scaleY;

        float xp_W = hp_W;
        float xp_H = 12 * scaleY;
        float xp_X = barsLeftX;
        float xp_Y = hp_Y - hp_H - 25 * scaleY;

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(Color.DARK_GRAY);
        shape.circle(avatarCenterX, avatarCenterY, avatarRadius);

        shape.setColor(Color.DARK_GRAY);
        shape.rect(hp_X, hp_Y, hp_W, hp_H);

        shape.setColor(Color.GREEN);
        shape.rect(hp_X, hp_Y, hp_W * hpPercent, hp_H);

        shape.setColor(Color.DARK_GRAY);
        shape.rect(xp_X, xp_Y, xp_W, xp_H);
        shape.setColor(Color.CYAN);
        shape.rect(xp_X, xp_Y, xp_W * xpPercent, xp_H);

        shape.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.getData().setScale(1.2f * Math.min(scaleX, scaleY));

        font.draw(batch, name + "  (Lvl " + level + ")", barsLeftX, topTextY);

        String hpText = String.format("HP: %.2f / %.2f", hpCur, hpMax);
        GlyphLayout hpLayout = new GlyphLayout(font, hpText);
        float hpTextX = hp_X + (hp_W - hpLayout.width) * 0.5f;
        float hpTextY = hp_Y + (hp_H + font.getCapHeight() * font.getScaleY()) * 0.5f;
        font.draw(batch, hpLayout, hpTextX, hpTextY);

        String xpText = "XP: " + (int)(xpPercent * 100) + "%";
        font.draw(batch, xpText, xp_X, xp_Y + xp_H + 16 * scaleY);

        if (iconTexture != null) {
            float iconSize = avatarRadius * 0.8f;
            batch.draw(iconTexture, avatarCenterX - iconSize / 2, avatarCenterY - iconSize / 2, iconSize, iconSize);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        this.scaleX = (width / 1920f) * 1.2f;
        this.scaleY = (height / 1080f) * 1.2f;
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shape.dispose();
        font.dispose();
        if (iconTexture != null) {
            iconTexture.dispose();
        }
    }
}