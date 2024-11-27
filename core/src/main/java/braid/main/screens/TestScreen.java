package braid.main.screens;

import braid.main.Braid;
import braid.main.Items.CollectableItem;
import braid.main.Items.Item;
import braid.main.Items.ItemDef;
import braid.main.Rewind;
import braid.main.RewindableSprite;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.tools.B2WorldCreator;
import braid.main.tools.GameCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.PriorityQueue;

/***********
 Diese Klasse ist eine Testklasse, um die grundlegen Funktionen zu implementieren.
 Sie stellt zudem einen Entwurf für die späteren Level Klassen dar.
 ***********/

public class  TestScreen implements Screen {
    private static final int GRAVITY = 1;
    // Keys
    static int RIGHT_KEY = Input.Keys.D;
    static int LEFT_KEY = Input.Keys.A;
    static int SHIFT = Input.Keys.SHIFT_LEFT;
    static int SPACEBAR = Input.Keys.SPACE;
    // World Variables
    private final Braid game;
    // Screen
    private final int worldWidth = Braid.V_WIDTH;
    private final int worldHeight = Braid.V_HEIGHT;
    private final TextureAtlas atlas;
    // Camera
    private final GameCamera gameCamera;
    // Map
    private final TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    // Box2D variables
    private final World world;
    private final Box2DDebugRenderer b2dr;
    // Rewind Control
    private final Array<Rewind> rewindObjects;
    // GameObject Variables
    private final Player player;
    private final Enemy enemy;
    private Array <Item> items;
    private PriorityQueue <ItemDef> itemsToSpawn;


    public TestScreen(Braid game) {
        // Setup basic world variables
        atlas = new TextureAtlas("packedimages/lion.atlas");
        this.game = game;

        // Setup Game Camera
        gameCamera = new GameCamera(Braid.V_WIDTH, Braid.V_HEIGHT);

        // Setup Level Map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // setup Box2D world
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map);

        // Setup Player
        player = new Player(world, this);
        player.setRewindController(new Rewind(new RewindableSprite(player.getSprite())));
        //
        items =new  Array <Item>();
        itemsToSpawn = new PriorityQueue<ItemDef>();



        // Setup Enemy
        enemy = new Enemy(world, this);
        enemy.setRewindController(new Rewind(new RewindableSprite(enemy.getSprite()))); //Später: testen was passiert wenn wir das bei einem weglassen (damit wir später non-rewind Objekte testen koennen)

        // Add all rewindable objects to Watcher
        rewindObjects = new Array<Rewind>();
        rewindObjects.add(player.getRewindController());
        rewindObjects.add(enemy.getRewindController());

    }
    public void spwanItem (ItemDef idef){
        itemsToSpawn.add(idef);
    }
    public void handleSpwaningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == CollectableItem.class){
                items.add(new CollectableItem(this, idef.position.x,idef.position.y));
            }
        }
    }


    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());
        update(delta);

        clearScreen();
        renderWorld();

        input();
        logic();
    }

    public void update(float dt) {
        handleSpwaningItems();
        // Update world physics
        world.step(dt, 6, 2);

        // Update Player and Enemies
        player.update(dt);
        enemy.update(dt);

        // Update Camera
        updateCamera();
        for(Item item : items)
            item.update(dt);
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void updateCamera() {
        gameCamera.followTarget(player.getSprite().getX());
    }

    private void renderWorld() {
        renderer.setView(gameCamera.getCamera());

        renderer.render();
        b2dr.render(world, gameCamera.getCamera().combined);

        game.batch.setProjectionMatrix(gameCamera.getCamera().combined);

        game.batch.begin();
        player.getSprite().draw(game.batch);
        enemy.getSprite().draw(game.batch);
        game.batch.end();
        for(Item item :items)
            item.draw(game.batch);
    }

    private void input() {
        // handle input for the player
        if (Gdx.input.isKeyPressed(RIGHT_KEY) || Gdx.input.isKeyPressed(LEFT_KEY)) {
            if (Gdx.input.isKeyPressed(RIGHT_KEY) && Math.abs(player.b2body.getLinearVelocity().x) <= player.getSpeed()) {
                player.b2body.applyLinearImpulse(new Vector2(player.getSpeed() * .5f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(LEFT_KEY) && Math.abs(player.b2body.getLinearVelocity().x) <= player.getSpeed()) {
                player.b2body.applyLinearImpulse(new Vector2(-player.getSpeed() * .5f, 0), player.b2body.getWorldCenter(), true);
            }
        } else {
            player.b2body.setLinearVelocity(0, player.b2body.getLinearVelocity().y);
        }

        if (Gdx.input.isKeyJustPressed(SPACEBAR) && !player.isJumping()) {
            player.jump();
            player.b2body.applyLinearImpulse(new Vector2(0, player.getJumpSpeed()), player.b2body.getWorldCenter(), true);
        }

        // move enemy based on the position of the player
        if (enemy.getSprite().getX() < player.getSprite().getX()) {
            enemy.b2body.applyLinearImpulse(new Vector2(enemy.getSpeed() * .5f, 0), enemy.b2body.getWorldCenter(), true);
        } else if (enemy.getSprite().getX() > player.getSprite().getX()) {
            enemy.b2body.applyLinearImpulse(new Vector2(-enemy.getSpeed() * .5f, 0), enemy.b2body.getWorldCenter(), true);
        }


        //if (player.isJumping()) {
        //    int currentVelocityY = player.getVelocityY(); //temporäre Lösung -> geht wahrscheinlich einfacher
        //    player.getSprite().translateY(player.getVelocityY());
        //    player.setVelocityY(currentVelocityY - GRAVITY);
        //}

        // Zeitmechanik für Rewind-Funktion
        if (Gdx.input.isKeyPressed(SHIFT)) {
            for (Rewind r : rewindObjects) {
                r.startRewinding();
            }

            // einfache Verfärbung der Sprites, um Rewind visuell deutlich zu machen
            if (player.getRewindController().hasRewindStorage()) {
                player.setColor(Color.BLUE);
            } else {
                player.setColor(Color.WHITE);
            }
            if (enemy.getRewindController().hasRewindStorage()) {
                enemy.setColor(Color.BLUE);
            } else {
                //enemy.getSprite().setColor(Color.WHITE);
                enemy.setColor(Color.WHITE);
            }
        } else {
            for (Rewind r : rewindObjects) {
                r.stopRewinding();
            }
            player.setColor(Color.WHITE);
            enemy.setColor(Color.WHITE);
        }

        // Update Informationen aus diesem Frame für alle gespeicherten Rewind Objekte
        for (Rewind r : rewindObjects) {
            r.update();
        }
    }

    private void logic() {
        if (player.getSprite().getY() <= 0) {
            player.land();
            player.setVelocityY(0);
            player.getSprite().setY(0);
        }
        if (player.getSprite().getX() <= 0) {
            player.getSprite().setX(0);
        }
        if (player.getSprite().getX() + player.getSprite().getWidth() >= worldWidth) {
            player.getSprite().setX(worldWidth - player.getSprite().getWidth());
        }

    }


    @Override
    public void resize(int width, int height) {
        gameCamera.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();

        world.dispose();
        b2dr.dispose();

        player.getTexture().dispose();
    }

    public World getWorld() {
        return world;
    }
}
