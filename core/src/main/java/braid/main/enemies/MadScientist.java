package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Random;

public class MadScientist extends Enemy implements EnemyAI {
    private final Random random = new Random();
    private final Player player;
    private float stateTimer = 0;
    private float chargedSpeed = 0f;
    private float respawnCd = 10f;
    private float jumpCd = 5f;
    private float attackCd = 2f;
    private float climbingSpeed;
    private boolean isAtLadder;


    public MadScientist(World world, LevelScreen screen, Player player, float x, float y) {
        super(world, screen, x, y);
        this.player = player;
        defineBody();

        speed = 0.15f;

        sprite = new Sprite(screen.getAtlas().findRegion("wissenschaftler"));

        idle = new Animation<>(0.1f, screen.getAtlas().findRegions("wissenschaftler"), Animation.PlayMode.LOOP);
        sprite.setBounds(0, 0, 24 / Braid.PPM, 24 / Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, true));
        b2body.setActive(true);
    }

    public void update(float dt) {
        super.update(dt);
        respawnCd -= dt;
        jumpCd -= dt;
        attackCd -= dt;
        climbingSpeed = dt;
        sprite.setRegion(getFrame(dt));
        attack();
        System.out.println("Enemy Y: " + getY() + " y Velocity: " +b2body.getLinearVelocity().y );
    }

    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);

        fdef.shape = shape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData(new UserData("EnemyBody", this));

        // Create head collider
        FixtureDef headFdef = new FixtureDef();
        PolygonShape head = new PolygonShape();
        head.setAsBox(4 / Braid.PPM, 1 / Braid.PPM, new Vector2(0, 10 / Braid.PPM), 0);
        headFdef.shape = head;
        headFdef.friction = 1f;
        Fixture headFixture = b2body.createFixture(headFdef);
        headFixture.setUserData(new UserData("EnemyHead", this));

        FixtureDef footFdef = new FixtureDef();
        PolygonShape feet = new PolygonShape();
        feet.setAsBox(4 / Braid.PPM, 1 / Braid.PPM, new Vector2(0, -10 / Braid.PPM), 0);
        footFdef.shape = feet;
        footFdef.friction = 1f;
        Fixture feetFixture = b2body.createFixture(footFdef);
        feetFixture.setUserData(new UserData("MadScientistFeet", this));
    }

    public TextureRegion getFrame(float dt) {
        // currently only has idle animation
        stateTimer += dt;
        return idle.getKeyFrame(stateTimer, true);
    }

    public boolean isAtLadder() {
        return isAtLadder;
    }

    public void atLadder(boolean atLadder) {
        isAtLadder = atLadder;
    }


    public void move(float moveSpeed) {
        b2body.applyLinearImpulse(new Vector2((moveSpeed) * .5f, 0), b2body.getWorldCenter(), true);
    }

    public void jump(float jumpHeight) {

        if(!isJumping()) {
            b2body.applyLinearImpulse(new Vector2(0, jumpHeight), b2body.getWorldCenter(), true);
        }
    }

    public boolean isJumping() {
        return Math.abs(b2body.getLinearVelocity().y) > 0.15;

    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {
        if (respawnCd <= 0) {
            respawnCd = this.getCurrentState() == AnimationState.ALIVE ? respawnCd : 10f;
            this.setCurrentState(Enemy.AnimationState.ALIVE) ;
            b2body.setLinearVelocity(0, 0);
        }
        else if(!player.isAlive()){
            attackCd = 5f;
            chargedSpeed = 2f;
            return;

        }

        if(attackCd > 0){
            b2body.setActive(false);
            chargedSpeed = 2f;
            return;

        }

        if (player.getRewindController().isRewinding()) {
            b2body.setActive(false);
            chargedSpeed += 0.5f;
            chargedSpeed = Math.min(5f, chargedSpeed);
        } else if(chargedSpeed > 0 && attackCd <= 0){
            b2body.setLinearVelocity(new Vector2(player.b2body.getPosition().x - b2body.getPosition().x, player.b2body.getPosition().y - b2body.getPosition().y).nor().scl(chargedSpeed + player.getSpeed()));
            chargedSpeed = Math.min(chargedSpeed-2.5f, 0);
        }else{
            if (isAtLadder()){
                b2body.setGravityScale(0);
                b2body.setLinearVelocity(0, (float) Math.sin(climbingSpeed * 20));
            }
            else {
                b2body.setGravityScale(1f);
                float moveSpeed = getSpeed();
                if (b2body.getPosition().x < player.b2body.getPosition().x) {
                    move(moveSpeed);
                } else if (b2body.getPosition().x > player.b2body.getPosition().x) {
                    move(-moveSpeed);
                }

                if (!isJumping() && player.b2body.getPosition().y > b2body.getPosition().y && jumpCd <= 0) {
                    jump(2f);
                    jumpCd = 3f;

                }
            }
        }
    }
}
