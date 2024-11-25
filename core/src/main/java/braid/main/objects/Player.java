package braid.main.objects;

import braid.main.Braid;
import braid.main.*;
import braid.main.screens.TestScreen;
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
    private final RewindableAnimation LionIdle;
    private final RewindableAnimation LionRunning;
    private final RewindableAnimation LionJumping;
    private boolean running_right;
    public float stateTimer;


    private RewindableBody rewindableBody;
    private RewindController rewindController;

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

        speed = .5f; //Langsamer gemacht, um Animationen zu sehen, vorher 1f
        stateTimer = 0;
        currentState = AnimationState.IDLE;
        previousState = AnimationState.IDLE;
        running_right = true;

        defineBody();

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));
        //stand = new TextureRegion(sprite.getTexture(), 2, 2, 24, 24);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        //sprite.setRegion(stand);


        // Animation loops
        LionIdle = new RewindableAnimation(new Animation<>(0.2f, screen.getAtlas().findRegions("lion-idle"), Animation.PlayMode.LOOP_PINGPONG));
        LionRunning = new RewindableAnimation(new Animation<>(0.1f, screen.getAtlas().findRegions("lion-run"), Animation.PlayMode.LOOP));
        LionJumping = new RewindableAnimation(new Animation<>(0.1f, screen.getAtlas().findRegions("lion-jump"), Animation.PlayMode.NORMAL));
        //Neu: Zugriff auf rewindableBody
        rewindableBody = new RewindableBody(b2body);
        rewindController = new RewindController(rewindableBody);
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
        b2body.createFixture(bodyFdef);

        FixtureDef footFdef = new FixtureDef();
        PolygonShape feet = new PolygonShape();
        feet.setAsBox(6 / Braid.PPM, 2 / Braid.PPM, new Vector2(0, -8 / Braid.PPM), 0);
        footFdef.shape = feet;
        footFdef.friction = 1f;
        b2body.createFixture(footFdef);
    }

    public void update(float dt) {
        currentState = getCurrentState(); //mit rewindableBody synchronisieren
        rewindableBody.setCurrentState(currentState);
        stateTimer = rewindableBody.getStateTimer();
        rewindableBody.setStateTimer(stateTimer = rewindController.isRewinding() ? Math.abs(stateTimer - dt) : stateTimer + dt); //todo: sehr wichtig, Max.abs umändern zu nem Modulo-Ding oder so

        rewindController.update();
        sprite.setRegion(getFrame(dt));

        setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
    }

    public TextureRegion getFrame(float dt){

        currentState = rewindableBody.getCurrentState(); //!= null ? rewindableBody.getCurrentState() : AnimationState.IDLE; //Null pointer fix falls die wieder auftauchen
        stateTimer = rewindableBody.getStateTimer(); // >= 0 ? rewindableBody.getStateTimer() : 0;
        // regions for the different States
        TextureRegion region;

        switch (currentState){
            case RUNNING:
                region = LionRunning.getKeyFrame(stateTimer, rewindController.isRewinding());
                break;
            case JUMPING:
                region = LionJumping.getKeyFrame(stateTimer, rewindController.isRewinding());
                break;
            case IDLE:
            default:
                region = LionIdle.getKeyFrame(stateTimer, rewindController.isRewinding());
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
        //todo: testen ob wir das hier brauchen, soweit ich getestet habe braucht man das eig nicht
        if(rewindController.isRewinding() && this.rewindableBody != null){
            stateTimer = rewindableBody.getStateTimer();
            currentState = rewindableBody.getCurrentState();
        } else{
            stateTimer = currentState == previousState ? stateTimer + dt : 0;
        }

        //Ich checke nicht warum die StateTimer sich nicht updaten, ruhig ausschalten als Comment
        System.out.println("currentState:" + currentState + " previousState: " + previousState + " RunningStateTimer: " + LionRunning.getStateTimer() + "JumpingStateTimer: " + LionJumping.getStateTimer());
        previousState = currentState;


        return region;
    }

    public void climb() {
        currentState = AnimationState.CLIMBING;
    }

    public void jump() {
        //if(currentState != AnimationState.JUMPING) {
        currentState = AnimationState.JUMPING;
        //}
        //velocityY = jumpVelocity;
    }

    // temporäre Methode, soll später mit Kollisionen automatisch erfolgen
    public void land() {
        currentState = AnimationState.IDLE;
    }

    //todo: State.CLIMBING integrieren
    public AnimationState getCurrentState() {
        if(b2body.getLinearVelocity().y > 0)
            return AnimationState.JUMPING;
        else if (b2body.getLinearVelocity().x != 0 && currentState != AnimationState.JUMPING) //eig offensichtlich, kann man safe iwann weglassen aber ohne war weird
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

    public final float getClimbingSpeed() {return climbingSpeed;}

    public int getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(int value) {
        velocityY = value;
    }

    public void setRewindController(RewindController rewindController) {
        this.rewindController = rewindController;
    }

    public RewindController getRewindController() {
        return rewindController;
    }

    public void atLadder(boolean atLadder) {isAtLadder = atLadder;}
    public boolean isAtLadder() {return isAtLadder;}
}
