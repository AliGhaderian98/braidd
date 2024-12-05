package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.screens.TestScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Wissenschaftler extends Enemy implements EnemyAI{
    private final Player player;
    public Wissenschaftler(World world, TestScreen screen, Player player) {
        super(world, screen);
        this.player = player;
        defineBody();

        speed = 0.15f;

        sprite = new Sprite(screen.getAtlas().findRegion("wissenschaftler"));

        idle = new Animation<>(0.2f, screen.getAtlas().findRegions("wissenschaftler"), Animation.PlayMode.LOOP);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, false));
    }

    public void update(float dt) {
        super.update(dt);
        attack();
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
        bodyFixture.setUserData("EnemyBody");

        // Create head collider
        FixtureDef headFdef = new FixtureDef();
        PolygonShape head = new PolygonShape();
        head.setAsBox(4 / Braid.PPM, 1 / Braid.PPM, new Vector2(0, 10 / Braid.PPM), 0);
        headFdef.shape = head;
        headFdef.friction = 1f;
        Fixture headFixture = b2body.createFixture(headFdef);
        headFixture.setUserData(this);
    }
    @Override
    public void idle() {

    }

    @Override
    public void attack() {
        // move enemy based on the position of the player
        if (getSprite().getX() < player.getSprite().getX()) {
            b2body.applyLinearImpulse(new Vector2(getSpeed() * .5f, 0), b2body.getWorldCenter(), true);
        } else if (getSprite().getX() > player.getSprite().getX()) {
            b2body.applyLinearImpulse(new Vector2(-getSpeed() * .5f, 0), b2body.getWorldCenter(), true);
        } else {
            if (player.isClimbing()) {
                world.setGravity(new Vector2(0, 0));
            }
        }
    }
}
