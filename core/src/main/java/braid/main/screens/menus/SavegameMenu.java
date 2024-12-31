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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;


public class SavegameMenu implements Screen{

    private final Stage stage;
    private final Braid game;
    private final ScrollPane scrollPane;
    private final Table table, rootTable;
    public static int currentSavegamKey;

    int width;
    int height;


    // Labels show on Screen
    private Label[] savegames, collectibles, unlockedLevels;
    private Group[] VerticalGroups;
    private int TotalSavegame;
    private Label newSaveGame,nomoreSaveGames;

    // used to target a Label
    private final Array<Label> SaveGameLabels;
    private final Array<Label> AllLabels;
    private int selectedIndex = 0;
    private Label currentLabel;

    // Sound
    private final Sound menuSound;


    public SavegameMenu(Braid game) {
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

        CountTotalSavegames();

        //  Create Table
        table = new Table();
        rootTable = new Table();


        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.getmidTextFont(), Color.GRAY);
        Label.LabelStyle LittleSelectionFont = new Label.LabelStyle(TextFontManager.getlittleTextFont(), Color.GRAY);

        // Setup Title and Options
        Label Title = new Label("Save games", TitelFont);
        newSaveGame = new Label("new Savegame", SelectionFont);
        nomoreSaveGames = new Label("only 10 Savegames possible", LittleSelectionFont);
        nomoreSaveGames.setColor(Color.RED);

        Title.setFontScale(2);

        // create Labels and Groups
        savegames = new Label[11];
        collectibles = new Label[11];
        unlockedLevels = new Label[11];
        VerticalGroups = new VerticalGroup[11];

        SaveGameLabels = new Array<>();
        AllLabels = new Array<>();


        for (int i = 1; i <= TotalSavegame; i++) {
            // create Labels
            savegames[i] = new Label("Savegame: " + i, SelectionFont);
            collectibles[i] = new Label("Collectibles: " + i, LittleSelectionFont);
            unlockedLevels[i] = new Label("Unlocked Levels: " + i, LittleSelectionFont);

            // add Labels to Groups
            VerticalGroups [i] = new VerticalGroup();
            VerticalGroups [i].addActor(savegames[i]);
            VerticalGroups [i].addActor(collectibles[i]);
            VerticalGroups [i].addActor(unlockedLevels[i]);

            // fill Array with Labels to target a Label
            SaveGameLabels.add(savegames [i]);

            // fill Array with all Labels to reset the Color
            AllLabels.add(savegames [i]);
            AllLabels.add(collectibles [i]);
            AllLabels.add(unlockedLevels [i]);
        }





        // Setup Table
        table.add(Title);

        for (int i = 1; i <= 10; i++) {
            table.row();
            table.add(VerticalGroups [i]).width(200).pad(20).row();
        }

        // add new Savegame/ Warning to Tablestructure
        if(TotalSavegame < 10){
            SaveGameLabels.add(newSaveGame);
            AllLabels.add(newSaveGame);
            table.add(newSaveGame);
        }else{
            table.add(nomoreSaveGames);
        }


        // Set up a ScrollPane to see all Save games
        scrollPane = new ScrollPane(table);
        scrollPane.setScrollingDisabled(true,false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);


        rootTable.setFillParent(true);
        rootTable.add(scrollPane).expand().fill().pad(10);

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
        // navigation with Keys
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("DOWN_KEY"))) {
            handleGoingDown();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("UP_KEY"))) {
            handleGoingUp();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ENTER"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            executeSelectedAction();
        }
        /*if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            LevelScreen.gameIsPaused = false;
            game.setScreen(new PauseMenu(game, this,true));
        }*/
    }

    // scrolling down
    private void handleGoingDown() {
        menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
        selectedIndex = (selectedIndex + 1) % SaveGameLabels.size;

        if (selectedIndex == 5) {
            scrollPane.setScrollY(1400);
            scrollPane.layout();
        } else if (selectedIndex < 5) {
            scrollPane.setScrollY(0);
            scrollPane.layout();
        }
        updateLabelSelection();
    }

    // scrolling up
    private void handleGoingUp() {
        menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
        selectedIndex = (selectedIndex - 1 + SaveGameLabels.size) % SaveGameLabels.size;

        if (selectedIndex == 5) {
            scrollPane.setScrollY(0);
            scrollPane.layout();
        } else if (selectedIndex > 5) {
            scrollPane.setScrollY(1400);
            scrollPane.layout();
        }
        updateLabelSelection();
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
        if (currentLabel.equals(savegames[1])) {
            setGroupColor(VerticalGroups [1]);
        } else if (currentLabel.equals(savegames[2])) {
            setGroupColor(VerticalGroups [2]);
        } else if (currentLabel.equals(savegames[3])) {
            setGroupColor(VerticalGroups [3]);
        } else if (currentLabel.equals(savegames[4])) {
            setGroupColor(VerticalGroups [4]);
        } else if (currentLabel.equals(savegames[5])) {
            setGroupColor(VerticalGroups [5]);
        } else if (currentLabel.equals(savegames[6])) {
            setGroupColor(VerticalGroups [6]);
        } else if (currentLabel.equals(savegames[7])) {
            setGroupColor(VerticalGroups [7]);
        } else if (currentLabel.equals(savegames[8])) {
            setGroupColor(VerticalGroups [8]);
        } else if (currentLabel.equals(savegames[9])) {
            setGroupColor(VerticalGroups [9]);
        } else if (currentLabel.equals(savegames[10])) {
            setGroupColor(VerticalGroups [10]);
        } else if(currentLabel.equals(newSaveGame)){
            newSaveGame.setColor(Color.WHITE);
        }

    }

    private void setGroupColor(Group group){
        for (Actor actor : group.getChildren()) {
            if (actor instanceof Label) {
                actor.setColor(Color.WHITE);
            }
        }
    }


    private void executeSelectedAction() {
        // load targeted SafeGame
        if (currentLabel == newSaveGame){
            startOverWorld(TotalSavegame+1);
            dispose();

        }else {
            startOverWorld(Integer.parseInt(currentLabel.toString().substring(17)));
            dispose();
        }
    }

    private void startOverWorld(int selectedSavegame) {
        Savemanager.playtimeStart = System.currentTimeMillis();
        currentSavegamKey = selectedSavegame;
        game.setScreen(new Overworld((Braid) game));
    }

    private void CountTotalSavegames(){
        TotalSavegame=0;

        for(int i=1;i<=10;i++){
            if(Savemanager.existGame(i)){
                TotalSavegame++;
            }
        }
        System.out.println(TotalSavegame);
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
        table.invalidate();
        scrollPane.layout();
        scrollPane.updateVisualScroll();
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
