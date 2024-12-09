package braid.main.screens;

import braid.main.Braid;
import braid.main.tools.Audiomanager;
import braid.main.tools.KeyBindings;
import braid.main.tools.PreferencesManager;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.*;

import static com.badlogic.gdx.graphics.Color.GOLD;
import static com.badlogic.gdx.graphics.Color.toFloatBits;

public class PauseMenu implements Screen {

    private final Stage stage;
    private final Game game;
    private final TestScreen previousScreen;

    private final Label Resume,mainMenu, Retry, Option, SaveAndExit;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;

    int width;
    int height;

    // Sound
    private final Sound menuSound;

    public PauseMenu(Game game, TestScreen previusScreen){
        // Setup Screen and save World for resume
        this.game = game;
        this.previousScreen = previusScreen;
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH, Braid.V_HEIGHT,new OrthographicCamera());
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
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(),Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(), Color.GRAY);

        // Set up the whole space to write on.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // create new Labels to display
        Label PauseLabel = new Label("Game Paused", TitelFont);
        PauseLabel.setFontScale(2);
        Resume = new Label("Resume", SelectionFont);
        mainMenu = new Label("Main Menu", SelectionFont);
        Retry = new Label("Retry", SelectionFont);
        Option = new Label("Option", SelectionFont);
        SaveAndExit = new Label("Save and Exit", SelectionFont);

        // fill Array with Labels to target a label
        menuLabels = new Array<>();
        menuLabels.add(Resume, mainMenu, Retry, Option);
        menuLabels.add(SaveAndExit);

        // set up table
        table.add(PauseLabel).expandX();
        table.row();
        table.add(Resume).expandX().padTop(10f);
        table.row();
        table.add(mainMenu).expandX();
        table.row();
        table.add(Retry).expandX();
        table.row();
        table.add(Option).expandX();
        table.row();
        table.add(SaveAndExit).expandX();

        // set up Stage
        stage.addActor(table);

        // mark first option
        updateLabelSelection();
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

        } else if (selectedLabel == mainMenu) {
            // switch to Main Menu

        } else if (selectedLabel == Retry) {
            game.setScreen(previousScreen);
            previousScreen.reset();
            dispose();

        } else if (selectedLabel == Option) {
            game.setScreen(new OptionMenu(game,previousScreen));

        } else if (selectedLabel == SaveAndExit) {
            // save is missing
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
        stage.dispose();
    }

}
