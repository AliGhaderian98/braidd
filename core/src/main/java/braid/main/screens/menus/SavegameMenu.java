package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.screens.levels.TestLevel;
import braid.main.tools.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class SavegameMenu implements Screen{

    private final Stage stage, overlay;
    private final Braid game;
    private final ScrollPane scrollPane;
    private final Table table, rootTable, overlayTable;
    public static int currentSavegamKey;
    private TextField textField;

    int width;
    int height;


    // Labels show on Screen
    private Label[] savegames, collectibles, unlockedLevels;
    private Group[] verticalGroups;
    private int totalSavegame;
    private Label newSaveGame,nomoreSaveGames, title, overlayTitle;

    // used to target a Label
    private final Array<Label> saveGameLabels;
    private final Array<Label> allLabels;
    private int selectedIndex = 0;
    private Label currentLabel;

    // Sound
    private final Sound menuSound;

    private boolean overlayActive;
    public static String newSavegameName;


    public SavegameMenu(Braid game) {
        this.game = game;

        // disable Overlay first
        overlayActive = false;

        // Setup Screen
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);
        overlay = new Stage(viewport,((Braid) game).batch);

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
        overlayTable = new Table();

        // Create Textfield
        Skin textFieldskin = new Skin(Gdx.files.internal("uiskin.json"));
        textField = new TextField("", textFieldskin);
        textField.setMessageText("");
        textField.getStyle().font.getData().setScale(3f);



        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.getmidTextFont(), Color.GRAY);
        Label.LabelStyle LittleSelectionFont = new Label.LabelStyle(TextFontManager.getlittleTextFont(), Color.GRAY);

        // Setup Title and footnote and OverlayTitle
        overlayTitle = new Label("Enter a Name for your Savegame", SelectionFont);
        title = new Label("Save games", TitelFont);
        newSaveGame = new Label("new Savegame", SelectionFont);
        nomoreSaveGames = new Label("only 10 Savegames possible", LittleSelectionFont);
        nomoreSaveGames.setColor(Color.RED);

        title.setFontScale(2);

        // create Labels and Groups
        savegames = new Label[11];
        collectibles = new Label[11];
        unlockedLevels = new Label[11];
        verticalGroups = new VerticalGroup[11];

        saveGameLabels = new Array<>();
        allLabels = new Array<>();


        for (int i = 1; i <= totalSavegame; i++) {
            if (Savemanager.existGame(i)) {

                // load saved Contend for this Savegame
                Savemanager.currentsavegame = Savemanager.loadGame(i);

                // create Labels
                savegames[i] = new Label(Savemanager.getSavegameName(Savemanager.currentsavegame), SelectionFont);
                collectibles[i] = new Label("Collectibles: " + Savemanager.AmountFoundCollectables(Savemanager.currentsavegame) + "/50", LittleSelectionFont);
                unlockedLevels [i] = new Label("Unlocked Levels: " + Savemanager.AmountUnlockedLevels(Savemanager.currentsavegame) + "/10", LittleSelectionFont);

                // add Labels to Groups
                verticalGroups[i] = new VerticalGroup();
                verticalGroups[i].addActor(savegames[i]);
                verticalGroups[i].addActor(collectibles[i]);
                verticalGroups[i].addActor(unlockedLevels[i]);

                // fill Array with Labels to target a Label
                saveGameLabels.add(savegames[i]);

                // fill Array with all Labels to reset the Color
                allLabels.add(savegames[i]);
                allLabels.add(collectibles[i]);
                allLabels.add(unlockedLevels[i]);
            }
        }



        // Setup Table
        table.add(title);

        for (int i = 1; i <= 10; i++) {
            table.row();
            table.add(verticalGroups[i]).width(200).pad(20).row();
        }

        // add new Savegame/ Warning to Tablestructure
        if(totalSavegame < 10){
            saveGameLabels.add(newSaveGame);
            allLabels.add(newSaveGame);
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


        // Set up Overlay
        overlayTable.setFillParent(true);
        overlayTable.add(overlayTitle);
        overlayTable.row();
        overlayTable.add(textField).width(1000).height(100);
        overlay.addActor(overlayTable);


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
        if (overlayActive){
            overlay.draw();
        }else {
            stage.draw();
        }
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
        selectedIndex = (selectedIndex + 1) % saveGameLabels.size;

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
        selectedIndex = (selectedIndex - 1 + saveGameLabels.size) % saveGameLabels.size;

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
        for (int i = 0; i < allLabels.size; i++) {
             allLabels.get(i).setColor(Color.GRAY);
        }

        // Set current Label
        for (int i = 0; i < saveGameLabels.size; i++) {
            Label label = saveGameLabels.get(i);
            if (i == selectedIndex) {
                currentLabel = label;
            }
        }

        // mark the Selected Savegame with the Attributes white
        if (currentLabel.equals(savegames[1])) {
            setGroupColor(verticalGroups[1]);
        } else if (currentLabel.equals(savegames[2])) {
            setGroupColor(verticalGroups[2]);
        } else if (currentLabel.equals(savegames[3])) {
            setGroupColor(verticalGroups[3]);
        } else if (currentLabel.equals(savegames[4])) {
            setGroupColor(verticalGroups[4]);
        } else if (currentLabel.equals(savegames[5])) {
            setGroupColor(verticalGroups[5]);
        } else if (currentLabel.equals(savegames[6])) {
            setGroupColor(verticalGroups[6]);
        } else if (currentLabel.equals(savegames[7])) {
            setGroupColor(verticalGroups[7]);
        } else if (currentLabel.equals(savegames[8])) {
            setGroupColor(verticalGroups[8]);
        } else if (currentLabel.equals(savegames[9])) {
            setGroupColor(verticalGroups[9]);
        } else if (currentLabel.equals(savegames[10])) {
            setGroupColor(verticalGroups[10]);
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
        if(overlayActive){
            // TODO hier läuft irgendwas schief
            // Start new Game with defined Name for the Savegame
            newSavegameName = textField.getText();
            System.out.println("Eingabe: " + newSavegameName);

            overlay.unfocus(textField);
            Gdx.input.setInputProcessor(null);
            overlayActive =false;

            textField.setDisabled(true);

            startOverWorld(totalSavegame + 1);
            dispose();

        } else {

            if (currentLabel == newSaveGame){
                // a new Savegame was targeted -> User can enter a Name for his Savegame
                overlayActive = true;
                Gdx.input.setInputProcessor(overlay);
                overlay.setKeyboardFocus(textField);
            } else {
                // an existing Savegame was targeted
                startOverWorld(currentSavegamKey + 1);
                dispose();
            }
        }
    }

    private void startOverWorld(int selectedSavegame) {
        Savemanager.playtimeStart = System.currentTimeMillis();
        currentSavegamKey = selectedSavegame;
        game.setScreen(new Overworld((Braid) game));
    }

    private void CountTotalSavegames(){
        totalSavegame =0;
        for(int i=1;i<=10;i++){
            if(Savemanager.existGame(i)){
                totalSavegame++;
            }
        }
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
        overlay.dispose();
    }
}
