package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Key extends Item {
    private final int keyID;

    public Key(LevelScreen screen, float x, float y, int keyID) {
        super(screen, x, y, 8 / Braid.PPM);
        this.keyID = keyID;

        // Sprite laden
        sprite = new Sprite(screen.getAtlas().findRegion("key"));
        sprite.setBounds(x / Braid.PPM, y / Braid.PPM, 16 / Braid.PPM, 16 / Braid.PPM);

        defineBody();
    }

    @Override
    public void use(Player player) {
        player.addKey(keyID);
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);
    }

    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x / Braid.PPM, y / Braid.PPM);

        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(8 / Braid.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }
}
