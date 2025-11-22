package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

/**
 * Overlay d'écran affiché lorsqu'un joueur monte de niveau.
 *
 * Cette classe crée une interface simple avec trois choix d'améliorations générés aléatoirement.
 * Lorsque le joueur sélectionne une option, l'amélioration est appliquée au Player fourni via Gameplay.
 * Le rendu utilise un Stage.
 * 
 * @author Roceann
 * @version 1.0
 */
public class LevelUp {
    private Stage stage;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture rectTex;
    private Texture contourTex;
    private final Gameplay gameplay;
    private List<UpgradeOption> upgradeTotal;
    private final Random random;

    public LevelUp(Gameplay gameplay) {
        this.gameplay = gameplay;
        this.random = new Random();
    }

    /**
     * Crée et affiche le Stage contenant l'UI de level up.
     *
     * Initialise les fonts, textures, génère les upgrades aléatoires,
     * construit l'UI et assigne le Stage comme InputProcessor courant.
     */
    public void show() {
        stage = new Stage(new ScreenViewport());
        gameplay.setIsPaused(true);
        createFonts();
        createParts();
        generateRandomUpgrades();
        buildUI();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Crée et configure les polices utilisées par l'overlay.
     * <p>
     * Font principales : une pour les labels, une pour le titre.
     */
    private void createFonts() {
        font = new BitmapFont();
        font.getData().setScale(1.3f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.3f);
        titleFont.setColor(Color.GOLD);
    }

    /**
     * Crée des textures simples (rectTex et contourTex) utilisées comme
     * arrière-plans des blocs d'UI. Les Pixmaps sont immédiatement disposés
     * après création des Textures.
     */
    private void createParts() {

        Pixmap p1 = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p1.setColor(0.95f, 0.85f, 0.1f, 1f);
        p1.fill();
        rectTex = new Texture(p1);
        p1.dispose();

        Pixmap p2 = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        p2.setColor(0f, 0f, 0f, 0.6f);
        p2.fill();
        contourTex = new Texture(p2);
        p2.dispose();
    }

    /**
     * Retourne la liste de toutes les améliorations possibles.
     *
     * Chaque élément contient un nom affiché, une plage min/max et un type
     * (INT ou FLOAT). Cette liste est utilisée comme base pour sélectionner
     * des options aléatoires à proposer au joueur.
     *
     * @return liste complète des UpgradeOption possibles
     */
    private List<UpgradeOption> allPossibleUpgrades() {
        List<UpgradeOption> all = new ArrayList<>();
        // Weapon level upgrade: +1 level (INT), limited by weapon impl (max 6)
        all.add(new UpgradeOption("Niveau Arme", 1, 1, UpgradeOption.StatType.INT));

        // Player movement speed: spawn = 2, cap = 5 — propose small increments to be balanced
        all.add(new UpgradeOption("Vitesse", 0.15f, 0.5f, UpgradeOption.StatType.FLOAT));

        // Max HP: base 100 — increase between small and medium steps
        all.add(new UpgradeOption("Points de Vie Max", 8f, 22f, UpgradeOption.StatType.FLOAT));

        // HP regen: default 5 per 10s (0.5/s) — increase regeneration amount (float)
        all.add(new UpgradeOption("Régénération HP", 0.3f, 1.2f, UpgradeOption.StatType.FLOAT));

        // Armor: small integer bumps
        all.add(new UpgradeOption("Armure", 1, 3, UpgradeOption.StatType.INT));

        // Crit chance: increments in percentage points (float)
        all.add(new UpgradeOption("Chance Critique", 1f, 5f, UpgradeOption.StatType.FLOAT));

        // Crit damage: multiplier increment (e.g. +0.1 => +10% crit multiplier)
        all.add(new UpgradeOption("Dégâts Critiques", 0.1f, 0.35f, UpgradeOption.StatType.FLOAT));

        // Difficulty: player-controlled difficulty factor (current 1, max 5) - small steps
        all.add(new UpgradeOption("Difficulté", 0.15f, 0.8f, UpgradeOption.StatType.FLOAT));

        return all;
    }

    /**
     * Sélectionne aléatoirement trois améliorations distinctes à proposer.
     *
     * Chaque option choisie voit sa valeur générée via generateRandomValue(Random).
     * La méthode évite les doublons.
     */
    private void generateRandomUpgrades() {
        List<UpgradeOption> all = allPossibleUpgrades();
        upgradeTotal = new ArrayList<>();
        int choisi = 0;
        while (choisi < 3) {
            int index = random.nextInt(all.size());
            if (upgradeTotal.contains(all.get(index))) {
                continue;
            }
            upgradeTotal.add(all.get(index));
            upgradeTotal.get(choisi).generateRandomValue(random);
            choisi++;
        }
    }

    /**
     * Construit l'interface graphique du LevelUp dans un Table et l'ajoute
     * au Stage. Pour chaque option disponible, crée un bouton/box cliquable
     * qui applique l'amélioration au Player et ferme l'overlay.
     */
    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Table visuelle = new Table();
        visuelle.setBackground(new TextureRegionDrawable(new TextureRegion(contourTex)));
        visuelle.pad(18);
        visuelle.center();

        Label title = new Label("LEVEL UP", new Label.LabelStyle(titleFont, Color.GOLD));
        title.setAlignment(Align.center);
        visuelle.add(title).colspan(3).padBottom(12).row();
        float screenW = Gdx.graphics.getWidth();
        float blockW = screenW / 6f;
        float blockH = 100f;

        for (UpgradeOption u : upgradeTotal) {
            String text = u.getDisplayName() + "  +" + u.getFormattedValue();
            Label label = new Label(text, new Label.LabelStyle(font, Color.BLACK));
            label.setAlignment(Align.center);

            Table box = new Table();
            box.setBackground(new TextureRegionDrawable(new TextureRegion(rectTex)));
            box.pad(6);
            box.add(label).width(blockW).height(blockH).center();

            box.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    applyUpgradeToPlayer(u);
                    hide();
                    gameplay.onLevelUpOverlayClosed();
                }
            });

            visuelle.add(box).pad(8);
        }

        root.add(visuelle).center();
        stage.addActor(root);
    }

    /**
     * Retourne le Stage utilisé par cet overlay.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Applique l'UpgradeOption fournie au Player courant.
     *
     * Effectue le mapping entre le nom affiché de l'upgrade et les setters
     * correspondants sur Player. Les conversions de types (int/float) sont
     * réalisées selon les besoins.
     */
    private void applyUpgradeToPlayer(UpgradeOption u) {
        Player p = gameplay.getPlayer();
        if (p == null) return;

        switch (u.getDisplayName()) {
            case "Vitesse":
                float newSpeed = p.getMovementSpeed() + u.getValue();
                if (newSpeed > 5f) newSpeed = 5f; // clamp to max 5
                p.setMovementSpeed(newSpeed);
                break;

            case "Points de Vie Max":
                float delta = u.getValue();
                p.setMaxHp(p.getMaxHp() + delta);
                p.setCurrentHp(Math.min(p.getHp() + delta, p.getMaxHp()));
                break;

            case "Armure":
                p.setArmor(p.getArmor() + (int) u.getValue());
                break;

            case "Régénération HP":
                p.setRegenHP(p.getRegenHP() + u.getValue());
                break;

            case "Chance Critique":
                p.setCritChance(p.getCritChance() + u.getValue());
                break;

            case "Dégâts Critiques":
                p.setCritDamage(p.getCritDamage() + u.getValue());
                break;

            case "Niveau Arme":
                if (p.getCurrentWeapon() != null) {
                    p.getCurrentWeapon().increaseWeaponLevel(); // Weapon caps at max internally
                }
                break;

            case "Difficulté":
                float newDiff = p.getDifficulter() + u.getValue();
                if (newDiff > 5f) newDiff = 5f;
                p.setDifficulter(newDiff);
                break;

            default: break;
        }
        gameplay.setIsPaused(false);
    }

    /**
     * Ferme l'overlay et libère ses ressources internes (Stage, fonts, textures).
     *
     * Après appel, getStage() retournera null.
     */
    public void hide() {
        try {
            if (Gdx.input.getInputProcessor() == stage) {
                Gdx.input.setInputProcessor(null);
            }
        } catch (Exception ignored) {}

        if (stage != null) {
            try {
                stage.clear();
                stage.dispose();
            } catch (IllegalArgumentException | IllegalStateException e) {
            } finally {
                stage = null;
            }
        }

        if (rectTex != null) {
            rectTex.dispose();
            rectTex = null;
        }
        if (contourTex != null) {
            contourTex.dispose();
            contourTex = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }

        try {
            if (gameplay != null) gameplay.setIsPaused(false);
        } catch (Exception ignored) {}
    }

    /**
     * Alias de hide() pour la gestion explicite des ressources.
     * Conserve le comportement identique.
     */
    public void dispose() {
        hide();
    }
}