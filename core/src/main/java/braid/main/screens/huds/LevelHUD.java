package braid.main.screens.huds;

import braid.main.Braid;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class LevelHUD implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private static Integer score;
    private final Label timerLabel;
    private static Label scoreLabel;
    Image pageImage;

    private final long startTime;
    private long elapsedTime;



    public LevelHUD(SpriteBatch batch, TextureAtlas atlas){
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
        score = 0;
        viewport = new FitViewport(Braid.V_WIDTH,Braid.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,batch);

        pageImage = new Image(atlas.findRegion("page"));
        pageImage.setScale(8,8);

        Label.LabelStyle TextFont = new Label.LabelStyle(TextFontManager.gettextFont(), Color.WHITE);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        timerLabel = new Label("00.00", TextFont);
        scoreLabel = new Label(String.format("%d",score), TextFont);

        Table scoreTable = new Table();
        scoreTable.add(scoreLabel);
        scoreTable.add(pageImage).padLeft(40).padRight(160).padTop(160);

        table.add(timerLabel).expandX().align(Align.left).pad(100);
        table.add(scoreTable).expandX().align(Align.right).pad(100);

        stage.addActor(table);
    }

    public void update(float dt){
        elapsedTime = System.currentTimeMillis() - startTime;

        // Convert elapsed time to hours, minutes, seconds, and milliseconds
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long seconds = (elapsedTime / 1000) % 60;
        long milliseconds = (elapsedTime % 1000)/10;

        // Build the formatted time string dynamically to only include a segment if it is larger than 0
        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(String.format("%d:", hours));
        }
        if (minutes > 0 || hours > 0) {
            formattedTime.append(String.format("%02d:", minutes));
        }
        if (seconds > 0 || minutes > 0 || hours > 0) {
            formattedTime.append(String.format("%02d", seconds));
        }
        if (milliseconds > 0) {
            if (!formattedTime.isEmpty()) {
                formattedTime.append(".");
            }
            formattedTime.append(String.format("%02d", milliseconds));
        }

        timerLabel.setText(formattedTime);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public static void addScore (int value){
        score += value;
        scoreLabel.setText(String.format("%d",score));
    }

    public boolean isTimerVisible() { return timerLabel.isVisible(); }

    public void setTimerVisible(boolean value) {
        timerLabel.setVisible(value);
    }



    public void dispose(){stage.dispose();}

}
