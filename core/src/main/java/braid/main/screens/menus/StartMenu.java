package braid.main.screens.menus;


import braid.main.Braid;
import braid.main.screens.levels.TestLevel;
import braid.main.tools.PreferencesManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class StartMenu extends ScreenAdapter {
    private final Stage stage;
    private final Game game;

    public StartMenu(Game game){
        this.game = game;

        // load Keybindings
        loadDisplayseedings();

        // Setup Viewport
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH, Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);


        // Setup Button
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        TextButton startButton = new TextButton("Start", skin);

        // Setup InputProcessor
        Gdx.input.setInputProcessor(stage);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TestLevel((Braid) game));
            }
        });

        // Setup Table
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(startButton);

    }

    private void loadDisplayseedings(){
        boolean fullscreen = PreferencesManager.getFullscreenPreferences().getBoolean("Fullscreen");
        if (fullscreen){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    private void startGame() {
        game.setScreen(new TestLevel((Braid) game));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int hight) {
        stage.getViewport().update(width, hight, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }
}
