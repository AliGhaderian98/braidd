package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.Audiomanager;
import braid.main.tools.PreferencesManager;
import braid.main.tools.UserData;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class Schalter extends InteractiveGameObject {

    public enum AnimationState {
        ON, OFF
    }

    private final Sprite sprite;
    private final SchalterMovingPlatform linkedPlatform;
    private final Sound clickSFX;
    private AnimationState currentState = AnimationState.OFF;
    private final TextureRegion offSprite;
    private final TextureRegion onSprite;

    public Schalter(World world, TextureAtlas atlas, Rectangle boundary, SchalterMovingPlatform linkedPlatform) {
        super(world, boundary, true);
        this.linkedPlatform = linkedPlatform;

        offSprite = atlas.findRegion("schalter-off");
        onSprite = atlas.findRegion("schalter-on");

        sprite = new Sprite(offSprite);
        sprite.setBounds(0, 0, sprite.getRegionWidth() / 2f / Braid.PPM, sprite.getRegionHeight() / 2f / Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);

        fixture.setUserData(new UserData("Schalter", this));

        clickSFX = Audiomanager.audiomanager.get("audio/sound/buttonClick.ogg", Sound.class);
    }

    public void toggle() {
        if (!linkedPlatform.isActive()) {
            clickSFX.play((PreferencesManager.getSliderPreferences().getFloat("sfxSlider")));
            linkedPlatform.setActive();

            // flip state and change sprite
            if (currentState == AnimationState.OFF)
                currentState = AnimationState.ON;
            else
                currentState = AnimationState.OFF;

            setSpriteToStatus();
        }
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    private void setSpriteToStatus() {
        if (currentState == AnimationState.ON)
            sprite.setRegion(onSprite);
        else
            sprite.setRegion(offSprite);
    }

    public boolean isRewindable() { return linkedPlatform.isRewindable(); }

    public Fixture getFixture() {
        return fixture;
    }
    @Override
    public void defineBody() {}

    @Override
    public void setCurrentState(Object state) {
        if (state instanceof Schalter.AnimationState) {
            currentState = (Schalter.AnimationState) state;
            setSpriteToStatus();
        }
    }

    @Override
    public Object getCurrentState() {
        return currentState;
    }
}
