package braid.main.screens;

import braid.main.*;
import braid.main.Items.CollectableItem;
import braid.main.Items.Item;
import braid.main.Items.ItemDef;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.tools.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
 Basisklasse für Level, die grundlegende Funktionen bereitstellt, die von allen
 Levelklassen benötigt werden.
 ***********/
public abstract class LevelScreen implements Screen {
    protected final Braid game;
    private static final int GRAVITY = -10;
    protected final TextureAtlas atlas;
    public static boolean gameIsPaused;
    public static boolean resetgame;

    protected final PlayerInputHandler inputHandler;

    protected final GameCamera gameCamera;

    protected final TmxMapLoader mapLoader;
    protected final TiledMap map;
    protected final OrthogonalTiledMapRenderer renderer;

    protected final World world;
    protected final Box2DDebugRenderer b2dr;

    protected Array<RewindController> rewindObjects;


    protected final Player player;
    protected Array<Enemy> enemies;
    protected Array<Item> items;
    protected PriorityQueue<ItemDef> itemsToSpawn;


    protected static final int SHIFT = Input.Keys.SHIFT_LEFT;

    public LevelScreen(Braid game, String mapPath, String atlasPath) {
        this.game = game;
        atlas = new TextureAtlas(atlasPath);

        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();


        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);


        world = new World(new Vector2(0, GRAVITY), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map);


        player = new Player(world, this);
        player.setRewindController(new RewindController(new RewindableBody(player.b2body, player)));

        enemies = new Array<>();

        rewindObjects = new Array<>();

        items = new Array<>();
        itemsToSpawn = new PriorityQueue<>();

        gameCamera = new GameCamera(Braid.V_WIDTH, Braid.V_HEIGHT, player);
        gameCamera.setMap(map);

        world.setContactListener(new WorldContactListener(player));
        inputHandler = new PlayerInputHandler(player, world, game, rewindObjects);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == CollectableItem.class) {
                items.add(new CollectableItem(this, idef.position.x, idef.position.y));
            }
        }
    }
    public World getWorld() {
        return world;
    }

    public void update(float dt) {
        if (!gameIsPaused) {
            handleSpawningItems();
            world.step(dt, 6, 2);

            player.update(dt);

            for (Enemy enemy : enemies) {
                enemy.update(dt);
            }

            for (Item item : items) {
                item.update(dt);
            }

            updateCamera();

            for (RewindController r : rewindObjects) {
                r.update();
            }

            resetIfNecessary();
        } else if (Gdx.input.isKeyPressed(SHIFT)) {
            gameIsPaused = false;
        }
    }

    private void updateCamera() {
        gameCamera.followTarget();
        gameCamera.getCamera().update();
    }

    private void resetIfNecessary() {
        if (resetgame) {
            resetgame = false;
            game.setScreen(getNewInstance());
        }
    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());
        update(delta);

        ScreenUtils.clear(0, 0, 0, 1);

        renderer.setView(gameCamera.getCamera());
        renderer.render();
        b2dr.render(world, gameCamera.getCamera().combined);

        game.batch.setProjectionMatrix(gameCamera.getCamera().combined);

        game.batch.begin();
        player.getSprite().draw(game.batch);

        for (Enemy enemy : enemies)
        {
            if (!enemy.isDead()) {
                enemy.getSprite().draw(game.batch);
            }
        }
        game.batch.end();

        for (Item item : items) {
            item.draw(game.batch);
        }

        inputHandler.handleInput();
    }

    @Override
    public void pause() {
        gameIsPaused = true;
        game.setScreen(new PauseScreen(game, this));
    }

    @Override
    public void resume() {
        gameIsPaused = false;
    }

    @Override
    public void resize(int width, int height) {
        gameCamera.resize(width, height);
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    protected abstract LevelScreen getNewInstance();
}
