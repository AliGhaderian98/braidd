package braid.main.objects;

import braid.main.Rewind;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

enum State {
    GROUNDED,
    JUMPING
}

public class Player extends GameObject {
    // Player variables
    final int enemySpeed = 3;
    int velocityY;

    //boolean isJumping = false;
    State currentState = State.GROUNDED;
    int jumpVelocity = 20;


    // Constructors
    public Player() {
        speed = 5;
    }

    public Player(Texture texture) {
        super(texture);
        speed = 5;
    }

    // Methods
    public void jump() {
        currentState = State.JUMPING;
        velocityY = jumpVelocity;
    }

    // temporäre Methode, soll später mit Kollisionen automatisch erfolgen
    public void land() {
        currentState = State.GROUNDED;
    }

    //Getter und Setter
    public boolean isJumping() {
        return currentState == State.JUMPING;
    }

    public final int getJumpVelocity () {
        return jumpVelocity;
    }

    public int getVelocityY() {
        return velocityY;
    }
    public void setVelocityY(int value) {
        velocityY = value;
    }
}
