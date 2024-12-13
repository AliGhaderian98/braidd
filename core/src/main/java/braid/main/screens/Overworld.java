package braid.main.screens;

import braid.main.Braid;
import braid.main.Items.Item;
import braid.main.objects.Enemy;
import braid.main.objects.OverworldPlayer;
import braid.main.tools.B2WorldCreator;
import braid.main.tools.GameCamera;
import braid.main.tools.KeyBindings;
import braid.main.tools.OverworldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public class Overworld implements Screen {

    private final Braid game;
    private final TextureAtlas atlas;
    public static boolean gameIsPaused = false;
    public static boolean debugRendererEnabled = true;

    // Camera and Map variables
    private final OverworldCamera camera;
    private final TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    // Box2D variables
    private final World world;
    private final Box2DDebugRenderer b2dr;

    private Stage stage;

    private OverworldPlayer player;

    public Overworld(Braid game) {
        this.game = game;
        atlas = new TextureAtlas("packedimages/sprites.atlas");

        // Load current keybindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup level map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/overworld-map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // World setup
        world = new World(new Vector2(0, 0), true);

        // B2WorldCreator
        b2dr = new Box2DDebugRenderer();

        stage = new Stage();
        player = new OverworldPlayer(this);
        stage.addActor(player);

        // Final setup steps
        camera = new OverworldCamera(25*16, 25*9, player);
        camera.setMap(map);

        setupInput();
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {
                // your touch down code here
                return true; // return true to indicate the event was handled
            }

            @Override
            public boolean keyUp (int keycode) {
                // your touch up code here
                return true; // return true to indicate the event was handled
            }
        });

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());

        KeyBindings.loadKeyBindings();

        stage.act(delta);
        updateCamera();

        clearScreen();
        renderWorld();

    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void renderWorld() {
        renderer.setView(camera.getCamera());
        renderer.render();
        if (debugRendererEnabled)
            b2dr.render(world, camera.getCamera().combined);
        game.batch.setProjectionMatrix(camera.getCamera().combined);

        // draw game objects
        game.batch.begin();

        player.getSprite().draw(game.batch);

        game.batch.end();
    }

    private void updateCamera() {
        camera.followTarget();
        camera.getCamera().update();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();

        world.dispose();
        b2dr.dispose();
    }

    // Getters and setters


    public TextureAtlas getAtlas() {
        return atlas;
    }
}
