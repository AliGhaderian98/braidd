package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import braid.main.screens.levels.LevelScreen;
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

public class Player extends DynamicGameObject {
    // Enumeration to represent the possible animation states a player can be in
    public enum AnimationState {
        IDLE,
        JUMPING,
        RUNNING,
        CLIMBING
    }

    // Animation variables
    private final Animation<TextureRegion> LionIdle, LionRunning, LionJumping, LionClimbing;
    private boolean runningRight;
    private boolean animationPaused;
    public float stateTimer;

    // Player specific variables
    float jumpSpeed = 3.5f;
    final float climbingSpeed = 1f;
    AnimationState currentState;
    AnimationState previousState;
    private boolean isAlive;
    private boolean isAtLadder;
    private boolean isAtEnd;


    // Constructors
    public Player(World world, LevelScreen screen) {
        super(world);

        speed = 1f;
        currentState = AnimationState.IDLE;
        previousState = AnimationState.IDLE;
        runningRight = true;
        isAlive = true;

        // Setup box2d body
        defineBody();

        // Setup sprite
        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);

        // Animation loops
        LionIdle = new Animation<>(0.2f, screen.getAtlas().findRegions("lion-idle"), Animation.PlayMode.LOOP);
        LionRunning = new Animation<>(0.1f, screen.getAtlas().findRegions("lion-run"), Animation.PlayMode.LOOP);
        LionJumping = new Animation<>(0.1f, screen.getAtlas().findRegions("lion-jump"), Animation.PlayMode.NORMAL);
        LionClimbing = new Animation<>(0.1f, screen.getAtlas().findRegions("lion-climb"), Animation.PlayMode.LOOP);
    }

    // Methods
    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Braid.PPM, 32 / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Create main collider
        FixtureDef bodyFdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);

        bodyFdef.shape = shape;
        bodyFdef.friction = 0f;
        Fixture bodyFixture = b2body.createFixture(bodyFdef);
        bodyFixture.setUserData(new UserData("PlayerBody", this));

        // Create feet collider
        FixtureDef footFdef = new FixtureDef();
        PolygonShape feet = new PolygonShape();
        feet.setAsBox(4 / Braid.PPM, 1 / Braid.PPM, new Vector2(0, -10 / Braid.PPM), 0);
        footFdef.shape = feet;
        footFdef.friction = 1f;
        Fixture feetFixture = b2body.createFixture(footFdef);
        feetFixture.setUserData(new UserData("PlayerFeet", this));

        shape.dispose();
        feet.dispose();
    }

    public void update(float dt) {
        sprite.setRegion(getFrame(dt));
        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
    }

    public TextureRegion getFrame(float dt){
        // regions for the different States
        currentState = getCurrentState();

        testAnimationPause();

        if (!animationPaused) {
            stateTimer = currentState == previousState ? stateTimer + dt : 0;
        }

        previousState = currentState;
        TextureRegion region = switch (currentState) {
            case RUNNING -> LionRunning.getKeyFrame(stateTimer, true);
            case JUMPING -> LionJumping.getKeyFrame(0, false);
            case CLIMBING -> LionClimbing.getKeyFrame(stateTimer, true);
            default -> LionIdle.getKeyFrame(stateTimer, true);
        };

        //checking if the model has to be flipped
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true,false);
            runningRight = true;

        }

        return region;
    }

    private void testAnimationPause() {
        if (isClimbing() && b2body.getLinearVelocity().y == 0)
            animationPaused = true;
        else
            animationPaused = false;
    }



    // temporäre Methode, soll später mit Kollisionen automatisch erfolgen
    public void land() {
        currentState = AnimationState.IDLE;
    }

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



    public void atLadder(boolean atLadder) { isAtLadder = atLadder; }

    public boolean isAtLadder() { return isAtLadder; }

    public void atEnd(boolean atEnd) { isAtEnd = atEnd; }

    public boolean isAtEnd() { return  isAtEnd; }

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

    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive;}

    public void die() {
        LevelScreen.gameIsPaused = true;
        isAlive = false;
    }


    // Inputs

    public void moveRight() {
        if (b2body.getLinearVelocity().x < speed) {
            b2body.applyLinearImpulse(new Vector2(getSpeed() * .5f, 0), b2body.getWorldCenter(), true);
        }
    }

    public void moveLeft() {
        if (b2body.getLinearVelocity().x > -speed) {
            b2body.applyLinearImpulse(new Vector2(-getSpeed() * .5f, 0), b2body.getWorldCenter(), true);
        }
    }

    public void stopMovement() {
        b2body.applyLinearImpulse(new Vector2(0, b2body.getLinearVelocity().y), b2body.getWorldCenter(), true);
    }

    public void climbUp() {
        currentState = AnimationState.CLIMBING;
        b2body.setGravityScale(0);
        b2body.setLinearVelocity(0, getClimbingSpeed());
    }

    public void climbDown() {
        b2body.setLinearVelocity(0, -getClimbingSpeed());
    }

    public void jump() {
        b2body.setGravityScale(1);
        b2body.applyLinearImpulse(new Vector2(0, getJumpSpeed()), b2body.getWorldCenter(), true);
        currentState = AnimationState.JUMPING;
    }

    public void jump(float multiplier) {
        b2body.setGravityScale(1);
        b2body.applyLinearImpulse(new Vector2(0, getJumpSpeed()*multiplier), b2body.getWorldCenter(), true);
        currentState = AnimationState.JUMPING;
    }

}
