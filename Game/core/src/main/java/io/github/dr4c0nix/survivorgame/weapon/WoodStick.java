package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

/**
 * WoodStick : bâton basique pour débuter, cadence correcte.
 */
public class WoodStick extends Weapon {

    /**
     * Constructeur initialisant le WoodStick.
     *
     * @param factory factory pour création des projectiles
     */
    public WoodStick(EntityFactory factory) {
        super(factory,
            75,
            0.5f,
            50,
            100f,
            1.5f,
            19f,
            12f,
            "Weapon/WoodStick/woodstick-effect.png",
            "Un bâton en bois simple pour commencer votre aventure !",
            "Weapon/WoodStick/WoodStick.png");
    }

    /**
     * Mise à jour appelée chaque frame : effectue le tir si possible.
     *
     * @param delta temps écoulé depuis la dernière frame (secondes)
     * @param player joueur qui utilise l'arme (position et direction utilisés)
     */
    @Override
    public void update(float delta, Player player) {
        cooldownTick(delta);
        if (canShoot()) {
            Vector2 playerCenter = new Vector2();
            player.getHitbox().getCenter(playerCenter);

            Vector2 facing = player.getFacingDirection();
            if (facing.isZero()) {
                facing = new Vector2(0, -1);
            }

            float offsetX = facing.x * (player.getHitbox().width * 0.5f);
            float offsetY = facing.y * (player.getHitbox().height * 0.5f);
            Vector2 spawnCenter = playerCenter.cpy().add(offsetX, offsetY);

            entityFactory.obtainProjectile(
                spawnCenter,
                facing.cpy().nor(),
                getProjectileSpeed(),
                (float) getRange(),
                (int) (getEffectiveDamage() * player.getForce()),
                getEffectiveProjectileSize(),
                getProjectileBaseWidth(),
                getProjectileBaseHeight(),
                getProjectileTexturePath(),
                player
            );

            resetCooldown();
        }
    }

}
