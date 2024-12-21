package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
//todo: stateTimer fixen/übersichtlicher machen, siehe print Ausgaben
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
        CLIMBING,
        FALLING,
        LANDING
    }

    // Animation variables
    private final Animation<TextureRegion> LionIdle, LionRunning, LionJumping, LionClimbing, LionLanding, LionFalling;
    private boolean runningRight;
    private boolean animationPaused;
    public float stateTimer;

    private boolean isGrounded;
    private boolean landingAnimationPlaying = false;

    // Player specific variables
    float jumpSpeed = 3.5f;
    final float climbingSpeed = 1f;
    AnimationState currentState;
    AnimationState previousState;
    private boolean isAlive;
    private boolean isAtLadder;
    private boolean isAtEnd;
    private boolean isOnMovingPlatform = false;
    private float platformVelocity = 0;
    private boolean moving = false;

    // Movement limits e.g. when climbing
    private Vector2 maxMoveLimit;
    private Vector2 minMoveLimit;

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
        LionLanding = new Animation<>(0.25f, screen.getAtlas().findRegions("lion-land"), Animation.PlayMode.NORMAL);
        LionFalling = new Animation<>(0.1f, screen.getAtlas().findRegions("lion-fall"), Animation.PlayMode.NORMAL);
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

        if (isOnMovingPlatform && !moving) {
            b2body.setLinearVelocity(new Vector2(platformVelocity, b2body.getLinearVelocity().y));
        }

        System.out.println("Moving: "+moving+
            ", SpeedX: "+b2body.getLinearVelocity().x+
            ", MaxSpeed: "+ speed);
    }

    public TextureRegion getFrame(float dt){
        testAnimationPause();

        if (!animationPaused) {
            stateTimer = currentState == previousState ? stateTimer + dt : 0;
        }

        previousState = currentState;

        TextureRegion region;

        if (landingAnimationPlaying)
            region = LionLanding.getKeyFrame(0,false);
        else {
            region = switch (currentState) {
                case RUNNING -> LionRunning.getKeyFrame(stateTimer, true);
                case JUMPING -> LionJumping.getKeyFrame(0, false);
                case FALLING -> LionFalling.getKeyFrame(0,false);
                case CLIMBING -> LionClimbing.getKeyFrame(stateTimer, true);
                default -> LionIdle.getKeyFrame(stateTimer, true);
            };
        }


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


    public void land() {
        currentState = AnimationState.LANDING;
        isGrounded = true;
        if (b2body.getLinearVelocity().y < -3.75) {
            landingAnimationPlaying = true;
            new Thread(() -> {
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() < time + 250){}
                Gdx.app.postRunnable(() -> {
                    landingAnimationPlaying = false;
                } );
            }).start();
        }

    }

    public void fall() {
        if (currentState != AnimationState.JUMPING) {
            currentState = AnimationState.FALLING;
            isGrounded = false;
        }
    }

    public void die() {
        LevelScreen.gameIsPaused = true;
        isAlive = false;
    }


    // Inputs

    public void moveRight() {
        moving = true;
        if (isGrounded)
            currentState = AnimationState.RUNNING;
        if (b2body.getLinearVelocity().x < speed) {
            b2body.applyLinearImpulse(new Vector2(getSpeed() * .5f, 0), b2body.getWorldCenter(), true);
        }
    }

    public void moveLeft() {
        moving = true;
        if (isGrounded)
            currentState = AnimationState.RUNNING;
        if (b2body.getLinearVelocity().x > -speed) {
            b2body.applyLinearImpulse(new Vector2(-getSpeed() * .5f, 0), b2body.getWorldCenter(), true);
        }
    }

    public void stopMovement() {
        moving = false;
        if (currentState != AnimationState.CLIMBING) {
            if (isGrounded)
                currentState = AnimationState.IDLE;
            if (!isOnMovingPlatform)
                b2body.setLinearVelocity(0, b2body.getLinearVelocity().y);
        }
    }

    public void climbUp() {
        currentState = AnimationState.CLIMBING;
        isGrounded = false;
        b2body.setGravityScale(0);
        Vector2 newPos = new Vector2(b2body.getPosition());

        if (b2body.getPosition().y < maxMoveLimit.y)
            b2body.setLinearVelocity(0, getClimbingSpeed());
        else {
            b2body.setLinearVelocity(0, 0);
            newPos.y = MathUtils.clamp(b2body.getPosition().y, minMoveLimit.y, maxMoveLimit.y);
        }

        b2body.setTransform(newPos, b2body.getAngle());
    }

    public void climbDown() {
        b2body.setLinearVelocity(0, -getClimbingSpeed());
    }

    public void jump() {
        b2body.setGravityScale(1);
        b2body.applyLinearImpulse(new Vector2(0, getJumpSpeed()), b2body.getWorldCenter(), true);
        currentState = AnimationState.JUMPING;
        isGrounded = false;
    }

    public void jump(float multiplier) {
        b2body.setGravityScale(1);
        b2body.applyLinearImpulse(new Vector2(0, getJumpSpeed()*multiplier), b2body.getWorldCenter(), true);
        currentState = AnimationState.JUMPING;
        isGrounded = false;
    }




    //Getter und Setter

    @Override
    public AnimationState getCurrentState() {
        return currentState;
    }

    public boolean isJumping() {
        return currentState == AnimationState.JUMPING;
    }

    public final float getJumpSpeed() {
        return jumpSpeed;
    }

    public boolean isClimbing() {return currentState == AnimationState.CLIMBING;}

    public final float getClimbingSpeed() { return climbingSpeed; }
    public void stopClimbing() {
        if (currentState != AnimationState.JUMPING) {
            if (isGrounded)
                currentState = AnimationState.IDLE;
            else
                currentState = AnimationState.FALLING;
        }
        b2body.setGravityScale(1);
    }

    public void setIsGrounded(boolean b) {
        isGrounded = b;
        if (!b)
            currentState = AnimationState.JUMPING;
        else
            currentState = AnimationState.IDLE;
    }

    public boolean isGrounded() { return isGrounded; }

    public void setPlatformVelocity(float velocity) {
        this.platformVelocity = velocity;
    }

    public float getPlatformVelocity() { return platformVelocity; }

    public void onMovingPlatform(boolean value) { isOnMovingPlatform = value; }

    public boolean onMovingPlatform() {return isOnMovingPlatform; }

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

    public void setMoveLimits(Vector2 max, Vector2 min) {
        maxMoveLimit = max;
        minMoveLimit = min;
    }

    public void resetMoveLimits() {
        maxMoveLimit = null;
        minMoveLimit = null;
    }
}
