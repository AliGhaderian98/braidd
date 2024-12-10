package braid.main.Items;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class CollectableItem extends Item {
    private final Sprite sprite;
    public CollectableItem(LevelScreen screen, float x, float y) {
        super(screen, x, y);

        defineItem();
        sprite = new Sprite(screen.getAtlas().findRegion("page"));
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x / Braid.PPM,y / Braid.PPM);

        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Braid.PPM);

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
       /* fdef.filter.categoryBits = Braid.ENEMY_BIT;
        fdef.filter.maskBits = Braid.GROUND_BIT |
            Braid.COIN_BIT |
            Braid.BRICK_BIT |
            Braid.ENEMY_BIT |
            Braid.OBJEKT_BIT |
            Braid.BRAID_BIT; */

    }

    @Override
    public void use() {
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y-getHeight()/2);
    }
}
