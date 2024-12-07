package braid.main.screens;


import braid.main.Braid;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class RewindHUD implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Camera camera;

    Image rewindImage;

    public RewindHUD(SpriteBatch sb, Sprite sprite) {
        rewindImage = new Image(sprite);

        camera = new OrthographicCamera();
        viewport = new FitViewport(Braid.V_WIDTH, Braid.V_HEIGHT, camera);
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top().right();
        table.setSize(Braid.V_WIDTH, Braid.V_HEIGHT);
        table.setFillParent(true);

        table.add(rewindImage).padTop(20).padRight(20);

        stage.addActor(table);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

