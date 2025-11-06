package io.github.dr4c0nix.survivorgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;
    protected Rectangle hitbox;
    protected float movementSpeed;
    protected boolean isAlive = true;
    protected TextureRegion currentFrame;

    public Entity (Vector2 spawnPoint, float hitboxWidth, float hitboxHeight){
        this.position = new Vector2(spawnPoint);
        this.hitbox = new Rectangle(spawnPoint.x, spawnPoint.y, hitboxWidth, hitboxHeight);
    }

  

    public void draw(SpriteBatch batch) {
    if (currentFrame != null) {
        batch.draw(currentFrame, position.x, position.y); // ca c pour redessiner mon entitée chq frame via batch(car sprite)
    }
}

    public abstract void update(float delta);

    public Vector2 getPosition() {
        return this.position;
    }

    public void setPosition(Vector2 value) {
      this.position = value;
    }

    public Rectangle getHitbox() {
      return this.hitbox;
    }

    public float getMovementSpeed() {
      return this.movementSpeed;
    }

    public void setMovementSpeed(float value) {
      this.movementSpeed = value;
    }

    public boolean getIsAlive() {
      return this.isAlive;
    }

    public TextureRegion getCurrentFrame() {
      return this.currentFrame;
    }

    public void setCurrentFrame(TextureRegion value) {
      this.currentFrame = value;
    }
}
