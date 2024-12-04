package braid.main.objects;

import braid.main.Braid;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.*;


public abstract class InteractiveGameObject extends GameObject {

    protected Fixture fixture;
    protected boolean isSensor;

    // constructor for box shaped objects
    public InteractiveGameObject(World world, TiledMap map, Rectangle boundary, boolean isSensor) {
        super(world);
        this.isSensor = isSensor;

        defineBody(boundary);
    }

    // constructor for circular objects
    public InteractiveGameObject(World world, TiledMap map, float radius, boolean isSensor, float posX, float posY) {
        super(world);
        this.isSensor = isSensor;

        defineBody(radius, posX, posY);
    }



    public void defineBody(Rectangle boundary) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((boundary.getX() + boundary.getWidth() / 2) / Braid.PPM, (boundary.getY() + boundary.getHeight() / 2) / Braid.PPM);

        b2body = world.createBody(bdef);

        shape.setAsBox(boundary.getWidth() / 2 / Braid.PPM, boundary.getHeight() / 2 / Braid.PPM);
        fdef.shape = shape;
        fdef.isSensor = isSensor;
        fixture = b2body.createFixture(fdef);

        shape.dispose();
    }

    public void defineBody(float radius, float posX, float posY) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(posX / Braid.PPM, posY / Braid.PPM);

        b2body = world.createBody(bdef);

        shape.setRadius(radius / Braid.PPM);
        fdef.shape = shape;
        fdef.isSensor = isSensor;
        fixture = b2body.createFixture(fdef);

        shape.dispose();
    }
}
