package braid.main.screens;

import braid.main.Braid;
import braid.main.Rewind;
import braid.main.RewindableSprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TestScreen implements Screen {
    private final Braid game;
    private Texture image;

    private OrthographicCamera gamecam;
    private Viewport gamePort;

    // Screen
    int worldWidth;
    int worldHeight;

    // Keys
    static int RIGHT_KEY = Input.Keys.D;
    static int LEFT_KEY = Input.Keys.A;
    static int SHIFT = Input.Keys.SHIFT_LEFT;
    static int SPACEBAR = Input.Keys.SPACE;

    // Player variables
    static final int playerSpeed = 5;
    static final int enemySpeed = 3;
    static boolean isJumping = false;
    static final int jumpVelocity = 20;
    static int velocityY;
    static final int gravity = 1;

    private Texture loewe;
    private Sprite loeweSprite;
    private Sprite boeserLoeweSprite;

    private Rewind rewindLoewe;
    private Rewind rewindBoeserLoewe;

    private Array<Rewind> rewindObjects;

    public TestScreen(Braid game) {
        this.game = game;

        worldWidth = Braid.V_WIDTH;
        worldHeight = Braid.V_HEIGHT;

        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(worldWidth / Braid.PPM , worldHeight / Braid.PPM,gamecam);

        image = new Texture("libgdx.png");
        loewe = new Texture("löwe.png");

        loeweSprite = new Sprite(loewe);
        loeweSprite.setSize(loeweSprite.getWidth() * 0.1f, loeweSprite.getHeight() * 0.1f);
        loeweSprite.setPosition(0,0);

        boeserLoeweSprite = new Sprite(loewe);
        boeserLoeweSprite.setSize(boeserLoeweSprite.getWidth() * 0.1f, boeserLoeweSprite.getHeight() * 0.1f);
        boeserLoeweSprite.setPosition(worldWidth - boeserLoeweSprite.getWidth(), 0);

        rewindLoewe = new Rewind(new RewindableSprite(loeweSprite));
        rewindBoeserLoewe = new Rewind(new RewindableSprite(boeserLoeweSprite)); //Später: testen was passiert wenn wir das bei einem weglassen (damit wir später non-rewind Objekte testen koennen)

        rewindObjects = new Array<Rewind>();
        rewindObjects.add(rewindLoewe);
        rewindObjects.add(rewindBoeserLoewe);
    }



    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        input();
        logic();
        draw();
    }

    private void input() {
        if (Gdx.input.isKeyPressed(RIGHT_KEY)) {
            loeweSprite.translateX(playerSpeed);
        }
        if (Gdx.input.isKeyPressed(LEFT_KEY)) {
            loeweSprite.translateX(-playerSpeed);
        }
        if (Gdx.input.isKeyJustPressed(SPACEBAR) && !isJumping) {
            isJumping = true;
            velocityY = jumpVelocity;
        }

        if (boeserLoeweSprite.getX() < loeweSprite.getX()) {
            boeserLoeweSprite.translateX(enemySpeed);
        } else if (boeserLoeweSprite.getX() > loeweSprite.getX()) {
            boeserLoeweSprite.translateX(-enemySpeed);
        }

        if (isJumping) {
            loeweSprite.translateY(velocityY);
            velocityY -= gravity;
        }

        // Zeitmechanik für Rewind-Funktion
        if (Gdx.input.isKeyPressed(SHIFT)) {
            for (Rewind r : rewindObjects) {
                r.startRewinding();
            }

            // einfache Verfärbung der Sprites, um Rewind visuell deutlich zu machen
            if (rewindLoewe.hasRewindStorage()) {
                loeweSprite.setColor(Color.BLUE);
            } else {
                loeweSprite.setColor(Color.WHITE);
            }
            if (rewindLoewe.hasRewindStorage()) {
                boeserLoeweSprite.setColor(Color.BLUE);
            } else {
                boeserLoeweSprite.setColor(Color.WHITE);
            }
        } else {
            for (Rewind r : rewindObjects) {
                r.stopRewinding();
            }
            loeweSprite.setColor(Color.WHITE);
            boeserLoeweSprite.setColor(Color.WHITE);
        }

        rewindLoewe.update(); // Updates für Rewind-Mechanik in jedem Frame
        rewindBoeserLoewe.update();
    }

    private void logic() {
        if (loeweSprite.getY() <= 0) {
            isJumping = false;
            velocityY = 0;
            loeweSprite.setY(0);
        }
        if (loeweSprite.getX() <= 0) {
            loeweSprite.setX(0);
        }
        if (loeweSprite.getX() + loeweSprite.getWidth() >= worldWidth) {
            loeweSprite.setX(worldWidth - loeweSprite.getWidth());
        }

    }
    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        game.batch.begin();
        game.batch.draw(image, 140, 210);
        loeweSprite.draw(game.batch);
        boeserLoeweSprite.draw(game.batch);
        game.batch.end();
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
        image.dispose();
        loewe.dispose();
    }
}
