package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.huds.LevelHUD;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class PowerUp extends Item {

    TypeOfPowerUp readTypePowerUp;
    TypeOfPowerUp previousPowerUp;
    private final float ritalinJumpModifier = 1.43f;
    private final float gleiterDescendingModifier = 0.01f;

    private final float floatRange = 3f/Braid.PPM;
    private float time = 0f;
    private final float floatSpeed = 300f/Braid.PPM;

    private boolean toReset = false;
    private static final int maxTime = 10; // 10 second timer measured in ms

    private final Vector2 defaultPosition;



    public PowerUp(LevelScreen screen, float x, float y,TypeOfPowerUp givenTypeOfPowerUp) {
        super(screen, x, y, 9);

        fixture.setUserData(new UserData("item", this));
        readTypePowerUp = givenTypeOfPowerUp;



        String regionName = getRegionName();
        sprite = new Sprite(screen.getAtlas().findRegion(regionName));
        sprite.setBounds(0,0,
            sprite.getRegionWidth()/ Braid.PPM,
            sprite.getRegionHeight()/Braid.PPM);

        if (readTypePowerUp == TypeOfPowerUp.GLEITER) {
            fixture.getShape().setRadius(12/Braid.PPM);
            defaultPosition = b2body.getPosition();
            defaultPosition.y -= sprite.getHeight()/4f;
        } else {
            defaultPosition = b2body.getPosition();
        }

        sprite.setPosition(defaultPosition.x - getWidth() / 2, defaultPosition.y-getHeight()/2);
    }

    public enum TypeOfPowerUp {
        RITALIN,
        GLEITER,
        HAMMER
    }

    private String getRegionName() {
        return switch (readTypePowerUp) {
            case RITALIN -> "ritalin";
            case GLEITER -> "balloon";
            case HAMMER -> "hammer";
            default -> "page";
        };
    }

    public String getType() {
        return readTypePowerUp.toString().toLowerCase();
    }

    @Override
    public void use(Player player) {
        if (player.hasActivePowerUp() && player.getPreviousPowerUp() == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(player.getJumpSpeed()*(1/ritalinJumpModifier));
            player.setNewestPowerUp(false);
        }
        if (player.hasActivePowerUp() && player.getPreviousPowerUp() == TypeOfPowerUp.GLEITER) {
            player.setDescendingGravity(player.getDescendingGravity()*(1/gleiterDescendingModifier));
            player.setGleiterActive(false);
            player.setNewestPowerUp(false);
        }
        if (player.hasActivePowerUp() && player.getPreviousPowerUp() == TypeOfPowerUp.HAMMER) {
            player.setHammerActive(false);
            player.setNewestPowerUp(false);
        }

        toReset = false;
        if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
            player.setJumpSpeed(player.getJumpSpeed()*ritalinJumpModifier);
        }
        if (readTypePowerUp == TypeOfPowerUp.GLEITER) {
            player.setDescendingGravity(player.getDescendingGravity()*gleiterDescendingModifier);
            player.setGleiterActive(true);
        }
        if (readTypePowerUp == TypeOfPowerUp.HAMMER) {
            player.setHammerActive(true);
        }

        LevelHUD.activatePowerUp(getType());
        player.setPreviousPowerUp(readTypePowerUp);
        player.hasActivePowerUp(true);
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
        if (player.isNewestPowerUp()) {
            LevelHUD.resetPowerUp();
            player.hasActivePowerUp(false);
            if (readTypePowerUp == TypeOfPowerUp.RITALIN) {
                player.setJumpSpeed(player.getJumpSpeed() * (1 / ritalinJumpModifier));
            }
            if (readTypePowerUp == TypeOfPowerUp.GLEITER) {
                player.setDescendingGravity(player.getDescendingGravity() * (1 / gleiterDescendingModifier));
                player.setGleiterActive(false);
            }
            if (readTypePowerUp == TypeOfPowerUp.HAMMER) {
                player.setHammerActive(false);
            }
        }
        player.setNewestPowerUp(true);
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

        sprite.setPosition(defaultPosition.x - sprite.getWidth() / 2, defaultPosition.y-sprite.getHeight()/2);

        // animate sprite up and down
        time += dt;
        sprite.setY(defaultPosition.y-sprite.getHeight()/4 + floatRange * (float) Math.sin(floatSpeed*time));
    }

    @Override
    public void defineBody() {}
}
