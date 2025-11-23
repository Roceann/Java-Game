package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

/**
 * Sword : arme polyvalente, cadence moyenne et dégats modérés.
 */
public class Sword extends Weapon {

    /**
     * Constructeur de l'épée avec ses paramètres par défaut.
     *
     * @param factory factory pour création des projectiles
     */
    public Sword(EntityFactory factory) {
        super(factory,
            100,
            0.75f,
            100,
            100f,
            2f,
            32f,
            8f,
            "Weapon/Sword/sword-effect.png",
            "Equipez vous d'une épée pour tabasser les triple monstres !",
            "Weapon/Sword/sword.png");
    }

    /**
     * Mise à jour appelée chaque frame : gère le cooldown et crée un projectile si possible.
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
