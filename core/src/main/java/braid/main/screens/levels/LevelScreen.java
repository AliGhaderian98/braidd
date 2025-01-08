package braid.main.screens.levels;

import braid.main.*;
import braid.main.Items.CollectableItem;
import braid.main.Items.Item;
import braid.main.Items.ItemDef;
import braid.main.enemies.Knight;
import braid.main.overworld.Overworld;
import braid.main.screens.huds.FinishHUD;
import braid.main.screens.huds.LevelHUD;
import braid.main.enemies.MadScientist;
import braid.main.enemies.PatrollingEnemy;
import braid.main.enemies.UnhingedEnemy;
import braid.main.objects.Enemy;
import braid.main.objects.MovingPlatform;
import braid.main.objects.Player;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.screens.huds.RewindHUD;
import braid.main.screens.menus.StartMenu;
import braid.main.tools.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.maps.tiled.BaseTmxMapLoader;
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


/***********
 Basisklasse für Level, die grundlegende Funktionen bereitstellt, die von allen
 Levelklassen benötigt werden.
 ***********/
public abstract class LevelScreen implements Screen {
    // Basic Game variables
    protected final Braid game;
    private static final int GRAVITY = -10;
    protected final TextureAtlas atlas;
    public static boolean gameIsPaused = false;
    public static boolean debugRendererEnabled = true;

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
    protected Array<MovingPlatform> movingPlatforms;
    protected Array<Item> items;

    public Array<RewindController> rewindObjects;

    // Music
    private final Music music;

    // Subtitles
    private final SubtitleManager subtitleManager;

    // Shader variables
    private ShaderProgram rewindShader;
    private ShaderProgram hitShader;
    private ShaderProgram pauseShader;
    private ShaderProgram activeShader;
    private FrameBuffer fbo;
    private Texture fboTex;
    private SpriteBatch fboBatch;
    private float time = 0f;
    private RewindHUD rewindHUD;

    private LevelHUD levelHUD;
    private FinishHUD finishHUD;
    private boolean finished = false;



    public LevelScreen(Braid game, String mapPath, String atlasPath, String musicPath) {
        // Setup Game Variables
        this.game = game;
        atlas = new TextureAtlas(atlasPath);
        gameIsPaused = false;

        items = new Array<>();

        subtitleManager = new SubtitleManager(this);
        subtitleManager.addSubtitle(new Subtitle("HIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHIHI"));

        // Load current Bindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();
        StartMenu.loadTimerVisible();


        // Setup level map
        //mapLoader = new TmxMapLoader();
        mapLoader = new TemplateTmxMapLoader();
        map = mapLoader.load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // World setup
        world = new World(new Vector2(0, GRAVITY), true);


        // Setup player
        player = new Player(world, this);
        player.setRewindController(new RewindController(new RewindableBody(player.b2body, player)));

        // Enemies
        enemies = new Array<>();

        movingPlatforms = new Array<>();

        // B2WorldCreator
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(world, map, this);


        // Setup Music
        music = Audiomanager.audiomanager.get(musicPath ,Music.class);
        music.setLooping(true);
        music.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        music.setPosition(0);
        music.play();




        rewindObjects = new Array<>();

        rewindObjects.add(player.getRewindController());
        for (Enemy e : enemies) {
            if (e.isRewindable())
                rewindObjects.add(e.getRewindController());
        }

        for (MovingPlatform m : movingPlatforms) {
            if (m.isRewindable())
                rewindObjects.add(m.getRewindController());
        }

        // Final setup steps
        gameCamera = new GameCamera(25*16, 25*9, player);
        gameCamera.setMap(map);

        world.setContactListener(new WorldContactListener(player, game, this));
        inputHandler = new PlayerInputHandler(player, world, game, this);
        setupShaders();

        int maxPages = 0;
        for (int i = 0; i < items.size; i++ ) {
            if (items.get(i) instanceof CollectableItem)
                maxPages++;
        }

        levelHUD = new LevelHUD(game.batch, atlas, maxPages);
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

        fragmentShader = Gdx.files.internal("shaders/red.frag").readString();
        hitShader = new ShaderProgram(vertexShader, fragmentShader);
        ShaderProgram.pedantic = false;
        if (!hitShader.isCompiled()) {
            throw new GdxRuntimeException("Shader compilation failed: " + hitShader.getLog());
        }

        vertexShader = Gdx.files.internal("shaders/standard.vert").readString();
        fragmentShader = Gdx.files.internal("shaders/grey.frag").readString();
        pauseShader = new ShaderProgram(vertexShader, fragmentShader);
        ShaderProgram.pedantic = false;
        if (!pauseShader.isCompiled()) {
            throw new GdxRuntimeException("Shader compilation failed: " + pauseShader.getLog());
        }

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        fboBatch = new SpriteBatch();

        Sprite rs = new Sprite(atlas.findRegion("rewind-symbol"));
        rs.setBounds(0,0,18,15);
        Sprite ps = new Sprite(atlas.findRegion("pause-symbol"));
        ps.setBounds(0,0,18,15);
        rewindHUD = new RewindHUD(game.batch, rs, ps);
    }

