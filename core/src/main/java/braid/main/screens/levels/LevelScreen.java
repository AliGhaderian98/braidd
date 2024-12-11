package braid.main.screens.levels;

import braid.main.*;
import braid.main.Items.CollectableItem;
import braid.main.Items.Item;
import braid.main.Items.ItemDef;
import braid.main.enemies.MadScientist;
import braid.main.enemies.PatrollingEnemy;
import braid.main.objects.Enemy;
import braid.main.objects.Player;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.screens.RewindHUD;
import braid.main.tools.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import braid.main.screens.menus.PauseMenu;

import java.util.PriorityQueue;

/***********
 Basisklasse für Level, die grundlegende Funktionen bereitstellt, die von allen
 Levelklassen benötigt werden.
 ***********/
public abstract class LevelScreen implements Screen {
    // Basic Game variables
    protected final Braid game;
    private static final int GRAVITY = -10;
    protected final TextureAtlas atlas;
    public static boolean gameIsPaused;

    protected final PlayerInputHandler inputHandler;

    // Camera and Map variables
    protected final GameCamera gameCamera;
    protected final TmxMapLoader mapLoader;
    protected final TiledMap map;
    protected final OrthogonalTiledMapRenderer renderer;

    // Box2D variables
    protected final World world;
    protected final Box2DDebugRenderer b2dr;

    // Game Objects
    protected final Player player;
    protected Array<Enemy> enemies;
    protected Array<Item> items;
    protected PriorityQueue<ItemDef> itemsToSpawn;

    public Array<RewindController> rewindObjects;

    // Music
    private final Music music;

    // Shader variables
    private ShaderProgram rewindShader;
    private ShaderProgram activeShader;
    private FrameBuffer fbo;
    private SpriteBatch fboBatch;
    private float time = 0f;
    private RewindHUD rewindHUD;


    public LevelScreen(Braid game, String mapPath, String atlasPath, String musicPath) {
        // Setup Game Variables
        this.game = game;
        atlas = new TextureAtlas(atlasPath);

        items = new Array<>();
        itemsToSpawn = new PriorityQueue<>();

        // Load current keybindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup level map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // World setup
        world = new World(new Vector2(0, GRAVITY), true);


        // Setup player
        player = new Player(world, this);
        player.setRewindController(new RewindController(new RewindableBody(player.b2body, player)));

        // Enemies
        enemies = new Array<>();

        // B2WorldCreator
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map, this);


        // Setup Music
        music = Audiomanager.audiomanager.get(musicPath ,Music.class);
        music.setLooping(true);
        music.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        music.play();


        rewindObjects = new Array<>();



        // Final setup steps
        gameCamera = new GameCamera(25*16, 25*9, player);
        gameCamera.setMap(map);

        world.setContactListener(new WorldContactListener(player));
        inputHandler = new PlayerInputHandler(player, world, game, this);
        setupShaders();
    }

    private void setupShaders() {
        // define shader program
        String vertexShader = Gdx.files.internal("shaders/standard.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/crt-rewind.frag").readString();
        rewindShader = new ShaderProgram(vertexShader, fragmentShader);
        ShaderProgram.pedantic = false;
        if (!rewindShader.isCompiled()) {
            throw new GdxRuntimeException("Shader compilation failed: " + rewindShader.getLog());
        }

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        fboBatch = new SpriteBatch();

        Sprite s = new Sprite(atlas.findRegion("rewind-symbol"));
        s.setBounds(0,0,18,15);
        rewindHUD = new RewindHUD(game.batch, s);
    }

    public void setRewindShader() { activeShader = rewindShader; }

    public void resetShader() { activeShader = null; }


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

    private void updateCamera() {
        gameCamera.followTarget();
        gameCamera.getCamera().update();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());
        time += delta;

        update(delta);
        inputHandler.handleInput();

        fbo.begin();
        clearScreen();
        renderWorld();
        fbo.end();

        applyPostProcessing(activeShader);
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void applyPostProcessing(ShaderProgram shader) {
        Texture fboTex = fbo.getColorBufferTexture();

        fboBatch.setShader(shader);

        fboBatch.begin();
        clearScreen();

        if (activeShader != null) {
            shader.setUniformf("u_time", time);
            shader.setUniformf("u_resolution", gameCamera.getViewport().getScreenWidth(), gameCamera.getViewport().getScreenHeight());
            shader.setUniformf("u_viewportOffset", gameCamera.getViewport().getScreenX(), gameCamera.getViewport().getScreenY());
        }

        fboBatch.draw(fboTex,
            gameCamera.getViewport().getScreenX(),gameCamera.getViewport().getScreenY(),
            gameCamera.getViewport().getScreenWidth(), gameCamera.getViewport().getScreenHeight(),
            0,0,1,1);
        fboBatch.end();

        if (activeShader == rewindShader)
            renderRewindHUD();
    }

    private void renderRewindHUD() {
        // Save the previous OpenGL state
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // Calculate black bar offsets (if any) from the FitViewport
        float viewportX = rewindHUD.stage.getViewport().getScreenX();
        float viewportY = rewindHUD.stage.getViewport().getScreenY();
        float viewportWidth = rewindHUD.stage.getViewport().getScreenWidth();
        float viewportHeight = rewindHUD.stage.getViewport().getScreenHeight();

        // Set scissor to HUD's viewport to clip within bounds
        Gdx.gl.glScissor((int) viewportX, (int) viewportY, (int) viewportWidth, (int) viewportHeight);

        rewindHUD.stage.getViewport().apply();
        rewindHUD.stage.draw();

        // Restore OpenGL state
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }


    private void renderWorld() {
        renderer.setView(gameCamera.getCamera());
        renderer.render();
        b2dr.render(world, gameCamera.getCamera().combined);
        game.batch.setProjectionMatrix(gameCamera.getCamera().combined);

        // draw game objects
        game.batch.begin();

        player.getSprite().draw(game.batch);
        for (Enemy e : enemies) {
            if (!e.isDead()) { // Hört auf Sprite zu malen, wenn Enemy stirbt (lieber in Enemy Datei?)
                e.getSprite().draw(game.batch);
            }
        }

        for(Item item :items)
            item.draw(game.batch);

        game.batch.end();
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

            for (Enemy enemy : enemies) {
                enemy.update(dt);
            }

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

    @Override
    public void pause() {
        gameIsPaused = true;
        music.pause();
        game.setScreen(new PauseMenu(game, this));
    }

    @Override
    public void resume() {
        KeyBindings.loadKeyBindings();
        gameIsPaused = false;
        music.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        music.play();
    }

    @Override
    public void resize(int width, int height) {
        gameCamera.resize(width, height);
        rewindHUD.resize(width, height);
        fboBatch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
    }


    public void reset(){
        gameIsPaused = false;
        // reset Screen
        game.setScreen(getNewInstance());
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();

        world.dispose();
        b2dr.dispose();

        fbo.dispose();
        fboBatch.dispose();
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    protected abstract LevelScreen getNewInstance();


    public void spawnEnemy(String enemyType, float x, float y) {
        if ("MadScientist".equals(enemyType)) {
            addEnemy(new MadScientist(world, this, player, x,y));
        } else if ("PatrollingEnemy".equals(enemyType)) {
            addEnemy(new PatrollingEnemy(world, this, x, y));
        }
    }

    private void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body, enemy)));
    }

    public void addItem(Item item) {
        items.add(item);
    }
}
