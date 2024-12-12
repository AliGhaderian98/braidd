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
    private float climbingSpeed;
    private boolean isAtLadder;


    public MadScientist(World world, LevelScreen screen, Player player, float x, float y) {
        super(world, screen, x, y);
        this.player = player;
        defineBody();

        speed = 0.15f;
        climbingSpeed = 0.5f;

        sprite = new Sprite(screen.getAtlas().findRegion("wissenschaftler"));

        idle = new Animation<>(0.1f, screen.getAtlas().findRegions("wissenschaftler"), Animation.PlayMode.LOOP);
        sprite.setBounds(0, 0, 24 / Braid.PPM, 24 / Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, true));
        b2body.setActive(true);
    }

    public void update(float dt) {
        super.update(dt);
        respawnCd -= dt;
        sprite.setRegion(getFrame(dt));
        attack();
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

    public void atLadder(boolean atLadder) { isAtLadder = atLadder; }



    public void jump(float jumpHeight){
        if (getSprite().getY() < player.getSprite().getY()) {
            b2body.applyLinearImpulse(new Vector2(0, jumpHeight), b2body.getWorldCenter(), true);
        } else if ((getSprite().getY() > player.getSprite().getY())) {
            b2body.applyLinearImpulse(new Vector2(0, jumpHeight), b2body.getWorldCenter(), true);
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {
        if (player.getRewindController().isRewinding()) {
            b2body.setActive(false);
            chargedSpeed += 0.5f;
            chargedSpeed = Math.min(5f, chargedSpeed);
            b2body.setLinearVelocity(0, 0);
        } else {
            if (respawnCd <= 0) {
                this.setCurrentState(Enemy.AnimationState.ALIVE);
                respawnCd = 10f;
                chargedSpeed = 0f;
                speed = 0.15f;
                b2body.setLinearVelocity(0, 0);
            }
            float moveSpeed = getSpeed() + chargedSpeed;
            if(isAtLadder()){
                b2body.setLinearVelocity(0, climbingSpeed = (getSprite().getY() > player.getSprite().getY()) ? climbingSpeed : -climbingSpeed);

            }else if (getSprite().getX() < player.getSprite().getX()) {
                b2body.applyLinearImpulse(new Vector2((moveSpeed) * .5f, 0), b2body.getWorldCenter(), true);
                chargedSpeed = Math.max(chargedSpeed - 2.5f, 0);
            }else if (getSprite().getX() > player.getSprite().getX()) {
                b2body.applyLinearImpulse(new Vector2(-(moveSpeed) * .5f, 0), b2body.getWorldCenter(), true);
                chargedSpeed = Math.max(chargedSpeed - 2.5f, 0);

            }
            if (!isJumping()) {
                jump(moveSpeed);
            }
        }
    }
}
