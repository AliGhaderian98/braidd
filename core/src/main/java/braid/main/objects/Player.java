package braid.main.objects;

import braid.main.Braid;
import braid.main.*;
import braid.main.screens.TestScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
//todo: Spring Animation fixen, stateTimer fixen/übersichtlicher machen, siehe print Ausgaben
/***********
 Diese Klasse implementiert den Spieler und soll sich, um alle Variablen und interaktiven
 Elemente davon kümmern.
 ***********/

public class Player extends GameObject {
    // Enumeration für die verschiedenen Animations-Zustände, in der sich ein Spieler befinden kann.
    public enum AnimationState {
        IDLE,
        JUMPING,
        RUNNING,
        CLIMBING
    }

    // Animation variables, Ab sofort von Typ RewindableAnimation
    private final Animation<TextureRegion> LionIdle, LionRunning, LionJumping;
    private boolean running_right;
    public float stateTimer;
    private TestScreen screen;

    // Player specific variables
    int velocityY;
    float jumpSpeed = 3.5f;
    final float climbingSpeed = 1f;
    AnimationState currentState;
    AnimationState previousState;

    private boolean isAtLadder;


    private TextureRegion stand;


    // Constructors
    public Player(World world, TestScreen screen) {
        super(world);
        this.screen = screen;

        speed = 1f;
        currentState = AnimationState.IDLE;
        previousState = AnimationState.IDLE;
        running_right = true;

        defineBody();

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);

        // Animation loops
        LionIdle = new Animation<>(0.2f, screen.getAtlas().findRegions("lion-idle"), Animation.PlayMode.LOOP);
        LionRunning = new Animation<>(0.1f, screen.getAtlas().findRegions("lion-run"), Animation.PlayMode.LOOP);
        LionJumping = new Animation<>(0.1f, screen.getAtlas().findRegions("lion-jump"), Animation.PlayMode.NORMAL);
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
        shape.setRadius(9 / Braid.PPM);

        bodyFdef.shape = shape;
        bodyFdef.friction = 0f;
        Fixture bodyFixture = b2body.createFixture(bodyFdef);
        bodyFixture.setUserData("PlayerBody");

        FixtureDef footFdef = new FixtureDef();
        PolygonShape feet = new PolygonShape();
        feet.setAsBox(6 / Braid.PPM, 2 / Braid.PPM, new Vector2(0, -8 / Braid.PPM), 0);
        footFdef.shape = feet;
        footFdef.friction = 1f;
        Fixture feetFixture = b2body.createFixture(footFdef);
        feetFixture.setUserData("PlayerFeet");

    }

    public void update(float dt) {
        sprite.setRegion(getFrame(dt));
        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
    }

    public TextureRegion getFrame(float dt){
        // regions for the different States
        currentState = getCurrentState();
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        TextureRegion region = switch (currentState) {
            case RUNNING -> LionRunning.getKeyFrame(stateTimer, true);
            case JUMPING -> LionJumping.getKeyFrame(stateTimer, true);
            default -> LionIdle.getKeyFrame(stateTimer, true);
        };

        //checking if the model has to be flipped
        if((b2body.getLinearVelocity().x < 0 || !running_right) && !region.isFlipX()){
            region.flip(true,false);
            running_right = false;
        } else if ((b2body.getLinearVelocity().x > 0 || running_right) && region.isFlipX()) {
            region.flip(true,false);
            running_right = true;

        }

        return region;
    }

    public void climb() {
        currentState = AnimationState.CLIMBING;
        b2body.setGravityScale(0);
    }

    public void jump() {
        currentState = AnimationState.JUMPING;
        //velocityY = jumpVelocity;
    }

    // temporäre Methode, soll später mit Kollisionen automatisch erfolgen
    public void land() {
        currentState = AnimationState.IDLE;
    }

    //todo: State.CLIMBING integrieren
    @Override
    public AnimationState getCurrentState() {
        if (currentState == AnimationState.CLIMBING)
            return AnimationState.CLIMBING;
        else if (b2body.getLinearVelocity().y != 0)
            return AnimationState.JUMPING;
        else if (b2body.getLinearVelocity().x != 0)
            return AnimationState.RUNNING;
        else
            return AnimationState.IDLE;
    }

    //Getter und Setter
    public boolean isJumping() {
        return currentState == AnimationState.JUMPING;
    }

    public final float getJumpSpeed() {
        return jumpSpeed;
    }

    public boolean isClimbing() {return currentState == AnimationState.CLIMBING;}

    public final float getClimbingSpeed() { return climbingSpeed; }
    public void stopClimbing() {
        currentState = AnimationState.IDLE;
        b2body.setGravityScale(1);
    }

    public int getVelocityY() { return velocityY;}

    public void setVelocityY(int value) { velocityY = value; }
    public void atLadder(boolean atLadder) { isAtLadder = atLadder; }

    public boolean isAtLadder() { return isAtLadder; }

    @Override
    public float getStateTimer() { return stateTimer; }

    @Override
    public void setStateTimer(float stateTimer) { this.stateTimer = stateTimer; }

    @Override
    public void setCurrentState(Object animationStates) {
        if (animationStates instanceof AnimationState) {
            this.currentState = (AnimationState) animationStates;
        }
    }
    public void die() {
        screen.gameIsPaused = true;
    }
}
