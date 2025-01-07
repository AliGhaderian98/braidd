package braid.main.screens.huds;

import braid.main.Braid;
import braid.main.Items.PowerUp;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Objects;


public class LevelHUD implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private static Integer collectedPages;
    private static Integer maxPages;
    private final Label timerLabel;
    private static Label pageLabel;
    private Image pageImage;

    private Image powerUpImage;
    private Image powerUpBackground;
    private Image powerUpTimer;
    private Animation<TextureRegion> powerUpBackgroundAnimation;
    private Animation<TextureRegion> powerUpTimerAnimation;
    private static float stateTime = 0;
    private SpriteDrawable ritalin;
    private SpriteDrawable gleiter;
    private SpriteDrawable hammer;
    private Stack powerUp;
    private static String activePowerUp;
    private static boolean justActivated;

    private static boolean powerUpActive;

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
        pageImage.setScale(8);

        powerUpActive = false;

        powerUpBackgroundAnimation = new Animation<>(0.1f, atlas.findRegions("ball"), Animation.PlayMode.LOOP);
        powerUpBackground = new Image(powerUpBackgroundAnimation.getKeyFrame(0));
        powerUpBackground.setScale(7);
        powerUpBackground.setOrigin(Align.center);

        powerUpTimerAnimation = new Animation<>(PowerUp.getMaxTime()/13f, atlas.findRegions("circle-timer"), Animation.PlayMode.NORMAL);
        powerUpTimer = new Image(powerUpTimerAnimation.getKeyFrame(0));
        powerUpTimer.setScale(3f);
        powerUpTimer.setSize(powerUpBackground.getWidth(), powerUpBackground.getHeight());
        powerUpTimer.setOrigin(Align.center);

        ritalin = new SpriteDrawable(atlas.createSprite("ritalin"));
        gleiter = new SpriteDrawable(atlas.createSprite("balloon-small"));
        hammer = new SpriteDrawable(atlas.createSprite("hammer"));
        powerUpImage = new Image();
        powerUpImage.setScale(2f);
        powerUpImage.setDrawable(ritalin);
        powerUpImage.setSize(powerUpBackground.getWidth(), powerUpBackground.getHeight());
        powerUpImage.setOrigin(Align.center);

        powerUp = new Stack();
        powerUp.add(powerUpBackground);
        powerUp.add(powerUpTimer);
        powerUp.add(powerUpImage);

        Label.LabelStyle TextFont = new Label.LabelStyle(TextFontManager.gettextFont(150), Color.WHITE);

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

        table.row();
        table.add(powerUp).colspan(2).expandX().align(Align.right).padRight(180).padTop(60);

        timerLabel.setVisible(timerVisible);
        powerUp.setVisible(false);

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

        if (powerUpActive != powerUp.isVisible())
            powerUp.setVisible(powerUpActive);

        if (justActivated) {
            setActivePowerUp();
            justActivated = false;
        }

        if (powerUpActive) {
            stateTime += dt;
            TextureRegion currentFrame = powerUpBackgroundAnimation.getKeyFrame(stateTime);
            powerUpBackground.setDrawable(new Image(currentFrame).getDrawable());
            currentFrame = powerUpTimerAnimation.getKeyFrame(stateTime);
            powerUpTimer.setDrawable(new Image(currentFrame).getDrawable());
        }
    }

    public static void activatePowerUp(String type) {
        powerUpActive = true;
        justActivated = true;
        activePowerUp = type;
        stateTime = 0f;
    }

    public static void resetPowerUp() {
        powerUpActive = false;
    }

    private void setActivePowerUp() {
        switch(activePowerUp) {
            case "ritalin" -> powerUpImage.setDrawable(ritalin);
            case "gleiter" -> powerUpImage.setDrawable(gleiter);
            case "hammer" -> powerUpImage.setDrawable(hammer);
        }
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
