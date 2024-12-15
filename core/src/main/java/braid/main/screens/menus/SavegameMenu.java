package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.Audiomanager;
import braid.main.tools.KeyBindings;
import braid.main.tools.PreferencesManager;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class SavegameMenu implements Screen{

    private final Stage stage;
    private final Game game;

    int width;
    int height;


    // Labels show on Screen
    private final Label Savegame1, Savegame2, Savegame3;
    private final Label SG1Collectibles,SG2Collectibles,SG3Collectibles;
    private final Label SG1UnlockedLevel, SG2UnlockedLevel, SG3UnlockedLevel;

    // used to target a Label
    private final Array<Label> SaveGameLabels;
    private final Array<Label> AllLabels;
    private int selectedIndex = 0;
    private  Label  currentLabel;

    // Sound
    private final Sound menuSound;


    public SavegameMenu(Game game) {
        this.game = game;

        // Setup Screen
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);

        // Setup ScreenRatio
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();


        KeyBindings.loadKeyBindings();

        // Setup Sound
        menuSound = Audiomanager.audiomanager.get("audio/sound/menuSound.mp3", Sound.class);


        //  Create Table
        Table table = new Table();
        table.center();
        table.setFillParent(true);


        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.getmidTextFont(), Color.GRAY);
        Label.LabelStyle LittleSelectionFont = new Label.LabelStyle(TextFontManager.getlittleTextFont(), Color.GRAY);



        // Setup Title and Options
        Label Title = new Label("Savegames", TitelFont);
        Title.setFontScale(2);

        Savegame1 = new Label("Savegame 1", SelectionFont);
        Savegame2 = new Label("Savegame 2", SelectionFont);
        Savegame3 = new Label("Savegame 3", SelectionFont);

        SG1Collectibles = new Label("Collectibles 1", LittleSelectionFont);
        SG2Collectibles = new Label("Collectibles 2", LittleSelectionFont);
        SG3Collectibles = new Label("Collectibles 3", LittleSelectionFont);

        SG1UnlockedLevel = new Label("Unlocked Levels 1", LittleSelectionFont);
        SG2UnlockedLevel = new Label("Unlocked Levels 2", LittleSelectionFont);
        SG3UnlockedLevel = new Label("Unlocked Levels 3", LittleSelectionFont);


        // fill Array with Labels to target a Label
        SaveGameLabels = new Array<>();
        SaveGameLabels.add(Savegame1,Savegame2,Savegame3);

        // fill Array with all Labels to reset the Color
        AllLabels = new Array<>();
        AllLabels.add(Savegame1,Savegame2,Savegame3);
        AllLabels.add(SG1Collectibles,SG2Collectibles,SG3Collectibles);
        AllLabels.add(SG1UnlockedLevel,SG2UnlockedLevel,SG3UnlockedLevel);



        // Setup Table
        table.add(Title);
        table.row();
        table.add(Savegame1).padTop(10f);
        table.row();
        table.add(SG1Collectibles);
        table.row();
        table.add(SG1UnlockedLevel);
        table.row();
        table.add(Savegame2).padTop(10f);
        table.row();
        table.add(SG2Collectibles);
        table.row();
        table.add(SG2UnlockedLevel);
        table.row();
        table.add(Savegame3).padTop(10f);
        table.row();
        table.add(SG3Collectibles);
        table.row();
        table.add(SG3UnlockedLevel);

        stage.addActor(table);

        // mark first option
        updateLabelSelection();
    }

    @Override
    public void render(float delta) {
        // show table
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        updateScreenRatio();
        stage.draw();
    }


    private void handleInput() {
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("DOWN_KEY"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            selectedIndex = (selectedIndex + 1) % SaveGameLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("UP_KEY"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            selectedIndex = (selectedIndex - 1 + SaveGameLabels.size) % SaveGameLabels.size;
            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ENTER"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            executeSelectedAction();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            LevelScreen.gameIsPaused = false;
            game.setScreen(new PauseMenu(game, this,true));
        }
    }

    private void updateLabelSelection() {
        // Set every Labele back to Grey
        for (int i = 0; i < AllLabels.size; i++) {
             AllLabels.get(i).setColor(Color.GRAY);
        }

        // Set current Label
        for (int i = 0; i < SaveGameLabels.size; i++) {
            Label label = SaveGameLabels.get(i);
            if (i == selectedIndex) {
                currentLabel = label;
            }
        }

        // mark the Selected Savegame with the Attributes white
        if(currentLabel == Savegame1){
            Savegame1.setColor(Color.WHITE);
            SG1Collectibles.setColor(Color.WHITE);
            SG1UnlockedLevel.setColor(Color.WHITE);

        } else if (currentLabel == Savegame2) {
            Savegame2.setColor(Color.WHITE);
            SG2Collectibles.setColor(Color.WHITE);
            SG2UnlockedLevel.setColor(Color.WHITE);

        }else if(currentLabel == Savegame3){
            Savegame3.setColor(Color.WHITE);
            SG3Collectibles.setColor(Color.WHITE);
            SG3UnlockedLevel.setColor(Color.WHITE);
        }
    }

    private void executeSelectedAction() {

        // load targeted Safegame
        if (currentLabel == Savegame1) {
            game.setScreen(new Overworld((Braid) game));

        } else if (currentLabel == Savegame2) {
            game.setScreen(new Overworld((Braid) game));

        } else if (currentLabel == Savegame3){
            game.setScreen(new Overworld((Braid) game));
        }
        dispose();
    }



    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
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
