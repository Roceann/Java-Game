package io.github.dr4c0nix.survivorgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

/**
 * Overlay d'interface affiché lors d'une montée de niveau (Level Up).
 *
 * Cet overlay :
 * - gèle le gameplay,
 * - génère un ensemble d'options d'amélioration (ou choix d'arme),
 * - affiche trois options cliquables et applique l'amélioration sélectionnée au Player,
 * - gère ses propres ressources graphiques (background, fonts, textures, Stage).
 *
 * Le LevelUp est conçu pour être utilisé par Gameplay : show() crée et affiche
 * l'overlay, hide()/dispose() nettoient les ressources et remettent le jeu en route.
 */
public class LevelUp {
    private Stage stage;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture rectTex;
    private Texture contourTex;
    private Texture backgroundTexture;
    private final Gameplay gameplay;
    private List<UpgradeOption> upgradeTotal;
    private final Random random;
    
    private float scaleX = 1f;
    private float scaleY = 1f;

    /**
     * Crée un overlay de LevelUp lié à l'instance Gameplay fournie.
     *
     * @param gameplay instance Gameplay qui déclenche / reçoit l'amélioration
     */
    public LevelUp(Gameplay gameplay) {
        this.gameplay = gameplay;
        this.random = new Random();
        updateScale();
    }

    /**
     * Met à jour les facteurs d'échelle (scaleX/scaleY) à partir de la résolution.
     *
     * Appel interne lors de construction et redimensionnement pour adapter l'UI.
     */
    private void updateScale() {
        this.scaleX = (Gdx.graphics.getWidth() / 1920f) * 1.2f;
        this.scaleY = (Gdx.graphics.getHeight() / 1080f) * 1.2f;
    }

    /**
     * Crée et affiche le Stage contenant l'UI de level up.
     *
     * Actions réalisées :
     * - gèle le jeu (gameplay.setIsPaused(true))
     * - initialise background, polices et textures
     * - génère 3 options aléatoires (ou choix d'arme si le joueur est au niveau 2)
     * - construit l'UI et active le Stage comme InputProcessor
     */
    public void show() {
        stage = new Stage(new ScreenViewport());
        try {
            backgroundTexture = new Texture(Gdx.files.internal("Background/levelupbg.jpg"));
            Image background = new Image(backgroundTexture);
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception ignored) {}
        gameplay.setIsPaused(true);
        createFonts();
        createParts();
        generateRandomUpgrades();
        buildUI();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Crée et configure les BitmapFont utilisés par l'overlay.
     *
     * Les tailles sont adaptées en fonction des facteurs d'échelle pour conserver
     * une apparence cohérente sur différentes résolutions.
     */
    private void createFonts() {
        font = new BitmapFont();
        font.getData().setScale(1.3f * Math.min(scaleX, scaleY));
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.3f * Math.min(scaleX, scaleY));
        titleFont.setColor(Color.WHITE);
    }

    /**
     * Crée des textures simples (Pixmap -> Texture) pour les blocs visuels.
     *
     * Les Pixmaps sont immédiatement disposés après la création des Texture.
     */
    private void createParts() {
        Pixmap p1 = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        p1.setColor(0.35f, 0.35f, 0.35f, 1f);
        p1.fill();
        rectTex = new Texture(p1);
        p1.dispose();

        Pixmap p2 = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
        p2.setColor(0.18f, 0.18f, 0.18f, 1f);
        p2.fill();
        contourTex = new Texture(p2);
        p2.dispose();
    }

    /**
     * Retourne la liste complète des améliorations potentielles.
     *
     * Cette liste sert de réservoir pour sélectionner 3 options aléatoires
     * proposées au joueur lors d'un level up normal.
     *
     * @return liste de toutes les UpgradeOption disponibles
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
     * Comportement spécial : si le joueur est exactement au niveau 2,
     * on propose un choix d'armes (Dagger, Sword, FireWand) au lieu d'upgrades numériques.
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
     * Construit l'interface graphique affichant les trois options et rattache
     * les ClickListener qui appliqueront l'amélioration sélectionnée.
     *
     * Chaque option clique :
     * - applique l'amélioration sur le Player
     * - ferme l'overlay (hide())
     * - notifie Gameplay via onLevelUpOverlayClosed()
     */
    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Table visuelle = new Table();
        visuelle.setBackground(new TextureRegionDrawable(new TextureRegion(contourTex)));
        visuelle.pad(18 * Math.min(scaleX, scaleY));
        visuelle.center();

        Label title = new Label("LEVEL UP", new Label.LabelStyle(titleFont, Color.WHITE));
        title.setAlignment(Align.center);
        visuelle.add(title).colspan(3).padBottom(12 * Math.min(scaleX, scaleY)).row();

        float screenW = Gdx.graphics.getWidth();
        float blockW = (screenW / 6f) * Math.min(scaleX, scaleY);
        float blockH = 100f * Math.min(scaleX, scaleY);

        for (UpgradeOption u : upgradeTotal) {
            boolean weaponChoiceMode = gameplay.getPlayer() != null && gameplay.getPlayer().getLevel() == 2;
            String text = weaponChoiceMode ? u.getDisplayName() : (u.getDisplayName() + "  +" + u.getFormattedValue());
            Label label = new Label(text, new Label.LabelStyle(font, Color.WHITE));
            label.setAlignment(Align.center);
            label.setWrap(true);

            Table box = new Table();
            box.setBackground(new TextureRegionDrawable(new TextureRegion(rectTex)));
            box.pad(6 * Math.min(scaleX, scaleY));
            box.add(label).width(blockW).height(blockH).center();

            box.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    applyUpgradeToPlayer(u);
                    hide();
                    gameplay.onLevelUpOverlayClosed();
                }
            });

            visuelle.add(box).pad(8 * Math.min(scaleX, scaleY));
        }

        root.add(visuelle).center();
        stage.addActor(root);
    }

    /**
     * Retourne le Stage utilisé par cet overlay.
     *
     * Retour utile pour que Gameplay puisse intégrer le stage dans la boucle
     * de rendu/act si nécessaire.
     *
     * @return Stage actif (ou null si fermé)
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Applique l'UpgradeOption sélectionnée au Player courant.
     *
     * Pour les choix d'arme (niveau 2) remplace l'arme du joueur par la nouvelle.
     * Pour les upgrades numériques, effectue le mapping vers les setters du Player.
     *
     * @param u option sélectionnée
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
     * Ferme l'overlay et libère ses ressources internes (Stage, polices, textures).
     *
     * Après appel, getStage() retournera null et le jeu reprendra si possible.
     */
    public void hide() {
        try {
            if (Gdx.input.getInputProcessor() == stage) {
                Gdx.input.setInputProcessor(null);
            }
        } catch (Exception ignored) {}
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
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
     *
     * Conserve le comportement identique (fermeture + cleanup).
     */
    public void dispose() {
        hide();
    }
}