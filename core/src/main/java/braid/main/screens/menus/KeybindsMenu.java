package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.Audiomanager;
import braid.main.tools.KeyBindings;
import braid.main.tools.PreferencesManager;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.table;

public class KeybindsMenu implements Screen {

    private final LevelScreen previousScreen;
    private final Stage mainStage;
    private final Stage Overlay;
    private final Stage ErrorOverlay;
    private final Game game;
    private final Label RunLeft, RunRight,UP_KEY,DOWN_KEY, Jump, Rewind, INTERACT, ResetKeybindings;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;
    public static boolean OverlayActive;
    public static boolean changeNotPossible;

    int width;
    int height;

    // Sound
    private final Sound menuSound;

    public KeybindsMenu(Game game, LevelScreen previousScreen) {
        // Default: disable all Overlay
        OverlayActive = false;
        changeNotPossible = false;

        // Setup Screen
        this.game = game;
        this.previousScreen = previousScreen;
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());

        mainStage = new Stage(viewport,((Braid) game).batch);
        Overlay = new Stage(viewport,((Braid) game).batch);
        ErrorOverlay = new Stage(viewport,((Braid) game).batch);


        // Setup ScreenRatio
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        // Setup Sound
        menuSound = Audiomanager.audiomanager.get("audio/sound/menuSound.mp3", Sound.class);

