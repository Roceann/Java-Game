package io.github.dr4c0nix.survivorgame.weapons;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public abstract class Weapon {

    protected EntityFactory factory;

    protected int level = 1;
    protected float shotDelay = 0.0f;

    protected float damage;
    protected float cadence;
    protected float critChance;
    protected float projectileSpeed;
    protected float projectileCount;
    protected int range;
    protected float critDamage;
    protected float duration;
    protected int pierce;
    protected String description;
    protected EffectType effectType = EffectType.NONE;

    protected TextureRegion imageArme; // Pour faire apparaitre l'icone d'arme dans l e HUD j'ai add ca

    public Weapon(EntityFactory factory, TextureRegion imageArme) {
        this.factory = factory;
        this.imageArme = imageArme;
    }

    public abstract void update(float delta, Player player);

    public void levelUp() {
        level += 1;
    }

    public TextureRegion getIcon() {
        return imageArme;
    }
}
