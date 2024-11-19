package braid.main.objects;

import braid.main.Braid;
import braid.main.Rewind;
import braid.main.screens.TestScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

enum State {
    GROUNDED,
    JUMPING
}

public class Player extends GameObject {
    // Player variables
    int velocityY;

    //boolean isJumping = false;
    State currentState = State.GROUNDED;
    int jumpVelocity = 20;

    private TextureRegion stand;


    // Constructors
    public Player(World world, TestScreen screen) {
        super(world);

        speed = 5;

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        stand = new TextureRegion(sprite.getTexture(), 0, 0, 24, 24);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(stand);
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
