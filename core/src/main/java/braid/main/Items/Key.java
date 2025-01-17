package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Key extends Item {
    private float time = 0f;
    private float rotationAmplitude = 5f;
    private float rotationSpeed = 4f;

    public Key(LevelScreen screen, float x, float y) {
        super(screen, x, y, 8);

        // Sprite laden
        sprite = new Sprite(screen.getAtlas().findRegion("key"));
        sprite.setBounds(x / Braid.PPM, y / Braid.PPM,
            20 / Braid.PPM, 20 / Braid.PPM);
        sprite.setOriginCenter();

        fixture.setUserData(new UserData("Key", this));
    }

    @Override
    public void use(Player player) {
        if (!player.hasKey()) {
            player.hasKey(true);
            destroy();
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);

        // animate sprite to slightly rotate
        time += dt;
        sprite.setRotation(rotationAmplitude * (float) Math.sin(time * rotationSpeed));
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
