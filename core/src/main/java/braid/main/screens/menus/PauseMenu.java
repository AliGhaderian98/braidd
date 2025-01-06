package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.*;

import static com.badlogic.gdx.graphics.Color.toFloatBits;

public class PauseMenu implements Screen {

    private final Stage stage;
    private final Braid game;
    private final Boolean Reduced;
    private final Screen previousScreen;

    private final Texture background;
    private final ShaderProgram pauseShader;

    private final Label Resume, Retry, Option, Overworld, Startmenu, SaveAndExit;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;

    int width;
    int height;

    // Sound
    private final Sound menuSound;

    public PauseMenu(Braid game, Screen previousScreen, Boolean Reduced, Texture background){
        // Setup Screen and save World for resume
        this.game = game;
        this.previousScreen = previousScreen;
        this.Reduced = Reduced;
        this.background = background;



        Viewport viewport = new FitViewport(Braid.V_WIDTH, Braid.V_HEIGHT,new OrthographicCamera());
        viewport.apply();

        stage = new Stage(viewport,((Braid) game).batch);

        // Setup ScreenRatio
         width = Gdx.graphics.getWidth();
         height = Gdx.graphics.getHeight();

        // Setup Keybinding
        KeyBindings.loadKeyBindings();

        // Setup Sound
        menuSound = Audiomanager.audiomanager.get("audio/sound/menuSound.mp3", Sound.class);

        // different Fonts for different Lines on the Screen
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(150),Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(150), Color.GRAY);

        // Set up the whole space to write on.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // create new Labels to display
        Label PauseLabel = new Label("Game Paused", TitelFont);
        PauseLabel.setFontScale(2);
        Resume = new Label("Resume", SelectionFont);
        Overworld = new Label("Overworld", SelectionFont);
        Startmenu = new Label("Startmenu", SelectionFont);
        Retry = new Label("Retry", SelectionFont);
        Option = new Label("Option", SelectionFont);
        SaveAndExit = new Label("Save and Exit", SelectionFont);

        // fill Array with Labels to target a label
        menuLabels = new Array<>();

        menuLabels.add(Resume);
        if(!Reduced) { // Leave this point out if the pause screen is opened from the Overworld or SaveGameMenu
            menuLabels.add(Retry,Overworld,Startmenu);
        }
        menuLabels.add(Option, SaveAndExit);

        // set up table
        table.add(PauseLabel).expandX();
        table.row();
        table.add(Resume).expandX().padTop(10f);
        table.row();
        if(!Reduced) { // don´t show these points if the pause screen is opened from the Overworld or SaveGameMenu
            table.add(Retry).expandX();
            table.row();
            table.add(Overworld).expandX();
            table.row();
            table.add(Startmenu).expandX();
            table.row();
        }
        table.add(Option).expandX();
        table.row();
        table.add(SaveAndExit).expandX();

        // set up Stage
        stage.addActor(table);

        // mark first option
        updateLabelSelection();

        pauseShader = createPauseShader();
    }

    private ShaderProgram createPauseShader() {
        final ShaderProgram pauseShader;
        String vertexShader = Gdx.files.internal("shaders/standard.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/grey.frag").readString();
        pauseShader = new ShaderProgram(vertexShader, fragmentShader);
        ShaderProgram.pedantic = false;
        if (!pauseShader.isCompiled()) {
            throw new GdxRuntimeException("Shader compilation failed: " + pauseShader.getLog());
        }
        return pauseShader;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // show table
        Gdx.gl.glClearColor( 0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        updateScreenRatio();

        game.batch.setShader(pauseShader);
        pauseShader.setUniformf("u_resolution", stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());

        game.batch.begin();
        game.batch.draw(background,
            stage.getViewport().getScreenX(),stage.getViewport().getScreenY(),
            stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight(),
            0,0,1,1);
        game.batch.end();

        game.batch.setProjectionMatrix(stage.getCamera().combined);

        game.batch.setShader(null);

        stage.draw();
    }

    private void handleInput() {
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("DOWN_KEY"))){
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            selectedIndex = (selectedIndex + 1) % menuLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("UP_KEY"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            selectedIndex = (selectedIndex - 1 + menuLabels.size) % menuLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ENTER"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            executeSelectedAction();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            previousScreen.resume();
            game.setScreen(previousScreen);
            dispose();
        }
    }

    private void updateLabelSelection() {
        // show visually selected Element
        for (int i = 0; i < menuLabels.size; i++) {
            Label label = menuLabels.get(i);
            if (i == selectedIndex) {
                label.setColor(Color.WHITE); // selected
            } else {
                label.setColor(Color.GRAY); // not selected
            }
        }
    }

    private void executeSelectedAction() {
        // execute yellow targeted Option
        Label selectedLabel = menuLabels.get(selectedIndex);

        if (selectedLabel == Resume) {
            previousScreen.resume();
            game.setScreen(previousScreen);
            dispose();

        } else if (selectedLabel == Retry) {
            if (previousScreen instanceof LevelScreen) {
                game.setScreen(previousScreen);
                ((LevelScreen) previousScreen).reset();
                dispose();
            }

        } else if (selectedLabel == Option) {
            game.setScreen(new OptionMenu(game,previousScreen, Reduced, background));

        } else if (selectedLabel == Overworld) {
            Savemanager.saveGame();
            game.setScreen(new Overworld(game));
            dispose();

        } else if (selectedLabel == Startmenu) {
            Savemanager.saveGame();
            game.setScreen(new StartMenu(game));
            dispose();

        } else if (selectedLabel == SaveAndExit) {
            Savemanager.saveGame();
            Gdx.app.exit();
        }
    }
    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update(i,i1,true);
    }

    public void updateScreenRatio(){
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        resize(width,height);
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
        Savemanager.saveGame();
        stage.dispose();
    }

}
