package braid.main.objects;

import braid.main.Braid;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;


public class InteractiveObject {

    protected World world;
    protected TiledMap map;
    protected Rectangle boundary;
    protected Body body;
    protected Fixture fixture;

    public InteractiveObject(World world, TiledMap map, Rectangle boundary, boolean isSensor) {
        this.world = world;
        this.map = map;
        this.boundary = boundary;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((boundary.getX() + boundary.getWidth() / 2) / Braid.PPM, (boundary.getY() + boundary.getHeight() / 2) / Braid.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(boundary.getWidth() / 2 / Braid.PPM, boundary.getHeight() / 2 / Braid.PPM);
        fdef.shape = shape;
        fdef.isSensor = isSensor;
        fixture = body.createFixture(fdef);

        shape.dispose();
    }
}
