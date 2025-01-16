package braid.main.objects;
import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Door extends InteractiveGameObject {
    private final int doorID;
    private final Sprite sprite;
    private final LevelScreen screen;
    private boolean isOpen;

    public Door(LevelScreen screen, Rectangle rect, int doorID) {
        super(screen.getWorld(), rect.width / 2 / Braid.PPM, false, rect.x + rect.width / 2, rect.y + rect.height / 2);
        this.screen = screen;
        this.doorID = doorID;
        this.isOpen = false;

        sprite = new Sprite(screen.getAtlas().findRegion("door-closed"));
        sprite.setBounds(rect.x / Braid.PPM, rect.y / Braid.PPM, rect.width / Braid.PPM, rect.height / Braid.PPM);

        defineBody();
    }

    public void open() {
        isOpen = true;
        sprite.setRegion(screen.getAtlas().findRegion("door-open"));
        world.destroyBody(b2body);
    }

    public int getDoorID() {
        return doorID;
    }

    public void draw() {
        sprite.draw(screen.getBatch());
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
}
