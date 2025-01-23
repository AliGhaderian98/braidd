package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

public class PatrollingEnemy extends Enemy implements EnemyAI {
    private int direction = 1;

    public PatrollingEnemy(World world, LevelScreen screen, float x, float y, boolean rewindable, String type) {
        super(world, screen,x,y, rewindable, type);
        defineBody();
        createSideSensor();
        createEdgeSensor();

        speed = 0.15f;

        setSprite(screen.getAtlas());
    }

    public void update(float dt) {
        super.update(dt);
        sprite.setRegion(getFrame(dt, walking));
        sprite.setFlip(direction<0,false);
        idle();
    }

    public void defineBody() {

        // defult
        float radius = 8/ Braid.PPM;
        float position =-3 /Braid.PPM;
        float hx = 4 / Braid.PPM;
        float yPoint = 10 / Braid.PPM;

        if(Objects.equals(type, "bear")){
            radius = 23/ Braid.PPM;
            position = -25 /Braid.PPM;
            hx = 30/ Braid.PPM;
            yPoint = -6 / Braid.PPM;
        } else if (type.equals("nakedguy")) {
            radius = 8/ Braid.PPM;
            position = -15 /Braid.PPM;
            hx = 4/ Braid.PPM;
            yPoint = 20 / Braid.PPM;
        }

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        shape.setPosition(new Vector2(0, position));

        fdef.shape = shape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData(new UserData("EnemyBody", this));



        // Create head collider
        FixtureDef headFdef = new FixtureDef();
        PolygonShape head = new PolygonShape();
        head.setAsBox(hx, 1 / Braid.PPM, new Vector2(0, yPoint), 0);
        headFdef.shape = head;
        headFdef.friction = 1f;
        Fixture headFixture = b2body.createFixture(headFdef);
        headFixture.setUserData(new UserData("EnemyHead", this));
    }

    @Override
    public void setSprite(TextureAtlas atlas) {
        switch (type) {
            case ("cat") -> {
                sprite = new Sprite(atlas.findRegion("cat-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("cat-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.1f, atlas.findRegions("cat-run"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.75f/ Braid.PPM,
                    sprite.getRegionHeight()*0.75f/Braid.PPM);
            }
            case ("bear") -> {
                sprite = new Sprite(atlas.findRegion("bear-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("bear-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.2f, atlas.findRegions("bear-run"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.75f/ Braid.PPM,
                    sprite.getRegionHeight()*0.75f/Braid.PPM);
            }
            case ("snake") -> {
                sprite = new Sprite(atlas.findRegion("snake-run"));
                walking = new Animation<>(0.1f, atlas.findRegions("snake-run"), Animation.PlayMode.LOOP_PINGPONG);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.75f/ Braid.PPM,
                    sprite.getRegionHeight()*0.75f/Braid.PPM);
            }
            case ("chicken") -> {
                sprite = new Sprite(atlas.findRegion("chicken-idle"));
                walking = new Animation<>(0.2f, atlas.findRegions("chicken-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
            case ("nakedguy") -> {
                sprite = new Sprite(atlas.findRegion("nakedguy-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("nakedguy-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.1f, atlas.findRegions("nakedguy-run"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
            case ("freudenberg-guy") -> {
                sprite = new Sprite(atlas.findRegion("freudenberg-guy"));
                walking = new Animation<>(0.2f, atlas.findRegions("freudenberg-guy"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.5f/ Braid.PPM,
                    sprite.getRegionHeight()*0.5f/Braid.PPM);
            }
            case ("Security-Guy") -> {
                sprite = new Sprite(atlas.findRegion("Security-Guy"));
                walking = new Animation<>(0.2f, atlas.findRegions("Security-Guy"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
            case ("wolf") -> {
                sprite = new Sprite(atlas.findRegion("wolf-walk"));
                walking = new Animation<>(0.1f, atlas.findRegions("wolf-walk"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*1.25f/ Braid.PPM,
                    sprite.getRegionHeight()*1.25f/Braid.PPM);
            }

            case ("rotatingCoffeeBean") -> {
                sprite = new Sprite(atlas.findRegion("rotatingCoffeeBean"));
                walking = new Animation<>(0.2f, atlas.findRegions("rotatingCoffeeBean"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }

            case ("PublicBus") -> {
                this.changeSpeed(5f);
                sprite = new Sprite(atlas.findRegion("Bus"));
                walking = new Animation<>(0.1f, atlas.findRegions("Bus"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0, walking));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
            default -> {
                sprite = new Sprite(atlas.findRegion("lion-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("lion-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.1f, atlas.findRegions("lion-run"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0, idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
        }
    }

    @Override
    public void idle() {
        b2body.setLinearVelocity(new Vector2(direction*getSpeed() * 2f, 0));
    }

    @Override
    public void attack() {

    }


    public void createEdgeSensor() {

        float hx = 1/ Braid.PPM; // defult
        float yPoint = -12 / Braid.PPM; // defult
        float rightxPoint = 6 / Braid.PPM; // defult
        float leftxPoint = -6 / Braid.PPM;
        if(Objects.equals(type, "bear")){
            hx = 1/ Braid.PPM;
            yPoint = -50 / Braid.PPM;
            rightxPoint = 28 / Braid.PPM;
            leftxPoint = -28 / Braid.PPM;
        } else if (type.equals("nakedguy")) {
            hx = 1/ Braid.PPM;
            yPoint = -23 / Braid.PPM;
            rightxPoint = 6 / Braid.PPM;
            leftxPoint = -6 / Braid.PPM;
        }

        // Sensor for the left side
        PolygonShape leftEdgeSensorShape = new PolygonShape();
        leftEdgeSensorShape.setAsBox(hx, 1 / Braid.PPM, new Vector2(leftxPoint, yPoint), 0);

        FixtureDef leftEdgeSensorDef = new FixtureDef();
        leftEdgeSensorDef.shape = leftEdgeSensorShape;
        leftEdgeSensorDef.isSensor = true;

        Fixture leftEdgeSensor = b2body.createFixture(leftEdgeSensorDef);
        leftEdgeSensor.setUserData(new UserData("EdgeSensor", this));

        // Sensor for the right side
        PolygonShape rightEdgeSensorShape = new PolygonShape();
        rightEdgeSensorShape.setAsBox(hx, 1 / Braid.PPM, new Vector2(rightxPoint, yPoint), 0);

        FixtureDef rightEdgeSensorDef = new FixtureDef();
        rightEdgeSensorDef.shape = rightEdgeSensorShape;
        rightEdgeSensorDef.isSensor = true;

        Fixture rightEdgeSensor = b2body.createFixture(rightEdgeSensorDef);
        rightEdgeSensor.setUserData(new UserData("EdgeSensor", this));
    }



    public void createSideSensor() {

        float hx = 9/ Braid.PPM; // defult
        float yPoint = 0; // defult
        if(Objects.equals(type, "bear")){
            hx = 30/ Braid.PPM;
            yPoint = -35 / Braid.PPM;
        }

        // Create side sensors
        PolygonShape sideSensorShape = new PolygonShape();
        sideSensorShape.setAsBox(hx, 1 / Braid.PPM, new Vector2(0, yPoint), 0);

        FixtureDef sideSensorDef = new FixtureDef();
        sideSensorDef.shape = sideSensorShape;
        sideSensorDef.isSensor = true;

        Fixture sideSensor = b2body.createFixture(sideSensorDef);
        sideSensor.setUserData(new UserData("SideSensor", this));
    }

    public void changeDirection() {
        direction *= -1;
    }

    public void changeSpeed(float newSpeed) {
        speed = newSpeed;
    }
}
