package braid.main.objects;

import braid.main.Braid;
import braid.main.Rewind;
import braid.main.screens.TestScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

/***********
 Diese Klasse implementiert den Spieler und soll sich  um alle Variablen und interaktiven
 Elemente davon kümmern.
 ***********/

public class Player extends GameObject {
    // Enumeration für die verschiedenen Zustände, in der sich ein Spieler befinden kann.
    public enum State {
        GROUNDED,
        JUMPING
    }

    // Player specific variables
    int velocityY;

    State currentState = State.GROUNDED;
    float jumpVelocity = 20f;

    private TextureRegion stand;


    // Constructors
    public Player(World world, TestScreen screen) {
        super(world);

        speed = 5f;

        defineBody();

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        stand = new TextureRegion(sprite.getTexture(), 2, 2, 24, 24);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(stand);
    }

    public Player(Texture texture) {
        super(texture);
        speed = 5;
    }

    // Methods
    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Braid.PPM, 32 / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(11/ Braid.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }

    public void update(float dt) {
        currentState = getCurrentState();
        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
    }


    public void jump() {
        currentState = State.JUMPING;
        //velocityY = jumpVelocity;
    }

    // temporäre Methode, soll später mit Kollisionen automatisch erfolgen
    public void land() {
        currentState = State.GROUNDED;
    }

    public State getCurrentState() {
        if(b2body.getLinearVelocity().y != 0)
            return State.JUMPING;
        else
            return State.GROUNDED;
    }

    //Getter und Setter
    public boolean isJumping() {
        return currentState == State.JUMPING;
    }

    public final float getJumpVelocity () {
        return jumpVelocity;
    }

    public int getVelocityY() {
        return velocityY;
    }
    public void setVelocityY(int value) {
        velocityY = value;
    }
}
