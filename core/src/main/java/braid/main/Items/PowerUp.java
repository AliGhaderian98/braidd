package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PowerUp extends Item {

    TypeOfPowerUp readTypePowerUp;

    public PowerUp(LevelScreen screen, float x, float y,TypeOfPowerUp givenTypeOfPowerUp) {
        super(screen, x, y, 7);

        fixture.setUserData(new UserData("item", this));
        readTypePowerUp = givenTypeOfPowerUp;

        //defineItem();
        String regionName = getRegionName();
        sprite = new Sprite(screen.getAtlas().findRegion(regionName)); //Später andere Sprites je nach TypeOfPowerUp
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y-getHeight()/2);

    }
    public enum TypeOfPowerUp {
        RITALIN,
        GLEITER,
        HAMMER;

    }

    private String getRegionName() {
        return switch (readTypePowerUp) {
            case RITALIN -> "ritalin";
            // case GLEITER -> "gleiter";
            // case HAMMER -> "hammer";
            default -> "page";
        };
    }

    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x / Braid.PPM, y / Braid.PPM);

        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Braid.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Player player) {
        if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(5.0f);
        }
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y-sprite.getHeight()/2);
    }

    @Override
    public void defineBody() {

    }
}
