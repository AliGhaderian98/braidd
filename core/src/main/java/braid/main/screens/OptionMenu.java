package braid.main.screens;

import braid.main.Braid;
import braid.main.tools.Audiomanager;
import braid.main.tools.KeyBindings;
import braid.main.tools.PreferencesManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

//todo: fullscreen einstellung speichern
public class OptionMenu implements Screen {

    private final TestScreen previusScreen;
    private final Stage stage;
    private final Game game;
    private final Label Resolution, Fullscreen, Music, Soundeffekt, Keybindings;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;
    private boolean isInFullscreen;
    private  Label  currentLabel;

    // Sound
    private final Sound menuSound;

    Slider musicSlider;
    Slider sfxSlider;

    public OptionMenu(Game game, TestScreen previusScreen) {
        // Setup Screen
        this.game = game;
        this.previusScreen = previusScreen;
        Viewport viewport = new FitViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);
        isInFullscreen = false;


        KeyBindings.loadKeyBindings();

        // Setup Sound
        menuSound = Audiomanager.audiomanager.get("audio/sound/menuSound.mp3", Sound.class);

        // Set up Slider to control music and soundeffekt volume
        Texture knobTexture = new Texture(Gdx.files.internal("Slider/slider_knob.png"));
        Texture backgroundTexture = new Texture(Gdx.files.internal("Slider/slider_background.png"));

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));

        sliderStyle.background.setMinHeight(3);
        sliderStyle.knob.setMinHeight(3);
        sliderStyle.knob.setMinWidth(3);

        musicSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        sfxSlider = new Slider(0, 1, 0.1f, false, sliderStyle);
        musicSlider.setValue(8);
        sfxSlider.setValue(8);

        //  Create Table
        Table table = new Table();
        table.center();
        table.setFillParent(true);


        // Setup Label styles for Title and Options
        Label.LabelStyle TitelFont = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        Label.LabelStyle SelectionFont = new Label.LabelStyle(new BitmapFont(), Color.GRAY);


        // Setup Title and Options
        Label Options = new Label("Options", TitelFont);
        Options.setFontScale(2);
        Resolution = new Label("Resolution", SelectionFont);
        Fullscreen = new Label("Fullscreen", SelectionFont);
        Music = new Label("Music", SelectionFont);
        Soundeffekt = new Label("Sound Effects", SelectionFont);
        Keybindings = new Label("Keybindings", SelectionFont);




        // fill Array with Labels to target a Label
        menuLabels = new Array<>();
        menuLabels.add(Resolution,Fullscreen,Music, Soundeffekt);
        menuLabels.add(Keybindings);



        // Setup Table
        table.add(Options).expandX();
        table.row();
        table.add(Resolution).expandX().padTop(10f);
        table.row();
        table.add(Fullscreen);
        table.row();
        table.add(Music);
        table.row();
        table.add(musicSlider);
        table.row();
        table.add(Soundeffekt);
        table.row();
        table.add(sfxSlider);
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
            TestScreen.gameIsPaused = false;
            game.setScreen(new PauseMenu(game,previusScreen));
            dispose();
        }
    }

    private void updateLabelSelection() {
        // show visually selected Element
        for (int i = 0; i < menuLabels.size; i++) {
            Label label = menuLabels.get(i);
            if (i == selectedIndex) {
                label.setColor(Color.YELLOW); // selected
                currentLabel = label;
            } else {
                label.setColor(Color.WHITE); // not selected
            }
        }
    }

    private void executeSelectedAction() {
        // execute yellow targeted Option
        Label selectedLabel = menuLabels.get(selectedIndex);

        if (selectedLabel == Resolution) {
            // TODO Resolution Drop down menu

        } else if (selectedLabel == Fullscreen) {
            toggleFullscreen();

        } else if (selectedLabel == Keybindings) {
            game.setScreen(new KeybindsMenu(game,previusScreen));
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
        if(!isInFullscreen){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            isInFullscreen = true;
        }else {
            Gdx.graphics.setWindowedMode(800,600);
            isInFullscreen = false;
        }
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
