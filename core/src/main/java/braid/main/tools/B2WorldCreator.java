package braid.main.tools;

import braid.main.Braid;
import braid.main.objects.Ladder;
import braid.main.objects.End;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class B2WorldCreator {
    private Array<Ladder> ladders;
    private Array<End> ends;
    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // create ground bodies and fixtures
        MapLayer groundLayer = map.getLayers().get("Ground");
        for (MapObject object : groundLayer.getObjects()) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Braid.PPM, (rect.getY() + rect.getHeight() / 2) / Braid.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Braid.PPM, (rect.getHeight() / 2) / Braid.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // Create Ladder Objects
        ladders = new Array<>();
        MapLayer ladderLayer =  map.getLayers().get("Ladder");
        for (MapObject object : ladderLayer.getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            Ladder ladder = new Ladder(world,map,rect);
            ladders.add(ladder);
        }
        // Create End Object
        ends = new Array<>();
        MapLayer endLayer =  map.getLayers().get("End");
        for (MapObject object : endLayer.getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            End end = new End(world,map,rect);
            ends.add(end);
        }
    }
}
