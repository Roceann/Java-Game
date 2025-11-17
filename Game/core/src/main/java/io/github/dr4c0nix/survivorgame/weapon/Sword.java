package io.github.dr4c0nix.survivorgame.weapons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Sword extends Weapon {

    public Sword(EntityFactory factory, TextureRegion icon) {
        super(factory, icon);
        this.damage = 1.0f;
        this.cadence = 1.0f;
        this.range = 1;
        this.critChance = 0.1f;
        this.critDamage = 1.0f;
        this.description = "Equipez vous d'une épée pour tabasser les triple monstres !";
    }


    @Override
    public void update(float delta, Player player) {}

}