        //  Create Table
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(), Color.GRAY);

        // Setup Title and Options for Screen
        Label Keybindings = new Label("Keybindings", TitelFont);
        Keybindings.setFontScale(2);
        RunLeft = new Label("Move Left: " +  Input.Keys.toString(KeyBindings.getKey("LEFT_KEY")), SelectionFont);
        RunLeft.setFontScale(0.7f);
        RunRight = new Label("Move Right: " + Input.Keys.toString(KeyBindings.getKey("RIGHT_KEY")), SelectionFont);
        RunRight.setFontScale(0.7f);
        Jump = new Label("Jump: " + Input.Keys.toString(KeyBindings.getKey("SPACEBAR")), SelectionFont);
        Jump.setFontScale(0.7f);
        Rewind = new Label("Rewind: " + Input.Keys.toString(KeyBindings.getKey("SHIFT")), SelectionFont);
        Rewind.setFontScale(0.7f);
        INTERACT = new Label("Interact: " + Input.Keys.toString(KeyBindings.getKey("INTERACT")), SelectionFont);
        INTERACT.setFontScale(0.7f);
        UP_KEY = new Label("Move Up: " + Input.Keys.toString(KeyBindings.getKey("UP_KEY")), SelectionFont);
        UP_KEY.setFontScale(0.7f);
        DOWN_KEY = new Label("Move Down: " + Input.Keys.toString(KeyBindings.getKey("DOWN_KEY")), SelectionFont);
        DOWN_KEY.setFontScale(0.7f);
        ResetKeybindings =  new Label("- Reset to Default -", SelectionFont);
        ResetKeybindings.setFontScale(0.7f);


        // fill Array with Labels to target a Label
        menuLabels = new Array<>();
        menuLabels.add(RunLeft, RunRight,UP_KEY,DOWN_KEY);
        menuLabels.add(Jump, Rewind, INTERACT, ResetKeybindings);


        // Setup Table
        table.add(Keybindings).expandX();
        table.row();
        table.add(RunLeft).padTop(10f);
        table.row();
        table.add(RunRight);
        table.row();
        table.add(UP_KEY);
        table.row();
        table.add(DOWN_KEY);
        table.row();
        table.add(Jump);
        table.row();
        table.add(Rewind);
        table.row();
        table.add(INTERACT);
        table.row();
        table.add(ResetKeybindings);

        // Setup Table for Overlay
        Label OverlayText = new Label("Press a Key to switch Keybindings", SelectionFont);
        Table OverlayTable = new Table();
        OverlayTable.center();
        OverlayTable.setFillParent(true);
        OverlayTable.add(OverlayText).expandX();


        // Setup Table for ErrorOverly
        Label ErrorOverlayText = new Label("Key is already in use", SelectionFont);
        Label ErrorOverlayText2 = new Label("Press Enter to proceed", SelectionFont);
        ErrorOverlayText2.setFontScale(0.5f);
        Table ErrorOverlayTable = new Table();
        ErrorOverlayTable.center();
        ErrorOverlayTable.setFillParent(true);
        ErrorOverlayTable.add(ErrorOverlayText).expandX();
        ErrorOverlayTable.row();
        ErrorOverlayTable.add(ErrorOverlayText2).expandX();


        mainStage.addActor(table);
        Overlay.addActor(OverlayTable);
        ErrorOverlay.addActor(ErrorOverlayTable);


        // mark first option
        updateLabelSelection();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        // show table
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateKeybindingLabel();
        handleInput();
        updateScreenRatio();
        if(OverlayActive){
            Overlay.draw();
        }else if(changeNotPossible){
            ErrorOverlay.draw();
        }
        else{
            mainStage.draw();
        }
    }

    private void handleInput() {
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("DOWN_KEY"))) {
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
            if(changeNotPossible){
                changeNotPossible = false;
            } else {
                executeSelectedAction();
            }
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            if(changeNotPossible || OverlayActive){
                changeNotPossible = false;
                OverlayActive = false;
            }else {
                game.setScreen(new OptionMenu(game,previousScreen));
                dispose();
            }
        }
    }

    private void updateLabelSelection() {
        // show visually selected Element
        Label selectedLabel = menuLabels.get(selectedIndex);
        for (int i = 0; i < menuLabels.size; i++) {
            Label label = menuLabels.get(i);
            if (i == selectedIndex) {
                if(selectedLabel == ResetKeybindings){
                    label.setColor(Color.RED); // selected and Label is ResetKeybindings
                } else {
                    label.setColor(Color.WHITE); // selected
                }
            } else {
                label.setColor(Color.GRAY); // not selected
            }
        }
    }

    private void updateKeybindingLabel(){
        // updates the current used Keybindings after a change
        RunLeft.setText("Move Left: " +  Input.Keys.toString(KeyBindings.getKey("LEFT_KEY")));
        RunRight.setText("Move Right: " + Input.Keys.toString(KeyBindings.getKey("RIGHT_KEY")));
        UP_KEY.setText("Move Up: " + Input.Keys.toString(KeyBindings.getKey("UP_KEY")));
        DOWN_KEY.setText("Move Down: " + Input.Keys.toString(KeyBindings.getKey("DOWN_KEY")));
        Jump.setText("Jump: " + Input.Keys.toString(KeyBindings.getKey("SPACEBAR")));
        Rewind.setText("Rewind: " + Input.Keys.toString(KeyBindings.getKey("SHIFT")));
        INTERACT.setText("Interact: " + Input.Keys.toString(KeyBindings.getKey("INTERACT")));
    }

    private void executeSelectedAction() {
        // execute yellow targeted Option
        Label selectedLabel = menuLabels.get(selectedIndex);
        if (selectedLabel == ResetKeybindings) {
            KeyBindings.standardKeybindings();
            KeyBindings.saveKeyBindings();
        }else {
            OverlayActive = true;
            if (selectedLabel == RunLeft) {
                KeyBindings.changeKeyBinding("LEFT_KEY");
            } else if (selectedLabel == RunRight) {
                KeyBindings.changeKeyBinding("RIGHT_KEY");
            } else if (selectedLabel == UP_KEY) {
                KeyBindings.changeKeyBinding("UP_KEY");
            } else if (selectedLabel == DOWN_KEY) {
                KeyBindings.changeKeyBinding("DOWN_KEY");
            } else if (selectedLabel == Jump) {
                KeyBindings.changeKeyBinding("SPACEBAR");
            } else if (selectedLabel == Rewind) {
                KeyBindings.changeKeyBinding("SHIFT");
            } else if (selectedLabel == INTERACT) {
                KeyBindings.changeKeyBinding("INTERACT");
            }
        }
    }

    @Override
    public void resize(int i, int i1) {
        mainStage.getViewport().update(i,i1,true);
        Overlay.getViewport().update(i,i1,true);
    }

    public void updateScreenRatio(){
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        resize(width,height);
    }

    private void executeENTER(){

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
        mainStage.dispose();
        Overlay.dispose();
    }
}
