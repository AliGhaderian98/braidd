package braid.main.screens;

import braid.main.Braid;
import braid.main.GameCamera;
import braid.main.Rewind;
import braid.main.RewindableSprite;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TestScreen implements Screen {
    // World Variables
    private final Braid game;
    private static final int GRAVITY = 1;

    // Screen
    int worldWidth;
    int worldHeight;

    // Camera

    private final GameCamera gameCamera;
    private Viewport gamePort;

    // Map
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    TmxMapLoader loader;


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
        this.game = game;
        worldWidth = Braid.V_WIDTH;
        worldHeight = Braid.V_HEIGHT;

        // Setup Game Camera
        gameCamera = new GameCamera(Braid.V_WIDTH, Braid.V_HEIGHT);

        loader = new TmxMapLoader();
        map = loader.load("maps/testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 3); // 3 als scaling factor, weil es irgendwie passt?
        gameCamera.setMap(map);

        // Setup Player
        Texture testTexture = new Texture("löwe.png");
        player = new Player(testTexture);
        player.setPosition(0,0);
        player.setRewindController(new Rewind(new RewindableSprite(player.getSprite())));


        // Setup Enemy
        enemy = new Enemy(testTexture);
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
    public void render(float v) {
        clearScreen();
        updateCamera();
        renderWorld();
        input();
        logic();
    }


    private void clearScreen() {
        ScreenUtils.clear(0,0,0,1);
    }

    private void updateCamera() {
        gameCamera.followTarget(player.getSprite().getX());
    }

    private void renderWorld() {
        renderer.setView(gameCamera.getCamera());
        renderer.render();

        game.batch.setProjectionMatrix(gameCamera.getCamera().combined);

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
    public void resize(int height, int width) {
        gameCamera.resize(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();


        player.getTexture().dispose();
    }
}
