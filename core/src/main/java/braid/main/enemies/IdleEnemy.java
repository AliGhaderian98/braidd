package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class IdleEnemy extends Enemy implements EnemyAI{
    private int direction = 1;

    public IdleEnemy(World world, LevelScreen screen, float x, float y, boolean rewindable, String type) {
        super(world, screen,x,y, rewindable, type);
        defineBody();

        speed = 0.15f;

        setSprite(screen.getAtlas());
        setStateTimer(MathUtils.random(0,idle.getKeyFrames().length));

    }

    public void update(float dt) {
        super.update(dt);
        sprite.setRegion(getFrame(dt, idle));

    }

    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);
        shape.setPosition(new Vector2(0, -3/Braid.PPM));

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
    public void setSprite(TextureAtlas atlas) {
        int Bridnumber = 0;
        switch (type) {
            case ("cat") -> {
                sprite = new Sprite(atlas.findRegion("cat-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("cat-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.75f/ Braid.PPM,
                    sprite.getRegionHeight()*0.75f/Braid.PPM);
            }
            case ("mushroom") -> {
                sprite = new Sprite(atlas.findRegion("mushroom-idle"));
                idle = new Animation<>(0.07f, atlas.findRegions("mushroom-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*.75f/ Braid.PPM,
                    sprite.getRegionHeight()*.75f/ Braid.PPM);
            }
            case ("henry") -> {
                sprite = new Sprite(atlas.findRegion("henry-idle"));
                idle = new Animation<>(0.2f, atlas.findRegions("henry-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*.75f/ Braid.PPM,
                    sprite.getRegionHeight()*.75f/ Braid.PPM);
            }
            case ("alessa") -> {
                sprite = new Sprite(atlas.findRegion("alessa-idle"));
                idle = new Animation<>(0.2f, atlas.findRegions("alessa-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*.75f/ Braid.PPM,
                    sprite.getRegionHeight()*.75f/ Braid.PPM);
            }
            case ("Bird1") -> {
                Bridnumber = 1;
            }
            case ("Bird2") -> {
                Bridnumber = 2;
            }
            case ("Bird3") -> {
                Bridnumber = 3;
            }
            case ("Bird4") -> {
                Bridnumber = 4;
            }
            case ("Bird5") -> {
                Bridnumber = 5;
            }
            case ("Bird6") -> {
                Bridnumber = 6;
            }
            case ("Bird7") -> {
                Bridnumber = 7;
            }

            default -> {
                sprite = new Sprite(atlas.findRegion("lion-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("lion-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0, idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
        }

        // Enemy is a Bird
        if(Bridnumber != 0){
            sprite = new Sprite(atlas.findRegion("Bird"+Bridnumber));
            idle = new Animation<>(0.4f, atlas.findRegions("Bird"+Bridnumber), Animation.PlayMode.LOOP);
            sprite.setRegion(getFrame(0,idle));
            sprite.setBounds(0,0,
                sprite.getRegionWidth()*0.75f/ Braid.PPM,
                sprite.getRegionHeight()*0.75f/Braid.PPM);
        }
    }

    @Override
    public void idle() {
    }

    @Override
    public void attack() {

    }
}
