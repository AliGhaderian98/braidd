package braid.main.screens.levels;

import braid.main.*;
import braid.main.Items.CollectableItem;
import braid.main.Items.Item;
import braid.main.enemies.Knight;
import braid.main.objects.*;
import braid.main.enemies.*;
import braid.main.objects.Brick;
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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import braid.main.screens.menus.PauseMenu;

import java.util.Objects;


/***********
 Basisklasse für Level, die grundlegende Funktionen bereitstellt, die von allen
 Levelklassen benötigt werden.
 ***********/
public abstract class LevelScreen implements Screen {
    protected String levelName;

    // Basic Game variables
    protected final Braid game;
    private static final int GRAVITY = -10;
    protected final TextureAtlas atlas;
    public static boolean gameIsPaused = false;
    public static boolean debugRendererEnabled = true;
    public static boolean loadedMap = false;

    protected final PlayerInputHandler inputHandler;

    // Camera and Map variables
    protected final GameCamera gameCamera;
    protected final TmxMapLoader mapLoader;
    protected final TiledMap map;
    protected final OrthogonalTiledMapRenderer renderer;

    // Box2D variables
    protected final World world;
    protected final Box2DDebugRenderer b2dr;
    protected final B2WorldCreator b2WC;
    private final EventListener event;


    // Game Objects
    protected final Player player;
    protected Array<Enemy> enemies;
    protected Array<NPC> npcs;
    protected Array<MovingPlatform> movingPlatforms;
    protected Array<SchalterMovingPlatform> schalterMovingPlatforms;
    protected Array<Button> buttons;
    protected Array<Item> items;
    protected Array<Brick> bricks;

    private final Array<Schalter> schalters = new Array<>();

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
    private float elapsedTime = 0f;
    private RewindHUD rewindHUD;

    private LevelHUD levelHUD;
    private FinishHUD finishHUD;
    private boolean finished = false;



    public LevelScreen(Braid game, String mapPath, String atlasPath, String musicPath) {
        // Setup Game Variables
        this.game = game;
        atlas = new TextureAtlas(atlasPath);
        gameIsPaused = false;
        setLevelName();

        items = new Array<>();

        subtitleManager = new SubtitleManager(this);

        // Load current Bindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();
        StartMenu.loadTimerVisible();


        // Setup level map
        mapLoader = new TemplateTmxMapLoader();
        map = mapLoader.load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        // World setup
        world = new World(new Vector2(0, GRAVITY), true);
        event = new EventListener(game, this, map);


        // Setup player
        player = new Player(world, this);
        player.setRewindController(new RewindController(new RewindableBody(player.b2body, player)));

        // Enemies
        enemies = new Array<>();

        npcs = new Array<>();

        movingPlatforms = new Array<>();
        schalterMovingPlatforms = new Array<>();

        buttons = new Array<>();

        bricks = new Array<>();

        // B2WorldCreator
        b2dr = new Box2DDebugRenderer();
        b2WC = new B2WorldCreator(world, map, this);


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

        for (SchalterMovingPlatform gm : schalterMovingPlatforms) {
            if (gm.isRewindable())
                rewindObjects.add(gm.getRewindController());
        }

        for (Brick b : bricks) {
            if (b.isRewindable())
                rewindObjects.add(b.getRewindController());
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

        drwaMovingPlatforms();
        drawSchalterMovingPlatforms();
        drawSchalters();
        drawBricks();
        drawItems();

        game.batch.end();

        levelHUD.stage.draw();
    }

    private void drwaMovingPlatforms() {
        for(MovingPlatform movingPlatform : movingPlatforms)
            movingPlatform.draw(game.batch);
    }

    private void drawSchalterMovingPlatforms() {
        for(SchalterMovingPlatform schalterMovingPlatform : schalterMovingPlatforms)
            schalterMovingPlatform.draw(game.batch);
    }

    public void drawSchalters() {
        for (Schalter schalter : schalters)
            schalter.draw(game.batch);
    }

    public void drawBricks() {
        for (Brick brick : bricks)
            brick.draw(game.batch);
    }

    public void drawItems() {
        for(Item item : items)
            item.draw(game.batch);
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

            elapsedTime += dt;

            // Update Entities
            player.update(dt);

            for (Enemy enemy : enemies) {
                enemy.update(dt);
            }

            for(Item item : items)
                item.update(dt);

            for(MovingPlatform movingPlatform : movingPlatforms)
                movingPlatform.update(dt);

            for (Brick brick : bricks) {
                brick.update(dt);
            }

            for(SchalterMovingPlatform schalterMovingPlatform : schalterMovingPlatforms)
                schalterMovingPlatform.update(dt);

            // Update Camera
            updateCamera();

            for (RewindController r : rewindObjects) {
                r.update(dt);
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
        if (!finished) {
            Savemanager.unlockNextLevel(levelName);
            Savemanager.currentsavegame.lastLevel = levelName;
            Savemanager.saveGame(false);
        }
        finished = true;
        gameIsPaused = true;
        activeShader = pauseShader;
        finishHUD = new FinishHUD(game.batch, atlas);
    }

    @Override
    public void pause() {
        if (!finished) {
            Savemanager.currentsavegame.lastLevel = levelName;
            Savemanager.saveGame(false);
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
        player.setNewestPowerUp(false);
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


    public void spawnEnemy(String enemyType, float x, float y, boolean rewindable, String type) {
        if ("MadScientist".equals(enemyType)) {
            addEnemy(new MadScientist(world, this, player, x,y, rewindable, type));
        } else if ("PatrollingEnemy".equals(enemyType)) {
            addEnemy(new PatrollingEnemy(world, this, x, y, rewindable, type));
        } else if ("UnhingedEnemy".equals(enemyType)) {
            addEnemy((new UnhingedEnemy(world, this, player, x, y, rewindable, type)));
        } else if ("Knight".equals(enemyType)) {
            addEnemy((new Knight(world, this,player, x, y, rewindable, type)));
        } else if ("IdleEnemy".equals(enemyType)) {
            addEnemy((new IdleEnemy(world, this, x, y, rewindable, type)));
        }
    }

    public void spawnNPC(String type, Rectangle npcBoundary, Rectangle npcTrigger, String text, String spriteName){
        NPC npc = new NPC(world, this, getSubtitleManager(), type, npcBoundary, npcTrigger, text, spriteName);
        npc.setEvent(this.event);
        addNPC(npc);
    }

    public void spawnButton(Rectangle boundary, String actionType, String targetName) {
        Button button = new Button(world, boundary, map, actionType, targetName, event);
        if(Objects.equals(actionType, "repairButton")) {
            event.addButtonCount();
        }
        addButton(button);
    }

    private void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body, enemy)));
    }

