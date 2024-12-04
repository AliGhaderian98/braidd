package braid.main.screens;


import braid.main.Braid;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class StartMenu extends ScreenAdapter {
    private  Stage stage;
    private  Skin skin;
    private final Game game;

    public StartMenu(Game game){
        this.game = game;
    }

    @Override
    public void show() {
        Viewport viewport = new FitViewport(Braid.V_WIDTH, Braid.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport,((Braid) game).batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton startButton = new TextButton("Start", skin);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TestScreen((Braid) game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(startButton).pad(10);
    }

    private void startGame() {
        game.setScreen(new TestScreen((Braid) game));
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
