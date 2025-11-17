package io.github.dr4c0nix.survivorgame.weapon;

import io.github.dr4c0nix.survivorgame.entities.EntityFactory;
import io.github.dr4c0nix.survivorgame.entities.player.Player;

public class Bow extends Weapon {

    public Bow(EntityFactory factory) {
        super(factory);
        this.damage = 1.5f;                         
        this.cadence = 0.8f;                       
        this.range = 10;                             
        this.critChance = 0.15f;                    
        this.critDamage = 2.2f;                     
        this.projectileSpeed = 3.5f;                
        this.projectileCount = 1.0f;                
        this.duration = 1.0f;                       
        this.pierce = 1;      
        //this.EffectType = EffectType.NONE;;                      
        this.description = "Un arc romeesque permettant de grequer.";
        this.iconPath = "weapon/bow.png";           
    }

    @Override
    public void update(float delta, Player player) {
        System.out.println("TEST");
    }
}
