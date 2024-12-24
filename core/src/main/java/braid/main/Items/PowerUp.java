package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.huds.LevelHUD;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PowerUp extends Item {

    TypeOfPowerUp readTypePowerUp;
    private final float ritalinJumpModifier = 1.43f;

    private final float floatRange = 3f/Braid.PPM;
    private float time = 0f;
    private final float floatSpeed = 300f/Braid.PPM;

    private boolean toReset = false;
    private int maxTime = 10000; // 10 second timer measured in ms



    public PowerUp(LevelScreen screen, float x, float y,TypeOfPowerUp givenTypeOfPowerUp) {
        super(screen, x, y, 7);

        fixture.setUserData(new UserData("item", this));
        readTypePowerUp = givenTypeOfPowerUp;

        String regionName = getRegionName(); //Später andere Sprites je nach TypeOfPowerUp
        sprite = new Sprite(screen.getAtlas().findRegion(regionName));
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y-getHeight()/2);

    }
    public enum TypeOfPowerUp {
        RITALIN,
        GLEITER,
        HAMMER
    }

    private String getRegionName() {
        return switch (readTypePowerUp) {
            case RITALIN -> "ritalin";
            // case GLEITER -> "gleiter";
            // case HAMMER -> "hammer";
            default -> "page";
        };
    }

    @Override
    public void use(Player player) {
        if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(player.getJumpSpeed()*ritalinJumpModifier);
        }
        LevelHUD.activatePowerUp(getRegionName());
        destroy();

        // deactivate power up after maxTime is over
        new Thread(() -> {
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + maxTime){} // add update to levelHUD here
            Gdx.app.postRunnable(() -> reset(player) );
        }).start();
    }

    public void reset(Player player) {
        toReset = true;
        LevelHUD.resetPowerUp();
        if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(player.getJumpSpeed()*(1/ritalinJumpModifier));
        }
    }

    @Override
    public void update(float dt) {
        if(toDestroy && !destroyed){
            b2body.setActive(false);
            destroyed = true;
        }
        if(toReset && destroyed) {
            b2body.setActive(true);
            destroyed = false;
            toReset = false;
            toDestroy = false;
        }

        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y-sprite.getHeight()/2);

        // animate sprite up and down
        time += dt;
        sprite.setY(b2body.getPosition().y-sprite.getHeight()/4 + floatRange * (float) Math.sin(floatSpeed*time));
    }

    @Override
    public void defineBody() {}
}
