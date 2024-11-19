package braid.main.screens;

import braid.main.Braid;
import braid.main.tools.B2WorldCreator;
import braid.main.tools.GameCamera;
import braid.main.Rewind;
import braid.main.RewindableSprite;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TestScreen implements Screen {
    // World Variables
    private final Braid game;
    private static final int GRAVITY = 1;
    private TextureAtlas atlas;

    // Screen
    private final int worldWidth = Braid.V_WIDTH;
    private final int worldHeight = Braid.V_HEIGHT;

    // Camera
    //private GameCamera gameCamera;
    private OrthographicCamera gamecam;
    private Viewport gamePort;

    // Map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2D variables
    private World world;
    private Box2DDebugRenderer b2dr;


    // Rewind Control
    private Array<Rewind> rewindObjects;

    // Keys
    static int RIGHT_KEY = Input.Keys.D;
    static int LEFT_KEY = Input.Keys.A;
    static int SHIFT = Input.Keys.SHIFT_LEFT;
    static int SPACEBAR = Input.Keys.SPACE;

    // GameObject Variables
    private Player player;
    private Enemy enemy;

    public TestScreen(Braid game) {
        // Setup basic world variables
        atlas = new TextureAtlas("packedimages/lion.atlas");
        this.game = game;

        // Setup Game Camera
        //gameCamera = new GameCamera(Braid.V_WIDTH, Braid.V_HEIGHT);
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(worldWidth/Braid.PPM, worldHeight/Braid.PPM, gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/Braid.PPM); // 3 als scaling factor, weil es irgendwie passt?
        //gameCamera.setMap(map);
        gamecam.position.set((float) gamePort.getWorldWidth()/2, (float) gamePort.getWorldHeight()/2, 0);

        // setup Box2D world
        world = new World(new Vector2(0,-10), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map);

        // Setup Player
        Texture testTexture = new Texture("löwe.png");
        //player = new Player(testTexture);
        player = new Player(world, this);
        player.setPosition(0,0);
        player.setRewindController(new Rewind(new RewindableSprite(player.getSprite())));


        // Setup Enemy
        //enemy = new Enemy(testTexture);
        enemy = new Enemy(world, this);
        enemy.setPosition(worldWidth - 5 * enemy.getSprite().getWidth(), 0);
        enemy.setRewindController(new Rewind(new RewindableSprite(enemy.getSprite()))); //Später: testen was passiert wenn wir das bei einem weglassen (damit wir später non-rewind Objekte testen koennen)

        // Add all rewindable objects to Watcher
        rewindObjects = new Array<Rewind>();
        rewindObjects.add(player.getRewindController());
        rewindObjects.add(enemy.getRewindController());
    }



    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        update(delta);

        clearScreen();
        renderWorld();

        input();
        logic();
    }

    public void update(float dt) {
        world.step(1/60f, 6, 2);
        updateCamera();
    }

    private void clearScreen() {
        ScreenUtils.clear(0,0,0,1);
    }

    private void updateCamera() {
        // gameCamera.followTarget(player.getSprite().getX());
        gamecam.position.x = player.getSprite().getX();
        gamecam.update();
        renderer.setView(gamecam);
    }

    private void renderWorld() {
        //renderer.setView(gameCamera.getCamera());

        renderer.render();
        b2dr.render(world, gamecam.combined);

        //game.batch.setProjectionMatrix(gameCamera.getCamera().combined);
        game.batch.setProjectionMatrix(gamecam.combined);

        game.batch.begin();
        player.getSprite().draw(game.batch);
        enemy.getSprite().draw(game.batch);
        game.batch.end();
    }
    private void input() {
        if (Gdx.input.isKeyPressed(RIGHT_KEY)) {
            player.getSprite().translateX(player.getSpeed());
        }
        if (Gdx.input.isKeyPressed(LEFT_KEY)) {
            player.getSprite().translateX(-player.getSpeed());
        }
        if (Gdx.input.isKeyJustPressed(SPACEBAR) && !player.isJumping()) {
            player.jump();
        }

        if (enemy.getSprite().getX() < player.getSprite().getX()) {
            enemy.getSprite().translateX(enemy.getSpeed());
        } else if (enemy.getSprite().getX() > player.getSprite().getX()) {
            enemy.getSprite().translateX(-enemy.getSpeed());
        }


        if (player.isJumping()) {
            int currentVelocityY = player.getVelocityY(); //temporäre Lösung -> geht wahrscheinlich einfacher
            player.getSprite().translateY(player.getVelocityY());
            player.setVelocityY(currentVelocityY - GRAVITY);
        }

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

        for (Rewind r : rewindObjects) {
            r.update();
        }
        //player.getRewindController().update(); // Updates für Rewind-Mechanik in jedem Frame
        //enemy.getRewindController().update();
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
        //gameCamera.resize(width,height);
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public TextureAtlas getAtlas() { return atlas; }

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
}
