package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.tools.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Objects;


public class SavegameMenu implements Screen{

    private final Stage stage, overlay;
    private final Braid game;
    private final ScrollPane scrollPane;
    private final Table table, rootTable, overlayTable;
    public static int currentSavegamKey;
    private boolean rdyToDelete;

    int width;
    int height;


    // Labels show on Screen
    private Label[] savegames, collectibles, unlockedLevels, playtimes;
    private Image[] trashcans;
    private Group[] verticalGroups;
    private Label newSaveGame,nomoreSaveGames, title, overlayTitle, noNameWaring;
    private int totalSavegame;
    private TextField textField;



    // used to target a Label
    private final Array<Label> saveGameLabels;
    private final Array<Label> allLabels;
    private int selectedIndex = 0;
    private Label currentLabel;

    // Sound
    private final Sound menuSound;

    // Textures
    private final Texture texturetrashcan, textureopenTrashcan;
    private final Sprite trashcan, opentrashcan;

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

        totalSavegame = Savemanager.CountTotalSavegames();

        //  Create Table
        table = new Table();
        rootTable = new Table();
        overlayTable = new Table();

        // Create TrashcanSkin
        texturetrashcan = new Texture(Gdx.files.internal("MenuDirectory/TrashCan/TrashCan.png"));
        textureopenTrashcan = new Texture(Gdx.files.internal("MenuDirectory/TrashCan/TrashCanOpen.png"));
        trashcan = new Sprite(texturetrashcan);
        opentrashcan = new Sprite(textureopenTrashcan);



