package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class SavegameMenu implements Screen{

    private final Stage stage;
    private final Game game;
    private final ScrollPane scrollPane;
    private final Table table;

    int width;
    int height;


    // Labels show on Screen
    private final Label Savegame1, Savegame2, Savegame3, Savegame4, Savegame5, Savegame6, Savegame7, Savegame8, Savegame9, Savegame10;
    private final Label SG1Collectibles, SG2Collectibles, SG3Collectibles, SG4Collectibles, SG5Collectibles, SG6Collectibles, SG7Collectibles, SG8Collectibles, SG9Collectibles, SG10Collectibles;
    private final Label SG1UnlockedLevel, SG2UnlockedLevel, SG3UnlockedLevel, SG4UnlockedLevel, SG5UnlockedLevel, SG6UnlockedLevel, SG7UnlockedLevel, SG8UnlockedLevel, SG9UnlockedLevel, SG10UnlockedLevel;
    private final VerticalGroup SG1, SG2, SG3, SG4, SG5, SG6, SG7, SG8, SG9, SG10;

    // used to target a Label
    private final Array<Label> SaveGameLabels;
    private final Array<Label> AllLabels;
    private int selectedIndex = 0;
    private  Label  currentLabel;

    // Sound
    private final Sound menuSound;


    public SavegameMenu(Game game) {
        Savemanager savemanager = new Savemanager();
        this.game = game;

        // Setup Screen
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);
        Gdx.input.setInputProcessor(stage);

        // Setup ScreenRatio
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();


        KeyBindings.loadKeyBindings();

        // Setup Sound
        menuSound = Audiomanager.audiomanager.get("audio/sound/menuSound.mp3", Sound.class);


        //  Create Table
        table = new Table();

        Table rootTable = new Table();


        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.getmidTextFont(), Color.GRAY);
        Label.LabelStyle LittleSelectionFont = new Label.LabelStyle(TextFontManager.getlittleTextFont(), Color.GRAY);

        // Setup Groups for better Handing of the Labels
        SG1 = new VerticalGroup();
        SG2 = new VerticalGroup();
        SG3 = new VerticalGroup();
        SG4 = new VerticalGroup();
        SG5 = new VerticalGroup();
        SG6 = new VerticalGroup();
        SG7 = new VerticalGroup();
        SG8 = new VerticalGroup();
        SG9 = new VerticalGroup();
        SG10 = new VerticalGroup();


        // Setup Title and Options
        Label Title = new Label("Savegames", TitelFont);
        Title.setFontScale(2);

        Savegame1 = new Label("Savegame 1", SelectionFont);
        Savegame2 = new Label("Savegame 2", SelectionFont);
        Savegame3 = new Label("Savegame 3", SelectionFont);
        Savegame4 = new Label("Savegame 4", SelectionFont);
        Savegame5 = new Label("Savegame 5", SelectionFont);
        Savegame6 = new Label("Savegame 6", SelectionFont);
        Savegame7 = new Label("Savegame 7", SelectionFont);
        Savegame8 = new Label("Savegame 8", SelectionFont);
        Savegame9 = new Label("Savegame 9", SelectionFont);
        Savegame10 = new Label("Savegame 10", SelectionFont);


        Savegame1.setPosition(0,0);
        Savegame2.setPosition(0,0);
        Savegame3.setPosition(0,0);
        Savegame4.setPosition(0,0);
        Savegame5.setPosition(0,0);
        Savegame6.setPosition(0,0);
        Savegame7.setPosition(0,0);
        Savegame8.setPosition(0,0);
        Savegame9.setPosition(0,0);
        Savegame10.setPosition(0,0);


        SG1Collectibles = new Label("Collectibles 1", LittleSelectionFont);
        SG2Collectibles = new Label("Collectibles 2", LittleSelectionFont);
        SG3Collectibles = new Label("Collectibles 3", LittleSelectionFont);
        SG4Collectibles = new Label("Collectibles 4", LittleSelectionFont);
        SG5Collectibles = new Label("Collectibles 5", LittleSelectionFont);
        SG6Collectibles = new Label("Collectibles 6", LittleSelectionFont);
        SG7Collectibles = new Label("Collectibles 7", LittleSelectionFont);
        SG8Collectibles = new Label("Collectibles 8", LittleSelectionFont);
        SG9Collectibles = new Label("Collectibles 9", LittleSelectionFont);
        SG10Collectibles = new Label("Collectibles 10", LittleSelectionFont);

        SG10Collectibles.setPosition(0,0);
        SG9Collectibles.setPosition(0,0);
        SG8Collectibles.setPosition(0,0);
        SG7Collectibles.setPosition(0,0);
        SG6Collectibles.setPosition(0,0);
        SG5Collectibles.setPosition(0,0);
        SG4Collectibles.setPosition(0,0);
        SG3Collectibles.setPosition(0,0);
        SG2Collectibles.setPosition(0,0);
        SG1Collectibles.setPosition(0,0);


        SG1UnlockedLevel = new Label("Unlocked Levels 1", LittleSelectionFont);
        SG2UnlockedLevel = new Label("Unlocked Levels 2", LittleSelectionFont);
        SG3UnlockedLevel = new Label("Unlocked Levels 3", LittleSelectionFont);
        SG4UnlockedLevel = new Label("Unlocked Levels 4", LittleSelectionFont);
        SG5UnlockedLevel = new Label("Unlocked Levels 5", LittleSelectionFont);
        SG6UnlockedLevel = new Label("Unlocked Levels 6", LittleSelectionFont);
        SG7UnlockedLevel = new Label("Unlocked Levels 7", LittleSelectionFont);
        SG8UnlockedLevel = new Label("Unlocked Levels 8", LittleSelectionFont);
        SG9UnlockedLevel = new Label("Unlocked Levels 9", LittleSelectionFont);
        SG10UnlockedLevel = new Label("Unlocked Levels 10", LittleSelectionFont);

        SG1UnlockedLevel.setPosition(0,0);
        SG2UnlockedLevel.setPosition(0,0);
        SG3UnlockedLevel.setPosition(0,0);
        SG4UnlockedLevel.setPosition(0,0);
        SG5UnlockedLevel.setPosition(0,0);
        SG6UnlockedLevel.setPosition(0,0);
        SG7UnlockedLevel.setPosition(0,0);
        SG8UnlockedLevel.setPosition(0,0);
        SG9UnlockedLevel.setPosition(0,0);
        SG10UnlockedLevel.setPosition(0,0);

        SG1.addActor(Savegame1);
        SG1.addActor(SG1Collectibles);
        SG1.addActor(SG1UnlockedLevel);
        SG1.space(10);

        SG2.addActor(Savegame2);
        SG2.addActor(SG2Collectibles);
        SG2.addActor(SG2UnlockedLevel);
        SG2.space(10);

        SG3.addActor(Savegame3);
        SG3.addActor(SG3Collectibles);
        SG3.addActor(SG3UnlockedLevel);
        SG3.space(10);

        SG4.addActor(Savegame4);
        SG4.addActor(SG4Collectibles);
        SG4.addActor(SG4UnlockedLevel);
        SG4.space(10);

        SG5.addActor(Savegame5);
        SG5.addActor(SG5Collectibles);
        SG5.addActor(SG5UnlockedLevel);
        SG5.space(10);

        SG6.addActor(Savegame6);
        SG6.addActor(SG6Collectibles);
        SG6.addActor(SG6UnlockedLevel);
        SG6.space(10);

        SG7.addActor(Savegame7);
        SG7.addActor(SG7Collectibles);
        SG7.addActor(SG7UnlockedLevel);
        SG7.space(10);

        SG8.addActor(Savegame8);
        SG8.addActor(SG8Collectibles);
        SG8.addActor(SG8UnlockedLevel);
        SG8.space(10);

        SG9.addActor(Savegame9);
        SG9.addActor(SG9Collectibles);
        SG9.addActor(SG9UnlockedLevel);
        SG9.space(10);

        SG10.addActor(Savegame10);
        SG10.addActor(SG10Collectibles);
        SG10.addActor(SG10UnlockedLevel);
        SG10.space(10);





        // fill Array with Labels to target a Label
        SaveGameLabels = new Array<>();
        SaveGameLabels.add(Savegame1,Savegame2,Savegame3,Savegame4);
        SaveGameLabels.add(Savegame5,Savegame6,Savegame7,Savegame8);
        SaveGameLabels.add(Savegame9,Savegame10);


        // fill Array with all Labels to reset the Color
        AllLabels = new Array<>();
        AllLabels.add(Savegame1,Savegame2,Savegame3,Savegame4);
        AllLabels.add(Savegame5,Savegame6,Savegame7,Savegame8);
        AllLabels.add(Savegame9,Savegame10);
        AllLabels.add(SG1Collectibles,SG2Collectibles,SG3Collectibles,SG4UnlockedLevel);
        AllLabels.add(SG1UnlockedLevel,SG2UnlockedLevel,SG3UnlockedLevel);



        // Setup Table
        table.add(Title);
        table.row();
        table.add(SG1).width(200).pad(20).row();
        table.row();
        table.add(SG2).width(200).pad(20).row();
        table.row();
        table.add(SG3).width(200).pad(20).row();
        table.row();
        table.add(SG4).width(200).pad(20).row();
        table.row();
        table.add(SG5).width(200).pad(20).row();
        table.row();
        table.add(SG6).width(200).pad(20).row();
        table.row();
        table.add(SG7).width(200).pad(20).row();
        table.row();
        table.add(SG8).width(200).pad(20).row();
        table.row();
        table.add(SG9).width(200).pad(20).row();
        table.row();
        table.add(SG10).width(200).pad(20).row();


        scrollPane = new ScrollPane(table);
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);

        table.invalidate();
        scrollPane.layout();

        rootTable.setFillParent(true);
        rootTable.add(scrollPane).expand().fill();


        stage.addActor(rootTable);

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
        float scrollSpeed = 200 * Gdx.graphics.getDeltaTime();
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("DOWN_KEY"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            selectedIndex = (selectedIndex + 1) % SaveGameLabels.size;
            float newScrollY = Math.min(scrollPane.getMaxY(), scrollPane.getScrollY() + 100);
            scrollPane.scrollTo(0, newScrollY, scrollPane.getWidth(), scrollPane.getHeight(), false, true);
            System.out.println("Scroll Down - New ScrollY: " + newScrollY);

            updateLabelSelection();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("UP_KEY"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            selectedIndex = (selectedIndex - 1 + SaveGameLabels.size) % SaveGameLabels.size;
            float newScrollY = Math.max(0, scrollPane.getScrollY() - 100);
            scrollPane.setScrollY(newScrollY);
            System.out.println("Scroll Up - New ScrollY: " + newScrollY);

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
