package braid.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

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
    static int VelocityY;
    static final int gravity = 1;

    private Texture löwe;
    private Sprite löweSprite;

    private Sprite böserLöweSprite;




    @Override
    public void create() {
        worldWidth = Gdx.graphics.getWidth();
        worldHeight = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        löwe = new Texture("löwe.png");

        löweSprite = new Sprite(löwe);
        löweSprite.setSize(löweSprite.getWidth() * 0.1f, löweSprite.getHeight() * 0.1f);

        böserLöweSprite = new Sprite(löwe);
        böserLöweSprite.setSize(böserLöweSprite.getWidth() * 0.1f, böserLöweSprite.getHeight() * 0.1f);
        böserLöweSprite.setX(worldWidth - böserLöweSprite.getWidth());

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
            VelocityY = jumpVelocity;
        }

        if (böserLöweSprite.getX() < löweSprite.getX()) {
            böserLöweSprite.translateX(enemySpeed);
        } else if (böserLöweSprite.getX() > löweSprite.getX()) {
            böserLöweSprite.translateX(-enemySpeed);
        }


        if (isJumping) {
            löweSprite.translateY(VelocityY);
            VelocityY -= gravity; // Behalte diese Zeile nur einmal
        }

    }

    private void logic() {
        if (löweSprite.getY() <= 0) {
            isJumping = false;
            VelocityY = 0;
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
