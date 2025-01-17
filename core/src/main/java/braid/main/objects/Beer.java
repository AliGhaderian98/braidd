package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Beer {
    private Vector2 position;
    private Vector2 velocity;
    private Sprite sprite;
    private Body b2body;
    private boolean destroyed;
    private boolean toBeDestroyed;
    private final World world;

    public Beer(float x, float y, Player player, TextureRegion region, World world) {
        this.position = new Vector2(x, y);
        this.world = world;
        sprite = new Sprite(region);
        destroyed = false;
        toBeDestroyed = false;

        Vector2 playerPosition = new Vector2(player.b2body.getPosition().x* Braid.PPM, player.b2body.getPosition().y * Braid.PPM);
        Vector2 startPosition = new Vector2(x, y);
        this.velocity = playerPosition.sub(startPosition).nor().scl(10f);

        defineBody();
    }

    private void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / Braid.PPM, position.y / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2 / Braid.PPM, 4 / Braid.PPM);
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.isSensor = true;
        Fixture fixture = b2body.createFixture(fdef);
        fixture.setUserData(new UserData("beerbottle", this));

        b2body.setLinearVelocity(velocity.x, velocity.y);
    }

    public void update(float dt) {
        if (toBeDestroyed && !destroyed) {
            destroy();
            return;
        }

        if (!destroyed) {
            position.set(b2body.getPosition().x * Braid.PPM, b2body.getPosition().y * Braid.PPM);
            sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
        }
    }

    public void markForDestroy() {
        toBeDestroyed = true;
    }

    private void destroy() {
        if (b2body != null && !destroyed) {
            b2body.getWorld().destroyBody(b2body);
            b2body = null;
            destroyed = true;
        }
    }
}
