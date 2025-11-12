package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Classe de gestion des options du jeu.
 * 
 * Cette classe utilise le système de Preferences de LibGDX pour sauvegarder
 * et charger les options du jeu de manière persistante.
 * 
 * @author Drac0niX
 * @version 1.0
 */
public class GameOptions {
    private static final String PREFS_NAME = "game-options";
    private Preferences prefs;
    
    private int keyUp;
    private int keyDown;
    private int keyLeft;
    private int keyRight;
    private boolean fullscreen;
    // private float volume; ADD LATER
    
    private static GameOptions instance;
    
    /**
     * Constructeur privé.
     * Charge les préférences sauvegardées ou utilise les valeurs par défaut.
     */
    private GameOptions() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        loadPreferences();
        syncFullscreenState();
    }
    
    /**
     * Récupère l'instance unique de GameOptions.
     * @return L'instance de GameOptions
     */
    public static GameOptions getInstance() {
        if (instance == null) {
            instance = new GameOptions();
        }
        return instance;
    }
    
    /**
     * Charge les préférences depuis le fichier de sauvegarde.
     * Si aucune sauvegarde n'existe, utilise les valeurs par défaut.
     */
    private void loadPreferences() {
        keyUp = prefs.getInteger("keyUp", Keys.Z);
        keyDown = prefs.getInteger("keyDown", Keys.S);
        keyLeft = prefs.getInteger("keyLeft", Keys.Q);
        keyRight = prefs.getInteger("keyRight", Keys.D);
        fullscreen = prefs.getBoolean("fullscreen", true);
    }
    
    /**
     * Synchronise l'état du fullscreen avec l'état réel de la fenêtre.
     */
    private void syncFullscreenState() {
        boolean actualFullscreen = Gdx.graphics.isFullscreen();
        if (fullscreen != actualFullscreen) {
            fullscreen = actualFullscreen;
            savePreferences();
        }
    }
    
    /**
     * Sauvegarde les préférences actuelles.
     */
    public void savePreferences() {
        prefs.putInteger("keyUp", keyUp);
        prefs.putInteger("keyDown", keyDown);
        prefs.putInteger("keyLeft", keyLeft);
        prefs.putInteger("keyRight", keyRight);
        prefs.putBoolean("fullscreen", fullscreen);
        prefs.flush();
    }
    
    /**
     * Réinitialise toutes les options aux valeurs par défaut.
     */
    public void resetToDefault() {
        keyUp = Keys.Z;
        keyDown = Keys.S;
        keyLeft = Keys.Q;
        keyRight = Keys.D;
        fullscreen = true;
        savePreferences();
    }
    
    public int getKeyUp() { 
        return keyUp;
    }

    public int getKeyDown() {
        return keyDown;
    }

    public int getKeyLeft() {
        return keyLeft;
    }

    public int getKeyRight() { 
        return keyRight;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }
    
    public void setKeyUp(int key) { 
        keyUp = key;
        savePreferences();
    }
    
    public void setKeyDown(int key) { 
        keyDown = key;
        savePreferences();
    }
    
    public void setKeyLeft(int key) { 
        keyLeft = key;
        savePreferences();
    }
    
    public void setKeyRight(int key) { 
        keyRight = key;
        savePreferences();
    }
    
    public void setFullscreen(boolean value) {
        fullscreen = value;
        savePreferences();
    }
    
    /**
     * Convertit un code de touche en chaîne de caractères lisible.
     * @param keyCode Le code de la touche
     * @return Le nom de la touche
     */
    public static String getKeyName(int keyCode) {
        return Keys.toString(keyCode);
    }
}