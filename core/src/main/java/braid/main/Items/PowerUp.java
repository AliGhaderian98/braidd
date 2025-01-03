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
import com.badlogic.gdx.utils.Timer;

public class PowerUp extends Item {

    TypeOfPowerUp readTypePowerUp;
    private final float ritalinJumpModifier = 1.43f;
    private final float gleiterDescendingModifier = 0.01f;

    private final float floatRange = 3f/Braid.PPM;
    private float time = 0f;
    private final float floatSpeed = 300f/Braid.PPM;

    private boolean toReset = false;
    private static final int maxTime = 10; // 10 second timer measured in ms



    public PowerUp(LevelScreen screen, float x, float y,TypeOfPowerUp givenTypeOfPowerUp) {
        super(screen, x, y, 7);

        fixture.setUserData(new UserData("item", this));
        readTypePowerUp = givenTypeOfPowerUp;

        String regionName = getRegionName(); //Später andere Sprites je nach TypeOfPowerUp
        sprite = new Sprite(screen.getAtlas().findRegion(regionName));
        sprite.setBounds(0,0,
            sprite.getRegionWidth()/ Braid.PPM,
            sprite.getRegionHeight()/Braid.PPM);
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
            case GLEITER -> "balloon"; //Später Gleiter, wenn Sprite vorhanden ist
            // case HAMMER -> "hammer";
            default -> "page";
        };
    }

    public String getType() {
        return readTypePowerUp.toString().toLowerCase();
    }

    @Override
    public void use(Player player) {
        if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(player.getJumpSpeed()*ritalinJumpModifier);

        }
        if (readTypePowerUp == TypeOfPowerUp.GLEITER) {
            player.setDescendingGravity(player.getDescendingGravity()*gleiterDescendingModifier);
        }
        LevelHUD.activatePowerUp(getType());
        destroy();

        // deactivate power up after maxTime is over
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> reset(player));
            }
        }, maxTime);
    }

    public void reset(Player player) {
        toReset = true;
        LevelHUD.resetPowerUp();
        if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(player.getJumpSpeed()*(1/ritalinJumpModifier));
        }
        if (readTypePowerUp == TypeOfPowerUp.GLEITER) {
            player.setDescendingGravity(player.getDescendingGravity()*(1/gleiterDescendingModifier));
        }
    }

    public static int getMaxTime() { return maxTime; }

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
