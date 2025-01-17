package braid.main.objects;
import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.w3c.dom.Text;

public class Door extends InteractiveGameObject {
    private final Sprite sprite;
    private final LevelScreen screen;
    private boolean isOpen;
    private boolean toDestroy = false;
    private boolean destroyed = false;
    private TextureRegion closedRegion;
    private TextureRegion openRegion;

    public Door(LevelScreen screen, Rectangle rect) {
        super(screen.getWorld(), rect, screen.getAtlas().findRegion("door-closed"), false);
        this.screen = screen;
        isOpen = false;

        closedRegion = screen.getAtlas().findRegion("door-closed");
        openRegion = screen.getAtlas().findRegion("door-open");
        sprite = new Sprite(closedRegion);
        sprite.setBounds(0,0,closedRegion.getRegionWidth()/ Braid.PPM, closedRegion.getRegionHeight()/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        fixture.setUserData(new UserData("Door", this));
    }

    public void open() {
        isOpen = true;
        sprite.setRegion(openRegion);
        sprite.setBounds(0,0,openRegion.getRegionWidth()/ Braid.PPM, openRegion.getRegionHeight()/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);
        toDestroy = true;
    }

    public void update() {
        if (toDestroy && !destroyed) {
            destroyed = true;
            world.destroyBody(b2body);
        }
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((sprite.getX() + sprite.getWidth() / 2), (sprite.getY() + sprite.getHeight() / 2));

        b2body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public boolean isOpen() { return isOpen; }
}
