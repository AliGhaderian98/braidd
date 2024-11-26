package braid.main.screens;

import braid.main.*;
import braid.main.tools.B2WorldCreator;
import braid.main.tools.GameCamera;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.tools.WorldContactListener;
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

/***********
  Diese Klasse ist eine Testklasse, um die grundlegen Funktionen zu implementieren.
  Sie stellt zudem einen Entwurf für die späteren Level Klassen dar.
 ***********/

public class TestScreen implements Screen {
    // World Variables
    private final Braid game;
    private static final int GRAVITY = -10;
    private TextureAtlas atlas;
    public static boolean gameIsPaused;
    public static boolean resetgame;


    // Screen
    private final int worldWidth = Braid.V_WIDTH;
    private final int worldHeight = Braid.V_HEIGHT;

    // Camera
    private GameCamera gameCamera;

    // Map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2D variables
    private World world;
    private Box2DDebugRenderer b2dr;


    // Rewind Control
    private Array<RewindController> rewindObjects;
    private RewindController rewindController;

    // Keys
    static int RIGHT_KEY = Input.Keys.D;
    static int LEFT_KEY = Input.Keys.A;
    static int UP_KEY = Input.Keys.W;
    static int DOWN_KEY = Input.Keys.S;
    static int SHIFT = Input.Keys.SHIFT_LEFT;
    static int SPACEBAR = Input.Keys.SPACE;
    static int ESC = Input.Keys.ESCAPE;


    // GameObject Variables
    private final Player player;
    private final Enemy enemy;


    public TestScreen(Braid game) {
        // Setup basic world variables
        atlas = new TextureAtlas("packedimages/lion.atlas");
        this.game = game;

        // Setup Level Map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/testmap2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // setup Box2D world
        world = new World(new Vector2(0, GRAVITY), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map);

        // Setup Player
        player = new Player(world, this);
        player.setRewindController(new RewindController(new RewindableBody(player.b2body)));

        // Setup Game Camera
        gameCamera = new GameCamera(Braid.V_WIDTH, Braid.V_HEIGHT, player);
        gameCamera.setMap(map);

        // Setup Enemy
        enemy = new Enemy(world, this);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body)));

        //Copy-Paste für Rewind:
        //das.setRewindController(new RewindController(new RewindableBody(das.b2body)));

        // Add all rewindable objects to Watcher
        rewindObjects = new Array<RewindController>();
        rewindObjects.add(player.getRewindController());
        rewindObjects.add(enemy.getRewindController());

        world.setContactListener(new WorldContactListener(player));
    }


    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        if(player == null || enemy == null){ return;}

        delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());
        update(delta);

        clearScreen();
        renderWorld();

        input();
        logic();
    }

    public void update(float dt) {
        //stop rendering if game is Paused
        if(!gameIsPaused) {

            // Update world physics
            if (!player.getRewindController().isRewinding()) {
                world.step(dt, 6, 2);
            }

            // Update Player and Enemies
            player.update(dt);
            enemy.update(dt);

            // Update Camera
            updateCamera();

            for (RewindController r : rewindObjects) {
                r.update();
            }
            // Reset Game if Player wants to retry (PauseScreen)
            ResetIfNecessary();
        }
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void updateCamera() {
        gameCamera.followTarget();
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

    }

    private void input() {
        // handle input for the player

        //if(player.getRewindController().isRewinding()){return;}, ich lass das nochmal hier für bugfixes
        if (Gdx.input.isKeyPressed(RIGHT_KEY) || Gdx.input.isKeyPressed(LEFT_KEY)) {
            if (Gdx.input.isKeyPressed(RIGHT_KEY) && !Gdx.input.isKeyPressed(LEFT_KEY) && Math.abs(player.b2body.getLinearVelocity().x) <= player.getSpeed()) {
                player.b2body.applyLinearImpulse(new Vector2(player.getSpeed() * .5f, 0), player.b2body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(LEFT_KEY) && !Gdx.input.isKeyPressed(RIGHT_KEY) && Math.abs(player.b2body.getLinearVelocity().x) <= player.getSpeed()) {
                player.b2body.applyLinearImpulse(new Vector2(-player.getSpeed() * .5f, 0), player.b2body.getWorldCenter(), true);
            }
        } else {
            player.b2body.setLinearVelocity(0, player.b2body.getLinearVelocity().y);
        }

        if (Gdx.input.isKeyJustPressed(SPACEBAR) && !player.isJumping()) {
            player.jump();
            player.b2body.applyLinearImpulse(new Vector2(0, player.getJumpSpeed()), player.b2body.getWorldCenter(), true);
        }

        if (Gdx.input.isKeyPressed(UP_KEY) && player.isAtLadder()) {
            // Climbing up the ladder
            player.b2body.setLinearVelocity(0, player.getClimbingSpeed()); // Only apply climbing speed
        } else if (Gdx.input.isKeyPressed(DOWN_KEY) && player.isAtLadder()) {
            // Climbing down the ladder
            player.b2body.setLinearVelocity(0, -player.getClimbingSpeed()); // Only apply climbing speed in the down direction
        }

        // move enemy based on the position of the player
        if (enemy.getSprite().getX() < player.getSprite().getX()) {
            enemy.b2body.applyLinearImpulse(new Vector2(enemy.getSpeed() * .5f, 0), enemy.b2body.getWorldCenter(), true);
        } else if (enemy.getSprite().getX() > player.getSprite().getX()) {
            enemy.b2body.applyLinearImpulse(new Vector2(-enemy.getSpeed() * .5f, 0), enemy.b2body.getWorldCenter(), true);
        } else {
            if (player.isClimbing()) {
                world.setGravity(new Vector2(0, 0)); // Funktioniert noch nicht, da player.getCurrentState nicht nach State.CLIMBING checkt
            }
        }

        // pause Game (game will be resumed in the PauseScreen)
        if (Gdx.input.isKeyPressed(ESC) && !gameIsPaused){
            pause();
        }

            //if (player.isJumping()) {
            //    int currentVelocityY = player.getVelocityY(); //temporäre Lösung -> geht wahrscheinlich einfacher
            //    player.getSprite().translateY(player.getVelocityY());
            //    player.setVelocityY(currentVelocityY );
            //}

            // Zeitmechanik für Rewind-Funktion
            if (Gdx.input.isKeyPressed(SHIFT)) {
                for (RewindController r : rewindObjects) {
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
                for (RewindController r : rewindObjects) {
                    r.stopRewinding();
                }
                player.setColor(Color.WHITE);
                enemy.setColor(Color.WHITE);

            // Update Informationen aus diesem Frame für alle gespeicherten Rewind Objekte
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
        // pause Game
        gameIsPaused = true;

        // jumps to PauseScreen and saves Game state
        game.setScreen(new PauseScreen(game,this));
    }

    @Override
    public void resume() {
        gameIsPaused = false;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void hide() {
    }

    public void ResetIfNecessary(){
        if (!resetgame) {
            return;
        }
        resetgame = false;
        // reset Screen
        game.setScreen(new TestScreen((Braid) game));
    }

    @Override
    public void dispose() {
        if(player.getTexture() != null){
            map.dispose();
            renderer.dispose();

            world.dispose();
            b2dr.dispose();

            player.getTexture().dispose();
        }

    }
}
