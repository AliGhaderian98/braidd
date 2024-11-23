package braid.main.tools;

import braid.main.Braid;
import braid.main.objects.Ladder;
import com.badlogic.gdx.maps.MapLayer;
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
import org.w3c.dom.css.Rect;

public class B2WorldCreator {
    private Array<Ladder> ladders;

    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        // create ground bodies and fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Braid.PPM, (rect.getY() + rect.getHeight() / 2) / Braid.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Braid.PPM, (rect.getHeight() / 2) / Braid.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        // Create ladders
        ladders = new Array<>();
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        for (MapLayer layer : map.getLayers()) {
            if ("Leiter".equals(layer.getName()) && layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x,y);
                        if (cell == null || cell.getTile() == null) {
                            continue;
                        }
                        Rectangle rect = new Rectangle(x*tileWidth, y*tileHeight, tileWidth, tileHeight);
                        ladders.add(new Ladder(world,map,rect));
                    }
                }
            }
        }
    }
}
