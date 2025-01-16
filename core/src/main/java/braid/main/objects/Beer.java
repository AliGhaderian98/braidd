package braid.main.objects;

import braid.main.Braid;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Beer {
    private Vector2 position;
    private Vector2 velocity;
    private Sprite sprite;
    private Body b2body;

    public Beer(float x, float y, float speedX, float speedY, TextureAtlas atlas, World world) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(speedX, speedY);
        setSprite(atlas);
        defineBody(world);
    }

    private void setSprite(TextureAtlas atlas) {
        sprite = new Sprite(atlas.findRegion("bierflasche"));
        sprite.setBounds(position.x, position.y, sprite.getWidth(), sprite.getHeight());
    }

    private void defineBody(World world) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / Braid.PPM, position.y / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / Braid.PPM, 10 / Braid.PPM);  // Bierflasche als kleines Rechteck
        fdef.shape = shape;
        fdef.density = 1f;
        Fixture fixture = b2body.createFixture(fdef);
        fixture.setUserData("bierflasche");
    }

    public void update(float dt) {
        position.add(velocity.x * dt, velocity.y * dt);
        sprite.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public boolean isOutOfBounds(float screenWidth, float screenHeight) {
        return position.x < 0 || position.x > screenWidth || position.y < 0 || position.y > screenHeight;
    }
}
