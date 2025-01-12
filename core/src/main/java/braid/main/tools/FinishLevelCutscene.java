package braid.main.tools;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FinishLevelCutscene {
    private class Schwebebahn extends Actor {
        public Sprite sprite;
        private Animation<TextureRegion> open;
        public boolean facingRight = false;
        private float stateTimer = 0;
        public boolean opening = false;

        public Schwebebahn() {
            super();
            sprite = new Sprite(level.getAtlas().findRegion("schwebebahn"));
            sprite.setScale(4f);
            open = new Animation<>(0.05f, level.getAtlas().findRegions("schwebebahn"), Animation.PlayMode.NORMAL);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            sprite.setPosition(getX(), getY());
            sprite.draw(batch);
        }

        public void update(float dt) {
            if (opening) {
                stateTimer += dt;
                sprite.setRegion(open.getKeyFrame(stateTimer));
            }
        }

        public void open() { opening = true; }

        public float getAnimationDuration() {
            return open.getAnimationDuration();
        }
    }

    private final LevelScreen level;
    private final SpriteBatch batch;
    private final FitViewport viewport;
    private final Player player;
    private final TiledMap map;
    private final Stage stage;
    private final Schwebebahn schwebebahn;
    private boolean playing = false;
    private boolean isFinished = false;

    public FinishLevelCutscene(LevelScreen level, SpriteBatch batch, Player player, TiledMap map) {
        this.level = level;
        this.batch = batch;
        this.player = player;
        this.map = map;

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        viewport = new FitViewport(width, height, new OrthographicCamera());
        stage = new Stage(viewport);

        schwebebahn = new Schwebebahn();
        stage.addActor(schwebebahn);
    }

    private void setSchwebebahnPosition() {
        Vector2 playerPosition = player.b2body.getPosition();
        playerPosition.y *= Braid.PPM;
        Vector2 maxMapCoords = new Vector2(map.getProperties().get("width", Integer.class) *
                                                map.getProperties().get("tilewidth", Integer.class),
                                            map.getProperties().get("height", Integer.class) *
                                                map.getProperties().get("tileheight", Integer.class));

        Vector2 spawnPosition = new Vector2(0,
            playerPosition.y + schwebebahn.sprite.getHeight()*schwebebahn.sprite.getScaleY());
        // schwebebahn gets spawned in on the right
        if (playerPosition.x < maxMapCoords.x/2)
            spawnPosition.x = maxMapCoords.x + schwebebahn.sprite.getWidth()/2;
        // schwebebahn gets spawned in on the left
        else {
            spawnPosition.x = 0 - schwebebahn.sprite.getWidth()/2;
            schwebebahn.facingRight = true;
            schwebebahn.sprite.flip(true, false);
        }

        schwebebahn.setPosition(spawnPosition.x, spawnPosition.y);
    }

    public void play() {
        playing = true;
        setSchwebebahnPosition();
        schwebebahn.addAction(Actions.sequence(
            Actions.moveTo(player.b2body.getPosition().x, schwebebahn.getY(), 1f),
            Actions.run(() -> schwebebahn.open()),
            Actions.delay(schwebebahn.getAnimationDuration() + 1f),
            Actions.run(() -> isFinished = true),
            Actions.run(() -> level.setFinishHUD())
        ));
    }

    public void render(float delta) {
        stage.act(delta);
        schwebebahn.update(delta);
        stage.draw();
    }

    public boolean isPlaying() { return playing; }

    public boolean isFinished() { return isFinished; }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getCamera().update();
    }

}
