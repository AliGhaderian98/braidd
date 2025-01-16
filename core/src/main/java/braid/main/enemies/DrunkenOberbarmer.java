package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Beer;
import braid.main.objects.Enemy;
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
    private float attackCooldown = 2f;  // Zeitintervall zwischen den Würfen
    private float attackTime = 0f;
    private boolean isInRange = false;
    private final LevelScreen screen;

    public DrunkenOberbarmer(World world, LevelScreen screen, float x, float y, boolean rewindable, String type) {
        super(world, screen, x, y, rewindable, type);
        defineBody();
        this.screen = screen;
        bierflaschen = new ArrayList<>();
        speed = 0.15f;
        setSprite(screen.getAtlas());
    }

    public void update(float dt) {
        super.update(dt);
        attackTime += dt;

        // Wenn genug Zeit vergangen ist, wird eine Bierflasche geworfen
        if (attackTime >= attackCooldown) {
            attackTime = 0f;
            throwBierflasche();
        }

        // Aktualisiere alle Bierflaschen
        for (Beer bierflasche : bierflaschen) {
            bierflasche.update(dt);
            if (bierflasche.isOutOfBounds(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
                bierflaschen.remove(bierflasche);
            }
        }

        sprite.setRegion(getFrame(dt, walking));
        sprite.setFlip(direction < 0, false);

        if (isInRange) {
            attack();
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
        shape.setRadius(70 / Braid.PPM);
        enemyRadiusDef.shape = shape;
        enemyRadiusDef.friction = 1f;
        enemyRadiusDef.isSensor = true;
        Fixture radiusFixture = b2body.createFixture(enemyRadiusDef);
        radiusFixture.setUserData(new UserData("EnemyRadius", this));

        shape.dispose();
    }

    @Override
    public void setSprite(TextureAtlas atlas) {
        sprite = new Sprite(atlas.findRegion("lion-idle"));
        idle = new Animation<>(0.1f, atlas.findRegions("lion-idle"), Animation.PlayMode.LOOP);
        walking = new Animation<>(0.1f, atlas.findRegions("lion-run"), Animation.PlayMode.LOOP);
        sprite.setRegion(getFrame(0, idle));
        sprite.setBounds(0, 0, sprite.getRegionWidth() / Braid.PPM, sprite.getRegionHeight() / Braid.PPM);
    }

    @Override
    public void idle() {
        // Idle-Logik hier (falls notwendig)
    }

    @Override
    public void attack() {
        // Eventuell hier weitere Angriffsinformationen
    }

    public void throwBierflasche() {
        float throwSpeedX = direction * 5f;  // Geschwindigkeit der Bierflasche in x-Richtung
        float throwSpeedY = 0f;              // Keine Geschwindigkeit in y-Richtung

        Beer bierflasche = new Beer(getX(), getY(), throwSpeedX, throwSpeedY, screen.getAtlas(), world);
        bierflaschen.add(bierflasche);
    }

    public void changeDirection() {
        direction *= -1;
    }

    public void isInRange(boolean inRange) {
        isInRange = inRange;
    }
}
