package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.Audiomanager;
import braid.main.tools.PreferencesManager;
import braid.main.tools.UserData;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class Schalter extends InteractiveGameObject {
    private final Sprite sprite;
    private final SchalterMovingPlatform linkedPlatform;
    private final Sound clickSFX;

    public Schalter(World world, TextureRegion region, Rectangle boundary, SchalterMovingPlatform linkedPlatform) {
        super(world, boundary, true);
        this.linkedPlatform = linkedPlatform;

        sprite = new Sprite(region);
        sprite.setBounds(0, 0, sprite.getRegionWidth() / 2f / Braid.PPM, sprite.getRegionHeight() / 2f / Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);

        fixture.setUserData(new UserData("Schalter", this));

        clickSFX = Audiomanager.audiomanager.get("audio/sound/buttonClick.ogg", Sound.class);
    }

    public void toggle() {
        clickSFX.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
        linkedPlatform.setActive();
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }


    public Fixture getFixture() {
        return fixture;
    }
    @Override
    public void defineBody() {}
}
