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

    private static Integer collectedPages;
    private static Integer maxPages;
    private final Label timerLabel;
    private static Label pageLabel;
    Image pageImage;

    private final long startTime;
    private long pauseTime;
    private long tempTimeStamp;
    private static long elapsedTime;

    private static boolean timerVisible;



    public LevelHUD(SpriteBatch batch, TextureAtlas atlas, int maxPages){

        startTime = System.currentTimeMillis();
        pauseTime = 0;
        tempTimeStamp = System.currentTimeMillis();
        elapsedTime = 0;
        collectedPages = 0;
        LevelHUD.maxPages = maxPages;
        viewport = new FitViewport(Braid.V_WIDTH,Braid.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,batch);

        pageImage = new Image(atlas.findRegion("page"));
        pageImage.setScale(8,8);

        Label.LabelStyle TextFont = new Label.LabelStyle(TextFontManager.gettextFont(), Color.WHITE);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        timerLabel = new Label("00.00", TextFont);
        pageLabel = new Label(String.format("%d/%d", collectedPages, LevelHUD.maxPages), TextFont);

        Table scoreTable = new Table();
        scoreTable.add(pageLabel);
        scoreTable.add(pageImage).padLeft(40).padRight(160).padTop(160);

        table.add(timerLabel).expandX().align(Align.left).pad(100);
        table.add(scoreTable).expandX().align(Align.right).pad(100);

        timerLabel.setVisible(timerVisible);

        stage.addActor(table);
    }

    public void update(float dt){
        elapsedTime = System.currentTimeMillis() - (startTime+pauseTime);

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

        if (timerVisible != timerLabel.isVisible())
            timerLabel.setVisible(timerVisible);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public static void addScore (int value){
        collectedPages += value;
        pageLabel.setText(String.format("%d/%d", collectedPages, maxPages));
    }

    public static boolean isTimerVisible() { return timerVisible; }

    public static void setTimerVisible(boolean value) { timerVisible = value; }

    public void pause() {
        tempTimeStamp = System.currentTimeMillis();
    }

    public void resume() {
        pauseTime += System.currentTimeMillis() - tempTimeStamp;
    }


    public void dispose(){stage.dispose();}

    public static int getCollectedPages() { return collectedPages; }
    public static int getMaxPages() { return maxPages; }
    public static long getElapsedTime() { return elapsedTime; }

}
