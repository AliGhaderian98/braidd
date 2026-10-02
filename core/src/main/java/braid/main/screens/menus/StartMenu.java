package braid.main.screens.menus;


import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.screens.huds.LevelHUD;
import braid.main.screens.levels.TestLevel;
import braid.main.tools.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class StartMenu extends ScreenAdapter {
    private final Stage stage;
    private final Braid game;
    private final Texture texture;

    public StartMenu(Braid game){
        this.game = game;

        LevelHUD.setTimerVisible(false);

        // load Bindings
        loadDisplayseedings();
        loadTimerVisible();
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup Viewport
        Viewport viewport = new FitViewport(Braid.V_WIDTH, Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);

        // Setup Background Image
        texture = new Texture(Gdx.files.internal("StartScreen.png"));
        Drawable backgroundImage = new TextureRegionDrawable(new TextureRegion(texture));;

        // Setup Label Styles
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(100), Color.BLACK);

        // Setup lable
        Label Startmessage = new Label("- Drücke eine Taste -", TitelFont);

        // Setup Table
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(backgroundImage);

        stage.addActor(table);

        table.add(Startmessage).expand().bottom().left().padBottom(550).padLeft(750);

        Overworld.music = Audiomanager.audiomanager.get("audio/music/lofi-loop.mp3" , Music.class);
        Overworld.music.setLooping(true);
        Overworld.music.setVolume(PreferencesManager.getSliderPreferences().getFloat("musicSlider"));
        Overworld.music.setPosition(0);
        Overworld.music.play();
    }

    private void loadDisplayseedings(){
        Braid.Fullscreen = PreferencesManager.getFullscreenPreferences().getBoolean("Vollbild");
        if (Braid.Fullscreen){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    private void startGame() {
        game.setScreen(new SavegameMenu(game));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            startGame();
        }

        stage.act(delta);
        stage.draw();
    }

    public static void loadTimerVisible(){
        LevelHUD.setTimerVisible(PreferencesManager.getcuntdownsettingsPreferences().getBoolean("Countdownpreferences"));
    }

    @Override
    public void resize(int width, int hight) {
        stage.getViewport().update(width, hight, true);
    }

    @Override
    public void dispose() {
        Savemanager.saveGame(true);
        stage.dispose();
        texture.dispose();
    }
}
