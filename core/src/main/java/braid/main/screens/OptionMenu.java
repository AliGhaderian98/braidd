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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class OptionMenu implements Screen {

    private final Screen previusScreen;
    private final Stage stage;
    private final Game game;
    private final Label Resolution, Fullscreen, Music, Soundeffekt, Keybindings;
    private final Array<Label> menuLabels;
    private int selectedIndex = 0;
    private  Label  currentLabel;

    Slider musicSlider;
    Slider soundeffectSlider;

    public OptionMenu(Game game, Screen previusScreen) {
        // Setup Screen
        this.game = game;
        this.previusScreen = previusScreen;
        Viewport viewport = new FitViewport(Braid.V_WIDTH,Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);

        KeyBindings.loadKeyBindings();

        // Set up Slider to control music and soundeffekt volume
        Texture knobTexture = new Texture(Gdx.files.internal("Slider/slider_knob.png"));
        Texture backgroundTexture = new Texture(Gdx.files.internal("Slider/slider_background.png"));

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));

        sliderStyle.background.setMinHeight(3);
        sliderStyle.knob.setMinHeight(3);
        sliderStyle.knob.setMinWidth(3);

        musicSlider = new Slider(0, 100, 1, false, sliderStyle);
        soundeffectSlider = new Slider(0, 100, 1, false, sliderStyle);
        musicSlider.setValue(50);
        soundeffectSlider.setValue(50);

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
        Soundeffekt = new Label("Soundeffekt", SelectionFont);
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
        table.add(soundeffectSlider);
        table.row();
        table.add(Keybindings);

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
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            moveSlider(-1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            moveSlider(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeSelectedAction();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            TestScreen.gameIsPaused = false;
            game.setScreen(new PauseScreen(game,previusScreen));
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
            // TODO switch Fullscreen or back

        } else if (selectedLabel == Music) {
            // TODO Music Slider

        } else if (selectedLabel == Soundeffekt) {
            // TODO Soundeffekt Slider

        } else if (selectedLabel == Keybindings) {
            game.setScreen(new KeybindsScreen(game,previusScreen));
            dispose();
        }
    }

    private void moveSlider(float move){
        if(currentLabel == Music){
            musicSlider.setValue(musicSlider.getValue() + move);
        }
        if(currentLabel == Soundeffekt){
            soundeffectSlider.setValue(soundeffectSlider.getValue() + move);
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