        // Create Textfield
        Skin textFieldskin = new Skin(Gdx.files.internal("uiskin.json"));
        textField = new TextField("", textFieldskin);
        textField.setMessageText("");
        textField.getStyle().font.getData().setScale(3f);



        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(150), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(80), Color.GRAY);
        Label.LabelStyle LittleSelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(60), Color.GRAY);
        Label.LabelStyle TinySelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(50), Color.GRAY);


        // Setup Title and footnote and OverlayTitle
        overlayTitle = new Label("Enter a Name for your Savegame", SelectionFont);
        noNameWaring = new Label("You need to Enter a Name!",LittleSelectionFont);
        noNameWaring.setColor(new Color(Color.RED));
        title = new Label("Save games", TitelFont);
        newSaveGame = new Label("new Savegame", SelectionFont);
        nomoreSaveGames = new Label("only 10 Savegames possible", LittleSelectionFont);
        nomoreSaveGames.setColor(Color.RED);

        title.setFontScale(2);

        // create Labels and Groups
        savegames = new Label[11];
        collectibles = new Label[11];
        unlockedLevels = new Label[11];
        playtimes = new Label[11];
        trashcans = new Image[11];

        verticalGroups = new VerticalGroup[11];

        saveGameLabels = new Array<>();
        allLabels = new Array<>();


        for (int i = 1; i <= 11; i++) {
            if (Savemanager.existGame(i)) {

                // load saved Contend for this Savegame
                Savemanager.currentsavegame = Savemanager.loadGame(i);


                // create Labels
                savegames[i] = new Label(Savemanager.getSavegameName(Savemanager.currentsavegame), SelectionFont);
                collectibles[i] = new Label("Collectibles: " + Savemanager.AmountFoundCollectables(Savemanager.currentsavegame) + "/50", LittleSelectionFont);
                unlockedLevels [i] = new Label("Unlocked Levels: " + Savemanager.AmountUnlockedLevels(Savemanager.currentsavegame) + "/10", LittleSelectionFont);
                playtimes [i] = new Label("Time Played: " + Savemanager.getSavegamePlaytimeTOString(Savemanager.currentsavegame), TinySelectionFont);
                trashcans[i] = new Image(new TextureRegionDrawable(new TextureRegion(trashcan.getTexture())));

                // add Labels to Groups
                verticalGroups[i] = new VerticalGroup();
                verticalGroups[i].addActor(savegames[i]);
                verticalGroups[i].addActor(collectibles[i]);
                verticalGroups[i].addActor(unlockedLevels[i]);
                verticalGroups[i].addActor(playtimes[i]);


                // fill Array with Labels to target a Label
                saveGameLabels.add(savegames[i]);

                // fill Array with all Labels to reset the Color
                allLabels.add(savegames[i]);
                allLabels.add(collectibles[i]);
                allLabels.add(unlockedLevels[i]);
                allLabels.add(playtimes[i]);
            }
        }



        // Setup Table
        table.add(title).colspan(2).center().padBottom(20);;

        for (int i = 1; i <= 10; i++) {
            if (verticalGroups[i] != null) {
                table.row();
                table.add(verticalGroups[i]).right().pad(10);
                table.add(trashcans[i]).size(50).pad(10).left();
            }
        }
        table.row();

        // add new Savegame/ Warning to Tablestructure
        if(totalSavegame < 10){
            saveGameLabels.add(newSaveGame);
            allLabels.add(newSaveGame);
            table.add(newSaveGame).colspan(2).center().padTop(20);;
        }else{
            table.add(nomoreSaveGames).colspan(2).center().padTop(20);;
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
        overlayTable.add(textField).width(1000).height(100).row();
        overlayTable.add(noNameWaring);
        noNameWaring.setVisible(false);
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
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("RIGHT_KEY"))) {
            handleGoingRIGHT();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("LEFT_KEY"))) {
            handleGoingLEFT();
        }
        /*if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            game.setScreen(new PauseMenu(game, this,true));
        }*/
    }

    private void handleGoingLEFT() {
        if(currentLabel != newSaveGame && rdyToDelete && !overlayActive){
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            trashcans[selectedIndex + 1].setDrawable(new TextureRegionDrawable(new TextureRegion(trashcan.getTexture())));
        }
        rdyToDelete = false;
    }

    private void handleGoingRIGHT() {
        if(currentLabel != newSaveGame && !rdyToDelete && !overlayActive){
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            trashcans [selectedIndex + 1].setDrawable(new TextureRegionDrawable(new TextureRegion(opentrashcan.getTexture())));
            rdyToDelete = true;
        }
    }


    // scrolling down
    private void handleGoingDown() {
        rdyToDelete = false;
        menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
        selectedIndex = (selectedIndex + 1) % saveGameLabels.size;

        if (selectedIndex  > 4) {
            scrollPane.setScrollY(1400);
            scrollPane.layout();
        } else if (selectedIndex < 4) {
            scrollPane.setScrollY(0);
            scrollPane.layout();
        }
        updateLabelSelection();
    }

    // scrolling up
    private void handleGoingUp() {
        rdyToDelete = false;
        menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
        selectedIndex = (selectedIndex - 1 + saveGameLabels.size) % saveGameLabels.size;

        if (selectedIndex < 5) {
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

        // Hide every trashcan
        for(int i=1; totalSavegame>=i;i++){
            if(trashcans [i] != null) {
                trashcans[i].setDrawable(new TextureRegionDrawable(new TextureRegion(trashcan.getTexture())));
                trashcans[i].setVisible(false);
            }
        }

        // Set current Label
        for (int i = 0; i < saveGameLabels.size; i++) {
            Label label = saveGameLabels.get(i);
            if (i == selectedIndex) {
                currentLabel = label;
            }
        }
        // set trashcan for current savegame to visible
        if(trashcans [selectedIndex + 1] != null){
            trashcans [selectedIndex + 1].setVisible(true);
        }

        // mark the Selected Savegame with the Attributes white
        for (int i=1;i<=10;i++){
            if (currentLabel.equals(savegames[i])) {
                setGroupColor(verticalGroups[i]);
                currentSavegamKey=i;
            }
        }
       if(currentLabel.equals(newSaveGame)){
            newSaveGame.setColor(Color.WHITE);
            currentSavegamKey = totalSavegame+1;
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
        if (overlayActive) {
            // Start new Game with defined Name for the Savegame
            if(Objects.equals(textField.getText(), "")){
                noNameWaring.setVisible(true);
            }else {
                newSavegameName = textField.getText();
                overlay.unfocus(textField);
                Gdx.input.setInputProcessor(null);
                overlayActive = false;
                textField.setDisabled(true);

                startnewOverworld();
                dispose();
            }
        } else {
            if (currentLabel == newSaveGame) {
                // a new Savegame was targeted -> User can enter a Name for his Savegame
                overlayActive = true;
                Gdx.input.setInputProcessor(overlay);
                overlay.setKeyboardFocus(textField);

            } else if (rdyToDelete) {
                // delete Savegame and relode screen
                Savemanager.deleteSavegame(currentSavegamKey);

                if(Savemanager.CountTotalSavegames() != 0){
                    Savemanager.rebalenceSavegames();
                }
                game.setScreen(new SavegameMenu(game));

            } else {
                // an existing Savegame was targeted
                startOverWorld(currentSavegamKey);
                dispose();
            }

        }
    }

    private void startnewOverworld() {
        int freeSpot = Savemanager.findOpenspot();
        startOverWorld(freeSpot);
    }

    private void startOverWorld(int selectedSavegame) {
        Savemanager.playtimeStart = System.currentTimeMillis();
        currentSavegamKey = selectedSavegame;
        Savemanager.currentsavegame = Savemanager.loadGame(selectedSavegame);

        game.setScreen(new Overworld((Braid) game));
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
        stage.dispose();
        overlay.dispose();
    }
}
