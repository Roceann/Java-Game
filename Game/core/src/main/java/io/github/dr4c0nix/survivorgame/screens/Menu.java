package io.github.dr4c0nix.survivorgame.screens;
import io.github.dr4c0nix.survivorgame.GameOptions;
import io.github.dr4c0nix.survivorgame.Main;

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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
    private Music menuMusic;
    private Slider.SliderStyle sliderStyle;
    private Texture sliderBackgroundTexture;
    private Texture sliderKnobTexture;

    private TextButton waitingForKeyButton = null;
    private String waitingForKeyType = null;

    /**
     * Constructeur du Menu.
     * 
     * Initialise la caméra orthographique et le viewport avec une résolution
     * de base de 800x600.
     */
    public Menu() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Song/Penumbra-chosic.wav"));
        menuMusic.setLooping(true);
        updateMenuMusicVolume();
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
        table.add(play).width(300).height(80).pad(10).center();
        table.row();

        Table middleRow = new Table();
        TextButton options = createButtons("Options", middleRow);
        TextButton duration = createButtons("Game Duration", middleRow);
        middleRow.add(options).width(300).height(80).pad(10).center();
        middleRow.add(duration).width(300).height(80).pad(10).center();
        table.add(middleRow);
        table.row();

        TextButton exit = createButtons("Exit", table);
        table.add(exit).width(300).height(80).pad(10).center();
        table.row();

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.changeScreen("Gameplay");
            }
        });

        options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptions(table);
            }
        });

        duration.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDurationMenu(table);
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
        currentButtons.add(btn);
        return btn;
    }

    /**
     * Efface tous les boutons du menu.
     */
    // mise en public pour les tests
    public void clearMenu() {
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
        table.add(fullscreenBtn).width(300).height(80).pad(10).center();
        table.row();
        final TextButton keybindBtn = createButtons("Configure Keys", table);
        table.add(keybindBtn).width(300).height(80).pad(10).center();
        table.row();
        final TextButton audioBtn = createButtons("Audio Settings", table);
        table.add(audioBtn).width(300).height(80).pad(10).center();
        table.row();
        final TextButton backBtn = createButtons("Back", table);
        table.add(backBtn).width(300).height(80).pad(10).center();
        table.row();

        GameOptions options = GameOptions.getInstance();
        isFullscreen = options.isFullscreen();
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
                options.setFullscreen(isFullscreen);
                String fullScreenStatus = isFullscreen ? "ON" : "OFF";
                fullscreenBtn.setText("Fullscreen: " + fullScreenStatus);
            }
        });

        keybindBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showKeybindMenu(table);
            }
        });

        audioBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showAudioMenu(table);
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
     * Affiche le menu de configuration des touches.
     * 
     * @param table La table contenant l'interface utilisateur
     */
    private void showKeybindMenu(final Table table) {
        clearMenu();
        table.clear();
        table.setFillParent(true);
        table.center();

        final GameOptions options = GameOptions.getInstance();

        Label titleLabel = new Label("Configure Keys", new Label.LabelStyle(font, Color.WHITE));
        titleLabel.setFontScale(1.8f);
        table.add(titleLabel).colspan(2).pad(20);
        table.row();

        final TextButton upBtn = createKeybindButtonInline("Move Up: ", options.getKeyUp(), table);
        final TextButton downBtn = createKeybindButtonInline("Move Down: ", options.getKeyDown(), table);
        table.row();

        final TextButton leftBtn = createKeybindButtonInline("Move Left: ", options.getKeyLeft(), table);
        final TextButton rightBtn = createKeybindButtonInline("Move Right: ", options.getKeyRight(), table);
        table.row();

        upBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyInput(upBtn, "up", options);
            }
        });

        downBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyInput(downBtn, "down", options);
            }
        });

        leftBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyInput(leftBtn, "left", options);
            }
        });

        rightBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                waitForKeyInput(rightBtn, "right", options);
            }
        });

        table.row();
        table.add().height(20).colspan(2);
        table.row();

        final TextButton resetBtn = createButtonInline("Reset to Default", table);
        final TextButton backBtn = createButtonInline("Back", table);

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                options.resetToDefault();
                showKeybindMenu(table);
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptions(table);
            }
        });
    }

    /**
     * Crée un bouton pour afficher et modifier une touche.
     */
    private TextButton createKeybindButtonInline(String label, int currentKey, Table table) {
        ensureStyle();
        String keyName = GameOptions.getKeyName(currentKey);
        TextButton btn = new TextButton(label + keyName, textButtonStyle);
        Label buttonLabel = btn.getLabel();
        buttonLabel.setFontScale(1.2f);
        table.add(btn).width(300).height(80).pad(10).center();
        currentButtons.add(btn);
        return btn;
    }

    /**
     * Crée un bouton standard pour la section de modification des touches.
     */
    private TextButton createButtonInline(String label, Table table) {
        ensureStyle();
        TextButton btn = new TextButton(label, textButtonStyle);
        Label buttonLabel = btn.getLabel();
        buttonLabel.setFontScale(1.4f);
        table.add(btn).width(300).height(80).pad(10).center();
        currentButtons.add(btn);
        return btn;
    }

    /**
     * Active le mode d'attente d'une touche pour le binding.
     * Si une touche interdite est pressée, ignore la modification.
     */
    private void waitForKeyInput(final TextButton button, final String keyType, final GameOptions options) {
        waitingForKeyButton = button;
        waitingForKeyType = keyType;
        button.setText("Press any key...");
        
        final int[] forbiddenKeys = {
            com.badlogic.gdx.Input.Keys.SHIFT_LEFT,
            com.badlogic.gdx.Input.Keys.SHIFT_RIGHT,
            com.badlogic.gdx.Input.Keys.CONTROL_LEFT,
            com.badlogic.gdx.Input.Keys.CONTROL_RIGHT,
            com.badlogic.gdx.Input.Keys.ALT_LEFT,
            com.badlogic.gdx.Input.Keys.ALT_RIGHT,
            com.badlogic.gdx.Input.Keys.SYM,
            com.badlogic.gdx.Input.Keys.CAPS_LOCK,
            com.badlogic.gdx.Input.Keys.SCROLL_LOCK,
            com.badlogic.gdx.Input.Keys.NUM_LOCK,
            com.badlogic.gdx.Input.Keys.ESCAPE,
            com.badlogic.gdx.Input.Keys.TAB,
            com.badlogic.gdx.Input.Keys.DEL,
            com.badlogic.gdx.Input.Keys.ENTER,
            com.badlogic.gdx.Input.Keys.BACKSPACE,
            com.badlogic.gdx.Input.Keys.SPACE,
            com.badlogic.gdx.Input.Keys.F1,
            com.badlogic.gdx.Input.Keys.F2,
            com.badlogic.gdx.Input.Keys.F3,
            com.badlogic.gdx.Input.Keys.F4,
            com.badlogic.gdx.Input.Keys.F5,
            com.badlogic.gdx.Input.Keys.F6,
            com.badlogic.gdx.Input.Keys.F7,
            com.badlogic.gdx.Input.Keys.F8,
            com.badlogic.gdx.Input.Keys.F9,
            com.badlogic.gdx.Input.Keys.F10,
            com.badlogic.gdx.Input.Keys.F11,
            com.badlogic.gdx.Input.Keys.F12
        };
        
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (waitingForKeyButton != null && waitingForKeyType != null) {
                    boolean isForbidden = false;
                    for (int forbiddenKey : forbiddenKeys) {
                        if (keycode == forbiddenKey) {
                            isForbidden = true;
                            break;
                        }
                    }
                    
                    String keyLabel = "";

                    if (isForbidden) {
                        int currentKey = 0;
                        switch (waitingForKeyType) {
                            case "up":
                                currentKey = options.getKeyUp();
                                keyLabel = "Move Up: ";
                                break;
                            case "down":
                                currentKey = options.getKeyDown();
                                keyLabel = "Move Down: ";
                                break;
                            case "left":
                                currentKey = options.getKeyLeft();
                                keyLabel = "Move Left: ";
                                break;
                            case "right":
                                currentKey = options.getKeyRight();
                                keyLabel = "Move Right: ";
                                break;
                        }
                        waitingForKeyButton.setText(keyLabel + GameOptions.getKeyName(currentKey));
                        waitingForKeyButton = null;
                        waitingForKeyType = null;
                        Gdx.input.setInputProcessor(stage);
                        return true;
                    }
                    
                    switch (waitingForKeyType) {
                        case "up":
                            options.setKeyUp(keycode);
                            keyLabel = "Move Up: ";
                            break;
                        case "down":
                            options.setKeyDown(keycode);
                            keyLabel = "Move Down: ";
                            break;
                        case "left":
                            options.setKeyLeft(keycode);
                            keyLabel = "Move Left: ";
                            break;
                        case "right":
                            options.setKeyRight(keycode);
                            keyLabel = "Move Right: ";
                            break;
                    }
                    
                    waitingForKeyButton.setText(keyLabel + GameOptions.getKeyName(keycode));
                    waitingForKeyButton = null;
                    waitingForKeyType = null;
                    Gdx.input.setInputProcessor(stage);
                }
                return true;
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
        Gdx.app.log("Menu", "Screen shown");

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);
        buildMenu(table);

        if (menuMusic != null && !menuMusic.isPlaying()) {
            updateMenuMusicVolume();
            menuMusic.play();
        }
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
        clearMenu();   
        dispose();
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
        // Efface tous les boutons
        clearMenu();
        
        // Dispose du Stage
        if (stage != null) {
            try {
                stage.dispose();
                stage = null;
            } catch (Exception e) {
                System.err.println("Error disposing stage: " + e.getMessage());
            }
        }
        
        // Dispose de la police
        if (font != null) {
            try {
                font.dispose();
                font = null;
            } catch (Exception e) {
                System.err.println("Error disposing font: " + e.getMessage());
            }
        }
        
        // Dispose des textures de boutons
        if (buttonTexture != null) {
            try {
                buttonTexture.dispose();
                buttonTexture = null;
            } catch (Exception e) {
                System.err.println("Error disposing buttonTexture: " + e.getMessage());
            }
        }
        
        if (buttonTextureDown != null) {
            try {
                buttonTextureDown.dispose();
                buttonTextureDown = null;
            } catch (Exception e) {
                System.err.println("Error disposing buttonTextureDown: " + e.getMessage());
            }
        }
        if (menuMusic != null) {
            try {
                menuMusic.stop();
                menuMusic.dispose();
                menuMusic = null;
            } catch (Exception e) {
                System.err.println("Error disposing menuMusic: " + e.getMessage());
            }
        }
        if (sliderBackgroundTexture != null) {
            sliderBackgroundTexture.dispose();
            sliderBackgroundTexture = null;
        }
        if (sliderKnobTexture != null) {
            sliderKnobTexture.dispose();
            sliderKnobTexture = null;
        }
    }

    /**
     * Sous-menu audio : slider volume, reset, back.
     */
    private void showAudioMenu(final Table table) {
        clearMenu();
        table.clear();
        table.setFillParent(true);
        table.center();
        ensureStyle();
        ensureSliderStyle();

        final GameOptions options = GameOptions.getInstance();

        Label titleLabel = new Label("Audio Settings", new Label.LabelStyle(font, Color.WHITE));
        titleLabel.setFontScale(1.8f);
        table.add(titleLabel).colspan(2).pad(20);
        table.row();

        Label sliderLabel = new Label("Music Volume", new Label.LabelStyle(font, Color.WHITE));
        sliderLabel.setFontScale(1.4f);
        final Slider volumeSlider = new Slider(0f, 100f, 1f, false, sliderStyle);
        volumeSlider.setValue(options.getMusicVolume());
        final Label volumeValue = new Label(options.getMusicVolume() + "%", new Label.LabelStyle(font, Color.WHITE));
        volumeValue.setFontScale(1.2f);

        table.add(sliderLabel).pad(10);
        table.add(volumeSlider).width(350).padLeft(10).padRight(60).padTop(10).padBottom(10);
        table.row();
        table.add(volumeValue).colspan(2).padBottom(20);
        table.row();

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                int value = (int) volumeSlider.getValue();
                options.setMusicVolume(value);
                volumeValue.setText(value + "%");
                updateMenuMusicVolume();
            }
        });

        final TextButton resetBtn = createButtonInline("Reset Volume", table);
        final TextButton backBtn = createButtonInline("Back", table);

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                options.setMusicVolume(100);
                volumeSlider.setValue(100f);
                volumeValue.setText("100%");
                updateMenuMusicVolume();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showOptions(table);
            }
        });
    }

    /**
     * Sous-menu durée de partie : slider durée, reset, back.
     */
    private void showDurationMenu(final Table table) {
        clearMenu();
        table.clear();
        table.setFillParent(true);
        table.center();
        ensureStyle();
        ensureSliderStyle();

        final GameOptions options = GameOptions.getInstance();

        Label titleLabel = new Label("Game Duration Settings", new Label.LabelStyle(font, Color.WHITE));
        titleLabel.setFontScale(1.8f);
        table.add(titleLabel).colspan(2).pad(20);
        table.row();

        Label sliderLabel = new Label("Duration (minutes)", new Label.LabelStyle(font, Color.WHITE));
        sliderLabel.setFontScale(1.4f);
        final Slider durationSlider = new Slider(1f, 15f, 1f, false, sliderStyle);
        durationSlider.setValue(options.getGameDuration());
        final Label durationValue = new Label(options.getGameDuration() + " min", new Label.LabelStyle(font, Color.WHITE));
        durationValue.setFontScale(1.2f);

        table.add(sliderLabel).pad(10);
        table.add(durationSlider).width(350).padLeft(10).padRight(60).padTop(10).padBottom(10);
        table.row();
        table.add(durationValue).colspan(2).padBottom(20);
        table.row();

        durationSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                int value = (int) durationSlider.getValue();
                options.setGameDuration(value);
                durationValue.setText(value + " min");
            }
        });

        final TextButton resetBtn = createButtonInline("Reset Duration", table);
        final TextButton backBtn = createButtonInline("Back", table);

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                options.setGameDuration(5);
                durationSlider.setValue(5);
                durationValue.setText("5 min");
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buildMenu(table);
            }
        });
    }

    private void ensureSliderStyle() {
        if (sliderStyle != null) return;

        Pixmap trackPix = new Pixmap(200, 8, Pixmap.Format.RGBA8888);
        trackPix.setColor(0.15f, 0.35f, 0.65f, 1f);
        trackPix.fill();
        trackPix.setColor(0.05f, 0.12f, 0.25f, 1f);
        trackPix.drawRectangle(0, 0, 200, 8);
        sliderBackgroundTexture = new Texture(trackPix);
        trackPix.dispose();

        Pixmap knobPix = new Pixmap(16, 24, Pixmap.Format.RGBA8888);
        knobPix.setColor(0.10f, 0.25f, 0.50f, 1f);
        knobPix.fillRectangle(0, 0, 16, 24);
        knobPix.setColor(0.03f, 0.08f, 0.18f, 1f);
        knobPix.drawRectangle(0, 0, 16, 24);
        sliderKnobTexture = new Texture(knobPix);
        knobPix.dispose();

        sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBackgroundTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));
    }

    // MIse en public pour les tests
    public void updateMenuMusicVolume() {
        GameOptions options = GameOptions.getInstance();
        if (menuMusic != null) {
            menuMusic.setVolume(options.getMusicVolume() / 100f);
        }
    }
}