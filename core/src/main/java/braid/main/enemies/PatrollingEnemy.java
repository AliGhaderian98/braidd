package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PatrollingEnemy extends Enemy implements EnemyAI{
    private int direction = 1;

    public PatrollingEnemy(World world, LevelScreen screen, float x, float y, boolean rewindable) {
        super(world, screen,x,y, rewindable);
        defineBody();
        createSideSensor();
        createEdgeSensor();

        speed = 0.15f;

        sprite = new Sprite(screen.getAtlas().findRegion("lion-idle"));

        idle = new Animation<>(0.2f, screen.getAtlas().findRegions("lion-idle"), Animation.PlayMode.LOOP);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, false));
    }

    public void update(float dt) {
        super.update(dt);
        idle();
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
    }

    @Override
    public void idle() {
        b2body.setLinearVelocity(new Vector2(direction*getSpeed() * 2f, 0));
    }

    @Override
    public void attack() {

    }


    public void createEdgeSensor() {
        // Sensor for the left side
        PolygonShape leftEdgeSensorShape = new PolygonShape();
        leftEdgeSensorShape.setAsBox(1 / Braid.PPM, 1 / Braid.PPM, new Vector2(-6 / Braid.PPM, -12 / Braid.PPM), 0);

        FixtureDef leftEdgeSensorDef = new FixtureDef();
        leftEdgeSensorDef.shape = leftEdgeSensorShape;
        leftEdgeSensorDef.isSensor = true;

        Fixture leftEdgeSensor = b2body.createFixture(leftEdgeSensorDef);
        leftEdgeSensor.setUserData(new UserData("EdgeSensor", this));

        // Sensor for the right side
        PolygonShape rightEdgeSensorShape = new PolygonShape();
        rightEdgeSensorShape.setAsBox(1 / Braid.PPM, 1 / Braid.PPM, new Vector2(6 / Braid.PPM, -12 / Braid.PPM), 0);

        FixtureDef rightEdgeSensorDef = new FixtureDef();
        rightEdgeSensorDef.shape = rightEdgeSensorShape;
        rightEdgeSensorDef.isSensor = true;

        Fixture rightEdgeSensor = b2body.createFixture(rightEdgeSensorDef);
        rightEdgeSensor.setUserData(new UserData("EdgeSensor", this));
    }



    public void createSideSensor() {
        // Create side sensors
        PolygonShape sideSensorShape = new PolygonShape();
        sideSensorShape.setAsBox(7 / Braid.PPM, 1 / Braid.PPM, new Vector2(0, 0), 0);

        FixtureDef sideSensorDef = new FixtureDef();
        sideSensorDef.shape = sideSensorShape;
        sideSensorDef.isSensor = true;

        Fixture sideSensor = b2body.createFixture(sideSensorDef);
        sideSensor.setUserData(new UserData("SideSensor", this));
    }

    public void changeDirection() { direction *= -1; }
}
