package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Beer {
    private Vector2 position;
    private Vector2 velocity;
    private Sprite sprite;
    private Body b2body;

    public Beer(float x, float y, float speedX, float speedY, TextureRegion region, World world) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(speedX, speedY);
        sprite = new Sprite(region);

        defineBody(world);
    }



    private void defineBody(World world) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / Braid.PPM, position.y / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2 / Braid.PPM, 4 / Braid.PPM);
        fdef.shape = shape;
        fdef.density = 1f;
        assert b2body != null;
        Fixture fixture = b2body.createFixture(fdef);
        fixture.setUserData(new UserData("beerbottle", this));
    }

    public void update(float dt) {
        position.add(velocity.x * dt, velocity.y * dt);
        sprite.setPosition(position.x, position.y);
    }




}
