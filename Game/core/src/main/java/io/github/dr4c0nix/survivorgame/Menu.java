package io.github.dr4c0nix.survivorgame;

import space.earlygrey.shapedrawer.ShapeDrawer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** First screen of the application. Displayed after the application is created. */
public class Menu implements Screen {
    
    private ShapeRenderer shape;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private ShapeDrawer drawer;
    private Texture whiteTex;


    @Override
    public void show() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTex = new Texture(pixmap);
        pixmap.dispose();
        drawer = new ShapeDrawer(batch, new TextureRegion(whiteTex));
    }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0.2f, 0.5f, 0.8f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            float hudX = 20;
            float hudY = Gdx.graphics.getHeight() - 20;
            float hudWidth = 300;
            float hudHeight = 70;
            float hpPercent = 0.8f;
            float xpPercent = 0.4f;

            float hp_X = hudX + 80;
            float hp_Y = hudY - 25;
            float hp_W = 200;
            float hp_H = 10;
            float weapon_X = hudX + 100; 
            float weapon_Y = hudY - hudHeight ;
            float slotWeaponW = 40;
            float slotWeaponH = 40;
            float slotSpacing = 10;

            float avatarCenterX = hudX + 35;
            float avatarCenterY = hudY - 35;
            float avatarRadius  = 30;
            float outerRadius = avatarRadius + 4;
            // cette partie me servira plus tard pour la gestion de player.xp et player.XpToNextLevel en gros
            // jvais faire sweepDeg =  360f*(player.xp/player.XpToNextLevel);
            float startDeg = 90f;
            float sweepDeg = 144f;
            float startRad = startDeg * com.badlogic.gdx.math.MathUtils.degreesToRadians;
            float sweepRad = sweepDeg * com.badlogic.gdx.math.MathUtils.degreesToRadians;

            
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            shape.rect(hudX, hudY - hudHeight, hudWidth, hudHeight);
            shape.setColor(Color.DARK_GRAY);
            shape.rect(hp_X, hp_Y, hp_W, hp_H);
            shape.setColor(Color.GREEN);
            shape.rect(hp_X, hp_Y, hp_W * hpPercent, hp_H);
            shape.setColor(Color.DARK_GRAY);
            for (int i = 0; i < 3; i++) {
                float x = weapon_X + i * (slotWeaponW + slotSpacing);
                shape.rect(x , weapon_Y, slotWeaponW, slotWeaponH);
                }
            shape.end();

            
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            drawer.setColor(new Color(1, 0, 1, 0.6f));
            drawer.setColor(new Color(1, 0, 1, 0.6f));
            drawer.arc(avatarCenterX, avatarCenterY, outerRadius, startRad, sweepRad, 4f);

            font.draw(batch, "player.name", hp_X + 5, hudY + 5);
            font.draw(batch, (int)(hpPercent * 100) + "%", hp_X + hp_W - 10, hp_Y - 10);
            batch.end();

    
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.DARK_GRAY);
            shape.circle(avatarCenterX, avatarCenterY, avatarRadius);
            shape.end();
        }



    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
    }
}