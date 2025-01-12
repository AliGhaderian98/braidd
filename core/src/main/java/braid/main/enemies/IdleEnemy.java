package braid.main.enemies;

import braid.main.Braid;
import braid.main.objects.Enemy;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class IdleEnemy extends Enemy implements EnemyAI{
    private int direction = 1;

    public IdleEnemy(World world, LevelScreen screen, float x, float y, boolean rewindable, String type) {
        super(world, screen,x,y, rewindable, type);
        defineBody();


        speed = 0.15f;

        setSprite(screen.getAtlas());
    }

    public void update(float dt) {
        super.update(dt);

    }

    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX() / Braid.PPM, getY() / Braid.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

    }

    @Override
    public void setSprite(TextureAtlas atlas) {
        switch (type) {
            case ("cat") -> {
                sprite = new Sprite(atlas.findRegion("cat-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("cat-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.1f, atlas.findRegions("cat-run"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0,idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.75f/ Braid.PPM,
                    sprite.getRegionHeight()*0.75f/Braid.PPM);
            }
            default -> {
                sprite = new Sprite(atlas.findRegion("lion-idle"));
                idle = new Animation<>(0.1f, atlas.findRegions("lion-idle"), Animation.PlayMode.LOOP);
                walking = new Animation<>(0.1f, atlas.findRegions("lion-run"), Animation.PlayMode.LOOP);
                sprite.setRegion(getFrame(0, idle));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()/ Braid.PPM,
                    sprite.getRegionHeight()/Braid.PPM);
            }
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {

    }
}
