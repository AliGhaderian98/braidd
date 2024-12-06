package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.screens.TestScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PatrollingEnemy extends Enemy implements EnemyAI{
    private int direction = 1;
    public PatrollingEnemy(World world, TestScreen screen) {
        super(world, screen);
        defineBody();
        createsideSensor();
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
        bdef.position.set((Braid.V_WIDTH - 32) / Braid.PPM, 32 / Braid.PPM);
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
        b2body.applyLinearImpulse(new Vector2(direction*getSpeed() * .6f, 0), b2body.getWorldCenter(), true);
    }

    @Override
    public void attack() {

    }

    public void createEdgeSensor() {
        PolygonShape edgeSensorShape = new PolygonShape();
        edgeSensorShape.setAsBox(2 / Braid.PPM, 1 / Braid.PPM, new Vector2(6 / Braid.PPM * direction, -6 / Braid.PPM), 0);

        FixtureDef edgeSensorDef = new FixtureDef();
        edgeSensorDef.shape = edgeSensorShape;
        edgeSensorDef.isSensor = true;

        Fixture edgeSensor = b2body.createFixture(edgeSensorDef);
        edgeSensor.setUserData(new UserData("EdgeSensor", this));
    }

    public void createsideSensor() {
        // Create side sensors
        PolygonShape sideSensorShape = new PolygonShape();
        sideSensorShape.setAsBox(2 / Braid.PPM, 4 / Braid.PPM, new Vector2(10 / Braid.PPM * direction, 0), 0);

        FixtureDef sideSensorDef = new FixtureDef();
        sideSensorDef.shape = sideSensorShape;
        sideSensorDef.isSensor = true; // Sensors don't influence physics directly

        Fixture sideSensor = b2body.createFixture(sideSensorDef);
        sideSensor.setUserData(new UserData("SideSensor", this));
    }
    public void changeDirection() { direction *= -1; }
}
