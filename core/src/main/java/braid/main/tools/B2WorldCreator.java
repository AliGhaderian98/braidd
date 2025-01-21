package braid.main.tools;

import braid.main.Braid;
import braid.main.Items.CollectableItem;
import braid.main.Items.Key;
import braid.main.Items.PowerUp;
import braid.main.objects.*;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Map;
import java.util.Objects;

import static braid.main.Items.PowerUp.TypeOfPowerUp.*;

public class B2WorldCreator {
    private Array<Ladder> ladders;
    private final Array<MovingPlatform> movingPlatforms = new Array<>();
   // private final Array<Schalter> schalters = new Array<>();
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
        spawnBricks();
        spawnLadders();
        spawnEnd();
        spawnEnemies();
        spawnMovingPlatform();
        spawnButtons();
        spawnItems();
        spawnSigns();
        spawnNPCs();
        spawnDoors();
    }

    private void spawnSigns() {
        MapLayer signLayer = map.getLayers().get("Signs");
        if (signLayer != null) {
            for (MapObject object : signLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                if (Objects.equals(object.getName(), "TutorialSign"))
                    new Sign(world, rect, screen.getSubtitleManager(),
                        (String) object.getProperties().get("Text"),
                        (String) object.getProperties().get("key"));
                else
                    new Sign(world, rect, screen.getSubtitleManager(), (String) object.getProperties().get("Text"));
            }
        }
    }

    private void spawnNPCs() {
        MapLayer npcLayer = map.getLayers().get("NPCs");
        if (npcLayer != null) {
            for (MapObject object : npcLayer.getObjects()) {
                if (Objects.equals(object.getName(), "NPC")) {
                    Rectangle npcBoundary = ((RectangleMapObject) object).getRectangle();

                    Rectangle npcTrigger = null;
                    Object npcTriggerObject = object.getProperties().get("npcTrigger");
                    if (npcTriggerObject instanceof RectangleMapObject) {
                        npcTrigger = ((RectangleMapObject) npcTriggerObject).getRectangle();
                    }
                    if(npcTrigger == null) {
                        npcTrigger = ((RectangleMapObject) object).getRectangle();
                    }

                    String text = (String) object.getProperties().get("text");
                    String type = (String) object.getProperties().get("Type");
                    String spriteName = (String) object.getProperties().get("Name");

                    screen.spawnNPC(type, npcBoundary, npcTrigger, text, spriteName);
                }

            }
        }
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
            float rotation = 0;
            if (object.getProperties().containsKey("rotation")) {
                rotation =  (float) object.getProperties().get("rotation");
            }

            bdef.type = BodyDef.BodyType.StaticBody;

                bdef.position.set(
                    (rect.getX() + rect.getWidth() / 2) / Braid.PPM,
                    (rect.getY() + rect.getHeight() / 2) / Braid.PPM

                );

            body = world.createBody(bdef);
                shape.setAsBox(
                    (rect.getWidth() / 2) / Braid.PPM,
                    (rect.getHeight() / 2) / Braid.PPM,
                    new Vector2(0,0),
                    (float) -Math.toRadians(rotation)
                );

            fdef.shape = shape;
            Fixture wallFixture = body.createFixture(fdef);
            wallFixture.setUserData(new UserData("Wall", this));
        }
    }

    private void spawnBricks() {
        MapLayer brickLayer = map.getLayers().get("Brick");
        TextureRegion brickRegion = screen.getAtlas().findRegion("brick");
        if(brickLayer != null) {
            for (MapObject object : brickLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                screen.addBrick(new Brick(world, rect, brickRegion, (boolean) object.getProperties().get("rewindable")));
            }
        }
    }

    private void spawnLadders() {
        MapLayer ladderLayer = map.getLayers().get("Ladder");
        if (ladderLayer != null) {
            for (MapObject object : ladderLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                new Ladder(world, rect);
            }
        }
    }
    private void spawnEnd() {
        ends = new Array<>();
        MapLayer endLayer = map.getLayers().get("End");
        if (endLayer != null) {
            for (MapObject object : endLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                End end = new End(world, rect);
                ends.add(end);
            }
        }
    }

    public void spawnMovingPlatform() {
        MapLayer movingPlatformLayer = map.getLayers().get("MovingPlatform");
        TextureRegion movingPlatformRegion = screen.getAtlas().findRegion("plattform");

        if (movingPlatformLayer == null || !movingPlatformLayer.isVisible()) return;

        for (MapObject object : movingPlatformLayer.getObjects()) {
            if (Objects.equals(object.getName(), "MovingPlatform")) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                float speed = (float) object.getProperties().get("speed");

                // create a new range type moving platform
                if (object.getProperties().get("rangeX") != null) {
                    float rangeX = (float) object.getProperties().get("rangeX");
                    screen.addMovingPlatform(new MovingPlatform(world, movingPlatformRegion, rect, speed, rangeX,
                        (boolean) object.getProperties().get("rewindable")));

                // create a new goal type moving platform
                } else {
                    RectangleMapObject goal = (RectangleMapObject) object.getProperties().get("goalPosition");
                    float goalPosX = goal.getRectangle().x+goal.getRectangle().getWidth()/2;
                    float goalPosY = goal.getRectangle().y+goal.getRectangle().getHeight()/2;

                    screen.addMovingPlatform(new MovingPlatform(world, movingPlatformRegion, rect, goalPosX, goalPosY,
                        speed, (boolean) object.getProperties().get("rewindable")));
                }

            // create moving platforms operated by a switch
            } else if (Objects.equals(object.getName(), "SchalterMovingPlatform")) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                float speed = (float) object.getProperties().get("speed");

                RectangleMapObject schalter = (RectangleMapObject) object.getProperties().get("schalter");
                float schalterPosX = schalter.getRectangle().x;
                float schalterPosY = schalter.getRectangle().y;

                RectangleMapObject goal = (RectangleMapObject) object.getProperties().get("goalPosition");
                float goalPosX = goal.getRectangle().x+goal.getRectangle().getWidth()/2;
                float goalPosY = goal.getRectangle().y+goal.getRectangle().getHeight()/2;

                screen.addSchalterMovingPlatform(new SchalterMovingPlatform(screen, world, movingPlatformRegion, rect, speed,
                     schalterPosX, schalterPosY, goalPosX, goalPosY, (boolean) object.getProperties().get("rewindable")));
            }
        }

    }

    private void spawnButtons() {
        MapLayer buttonLayer = map.getLayers().get("Buttons");
        if (buttonLayer != null){
            for(MapObject object : buttonLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                Rectangle boundary = ((RectangleMapObject) object).getRectangle() ;
                String actionType = (String) object.getProperties().get("actionType");
                String targetName = (String) object.getProperties().get("targetName");
                screen.spawnButton(boundary, actionType, targetName);
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
            String type = object.getName();

            // Spawning enemy
            if ("MadScientist".equals(type)) {
                screen.spawnEnemy("MadScientist", x, y,
                    (boolean) object.getProperties().get("rewindable"),
                    (String) object.getProperties().get("type"));
            } else if ("PatrollingEnemy".equals(type)) {
                screen.spawnEnemy("PatrollingEnemy", x, y,
                    (boolean) object.getProperties().get("rewindable"),
                    (String) object.getProperties().get("type"));
            } else if ("UnhingedEnemy".equals(type)) {
                screen.spawnEnemy("UnhingedEnemy", x, y,
                    (boolean) object.getProperties().get("rewindable"),
                    (String) object.getProperties().get("type"));
            } else if ("Knight".equals(type)) {
                screen.spawnEnemy("Knight", x, y,
                    (boolean) object.getProperties().get("rewindable"),
                    (String) object.getProperties().get("type"));
            } else if ("IdleEnemy".equals(type)) {
                screen.spawnEnemy("IdleEnemy", x, y,
                    (boolean) object.getProperties().get("rewindable"),
                    (String) object.getProperties().get("type"));
            }
        }
    }

    public void spawnItems() {
        MapLayer itemsLayer = map.getLayers().get("Items");
        if (itemsLayer == null || !itemsLayer.isVisible()) return;

        for (MapObject object : itemsLayer.getObjects()) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // Position of the Item
            float x = (rect.getX() + rect.getWidth() / 2);
            float y = (rect.getY() + rect.getHeight() / 2);


            // Type of the Item
            String type = (String) object.getName();

            // Spawning Item
            if ("Page".equals(type)) {
                Array<Boolean> savedCollectables = Savemanager.currentsavegame.Collectables.get(screen.getLevelName());
                int pageID = (Integer) object.getProperties().get("ID");
                if (pageID >= savedCollectables.size || !savedCollectables.get(pageID))
                    screen.addItem(new CollectableItem(screen, x, y, pageID));
            }
            if ("Gleiter".equals(type)) {
                screen.addItem((new PowerUp(screen, x, y, GLEITER)));
            }
            if ("Ritalin".equals(type)) {
                screen.addItem((new PowerUp(screen, x, y, RITALIN)));
            }
            if ("Hammer".equals(type)) {
                screen.addItem((new PowerUp(screen, x, y, HAMMER)));
            }
            if ("Key".equals(type)) {
                screen.addItem(new Key(screen, x, y));
            }
        }
    }

    private void spawnDoors() {
        MapLayer doorLayer = map.getLayers().get("Doors");
        if (doorLayer != null) {
            for (MapObject object : doorLayer.getObjects()) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                screen.addDoor(new Door(screen, rect));
            }
        }
    }
}
