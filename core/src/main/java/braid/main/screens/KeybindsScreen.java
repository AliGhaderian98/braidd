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

public class KeybindsScreen implements Screen {

    private final Screen previusScreen;
    private final Stage stage;
    private final Game game;
    private final Label RunLeft, RunRight, Jump, Rewind, INTERACT;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;

    public KeybindsScreen(Game game, Screen previusScreen) {
        // Setup Screen
        this.game = game;
        this.previusScreen = previusScreen;
        Viewport viewport = new FitViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);


        //  Create Table
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(new BitmapFont(), Color.GRAY);

        // Setup Title and Options
        Label Keybindings = new Label("Keybindings", TitelFont);
        Keybindings.setFontScale(2);
        RunLeft = new Label("Move Left: " +  Input.Keys.toString(KeyBindings.getKey("LEFT_KEY")), SelectionFont);
        RunRight = new Label("Move Right: " + Input.Keys.toString(KeyBindings.getKey("RIGHT_KEY")), SelectionFont);
        Jump = new Label("Jump: " + Input.Keys.toString(KeyBindings.getKey("SPACEBAR")), SelectionFont);
        Rewind = new Label("Rewind: " + Input.Keys.toString(KeyBindings.getKey("SHIFT")), SelectionFont);
        INTERACT = new Label("INTERACT: " + Input.Keys.toString(KeyBindings.getKey("INTERACT")), SelectionFont);



        // fill Array with Labels to target a Label
        menuLabels = new Array<>();
        menuLabels.add(RunLeft, RunRight, Jump, Rewind);
        menuLabels.add(INTERACT);


        // Setup Table
        table.add(Keybindings).expandX();
        table.row();
        table.add(RunLeft).padTop(10f);
        table.row();
        table.add(RunRight);
        table.row();
        table.add(Jump);
        table.row();
        table.add(Rewind);
        table.row();
        table.add(INTERACT);

        stage.addActor(table);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        // show table
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        stage.draw();
    }
    private void handleInput() {
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedIndex = (selectedIndex + 1) % menuLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedIndex = (selectedIndex - 1 + menuLabels.size) % menuLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeSelectedAction();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new OptionMenu(game,previusScreen));
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
        // TODO im KeybindsScreen anzeigen lassen
        System.out.println("inside execute");
        if (selectedLabel == RunLeft) {
            KeyBindings.changeKeyBinding("RunLeft");

        } else if (selectedLabel == RunRight) {
            // switch to Main Menu

        } else if (selectedLabel == Jump) {


        } else if (selectedLabel == Rewind) {

        } else if (selectedLabel == INTERACT) {

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