    protected void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public void addMovingPlatform(MovingPlatform movingPlatform) {
        movingPlatforms.add(movingPlatform);
        movingPlatform.setRewindController(new RewindController(new RewindableBody(movingPlatform.b2body, movingPlatform)));
    }

    public void addSchalterMovingPlatform(SchalterMovingPlatform schalterMovingPlatform) {
        schalterMovingPlatforms.add(schalterMovingPlatform);
        schalterMovingPlatform.setRewindController(new RewindController(new RewindableBody(schalterMovingPlatform.b2body, schalterMovingPlatform)));
    }

    public void addSchalter(Schalter schalter) {
        schalters.add(schalter);
    }

    public void addButton(Button button){
        buttons.add(button);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addBrick (Brick brick) {
        bricks.add(brick);
        brick.setRewindController((new RewindController((new RewindableBody(brick.b2body,brick)))));
    }

    public SubtitleManager getSubtitleManager() { return subtitleManager; }

    public B2WorldCreator getB2WC () { return b2WC; }

    public String getLevelName() { return levelName; }

    public Array<Enemy> getEnemies() { return enemies; }

    private void setLevelName() {
        if (this instanceof ArkadenLevel)
            levelName = "ARKADEN";
        else if (this instanceof BayerLevel)
            levelName = "BAYER";
        else if (this instanceof FreudenbergLevel)
            levelName = "FREUDENBERG";
        else if (this instanceof HBFLevel)
            levelName = "HBF";
        else if (this instanceof LuisenviertelLevel)
            levelName = "LUISENVIERTEL";
        else if (this instanceof OberbarmenLevel)
            levelName = "OBERBARMEN";
        else if (this instanceof SchlossBurgLevel)
            levelName = "SCHLOSSBURG";
        else if (this instanceof UniLevel)
            levelName = "UNI";
        else if (this instanceof WegZurUniLevel)
            levelName = "WEGZURUNI";
        else if (this instanceof ZooLevel)
            levelName = "ZOO";
        else if (this instanceof MountainMap)
            levelName = "WEGZURUNI";
        else if (this instanceof PastMap)
            levelName = "WEGZURUNI";
        else if (this instanceof PresentMap)
            levelName = "WEGZURUNI";
        else if (this instanceof FutureMap)
            levelName = "Future";
//        else if (this instanceof FinaleMap)
//            levelName = "Finale"
    }
}
