package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.TestScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/***********
 Diese Klasse implementiert den Spieler und soll sich, um alle Variablen und interaktiven
 Elemente davon kümmern.
 ***********/

public class Player extends GameObject {
    // Enumeration für die verschiedenen Zustände, in der sich ein Spieler befinden kann.
    public enum State {
        IDLE,
        JUMPING,
        RUNNING
    }

    // Animation variables
    public Animation LionIdle;
    public Animation LionRunning;
    public Animation LionJumping;
    private boolean running_right;
    private float stateTimer;

    // Player specific variables
    int velocityY;
    float jumpSpeed = 3.5f;
    State currentState;
    State previousState;

    private TextureRegion stand;


    // Constructors
    public Player(World world, TestScreen screen) {
        super(world);

        speed = 1f;
        stateTimer = 0;
        currentState = State.IDLE;
        previousState = State.IDLE;
        running_right = true;

        defineBody();

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        //stand = new TextureRegion(sprite.getTexture(), 2, 2, 24, 24);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        //sprite.setRegion(stand);


        // Animation loops
        LionIdle = new Animation<TextureRegion>(0.2f, screen.getAtlas().findRegions("lion-idle"), Animation.PlayMode.LOOP_PINGPONG);
        LionRunning = new Animation<TextureRegion>(0.1f, screen.getAtlas().findRegions("lion-run"), Animation.PlayMode.LOOP);
        LionJumping = new Animation<TextureRegion>(0.1f, screen.getAtlas().findRegions("lion-jump"), Animation.PlayMode.NORMAL);

    }

    // Methods
    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Braid.PPM, 32 / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef bodyFdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(9/ Braid.PPM);

        bodyFdef.shape = shape;
        bodyFdef.friction = 0f;
        b2body.createFixture(bodyFdef);

        FixtureDef footFdef = new FixtureDef();
        PolygonShape feet = new PolygonShape();
        feet.setAsBox(6 / Braid.PPM, 2 / Braid.PPM, new Vector2(0,-8 / Braid.PPM), 0);
        footFdef.shape = feet;
        footFdef.friction = 1f;
        b2body.createFixture(footFdef);
    }

    public void update(float dt) {
        currentState = getCurrentState();
        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
        sprite.setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getCurrentState();

        // regions for the different States
        TextureRegion region;
        switch (currentState){
            case RUNNING:
                region = (TextureRegion) LionRunning.getKeyFrame(stateTimer,true);
                break;
            case JUMPING:
                region = (TextureRegion) LionJumping.getKeyFrame(stateTimer);
                break;
            case IDLE:
            default:
                region = (TextureRegion) LionIdle.getKeyFrame(stateTimer,true);
                break;
        }

        //checking if the model has to be flipped
        if((b2body.getLinearVelocity().x < 0 || !running_right) && !region.isFlipX()){
            region.flip(true,false);
            running_right = false;
        } else if ((b2body.getLinearVelocity().x > 0 || running_right) && region.isFlipX()) {
            region.flip(true,false);
            running_right = true;

        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public void jump() {
        currentState = State.JUMPING;
        //velocityY = jumpVelocity;
    }

    // temporäre Methode, soll später mit Kollisionen automatisch erfolgen
    public void land() {
        currentState = State.IDLE;
    }

    public State getCurrentState() {
        if(b2body.getLinearVelocity().y > 0)
            return State.JUMPING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.IDLE;
    }

    //Getter und Setter
    public boolean isJumping() {
        return currentState == State.JUMPING;
    }

    public final float getJumpSpeed() {
        return jumpSpeed;
    }

    public int getVelocityY() {
        return velocityY;
    }
    public void setVelocityY(int value) {
        velocityY = value;
    }

}
