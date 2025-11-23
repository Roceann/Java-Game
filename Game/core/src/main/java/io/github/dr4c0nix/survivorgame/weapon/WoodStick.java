package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class WoodStick extends Weapon {

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
            "Weapon/Sword/Sword.png");
    }

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
