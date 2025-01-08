package braid.main.tools;

import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

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
            open = new Animation<>(0.1f, level.getAtlas().findRegions("schwebebahn"), Animation.PlayMode.NORMAL);
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
    }

    private final LevelScreen level;
    private final SpriteBatch batch;
    private final Player player;
    private final TiledMap map;
    private final Stage stage;
    private final Schwebebahn schwebebahn;
    private boolean playing = true;

    public FinishLevelCutscene(LevelScreen level, SpriteBatch batch, Player player, TiledMap map) {
        this.level = level;
        this.batch = batch;
        this.player = player;
        this.map = map;

        stage = new Stage();
        schwebebahn = new Schwebebahn();

        Vector2 playerPosition = player.b2body.getPosition();
        Vector2 maxMapCoords = new Vector2(map.getProperties().get("width", Integer.class) *
                                                map.getProperties().get("tilewidth", Integer.class),
                                            map.getProperties().get("height", Integer.class) *
                                                map.getProperties().get("tileheight", Integer.class));

        Vector2 spawnPosition = new Vector2(0, playerPosition.y);
        // schwebebahn gets spawned in on the right
        if (playerPosition.x < maxMapCoords.x/2)
            spawnPosition.x = maxMapCoords.x + schwebebahn.sprite.getWidth()/2;
        // schebebahn gets spawned in on the left
        else
            spawnPosition.x = 0 - schwebebahn.sprite.getWidth()/2;

        schwebebahn.setPosition(spawnPosition.x, spawnPosition.y);

        stage.addActor(schwebebahn);
    }

    public void play() {
        schwebebahn.addAction(Actions.moveTo(player.b2body.getPosition().x, schwebebahn.getY(), 1f));
    }

    public void render(float delta) {
        stage.act(delta);
        schwebebahn.update(delta);
        schwebebahn.draw(batch, 1f);
        stage.draw();
    }

    public boolean isPlaying() { return playing; }

}
