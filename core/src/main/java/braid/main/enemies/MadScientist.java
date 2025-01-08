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

public class MadScientist extends Enemy implements EnemyAI{
    private final Player player;
    private float stateTimer = 0;

    public MadScientist(World world, LevelScreen screen, Player player, float x, float y, boolean rewindable) {
        super(world, screen,x,y, rewindable);
        this.player = player;
        defineBody();

        speed = 0.15f;

        sprite = new Sprite(screen.getAtlas().findRegion("wissenschaftler"));

        idle = new Animation<>(0.1f, screen.getAtlas().findRegions("wissenschaftler"), Animation.PlayMode.LOOP);
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setRegion(idle.getKeyFrame(0, true));
    }

    public void update(float dt) {
        super.update(dt);
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
    }

    public TextureRegion getFrame(float dt){
        // currently only has idle animation
        stateTimer += dt;
        return idle.getKeyFrame(stateTimer, true);
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
        }
    }
}
