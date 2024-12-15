package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MovingPlatform extends InteractiveGameObject {

    private final float rangeX;
    private final float speed;
    private float startX;
    private boolean movingRight = true;
    private Rectangle boundary;

    public MovingPlatform(World world, TiledMap map, Rectangle boundary, float rangeX, float speed) {
        super(world, map, boundary, true);
        this.boundary = boundary;
        this.rangeX = rangeX / 2;
        this.speed = speed;
        this.startX = b2body.getPosition().x;

        fixture.setUserData(new UserData("MovingPlatform", this));
    }

    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((boundary.getX() + boundary.getWidth() / 2) / Braid.PPM,
            (boundary.getY() + boundary.getHeight() / 2) / Braid.PPM);
        bdef.type = BodyDef.BodyType.KinematicBody;

        b2body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boundary.getWidth() / 2 / Braid.PPM, boundary.getHeight() / 2 / Braid.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        fixture = b2body.createFixture(fdef);
        shape.dispose();
    }

    public void update(float dt) {
        float currentX = b2body.getPosition().x;
        if (movingRight && currentX > startX + rangeX) {
            movingRight = false;
        } else if (!movingRight && currentX < startX - rangeX) {
            movingRight = true;
        }

        float velocityX = movingRight ? speed : -speed;
        b2body.setLinearVelocity(velocityX, 0);



        System.out.println("CurrentX: " + currentX + ", StartX: " + startX + ", RangeX: " + rangeX + ", MovingRight: " + movingRight);
    }

    public void draw(Batch batch) {

        batch.draw(new Texture("path_to_platform_texture.png"),
            b2body.getPosition().x - boundary.getWidth() / 2 / Braid.PPM,
            b2body.getPosition().y - boundary.getHeight() / 2 / Braid.PPM,
            boundary.getWidth() / Braid.PPM,
            boundary.getHeight() / Braid.PPM);

    }
}
