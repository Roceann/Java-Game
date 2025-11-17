package io.github.dr4c0nix.survivorgame.weapon;

import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Sword extends Weapon {

    public Sword(EntityFactory factory) {
        super(factory);
        this.damage = 1.0f;
        this.cadence = 1.0f;
        this.range = 1;                        
        this.critChance = 0.1f;                                                                    
        this.critDamage = 2.0f;                 
        this.projectileSpeed = 1.0f;
        this.projectileCount = 1.0f;
        this.duration = 0.5f;
        this.pierce = 1;
        //this.EffectType = EffectType.NONE;; 
        this.description = "Equipez vous d'une épée pour tabasser les triple monstres !";
        this.iconPath = "weapon/sword.png";
    }


    @Override
    public void update(float delta, Player player) {}

}
