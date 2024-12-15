package braid.main.tools;

import braid.main.Braid;
import braid.main.Items.CollectableItem;
import braid.main.objects.Ladder;
import braid.main.objects.End;
import braid.main.objects.MovingPlatform;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.logging.Level;

public class B2WorldCreator {
    private Array<Ladder> ladders;
    private final Array<MovingPlatform> movingPlatforms = new Array<>();
   // private  Array<MovingPlatform> movingPlatforms;
    private Array<End> ends;
    private final World world;
    private final TiledMap map;
    private final LevelScreen screen;
    private BodyDef bdef;
    private PolygonShape shape;
    private FixtureDef fdef;
    private Body body;

    public B2WorldCreator(World world, TiledMap map, LevelScreen screen) {
        this.map = map;
        this.world = world;
        this.screen = screen;



        bdef = new BodyDef();
        shape = new PolygonShape();
        fdef = new FixtureDef();


        spawnGround();
        spawnWalls();
        spawnLadders();
        spawnEnemies();
        spawnMovingPlatform();
        spawnItems();
    }

    private void spawnGround() {
        MapLayer groundLayer = map.getLayers().get("Ground");
        for (MapObject object : groundLayer.getObjects()) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Braid.PPM, (rect.getY() + rect.getHeight() / 2) / Braid.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Braid.PPM, (rect.getHeight() / 2) / Braid.PPM);
            fdef.shape = shape;
            Fixture groundFixture = body.createFixture(fdef);
            groundFixture.setUserData(new UserData("Ground", this));
        }
    }

    private void spawnWalls() {
        MapLayer wallLayer = map.getLayers().get("Walls");
        for (MapObject object : wallLayer.getObjects()) {

            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Braid.PPM, (rect.getY() + rect.getHeight() / 2) / Braid.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Braid.PPM, (rect.getHeight() / 2) / Braid.PPM);
            fdef.shape = shape;
            Fixture wallFixture = body.createFixture(fdef);
            wallFixture.setUserData(new UserData("Wall", this));
        }
    }

    private void spawnLadders() {
        MapLayer ladderLayer = map.getLayers().get("Ladder");
        if (ladderLayer != null) {
            for (MapObject object : ladderLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                new Ladder(world, map, rect);
            }
        }
        // Create End Object
        ends = new Array<>();
        MapLayer endLayer = map.getLayers().get("End");
        if (endLayer != null) {
            for (MapObject object : endLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                End end = new End(world, map, rect);
                ends.add(end);
            }
        }
    }

    private void spawnMovingPlatform() {
        MapLayer movingPlatformLayer = map.getLayers().get("MovingPlatform");

        for (MapObject object : movingPlatformLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                float rangeX = Float.parseFloat(object.getProperties().get("rangeX", "100", String.class));
                float speed = Float.parseFloat(object.getProperties().get("speed", "1.5", String.class));

               MovingPlatform movingPlatform = new MovingPlatform(world, map, rect, rangeX, speed);
              // movingPlatforms.add(movingPlatform);
                screen.addMovingPlatform(new MovingPlatform(world, map, rect, speed, rangeX));


            }
        }

    }

        private void spawnEnemies() {
            if (map.getLayers().get("Enemies") == null) return;

            MapLayer enemyLayer = map.getLayers().get("Enemies");

            for (MapObject object : enemyLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                // Position of the enemy
                float x = (rect.getX() + rect.getWidth() / 2);
                float y = (rect.getY() + rect.getHeight() / 2);

                // Type of the enemy
                String type = (String) object.getProperties().get("type");

                // Spawning enemy
                if ("MadScientist".equals(type)) {
                    screen.spawnEnemy("MadScientist", x, y);
                } else if ("PatrollingEnemy".equals(type)) {
                    screen.spawnEnemy("PatrollingEnemy", x, y);
                } else if ("UnhingedEnemy".equals(type)) {
                    screen.spawnEnemy("UnhingedEnemy", x, y);
                }
            }
        }





    private void spawnItems() {
        if (map.getLayers().get("Items") == null) return;

        MapLayer itemsLayer = map.getLayers().get("Items");

        for (MapObject object : itemsLayer.getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // Position of the enemy
            float x = (rect.getX() + rect.getWidth() / 2);
            float y = (rect.getY() + rect.getHeight() / 2);


            // Type of the enemy
            String type = (String) object.getProperties().get("type");

            // Spawning enemy
            if ("Page".equals(type)) {
                screen.addItem(new CollectableItem(screen, x, y));
            }
        }
    }
}
