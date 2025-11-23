package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class FireWand extends Weapon {

    public FireWand(EntityFactory factory) {
        super(factory,
            250,
            1.4f,
            250,
            150f,
            1.5f,
            17f,
            32f,
            "Weapon/FireWand/firewand-effect.png",
            "Lancez des boules de feu dévastatrices !",
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
