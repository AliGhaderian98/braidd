package braid.main.objects;

import braid.main.Braid;
import braid.main.Items.PowerUp;
import braid.main.screens.huds.LevelHUD;
import braid.main.tools.UserData;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

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

    LevelScreen screen;

    // Animation variables
    private final Animation<TextureRegion> LionIdle, LionRunning, LionJumping, LionClimbing, LionLanding, LionFalling;
    private boolean runningRight;
    private boolean animationPaused;
    public float stateTimer;

    private boolean isGrounded = true;
    private boolean landingAnimationPlaying = false;

    private Timer.Task landingTask;

    // Player specific variables
    private final float defaultGravity = 1.2f;
    private float descendingGravity = defaultGravity*1.3f; //Soll glaube ich nicht mehr Final sein, wegen Gleiter
    private final float variableJumpHeightFactor = 0.5f;
    public boolean holdingJump;
    float jumpSpeed = 3.0f;


    private final float maxCoyoteTime = 0.15f;
    private boolean coyoteActive = false;

    private final float jumpBufferTime = 0.1f;
    private boolean jumpBuffered;

    final float climbingSpeed = 1f;

    AnimationState currentState;
    AnimationState previousState;
    private boolean isAlive;
    private boolean isAtLadder;
    private boolean isAtEnd;
    private boolean isOnMovingPlatform = false;
    private float platformVelocity = 0;
    private boolean moving = false;
    private boolean hasActivePowerUp = false;
    private boolean hammerActive = false;
    private Brick collidingBrick;
    private PowerUp.TypeOfPowerUp previousPowerUp;
    private boolean newestPowerUp = true;
    private boolean isAtSchalter = false;
    private Schalter collidingSchalter = null;
    private boolean hasKey;

  // Movement limits e.g. when climbing
    private Vector2 maxMoveLimit = new Vector2(0,0);
    private Vector2 minMoveLimit = new Vector2(0,0);

    // Constructors
    public Player(World world, LevelScreen screen) {
        super(world);
        this.screen = screen;

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


        landingTask = new Timer.Task() {
            @Override
            public void run() {
                landingAnimationPlaying = false;
                currentState = AnimationState.IDLE;
            }
        };
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

        // Create Hammer colliders
        FixtureDef rechteHammerFdef = new FixtureDef();
        PolygonShape rechterHammer = new PolygonShape();
        rechterHammer.setAsBox(15 / Braid.PPM, 20 / Braid.PPM, new Vector2(0.25F, 10 / Braid.PPM), 0);
        rechteHammerFdef.shape = rechterHammer;
        rechteHammerFdef.friction = 1f;
        rechteHammerFdef.isSensor = true;
        Fixture rechteHammerFixture = b2body.createFixture(rechteHammerFdef);
        rechteHammerFixture.setUserData(new UserData("RechteHammerHitBox", this));

        FixtureDef linkeHammerFdef = new FixtureDef();
        PolygonShape linkerHammer = new PolygonShape();
        linkerHammer.setAsBox(15 / Braid.PPM, 20 / Braid.PPM, new Vector2(-0.25F, 10 / Braid.PPM), 0);
        linkeHammerFdef.shape = linkerHammer;
        linkeHammerFdef.friction = 1f;
        linkeHammerFdef.isSensor = true;
        Fixture linkeHammerFixture = b2body.createFixture(linkeHammerFdef);
        linkeHammerFixture.setUserData(new UserData("LinkeHammerHitBox", this));

        feet.dispose();
        shape.dispose();
        rechterHammer.dispose();
        linkerHammer.dispose();
    }

    public void update(float dt) {
        sprite.setRegion(getFrame(dt));
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);

        // apply variable jump height
        if (isJumping()) {
            if (holdingJump && b2body.getLinearVelocity().y > 0)
                b2body.setGravityScale(variableJumpHeightFactor *defaultGravity);
            else
                b2body.setGravityScale(defaultGravity);

        }

        // enables no gravity after rewinding to a state while climbing
        if (currentState == AnimationState.CLIMBING && b2body.getGravityScale() > 0) {
            b2body.setGravityScale(0);
            isGrounded = false;
        }

        // apply higher descending velocity
        if (!isClimbing() && b2body.getLinearVelocity().y < 0) {
            b2body.setGravityScale(descendingGravity);
        }

        // update velocity on moving platform
        if (isOnMovingPlatform && !moving) {
            b2body.setLinearVelocity(new Vector2(platformVelocity, b2body.getLinearVelocity().y));
        }
    }

    public TextureRegion getFrame(float dt) {
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
        if (jumpBuffered || b2body.getLinearVelocity().y > 0) {
            jump(1f);
            jumpBuffered = false;
        } else if(!isGrounded && b2body.getLinearVelocity().y < 0) {
            currentState = AnimationState.IDLE;
            isGrounded = true;
            holdingJump = false;
            coyoteActive = false;

            if (b2body.getLinearVelocity().y < -4.75) {
                currentState = AnimationState.LANDING;
                landingAnimationPlaying = true;

                landingTask.cancel();
                Timer.schedule(landingTask, 0.25f);
            }
        }
    }

    public void fall() {
        if (currentState != AnimationState.JUMPING) {
            currentState = AnimationState.FALLING;
            isGrounded = false;
            setCoyoteTime();
        }
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

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
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
        if (b)
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

    public boolean hasActivePowerUp() { return hasActivePowerUp; }
    public void hasActivePowerUp(boolean hasActivePowerUp) { this.hasActivePowerUp = hasActivePowerUp; }

    public boolean isHammerActive() {return hammerActive;}
    public void setHammerActive(boolean hammerActive) {this.hammerActive = hammerActive;}

    public PowerUp.TypeOfPowerUp getPreviousPowerUp() { return previousPowerUp; }
    public void setPreviousPowerUp(PowerUp.TypeOfPowerUp previousPowerUp) { this.previousPowerUp = previousPowerUp; }


    public boolean isNewestPowerUp() { return newestPowerUp; }

    public void setNewestPowerUp(boolean newestPowerUp) { this.newestPowerUp = newestPowerUp; }

    public Brick getCollidingBrick() { return collidingBrick; }
    public void setCollidingBrick(Brick collidingBrick) { this.collidingBrick = collidingBrick; }

    public void resetCollidingBrick() {this.collidingBrick = null;}


    public void setMoveLimits(Vector2 max, Vector2 min) {
        maxMoveLimit = max;
        minMoveLimit = min;
    }

    public void isAtSchalter(boolean value, Schalter schalter) {
        isAtSchalter = value;
        collidingSchalter = schalter;
    }

    public boolean isAtSchalter() { return isAtSchalter; }

    public Schalter getCollidingSchalter() { return collidingSchalter; }

    public boolean hasCoyoteTime() { return coyoteActive; }

    public void hasKey(boolean value) {
        hasKey = value;
        LevelHUD.playerHasKey(value);
    }

    public boolean hasKey() { return hasKey; }

    public void die() {
        screen.setHitShader();
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

    public void jump(float multiplier) {
        if (landingTask.isScheduled()) {
            landingTask.cancel();
            landingAnimationPlaying = false;
        }
        currentState = AnimationState.JUMPING;
        b2body.setGravityScale(1);
        b2body.setLinearVelocity(new Vector2(0, getJumpSpeed()*multiplier));
        isGrounded = false;
    }

    public void setJumpBuffer() {
        if (!jumpBuffered) {
            jumpBuffered = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    jumpBuffered = false;
                }
            }, jumpBufferTime);
        }
    }

    private void setCoyoteTime() {
        coyoteActive = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                coyoteActive = false;
            }
        }, maxCoyoteTime);
    }

    public void setDescendingGravity(float descendingGravity) {
        this.descendingGravity = descendingGravity;
    }

    public float getDescendingGravity() {
        return descendingGravity;
    }
}
