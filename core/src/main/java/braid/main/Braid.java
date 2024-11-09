package braid.main;
//Steuerung und Logik vom Char und erste Logik für Gegner (Invoker?)
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Vector2;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Braid extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

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

    private Texture löwe;
    private Sprite löweSprite;
    private Sprite böserLöweSprite;

    private Rewind rewindLöwe;
    private Rewind rewindBöserLöwe;

    private Array<Rewind> rewindObjects;

    @Override
    public void create() {
        worldWidth = Gdx.graphics.getWidth();
        worldHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        löwe = new Texture("löwe.png");


        löweSprite = new Sprite(löwe);
        löweSprite.setSize(löweSprite.getWidth() * 0.1f, löweSprite.getHeight() * 0.1f);
        löweSprite.setPosition(0,0);

        böserLöweSprite = new Sprite(löwe);
        böserLöweSprite.setSize(böserLöweSprite.getWidth() * 0.1f, böserLöweSprite.getHeight() * 0.1f);
        böserLöweSprite.setPosition(worldWidth - böserLöweSprite.getWidth(), 0);

        rewindLöwe = new Rewind(new RewindableSprite(löweSprite));
        rewindBöserLöwe = new Rewind(new RewindableSprite(böserLöweSprite)); //Später: testen was passiert wenn wir das bei einem weglassen (damit wir später non-rewind Objekte testen können)

        rewindObjects = new Array<Rewind>();
        rewindObjects.add(rewindLöwe);
        rewindObjects.add(rewindBöserLöwe);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        löwe.dispose();
    }

    private void input() {
        if (Gdx.input.isKeyPressed(RIGHT_KEY)) {
            löweSprite.translateX(playerSpeed);
        }
        if (Gdx.input.isKeyPressed(LEFT_KEY)) {
            löweSprite.translateX(-playerSpeed);
        }
        if (Gdx.input.isKeyJustPressed(SPACEBAR) && !isJumping) {
            isJumping = true;
            velocityY = jumpVelocity;
        }

        if (böserLöweSprite.getX() < löweSprite.getX()) {
            böserLöweSprite.translateX(enemySpeed);
        } else if (böserLöweSprite.getX() > löweSprite.getX()) {
            böserLöweSprite.translateX(-enemySpeed);
        }

        if (isJumping) {
            löweSprite.translateY(velocityY);
            velocityY -= gravity;
        }

        // Zeitmechanik für Rewind-Funktion
        if (Gdx.input.isKeyPressed(SHIFT)) {
            for (Rewind r : rewindObjects) {
                r.startRewinding();
            }

            // einfache Verfärbung der Sprites, um Rewind visuell deutlich zu machen
            if (rewindLöwe.hasRewindStorage()) {
                löweSprite.setColor(Color.BLUE);
            } else {
                löweSprite.setColor(Color.WHITE);
            }
            if (rewindLöwe.hasRewindStorage()) {
                böserLöweSprite.setColor(Color.BLUE);
            } else {
                böserLöweSprite.setColor(Color.WHITE);
            }
        } else {
            for (Rewind r : rewindObjects) {
                r.stopRewinding();
            }
        }

        rewindLöwe.update(); // Updates für Rewind-Mechanik in jedem Frame
        rewindBöserLöwe.update();
    }

    private void logic() {
        if (löweSprite.getY() <= 0) {
            isJumping = false;
            velocityY = 0;
            löweSprite.setY(0);
        }
        if (löweSprite.getX() <= 0) {
            löweSprite.setX(0);
        }
        if (löweSprite.getX() + löweSprite.getWidth() >= worldWidth) {
            löweSprite.setX(worldWidth - löweSprite.getWidth());
        }

    }
    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(image, 140, 210);
        löweSprite.draw(batch);
        böserLöweSprite.draw(batch);
        batch.end();
    }
}
