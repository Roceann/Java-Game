package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Menu qui implémente l'interface Screen de LibGDX.
 * 
 * Cette classe gère l'affichage et l'interaction du menu principal du jeu.
 * Elle crée une interface utilisateur avec des boutons pour démarrer le jeu,
 * accéder aux options, et quitter l'application.
 * 
 * @author Roceann
 * @version 1.0
 */
public class Menu implements Screen {
    private OrthographicCamera camera;
    private FitViewport viewport;
    private Stage stage;

    private BitmapFont font;
    private TextButton.TextButtonStyle textButtonStyle;
    private Texture buttonTexture;
    private Texture buttonTextureDown;
    private List<TextButton> currentButtons = new ArrayList<>();
    private boolean isFullscreen = false;

    /**
     * Constructeur du Menu.
     * 
     * Initialise la caméra orthographique et le viewport avec une résolution
     * de base de 800x600.
     */
    public Menu() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
    }

    /**
     * Construit le menu principal avec les boutons Play, Options et Exit.
     * 
     * Cette méthode crée les trois boutons principaux et configure leurs
     * écouteurs pour gérer les clics utilisateur :
     * - Play : affiche un log
     * - Options : accède au menu des options
     * - Exit : quitte l'application
     * 
     * @param table La table dans laquelle ajouter les boutons
     */
    private void buildMenu(Table table) {
        clearMenu();
        table.clear();
        table.setFillParent(true);
        table.center();
        TextButton play = createButtons("Play", table);
        TextButton options = createButtons("Options", table);
        TextButton exit = createButtons("Exit", table);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Menu", "clique sur play");
            }
        });
        options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptions(table);
            }
        });
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    /**
     * Assure que le style des boutons est initialisé.
     * 
     * Cette méthode crée et configure le style des boutons, y compris :
     * - La police de caractères (BitmapFont)
     * - Les textures pour les états normal et appuyé
     * - Les couleurs et styles du bouton
     * 
     * L'initialisation n'est effectuée qu'une seule fois pour éviter
     * les allocations mémoire répétées.
     */
    private void ensureStyle() {
        if (font != null) return;
        font = new BitmapFont();

        Pixmap pix = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pix.setColor(0.15f, 0.35f, 0.65f, 1f);
        pix.fill();
        pix.setColor(0.05f, 0.12f, 0.25f, 1f);
        pix.drawRectangle(0, 0, 8, 8);
        buttonTexture = new Texture(pix);

        Pixmap pixDown = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        pixDown.setColor(0.10f, 0.25f, 0.50f, 1f);
        pixDown.fill();
        pixDown.setColor(0.03f, 0.08f, 0.18f, 1f);
        pixDown.drawRectangle(0, 0, 8, 8);
        buttonTextureDown = new Texture(pixDown);

        pix.dispose();
        pixDown.dispose();

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTextureDown));
    }

    /**
     * Crée un bouton avec le label donné et l'ajoute à la table.
     * 
     * Cette méthode initialise le style si nécessaire, crée un nouveau bouton,
     * configure sa taille et son apparence, puis l'ajoute à la table fournie.
     * Le bouton est également ajouté à la liste des boutons actuels.
     * 
     * @param label Le texte affiché sur le bouton
     * @param table La table dans laquelle ajouter le bouton
     * @return Le bouton créé
     */
    private TextButton createButtons(String label, Table table) {
        ensureStyle();
        TextButton btn = new TextButton(label, textButtonStyle);
        Label buttonLabel = btn.getLabel();
        buttonLabel.setFontScale(1.4f);
        table.add(btn).width(300).height(80).pad(10).center();
        table.row();
        currentButtons.add(btn);
        return btn;
    }

    /**
     * Efface tous les boutons du menu.
     * 
     * Cette méthode supprime tous les écouteurs d'événements des boutons
     * actuels, les retire de la scène, puis vide la liste.
     * Cela permet de libérer les ressources et les références.
     */
    private void clearMenu() {
        for (TextButton b : currentButtons) {
            if (b != null) {
                b.clearListeners();
                b.remove();
            }
        }
        currentButtons.clear();
    }

    /**
     * Affiche le menu des options.
     * 
     * Cette méthode remplace le contenu du menu par les options disponibles :
     * - Un bouton pour basculer le mode plein écran
     * - Un bouton pour revenir au menu principal
     * 
     * @param table La table contenant l'interface utilisateur
     */
    private void showOptions(final Table table) {
        table.clear();
        table.setFillParent(true);
        table.center();

        final TextButton fullscreenBtn = createButtons("Fullscreen: ", table);
        final TextButton backBtn = createButtons("Back", table);

        isFullscreen = Gdx.graphics.isFullscreen();
        fullscreenBtn.setText("Fullscreen: " + (isFullscreen ? "ON" : "OFF"));

        fullscreenBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isFullscreen) {
                    Gdx.graphics.setWindowedMode(800, 600);
                    isFullscreen = false;
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    isFullscreen = true;
                }
                fullscreenBtn.setText("Fullscreen: " + (isFullscreen ? "ON" : "OFF"));
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buildMenu(table);
            }
        });
    }

    /**
     * Initialise et affiche le menu sur l'écran.
     * 
     * Cette méthode est appelée lorsque l'écran est affiché. Elle :
     * - Crée une nouvelle scène (Stage)
     * - Configure le gestionnaire d'entrée
     * - Crée une table pour organiser l'interface utilisateur
     * - Construit le menu principal
     */
    @Override
    public void show() {
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);
        buildMenu(table);
    }

    /**
     * Rend le menu à chaque image (frame).
     * 
     * Cette méthode est appelée continuellement pour afficher le menu.
     * Elle :
     * - Efface l'écran avec une couleur noire
     * - Applique le viewport
     * - Met à jour et affiche la scène
     * 
     * @param delta Le temps écoulé depuis la dernière image en secondes
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Redimensionne le menu en fonction de la taille de la fenêtre.
     * 
     * Cette méthode est appelée lorsque la fenêtre de jeu est redimensionnée.
     * Elle met à jour les dimensions du viewport et de la scène pour maintenir
     * l'interface utilisateur correctement alignée.
     * 
     * @param width La nouvelle largeur de la fenêtre en pixels
     * @param height La nouvelle hauteur de la fenêtre en pixels
     */
    @Override
    public void resize(int width, int height) {

        if(width <= 0 || height <= 0) return;

        if (viewport != null) {
            viewport.update(width, height, true);
        }
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    /**
     * Appelée lorsque l'application est mise en pause.
     * 
     * Cette méthode est invoquée par LibGDX lorsque l'application passe en arrière-plan
     * ou est mise en pause (par exemple, sur mobile).
     */
    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    /**
     * Appelée lorsque l'application reprend après une mise en pause.
     * 
     * Cette méthode est invoquée par LibGDX lorsque l'application redevient active
     * après avoir été mise en pause (par exemple, après être revenue en avant-plan sur mobile).
     */
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    /**
     * Appelée lorsqu'un autre écran remplace celui-ci.
     * 
     * Cette méthode est invoquée par LibGDX lorsque le menu est caché
     * et qu'un autre écran (par exemple l'écran de jeu) prend sa place.
     */
    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    /**
     * Libère toutes les ressources utilisées par le menu.
     * 
     * Cette méthode est appelée lorsque le menu est fermé et n'est plus utilisé.
     * Elle :
     * - Efface tous les boutons et leurs écouteurs
     * - Dispose de la scène
     * - Dispose de la police de caractères
     * - Dispose des textures des boutons
     * 
     * Cette méthode est essentielle pour éviter les fuites mémoire en libérant
     * les ressources graphiques allouées par LibGDX.
     */
    @Override
    public void dispose() {
        clearMenu();
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonTextureDown != null) buttonTextureDown.dispose();
    }
}