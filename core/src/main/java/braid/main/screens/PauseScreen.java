package braid.main.screens;

import braid.main.Braid;
import braid.main.tools.KeyBindings;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PauseScreen implements Screen {

    private final Stage stage;
    private final Game game;
    private final TestScreen previousScreen;

    private final Label Resume,mainMenu, Retry, Option, SaveAndExit;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;


    public PauseScreen(Game game, TestScreen previusScreen){
        // Setup Screen and save World for resume
        this.game = game;
        this.previousScreen = previusScreen;
        Viewport viewport = new FitViewport(Braid.V_WIDTH, Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);

        KeyBindings.loadKeyBindings();

        // different Fonts for different Lines on the Screen
        Label.LabelStyle TitelFont = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(new BitmapFont(), Color.GRAY);

        // Set up the whole space to write on.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // create new Labels to display
        Label PauseLabel = new Label("Game Paused", TitelFont);
        PauseLabel.setFontScale(2);
        Resume = new Label("Resume", SelectionFont);
        mainMenu = new Label("main menu", SelectionFont);
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
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        stage.draw();


    }
    private void handleInput() {
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("DOWN_KEY"))){
            selectedIndex = (selectedIndex + 1) % menuLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("UP_KEY"))) {
            selectedIndex = (selectedIndex - 1 + menuLabels.size) % menuLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ENTER"))) {
            executeSelectedAction();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            TestScreen.gameIsPaused = false;
            game.setScreen(previousScreen);
            dispose();
        }
    }

    private void updateLabelSelection() {
        // show visually selected Element
        for (int i = 0; i < menuLabels.size; i++) {
            Label label = menuLabels.get(i);
            if (i == selectedIndex) {
                label.setColor(Color.YELLOW); // selected
            } else {
                label.setColor(Color.WHITE); // not selected
            }
        }
    }

    private void executeSelectedAction() {
        // execute yellow targeted Option
        Label selectedLabel = menuLabels.get(selectedIndex);

        if (selectedLabel == Resume) {
            TestScreen.gameIsPaused = false;
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
