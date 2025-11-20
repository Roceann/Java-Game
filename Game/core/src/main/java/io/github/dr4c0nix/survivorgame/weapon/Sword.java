package io.github.dr4c0nix.survivorgame.weapon;

import com.badlogic.gdx.math.Vector2;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Sword extends Weapon {

    public Sword(EntityFactory factory) {
        super(factory,
            1.0f,
            0.5f,
            150,
            0.10f,
            100f,
            1,
            2f,
            1,
            32f,
            8f,
            "Weapon/Sword/sword-effect.png",
            "Equipez vous d'une épée pour tabasser les triple monstres !",
            "Weapon/Sword/sword.png");
    }

    @Override
    public void update(float delta, Player player) {
        cooldownTick(delta);
        if (!canShoot()) {
            return;
        }

        Vector2 facing = player.getFacingDirection();
        if (facing.isZero(0.0001f)) {
            facing.set(0f, -1f);
        }

        Vector2 playerCenter = new Vector2(player.getHitbox().x + player.getHitbox().width * 0.5f, player.getHitbox().y + player.getHitbox().height * 0.5f);

        float offsetX = facing.x * (player.getHitbox().width * 0.5f);
        float offsetY = facing.y * (player.getHitbox().height * 0.5f);
        Vector2 spawnCenter = playerCenter.cpy().add(offsetX, offsetY);

        entityFactory.obtainSwordProjectile(spawnCenter, facing, projectileSpeed, range, damage * player.getForce(), projectileSize, projectileBaseWidth, projectileBaseHeight, projectileTexturePath, player);
        resetCooldown();
    }
}
