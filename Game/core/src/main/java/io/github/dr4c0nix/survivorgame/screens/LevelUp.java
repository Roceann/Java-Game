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
import io.github.dr4c0nix.survivorgame.weapon.Dagger;
import io.github.dr4c0nix.survivorgame.weapon.FireWand;
import io.github.dr4c0nix.survivorgame.weapon.Sword;

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
        all.add(new UpgradeOption("Weapon Level", 1, 1, UpgradeOption.StatType.INT));
        all.add(new UpgradeOption("Speed", 0.25f, 0.75f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Max Health", 10f, 25f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("HP Regeneration", 1f, 5f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Armor", 1, 3, UpgradeOption.StatType.INT));
        all.add(new UpgradeOption("Critical Chance", 2f, 5f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Critical Damage", 0.2f, 0.4f, UpgradeOption.StatType.FLOAT)); // 0.2 = 20%
        all.add(new UpgradeOption("Difficulty", 0.15f, 0.8f, UpgradeOption.StatType.FLOAT));

        return all;
    }

    /**
     * Sélectionne aléatoirement trois améliorations distinctes à proposer.
     *
     * Chaque option choisie voit sa valeur générée via generateRandomValue(Random).
     * La méthode évite les doublons.
     */
    private void generateRandomUpgrades() {
        Player p = gameplay.getPlayer();
        if (p != null && p.getLevel() == 2) {
            upgradeTotal = new ArrayList<>();
            upgradeTotal.add(new UpgradeOption("Dagger\nShort range, rapid attack", 0, 0, UpgradeOption.StatType.INT));
            upgradeTotal.add(new UpgradeOption("Sword\nMost balanced", 0, 0, UpgradeOption.StatType.INT));
            upgradeTotal.add(new UpgradeOption("FireWand\nHigh damage, long range, slow attack", 0, 0, UpgradeOption.StatType.INT));
            return;
        }

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
            boolean weaponChoiceMode = gameplay.getPlayer() != null && gameplay.getPlayer().getLevel() == 2;
            String text = weaponChoiceMode ? u.getDisplayName() : (u.getDisplayName() + "  +" + u.getFormattedValue());
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

        if (p.getLevel() == 2) {
            switch (u.getDisplayName()) {
                case "Dagger\nShort range, rapid attack":
                    p.setWeapon(new Dagger(gameplay.getEntityFactory()));
                    break;
                case "Sword\nMost balanced":
                    p.setWeapon(new Sword(gameplay.getEntityFactory()));
                    break;
                case "FireWand\nHigh damage, long range, slow attack":
                    p.setWeapon(new FireWand(gameplay.getEntityFactory()));
                    break;
                default:
                    break;
            }
            gameplay.setIsPaused(false);
            return;
        }

        switch (u.getDisplayName()) {
            case "Speed":
                float newSpeed = p.getMovementSpeed() + u.getValue();
                if (newSpeed > 5f) newSpeed = 5f;
                p.setMovementSpeed(newSpeed);
                break;

            case "Max Health":
                float delta = u.getValue();
                p.setMaxHp(p.getMaxHp() + delta);
                p.setCurrentHp(Math.min(p.getHp() + delta, p.getMaxHp()));
                break;

            case "Armor":
                p.setArmor(p.getArmor() + (int) u.getValue());
                break;

            case "HP Regeneration":
                p.setRegenHP(p.getRegenHP() + u.getValue());
                break;

            case "Critical Chance":
                p.setCritChance(p.getCritChance() + u.getValue());
                break;

            case "Critical Damage":
                p.setCritDamage(p.getCritDamage() + u.getValue());
                break;

            case "Weapon Level":
                if (p.getCurrentWeapon() != null) {
                    p.getCurrentWeapon().increaseWeaponLevel();
                }
                break;

            case "Difficulty":
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