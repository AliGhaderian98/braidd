package braid.main.screens.menus;


import braid.main.Braid;
import braid.main.overworld.Overworld;
import braid.main.screens.huds.LevelHUD;
import braid.main.screens.levels.TestLevel;
import braid.main.tools.PreferencesManager;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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


        // load Bindings
        loadDisplayseedings();
        loadTimervisabile();

        // Setup Viewport
        Viewport viewport = new ExtendViewport(Braid.V_WIDTH, Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);

        // Setup Label Styles
        Label.LabelStyle TitelFont = new Label.LabelStyle(TextFontManager.gettextFont(), Braid.BUWColor);

        // Setup lable
        Label Startmessage = new Label("Press ENTER to Proceed", TitelFont);

        // Setup Table
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(Startmessage);

    }

    private void loadDisplayseedings(){
        boolean fullscreen = PreferencesManager.getFullscreenPreferences().getBoolean("Fullscreen");
        if (fullscreen){
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    private void startGame() {
        game.setScreen(new Overworld((Braid) game));
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

    public static void loadTimervisabile(){
        LevelHUD.setTimerVisible(PreferencesManager.getcuntdownsettingsPreferences().getBoolean("Countdownpreferences"));
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
