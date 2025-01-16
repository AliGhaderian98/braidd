package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.*;
import org.w3c.dom.css.Rect;


public abstract class InteractiveGameObject extends GameObject {

    protected Fixture fixture;
    protected boolean isSensor;


    public InteractiveGameObject(World world, Rectangle boundary, boolean isSensor) {
        super(world);
        this.isSensor = isSensor;

        defineBody(boundary);
    }


    public InteractiveGameObject(World world, float radius, boolean isSensor, float posX, float posY) {
        super(world);
        this.isSensor = isSensor;

        defineBody(radius, posX, posY);
    }


    public InteractiveGameObject(World world, Rectangle boundary, TextureRegion region, boolean isSensor) {
        super(world);
        this.isSensor = isSensor;

        defineBody(boundary, region);
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

    public void defineBody(Rectangle boundary, TextureRegion region) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((boundary.getX() + boundary.getWidth() / 2) / Braid.PPM,
            (boundary.getY() + boundary.getHeight() / 2) / Braid.PPM);
        bdef.type = BodyDef.BodyType.KinematicBody;

        b2body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(region.getRegionWidth() / 4f / Braid.PPM, region.getRegionHeight() / 4f / Braid.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = isSensor;

        fixture = b2body.createFixture(fdef);
        shape.dispose();
    }
}
