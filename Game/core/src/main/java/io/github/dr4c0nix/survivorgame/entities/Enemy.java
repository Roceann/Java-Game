package io.github.dr4c0nix.survivorgame.entities;

import java.util.Vector;

import com.badlogic.gdx.math.Vector2;

public abstract class Enemy extends LivingEntity {

    protected Player target;
    protected float xpDrop;


 public Enemy(Vector2 spawnPoint, float hitboxWidth, float hitboxHeight, Player target, float xpDrop, int hp, int armor, float force) {
        super(spawnPoint, hitboxWidth, hitboxHeight, hp, armor, force);
        this.target = target;
        this.xpDrop = xpDrop;
    }

    public void update (float delta){
        if (!target.isAlive) return;
        if (target == null ) return;

        // L'objectif ici : Aller récuperer la position de l'enemy OK, la comparer avec la position du joueur si inf sup ou egale NOK ensuite on fait en sorte d'incrémenter celle de l'enemy pour rendre les 2 positions égales NOK

        float playerX = target.position.x;
        float playerY = target.position.y;
        float enemyX = this.position.x;
        float enemyY = this.position.y;
        
        if (playerX - enemyX <0){
            this.position.x -= 1;
        } else {  this.position.x += 1f ;}
        

    };

    public void reset(){


    };

}