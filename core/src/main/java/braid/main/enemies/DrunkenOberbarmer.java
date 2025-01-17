package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Beer;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.ArrayList;
import java.util.List;

public class DrunkenOberbarmer extends Enemy implements EnemyAI {
    private int direction = 1;
    private List<Beer> bierflaschen;
    private float attackCooldown = 2f;
    private float attackTime = 0f;
    private boolean isInRange = false;
    private final LevelScreen screen;
    private final Player player;


    public DrunkenOberbarmer(World world, LevelScreen screen, Player player, float x, float y, boolean rewindable, String type) {
        super(world, screen, x, y, rewindable, type);
        this.player = player;
        this.screen = screen;

        bierflaschen = new ArrayList<>();
        speed = 0.15f;


        defineBody();
        setSprite(screen.getAtlas());
    }

    public void update(float dt) {
        super.update(dt);

        for (Beer bierflasche : bierflaschen) {
            bierflasche.update(dt);

        }

        sprite.setRegion(getFrame(dt, walking));
        sprite.setFlip(direction < 0, false);

        if (isInRange) {
            attack(dt);
        } else {
            idle();
        }
    }

    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Create EnemyBody
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);
        shape.setPosition(new Vector2(0, -3/Braid.PPM));

        fdef.shape = shape;
        fdef.friction = 1f;
        Fixture bodyFixture = b2body.createFixture(fdef);
        bodyFixture.setUserData(new UserData("EnemyBody", this));

        // Create EnemyRadius
        FixtureDef enemyRadiusDef = new FixtureDef();
        shape.setRadius(130 / Braid.PPM);
        enemyRadiusDef.shape = shape;
        enemyRadiusDef.friction = 1f;
        enemyRadiusDef.isSensor = true;
        Fixture radiusFixture = b2body.createFixture(enemyRadiusDef);
        radiusFixture.setUserData(new UserData("drunkenEnemyRadius", this));

        shape.dispose();
    }

    @Override
    public void setSprite(TextureAtlas atlas) {
        sprite = new Sprite(atlas.findRegion("cat-idle"));
        idle = new Animation<>(0.1f, atlas.findRegions("lion-idle"), Animation.PlayMode.LOOP);
        walking = new Animation<>(0.1f, atlas.findRegions("lion-run"), Animation.PlayMode.LOOP);
        sprite.setRegion(getFrame(0, idle));
        sprite.setBounds(0, 0, sprite.getRegionWidth() / Braid.PPM, sprite.getRegionHeight() / Braid.PPM);
    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {

    }


    public void attack(float dt) {
        attackTime += dt;

        if (attackTime >= attackCooldown) {
            attackTime = 0f;
            throwBierflasche();
        }
    }

    public void throwBierflasche() {
        Beer bierflasche = new Beer(
            getX(),
            getY(),
            player,
            screen.getAtlas().findRegion("schalter"),
            world
        );

        bierflaschen.add(bierflasche);
    }

    public void isInRange(boolean inRange) {
        isInRange = inRange;
    }
}
