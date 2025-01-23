package braid.main.screens.huds;

import braid.main.Braid;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.w3c.dom.Text;

public class FinishHUD implements Disposable {
    public Stage stage;
    private Viewport viewport;

    protected Label.LabelStyle TextFont;

    protected Image pageImage;
    protected Label finishLabel;
    protected Label pageLabel;
    protected Label timeLabel;
    protected Table table;


    public FinishHUD(SpriteBatch batch, TextureAtlas atlas) {
        viewport = new FitViewport(Braid.V_WIDTH,Braid.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,batch);

        pageImage = new Image(atlas.findRegion("page"));
        pageImage.setScale(10,10);

        TextFont = new Label.LabelStyle(TextFontManager.gettextFont(150), Color.WHITE);

        finishLabel = new Label("LEVEL FINISHED!", TextFont);
        finishLabel.setFontScale(2);

        timeLabel = new Label("", TextFont);
        pageLabel = new Label(String.format("collected pages: %d/%d",
            (LevelHUD.getCollectedPages() + LevelHUD.getPreviouslyCollectedPages()),
            (LevelHUD.getMaxPages() + LevelHUD.getPreviouslyCollectedPages())),
            TextFont);
        timeLabel.setText("time: "+formatTime(LevelHUD.getElapsedTime()));

        Table scoreTable = new Table();
        scoreTable.add(pageLabel);
        scoreTable.add(pageImage).padLeft(40).padRight(160).padTop(160);

        table = new Table();
        table.center();
        table.setFillParent(true);

        table.add(finishLabel).expandX();
        table.row();
        table.add(scoreTable).padTop(300);
        table.row();
        table.add(timeLabel).padTop(100);

        stage.addActor(table);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }


    protected String formatTime(long time) {
        long hours = (time / (1000 * 60 * 60)) % 24;
        long minutes = (time / (1000 * 60)) % 60;
        long seconds = (time / 1000) % 60;
        long milliseconds = (time % 1000)/10;

        return String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds);
    }

    public void updateHUD() {
        timeLabel.setText("time: "+formatTime(LevelHUD.getElapsedTime()));
        pageLabel.setText(String.format("collected pages: %d/%d", LevelHUD.getCollectedPages(), LevelHUD.getMaxPages()));
    }


    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getCamera().update();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
