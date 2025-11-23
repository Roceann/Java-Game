package io.github.dr4c0nix.survivorgame;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Classe de gestion des options du jeu.
 * <p>
 * Utilise Preferences pour persister les options (touches, fullscreen, volumes...).
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
    private int musicVolume;
    private int gameDuration;
    
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
     *
     * @return instance singleton de GameOptions
     */
    public static GameOptions getInstance() {
        if (instance == null) {
            instance = new GameOptions();
        }
        return instance;
    }
    
    /**
     * Charge les préférences depuis le stockage persistant.
     * Les valeurs par défaut sont utilisées si aucune préférence n'existe.
     */
    private void loadPreferences() {
        keyUp = prefs.getInteger("keyUp", Keys.Z);
        keyDown = prefs.getInteger("keyDown", Keys.S);
        keyLeft = prefs.getInteger("keyLeft", Keys.Q);
        keyRight = prefs.getInteger("keyRight", Keys.D);
        fullscreen = prefs.getBoolean("fullscreen", true);
        musicVolume = prefs.getInteger("musicVolume", 100);
        gameDuration = prefs.getInteger("gameDuration", 5);
    }
    
    /**
     * Synchronise l'état du paramètre fullscreen avec l'état réel de la fenêtre.
     * Si une différence est détectée, la préférence est mise à jour.
     */
    private void syncFullscreenState() {
        boolean actualFullscreen = Gdx.graphics.isFullscreen();
        if (fullscreen != actualFullscreen) {
            fullscreen = actualFullscreen;
            savePreferences();
        }
    }
    
    /**
     * Sauvegarde les préférences actuelles dans le stockage persistant.
     */
    public void savePreferences() {
        prefs.putInteger("keyUp", keyUp);
        prefs.putInteger("keyDown", keyDown);
        prefs.putInteger("keyLeft", keyLeft);
        prefs.putInteger("keyRight", keyRight);
        prefs.putBoolean("fullscreen", fullscreen);
        prefs.putInteger("musicVolume", musicVolume);
        prefs.putInteger("gameDuration", gameDuration);
        prefs.flush();
    }
    
    /**
     * Réinitialise toutes les options aux valeurs par défaut et les sauvegarde.
     */
    public void resetToDefault() {
        keyUp = Keys.Z;
        keyDown = Keys.S;
        keyLeft = Keys.Q;
        keyRight = Keys.D;
        fullscreen = true;
        musicVolume = 100;
        gameDuration = 5;
        savePreferences();
    }
    
    /**
     * Retourne la touche configurée pour monter (up).
     *
     * @return code de la touche (Keys.*)
     */
    public int getKeyUp() { 
        return keyUp;
    }

    /**
     * Retourne la touche configurée pour descendre (down).
     *
     * @return code de la touche (Keys.*)
     */
    public int getKeyDown() {
        return keyDown;
    }

    /**
     * Retourne la touche configurée pour aller à gauche.
     *
     * @return code de la touche (Keys.*)
     */
    public int getKeyLeft() {
        return keyLeft;
    }

    /**
     * Retourne la touche configurée pour aller à droite.
     *
     * @return code de la touche (Keys.*)
     */
    public int getKeyRight() { 
        return keyRight;
    }

    /**
     * Indique si le jeu est configuré en plein écran.
     *
     * @return true si fullscreen activé
     */
    public boolean isFullscreen() {
        return fullscreen;
    }
    
    /**
     * Définit la touche pour monter et sauvegarde la préférence.
     *
     * @param key code de la touche (Keys.*)
     */
    public void setKeyUp(int key) { 
        keyUp = key;
        savePreferences();
    }
    
    /**
     * Définit la touche pour descendre et sauvegarde la préférence.
     *
     * @param key code de la touche (Keys.*)
     */
    public void setKeyDown(int key) { 
        keyDown = key;
        savePreferences();
    }
    
    /**
     * Définit la touche pour aller à gauche et sauvegarde la préférence.
     *
     * @param key code de la touche (Keys.*)
     */
    public void setKeyLeft(int key) { 
        keyLeft = key;
        savePreferences();
    }
    
    /**
     * Définit la touche pour aller à droite et sauvegarde la préférence.
     *
     * @param key code de la touche (Keys.*)
     */
    public void setKeyRight(int key) { 
        keyRight = key;
        savePreferences();
    }
    
    /**
     * Active ou désactive le mode plein écran et sauvegarde.
     *
     * @param value true pour plein écran, false sinon
     */
    public void setFullscreen(boolean value) {
        fullscreen = value;
        savePreferences();
    }
    
    /**
     * Retourne le volume de la musique (0..100).
     *
     * @return volume musique
     */
    public int getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * Définit le volume de la musique (clamp 0..100) et sauvegarde.
     *
     * @param volume valeur souhaitée (0..100)
     */
    public void setMusicVolume(int volume) {
        musicVolume = Math.max(0, Math.min(100, volume));
        savePreferences();
    }
    
    /**
     * Retourne la durée de partie configurée (minutes).
     *
     * @return durée de jeu
     */
    public int getGameDuration() {
        return gameDuration;
    }
    
    /**
     * Définit la durée de partie (clamp 1..60 minutes) et sauvegarde.
     *
     * @param duration durée souhaitée en minutes
     */
    public void setGameDuration(int duration) {
        gameDuration = Math.max(1, Math.min(60, duration));
        savePreferences();
    }
    
    /**
     * Convertit un code de touche en chaîne lisible (ex: Keys.Z -> "Z").
     *
     * @param keyCode code de la touche
     * @return nom lisible de la touche
     */
    public static String getKeyName(int keyCode) {
        return Keys.toString(keyCode);
    }
}