    public void setRewindShader() {
        if (!finished)
            activeShader = rewindShader;
    }

    public void removeRewindShader() {
        if (activeShader == rewindShader)
            resetShader();
    }

    public void setHitShader() { activeShader = hitShader; }

    public void resetShader() { activeShader = null; }

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
        subtitleManager.render(delta);      // Update Subtitles
        fbo.end();

        applyPostProcessing(activeShader);

        if (finished)
            renderFinishHUD();

        game.batch.setProjectionMatrix(levelHUD.stage.getCamera().combined);
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void applyPostProcessing(ShaderProgram shader) {
        fboTex = fbo.getColorBufferTexture();

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

        if (activeShader == rewindShader) {
            rewindHUD.rewindMode();
            renderRewindHUD();
        } else if (activeShader == hitShader) {
            rewindHUD.pauseMode();
            renderRewindHUD();
        }
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

    private void renderFinishHUD() {
        // Save the previous OpenGL state
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // Calculate black bar offsets (if any) from the FitViewport
        float viewportX = finishHUD.stage.getViewport().getScreenX();
        float viewportY = finishHUD.stage.getViewport().getScreenY();
        float viewportWidth = finishHUD.stage.getViewport().getScreenWidth();
        float viewportHeight = finishHUD.stage.getViewport().getScreenHeight();

        // Set scissor to HUD's viewport to clip within bounds
        Gdx.gl.glScissor((int) viewportX, (int) viewportY, (int) viewportWidth, (int) viewportHeight);

        finishHUD.stage.getViewport().apply();
        finishHUD.stage.draw();

        // Restore OpenGL state
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }


    private void renderWorld() {
        renderer.setView(gameCamera.getCamera());
        renderer.render();
        if (debugRendererEnabled)
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

        for(MovingPlatform movingPlatform :movingPlatforms)
            movingPlatform.draw(game.batch);

        game.batch.end();

        levelHUD.stage.draw();
    }


    public void update(float dt) {
        KeyBindings.loadKeyBindings();

        if( !(gameIsPaused && player.isAlive()) )
            levelHUD.update(dt);

        //stop updating the game logic if game is paused or the player got hit
        if(!gameIsPaused && player.isAlive()) {
            KeyBindings.loadKeyBindings();

            // Update world physics
            world.step(dt, 6, 2);

            // Update Entities
            player.update(dt);

            for (Enemy enemy : enemies) {
                enemy.update(dt);
            }

            for(Item item : items)
                item.update(dt);

            for(MovingPlatform movingPlatform : movingPlatforms)
                movingPlatform.update(dt);

            // Update Camera
            updateCamera();

            for (RewindController r : rewindObjects) {
                r.update();
            }
        }
        else {
            if(!finished && Gdx.input.isKeyPressed(KeyBindings.getKey("SHIFT"))) {
                player.setAlive(true);
                gameIsPaused = false;
                resetShader();
            }
            if(finished &&
                (Gdx.input.isKeyJustPressed(KeyBindings.getKey("INTERACT")) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {

                game.setScreen(new Overworld(game));
            }
        }
    }

    public void finish() {
        finished = true;
        gameIsPaused = true;
        activeShader = pauseShader;
        finishHUD = new FinishHUD(game.batch, atlas);
    }

    @Override
    public void pause() {
        if (!finished) {
            gameIsPaused = true;
            music.pause();
            levelHUD.pause();
            game.setScreen(new PauseMenu(game, this, false, fboTex));
        }
    }

    @Override
    public void resume() {
        KeyBindings.loadKeyBindings();
        gameIsPaused = false;
        music.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        music.play();
        levelHUD.resume();
    }

    @Override
    public void resize(int width, int height) {
        gameCamera.resize(width, height);
        rewindHUD.resize(width, height);
        levelHUD.resize(width, height);
        if (finished)
            finishHUD.resize(width, height);
        fboBatch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
    }


    public void reset(){
        gameIsPaused = false;
        // reset Screen
        game.setScreen(getNewInstance());
    }

    @Override
    public void dispose() {
        Savemanager.saveGame(true);

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


    public void spawnEnemy(String enemyType, float x, float y, boolean rewindable) {
        if ("MadScientist".equals(enemyType)) {
            addEnemy(new MadScientist(world, this, player, x,y, rewindable));
        } else if ("PatrollingEnemy".equals(enemyType)) {
            addEnemy(new PatrollingEnemy(world, this, x, y, rewindable));
        } else if ("UnhingedEnemy".equals(enemyType)) {
            addEnemy((new UnhingedEnemy(world, this, player, x, y, rewindable)));
        } else if ("Knight".equals(enemyType)) {
            addEnemy((new Knight(world, this,player, x, y, rewindable)));
        }
    }

    private void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body, enemy)));
    }

    public void addMovingPlatform(MovingPlatform movingPlatform) {
        movingPlatforms.add(movingPlatform);
        movingPlatform.setRewindController(new RewindController(new RewindableBody(movingPlatform.b2body, movingPlatform)));
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public SubtitleManager getSubtitleManager() { return subtitleManager; }
}
