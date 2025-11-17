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
        all.add(new UpgradeOption("Vitesse", 0.1f, 0.5f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Points de Vie Max", 5, 20, UpgradeOption.StatType.INT));
        all.add(new UpgradeOption("Armure", 1, 5, UpgradeOption.StatType.INT));
        all.add(new UpgradeOption("Force", 0.05f, 0.25f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Projectiles", 1, 1, UpgradeOption.StatType.INT));
        all.add(new UpgradeOption("Chance", 1, 10, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Régénération HP", 0.5f, 2.0f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Vol de Vie", 1, 5, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Chance Critique", 2, 10, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Portée de Ramassage", 5, 15, UpgradeOption.StatType.INT));
        all.add(new UpgradeOption("Dégâts Critiques", 0.1f, 0.5f, UpgradeOption.StatType.FLOAT));
        all.add(new UpgradeOption("Durée Effets", 0.1f, 0.5f, UpgradeOption.StatType.FLOAT));
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
        switch (u.getDisplayName()) {
            case "Vitesse": 
                p.setMovementSpeed(p.getMovementSpeed() + u.getValue()); 
                break;
            case "Points de Vie Max": 
                p.setCUrrentHp(p.getHp() + (int) u.getValue());
                p.setMaxHp(p.getMaxHp() + (int) u.getValue()); 
                
                break;
            case "Armure": 
                p.setArmor(p.getArmor() + (int) u.getValue()); 
                break;
            case "Force": 
                p.setForce(p.getForce() + u.getValue()); 
                break;
            case "Nombre de Projectiles": 
                p.setNbProjectil(p.getNbprojectil() + (int) u.getValue()); 
                break;
            case "Chance": 
                p.setChance(p.getChance() + u.getValue()); 
                break;
            case "Régénération HP": 
                p.setRegenHP(p.getRegenHP() + u.getValue()); 
                break;
            case "Vol de Vie": 
                p.setLifeSteal(p.getLifeSteal() + u.getValue()); 
                break;
            case "Chance de Crit": 
                p.setCritChance(p.getCritChance() + u.getValue()); 
                break;
            case "Portée de Ramassage": 
                p.setPickUpRange(p.getPickUpRange() + (int) u.getValue()); 
                break;
            case "Dégâts Critiques": 
                p.setCritDamage(p.getCritDamage() + u.getValue()); 
                break;
            case "Durée des Effets": 
                p.setDurationEffect(p.getDurationEffect() + u.getValue()); 
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