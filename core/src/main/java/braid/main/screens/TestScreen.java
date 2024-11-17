package braid.main.screens;

import braid.main.Braid;
import braid.main.Rewind;
import braid.main.RewindableSprite;
import braid.main.Player;
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

    Player player = new Player();

    public TestScreen(Braid game) {
        this.game = game;

        worldWidth = Braid.V_WIDTH;
        worldHeight = Braid.V_HEIGHT;

        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(worldWidth / Braid.PPM , worldHeight / Braid.PPM,gamecam);

        image = new Texture("libgdx.png");
        player.setLoewe(new Texture("löwe.png"));

        player.setLoeweSprite(new Sprite(player.getLoewe()));
        player.getLoeweSprite().setSize(player.getLoeweSprite().getWidth() * 0.1f, player.getLoeweSprite().getHeight() * 0.1f);
        player.getLoeweSprite().setPosition(0,0);

        player.setBoeserLoeweSprite(new Sprite(player.getLoewe()));
        player.getBoeserLoeweSprite().setSize(player.getBoeserLoeweSprite().getWidth() * 0.1f, player.getBoeserLoeweSprite().getHeight() * 0.1f);
        player.getBoeserLoeweSprite().setPosition(worldWidth - 5*player.getBoeserLoeweSprite().getWidth(), 0);

        player.setRewindLoewe(new Rewind(new RewindableSprite(player.getLoeweSprite())));
        player.setRewindBoeserLoewe(new Rewind(new RewindableSprite(player.getBoeserLoeweSprite()))); //Später: testen was passiert wenn wir das bei einem weglassen (damit wir später non-rewind Objekte testen koennen)

        player.setRewindObjects(new Array<Rewind>());
        player.getRewindObjects().add(player.getRewindLoewe());
        player.getRewindObjects().add(player.getRewindBoeserLoewe());
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
            player.getLoeweSprite().translateX(player.getPlayerSpeed());
        }
        if (Gdx.input.isKeyPressed(LEFT_KEY)) {
            player.getLoeweSprite().translateX(-player.getPlayerSpeed());
        }
        if (Gdx.input.isKeyJustPressed(SPACEBAR) && !player.isIsJumping()) {
            player.setIsJumping(true);
            player.setVelocityY(player.getJumpVelocity());
        }

        if (player.getBoeserLoeweSprite().getX() < player.getLoeweSprite().getX()) {
            player.getBoeserLoeweSprite().translateX(player.getEnemySpeed());
        } else if (player.getBoeserLoeweSprite().getX() > player.getLoeweSprite().getX()) {
            player.getBoeserLoeweSprite().translateX(-player.getEnemySpeed());
        }


        if (player.isIsJumping()) {
            int currentVelocityY = player.getVelocityY(); //temporäre Lösung -> geht wahrscheinlich einfacher
            player.getLoeweSprite().translateY(player.getVelocityY());
            player.setVelocityY(currentVelocityY - player.getGravity());
        }

        // Zeitmechanik für Rewind-Funktion
        if (Gdx.input.isKeyPressed(SHIFT)) {
            for (Rewind r : player.getRewindObjects()) {
                r.startRewinding();
            }

            // einfache Verfärbung der Sprites, um Rewind visuell deutlich zu machen
            if (player.getRewindLoewe().hasRewindStorage()) {
                player.getLoeweSprite().setColor(Color.BLUE);
            } else {
                player.getLoeweSprite().setColor(Color.WHITE);
            }
            if (player.getRewindLoewe().hasRewindStorage()) {
                player.getBoeserLoeweSprite().setColor(Color.BLUE);
            } else {
                player.getBoeserLoeweSprite().setColor(Color.WHITE);
            }
        } else {
            for (Rewind r : player.getRewindObjects()) {
                r.stopRewinding();
            }
            player.getLoeweSprite().setColor(Color.WHITE);
            player.getBoeserLoeweSprite().setColor(Color.WHITE);
        }

        player.getRewindLoewe().update(); // Updates für Rewind-Mechanik in jedem Frame
        player.getRewindBoeserLoewe().update();
    }

    private void logic() {
        if (player.getLoeweSprite().getY() <= 0) {
            player.setIsJumping(false);
            player.setVelocityY(0);
            player.getLoeweSprite().setY(0);
        }
        if (player.getLoeweSprite().getX() <= 0) {
            player.getLoeweSprite().setX(0);
        }
        if (player.getLoeweSprite().getX() + player.getLoeweSprite().getWidth() >= worldWidth) {
            player.getLoeweSprite().setX(worldWidth - player.getLoeweSprite().getWidth());
        }

    }
    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        game.batch.begin();
        game.batch.draw(image, 140, 210);
        player.getLoeweSprite().draw(game.batch);
        player.getBoeserLoeweSprite().draw(game.batch);
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
        player.getLoewe().dispose();
    }
}
