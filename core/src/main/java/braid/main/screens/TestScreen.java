package braid.main.screens;

import braid.main.*;
import braid.main.Items.CollectableItem;
import braid.main.Items.Item;
import braid.main.Items.ItemDef;
import braid.main.tools.*;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

public class TestScreen implements Screen {
    // World Variables
    private final Braid game;
    private static final int GRAVITY = -10;
    private final TextureAtlas atlas;
    public static boolean gameIsPaused;


    // Tools
    private final PlayerInputHandler inputHandler;

    // Screen
    private final int worldWidth = Braid.V_WIDTH;
    private final int worldHeight = Braid.V_HEIGHT;

    // Camera
    private final GameCamera gameCamera;

    // Map
    private TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    // Music
    private  Music Wintermusic;

    // Box2D variables
    private final World world;
    private final Box2DDebugRenderer b2dr;

    // Rewind Control
    private final Array<RewindController> rewindObjects;

    // Keys
    static int SHIFT = Input.Keys.SHIFT_LEFT;


    // GameObject Variables
    private final Player player;
    private final Enemy enemy;
    private Array <Item> items;
    private PriorityQueue <ItemDef> itemsToSpawn;


    public TestScreen(Braid game) {
        // Setup basic world variables
        atlas = new TextureAtlas("packedimages/sprites.atlas");
        this.game = game;

        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup Level Map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/wintermap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // setup Box2D world
        world = new World(new Vector2(0, GRAVITY), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map);

        // Setup Player
        player = new Player(world, this);
        player.setRewindController(new RewindController(new RewindableBody(player.b2body, player)));

        //
        items =new  Array <Item>();
        itemsToSpawn = new PriorityQueue<ItemDef>();

        // Setup Music
        Wintermusic = Audiomanager.audiomanager.get("audio/music/background_music.mp3",Music.class);
        Wintermusic.setLooping(true);
        Wintermusic.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        Wintermusic.play();

        // Setup Game Camera
        gameCamera = new GameCamera(Braid.V_WIDTH, Braid.V_HEIGHT, player);
        gameCamera.setMap(map);

        // Setup Enemy
        enemy = new Enemy(world, this);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body, enemy)));

        //Copy-Paste für Rewind:
        //das.setRewindController(new RewindController(new RewindableBody(das.b2body)));

        // Add all rewindable objects to Watcher
        rewindObjects = new Array<>();
        rewindObjects.add(player.getRewindController());
        rewindObjects.add(enemy.getRewindController());

        world.setContactListener(new WorldContactListener(player));

        // Initialisiere den InputHandler
        inputHandler = new PlayerInputHandler(player,enemy,world,game, rewindObjects);
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == CollectableItem.class) {
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

        inputHandler.handleInput();
    }

    public void update(float dt) {
        KeyBindings.loadKeyBindings();
        //stop rendering if game is Paused
        if(!gameIsPaused && player.isAlive()) {
            KeyBindings.loadKeyBindings();
            handleSpawningItems();
            // Update world physics
            world.step(dt, 6, 2);

            // Update Entities
            player.update(dt);
            enemy.update(dt);

            for(Item item : items)
                item.update(dt);

            // Update Camera
            updateCamera();

            for (RewindController r : rewindObjects) {
                r.update();
            }
        }
        else {
            if(Gdx.input.isKeyPressed(KeyBindings.getKey("SHIFT"))) {
                player.setAlive(true);
                gameIsPaused = false;
            }
        }
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void updateCamera() {
        gameCamera.followTarget();
        gameCamera.getCamera().update();
    }

    private void renderWorld() {
        renderer.setView(gameCamera.getCamera());

        renderer.render();
        b2dr.render(world, gameCamera.getCamera().combined);

        game.batch.setProjectionMatrix(gameCamera.getCamera().combined);

        game.batch.begin();
        player.getSprite().draw(game.batch);
        if (!enemy.isDead()) { //Hört auf Sprite zu malen, wenn Enemy stirbt (lieber in Enemy Datei?)
            enemy.getSprite().draw(game.batch);
        }
        game.batch.end();

        for(Item item :items)
            item.draw(game.batch);
    }

    @Override
    public void resize(int width, int height) {
        gameCamera.resize(width, height);
    }

    @Override
    public void pause() {
        // pause Game
        gameIsPaused = true;
        Wintermusic.pause();

        // jumps to PauseScreen and saves Game state
        game.setScreen(new PauseMenu(game,this));
    }

    @Override
    public void resume() {
        gameIsPaused = false;
        Wintermusic.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        //System.out.println(Wintermusic.getVolume());
        //System.out.println(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        Wintermusic.play();

    }


    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void hide() {
    }

    public void reset(){
        gameIsPaused = false;
        // reset Screen
        game.setScreen(new TestScreen(game));
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();

        world.dispose();
        b2dr.dispose();
    }

    public World getWorld() {
        return world;
    }
}
