package braid.main.tools;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FinishLevelCutscene {
    private class Schwebebahn extends Actor {
        public Sprite sprite;
        private final Animation<TextureRegion> open;
        public boolean facingRight = false;
        private float stateTimer = 0;
        public boolean opening = false;

        public Schwebebahn() {
            super();
            sprite = new Sprite(level.getAtlas().findRegion("schwebebahn"));
            sprite.setBounds(0,0,sprite.getRegionWidth()/ Braid.PPM, sprite.getRegionHeight()/Braid.PPM);
            open = new Animation<>(0.05f, level.getAtlas().findRegions("schwebebahn"), Animation.PlayMode.NORMAL);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            sprite.setPosition(getX(), getY());
            if (facingRight)
                sprite.setFlip(true, false);
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

    private class SchwebebahnRail extends Actor {
        public Sprite sprite;

        public SchwebebahnRail() {
            super();
            sprite = new Sprite(level.getAtlas().findRegion("schwebebahn-rails"));
            sprite.setBounds(0,0,sprite.getRegionWidth()/ Braid.PPM, sprite.getRegionHeight()/Braid.PPM);

            Vector2 screenTopEdge = stage.getViewport().unproject(new Vector2(0, stage.getViewport().getScreenHeight()));
            setY(screenTopEdge.y);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            sprite.setPosition(getX(), getY());
            sprite.draw(batch);
        }
    }

    private final LevelScreen level;
    private final Player player;
    private final Stage stage;
    private final Schwebebahn schwebebahn;
    private final SchwebebahnRail schwebebahnRail;
    private boolean playing = false;
    private boolean isFinished = false;

    public FinishLevelCutscene(LevelScreen level, SpriteBatch batch, Viewport viewport, Player player) {
        this.level = level;
        this.player = player;

        stage = new Stage(viewport, batch);

        schwebebahn = new Schwebebahn();
        stage.addActor(schwebebahn);
        schwebebahnRail = new SchwebebahnRail();
        stage.addActor(schwebebahnRail);
    }

    private void setSchwebebahnPosition() {
        Vector2 playerPosition = player.b2body.getPosition();
        playerPosition.y *= Braid.PPM;

        Vector2 screenRightEdge = stage.getViewport().unproject(new Vector2(stage.getViewport().getScreenWidth(), 0));
        Vector2 screenLeftEdge = stage.getViewport().unproject(new Vector2(0, 0));
        Vector2 screenTopEdgeCenter = stage.getViewport().unproject(new Vector2(stage.getViewport().getScreenWidth()/2f, 0));

        Vector2 spawnPosition = new Vector2(0,
            playerPosition.y/Braid.PPM + (schwebebahn.sprite.getHeight()/Braid.PPM));
        // schwebebahn gets spawned in on the right
        if (playerPosition.x < screenTopEdgeCenter.x)
            spawnPosition.x = screenRightEdge.x;
        // schwebebahn gets spawned in on the left
        else {
            spawnPosition.x = screenLeftEdge.x - schwebebahn.sprite.getWidth();
            schwebebahn.facingRight = true;
        }

        schwebebahn.setPosition(spawnPosition.x, spawnPosition.y);
        schwebebahnRail.setPosition((screenTopEdgeCenter.x - schwebebahnRail.sprite.getWidth()/2), screenTopEdgeCenter.y);
    }

    public void play() {
        playing = true;
        setSchwebebahnPosition();

        schwebebahnRail.addAction(Actions.sequence(
            Actions.moveTo(schwebebahnRail.getX(), schwebebahn.getY(), 0.3f, Interpolation.sineOut),
            Actions.run(() -> schwebebahn.addAction(Actions.sequence(
                Actions.moveTo((player.b2body.getPosition().x - schwebebahn.sprite.getWidth() / 2), schwebebahn.getY(),
                    1.5f, Interpolation.fastSlow),
                Actions.run(schwebebahn::open),
                Actions.delay(schwebebahn.getAnimationDuration() + 1f),
                Actions.run(() -> isFinished = true),
                Actions.run(level::setFinishHUD)
            )))
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
