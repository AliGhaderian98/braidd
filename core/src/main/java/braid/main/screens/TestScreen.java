package braid.main.screens;

import braid.main.Braid;
import braid.main.Items.Item;
import braid.main.RewindController;
import braid.main.RewindableBody;
import braid.main.objects.Enemy;
import braid.main.tools.Audiomanager;
import braid.main.tools.KeyBindings;
import braid.main.tools.PreferencesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TestScreen extends LevelScreen {

    // Music
    private final Music Wintermusic;

    // Shaders
    private ShaderProgram rewindShader;
    private ShaderProgram activeShader;
    private FrameBuffer fbo;
    private SpriteBatch fboBatch;
    private float time = 0f;
    private RewindHUD rewindHUD;

    public TestScreen(Braid game) {
        super(game, "maps/wintermap.tmx", "packedimages/sprites.atlas");

        // SetupKeybindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup Music
        Wintermusic = Audiomanager.audiomanager.get("audio/music/background_music.mp3",Music.class);
        Wintermusic.setLooping(true);
        Wintermusic.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        Wintermusic.play();

        setupShaders();

        player.setPosition(32/Braid.PPM, 32/Braid.PPM);


        Enemy enemy = new Enemy(world, this);
        enemy.setRewindController(new RewindController(new RewindableBody(enemy.b2body, enemy)));
        enemies.add(enemy);

        rewindObjects.add(player.getRewindController());
        for (Enemy e : enemies) {
            rewindObjects.add(e.getRewindController());
        }
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

    @Override
    public void resume() {
        KeyBindings.loadKeyBindings();
        gameIsPaused = false;
        Wintermusic.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        Wintermusic.play();

    }

    @Override
    protected LevelScreen getNewInstance() {
        return new TestScreen(game);
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

        fbo.dispose();
        fboBatch.dispose();
    }

    public World getWorld() {
        return world;
    }
}

    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}
