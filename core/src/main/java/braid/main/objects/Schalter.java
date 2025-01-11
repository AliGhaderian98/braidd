package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class Schalter extends InteractiveGameObject {
    private boolean isActive;
    private final Sprite sprite;
    private final MovingPlatform linkedPlatform;

    public Schalter(World world, TextureRegion region, Rectangle boundary, MovingPlatform linkedPlatform) {
        super(world, boundary, region, true);
        this.isActive = false;
        this.linkedPlatform = linkedPlatform;


        sprite = new Sprite(region);
        sprite.setBounds(0, 0, region.getRegionWidth() / 2f / Braid.PPM, region.getRegionHeight() / 2f / Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);


        fixture.setUserData(new UserData("Schalter", this));
    }

    public void toggle() {
        isActive = !isActive;
        linkedPlatform.setActive(isActive);
    }

    public void update(float dt) {
        sprite.setPosition(b2body.getPosition().x - sprite.getWidth() / 2, b2body.getPosition().y - sprite.getHeight() / 2);
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public boolean isActive() {
        return isActive;
    }

    public Fixture getFixture() {
        return fixture;
    }
    @Override
    public void defineBody() {}
}
