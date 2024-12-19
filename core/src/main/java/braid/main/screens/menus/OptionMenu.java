package braid.main.screens.menus;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class OptionMenu implements Screen {

    private final Screen previousScreen;
    private final Stage stage;
    private final Game game;
    private final Label Fullscreen, Music, Soundeffekt, Keybindings;
    private final Array<Label> menuLabels;
    private final Boolean Reduced;
    private int selectedIndex = 0;
    private  Label  currentLabel;

    int width;
    int height;

    // Sound
    private final Sound menuSound;

    private final Slider musicSlider;
    private final Slider sfxSlider;

    public OptionMenu(Game game, Screen previousScreen, Boolean Reduced) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.Reduced = Reduced;

        // Setup Screen
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);
        Braid.Fullscreen = (PreferencesManager.getFullscreenPreferences().getBoolean("Fullscreen", Braid.Fullscreen));

        // Setup ScreenRatio
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        KeyBindings.loadKeyBindings();

        // Setup Sound
        menuSound = Audiomanager.audiomanager.get("audio/sound/menuSound.mp3", Sound.class);

        // Set up Slider to control music and soundeffekt volume
        Texture knobTexture = new Texture(Gdx.files.internal("Slider/slider_knob.png"));
        Texture backgroundTexture = new Texture(Gdx.files.internal("Slider/slider_background.png"));

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));

        sliderStyle.background.setMinHeight(20);
        sliderStyle.knob.setMinHeight(20);


        musicSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        sfxSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        musicSlider.setValue(8);
        sfxSlider.setValue(8);

        //  Create Table
        Table table = new Table();
        table.center();
        table.setFillParent(true);


        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(TextFontManager.gettextFont(), Color.GRAY);


        // Setup Title and Options
        Label Options = new Label("Options", TitelFont);
        Options.setFontScale(2);
        Fullscreen = new Label("Fullscreen", SelectionFont);
        Music = new Label("Music", SelectionFont);
        Soundeffekt = new Label("Sound Effects", SelectionFont);
        Keybindings = new Label("Keybindings", SelectionFont);




        // fill Array with Labels to target a Label
        menuLabels = new Array<>();
        menuLabels.add(Music, Soundeffekt,Fullscreen, Keybindings);



        // Setup Table
        table.add(Options);
        table.row();
        table.add(Music);
        table.row();
        table.add(musicSlider).width(500);
        table.row();
        table.add(Soundeffekt).padTop(50);
        table.row();
        table.add(sfxSlider).width(500);
        table.row();
        table.add(Fullscreen).padTop(50);
        table.row();
        table.add(Keybindings);
        loadSlider();

        stage.addActor(table);

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
        handleInput();
        updateScreenRatio();
        stage.draw();
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
        if(Gdx.input.isKeyJustPressed(KeyBindings.getKey("LEFT_KEY"))){
            moveSlider(-.1f);
        }
        if(Gdx.input.isKeyJustPressed(KeyBindings.getKey("RIGHT_KEY"))){
            moveSlider(.1f);
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ENTER"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            executeSelectedAction();
        }
        if (Gdx.input.isKeyJustPressed(KeyBindings.getKey("ESC"))) {
            menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            LevelScreen.gameIsPaused = false;
            game.setScreen(new PauseMenu(game, previousScreen,Reduced));
            dispose();
        }
    }

    private void updateLabelSelection() {
        // show visually selected Element
        for (int i = 0; i < menuLabels.size; i++) {
            Label label = menuLabels.get(i);
            if (i == selectedIndex) {
                label.setColor(Color.WHITE); // selected
                currentLabel = label;
            } else {
                label.setColor(Color.GRAY); // not selected
            }

            // mark Slider if the Label is selected
            if (currentLabel == Music){
                musicSlider.setColor(Color.WHITE);
                sfxSlider.setColor(Color.GRAY);
            }else if(currentLabel == Soundeffekt){
                sfxSlider.setColor(Color.WHITE);
                musicSlider.setColor(Color.GRAY);

            }else {
                musicSlider.setColor(Color.GRAY);
                sfxSlider.setColor(Color.GRAY);
            }
        }
    }

    private void executeSelectedAction() {
        // execute targeted Option
        Label selectedLabel = menuLabels.get(selectedIndex);

        if (selectedLabel == Fullscreen) {
            toggleFullscreen();

        } else if (selectedLabel == Keybindings) {
            game.setScreen(new KeybindsMenu(game, previousScreen, Reduced));
            dispose();
        }
    }

    // moves the Slider of the selected Slider
    private void moveSlider(float move){
        if(currentLabel == Music){
            musicSlider.setValue(musicSlider.getValue() + move);
        }
        if(currentLabel == Soundeffekt){
            sfxSlider.setValue(sfxSlider.getValue() + move);
        }
        saveSlider();
        loadSlider();
        menuSound.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
    }

    private void toggleFullscreen(){
        if(!Braid.Fullscreen){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            Braid.Fullscreen = true;
        }else {
            Gdx.graphics.setWindowedMode(16*80,9*80);
            Braid.Fullscreen = false;
        }
        PreferencesManager.getFullscreenPreferences().putBoolean("Fullscreen",Braid.Fullscreen);
        PreferencesManager.getFullscreenPreferences().flush();
    }

    private void saveSlider(){
        PreferencesManager.getSliderPreferences().putFloat("sfxSlider", sfxSlider.getValue());
        PreferencesManager.getSliderPreferences().putFloat("musicSlider",musicSlider.getValue());
        PreferencesManager.getSliderPreferences().flush();
    }

    private void loadSlider(){
        sfxSlider.setValue(PreferencesManager.getSliderPreferences().getFloat("sfxSlider", sfxSlider.getValue()));
        musicSlider.setValue(PreferencesManager.getSliderPreferences().getFloat("musicSlider",musicSlider.getValue()));
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
