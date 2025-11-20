package io.github.dr4c0nix.survivorgame.weapon;

import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Bow extends Weapon {

    public Bow(EntityFactory factory) {
        super(factory,
            1.5f,
            0.8f,
            10,
            0.15f,
            3.5f,
            1,
            1.0f,
            1,
            24f,
            6f,
            "Weapon/Bow/arrow.png",
            "Un arc romeesque permettant de grequer.",
            "Weapon/bow.png");
    }

    @Override
    public void update(float delta, Player player) {
        cooldownTick(delta);
        if (canShoot()) {
            // entityFactory pour obtenir un projectile et spawn // plus tard
            resetCooldown();
            System.out.println("Bow fired: damage=" + damage);
        }
    }
}
