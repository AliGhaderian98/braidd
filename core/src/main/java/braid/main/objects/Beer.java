package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final int xBounds;

    public Beer(float x, float y, Player player, TextureRegion region, World world, int xbounds) {
        this.world = world;
        this.xBounds = xbounds;
        position = new Vector2(x, y);
        destroyed = false;
        toBeDestroyed = false;


        Vector2 playerPosition = new Vector2(player.b2body.getPosition().x* Braid.PPM, player.b2body.getPosition().y * Braid.PPM);
        Vector2 startPosition = new Vector2(x, y);
        velocity = playerPosition.sub(startPosition).nor().scl(10f);

        defineBody();

        sprite = new Sprite(region);
        sprite.setBounds(0,0,20/Braid.PPM, 20/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);
    }

    private void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / Braid.PPM, position.y / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(3 / Braid.PPM, 5 / Braid.PPM);
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.isSensor = true;
        Fixture fixture = b2body.createFixture(fdef);
        fixture.setUserData(new UserData("beerbottle", this));

        b2body.setLinearVelocity(velocity.x, velocity.y);
    }

    public void update(float dt) {
        if (toBeDestroyed && !destroyed || isOutOfBounds()) {
            destroy();
            return;
        }

        if (!destroyed) {
            position.set(b2body.getPosition().x * Braid.PPM, b2body.getPosition().y * Braid.PPM);
            sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);
        }
    }

    private boolean isOutOfBounds() {
        return b2body != null && (b2body.getPosition().x < 0 ||
            b2body.getPosition().x > xBounds ||
            b2body.getPosition().y < 0);
    }

    public void draw(SpriteBatch batch) {
        if (b2body != null && !destroyed) {
            sprite.draw(batch);
        }
    }

    public void markForDestroy() {
        toBeDestroyed = true;
    }

    private void destroy() {
        if ((b2body != null && !destroyed) || isOutOfBounds()) {
            b2body.getWorld().destroyBody(b2body);
            b2body = null;
            destroyed = true;
        }
    }
}
