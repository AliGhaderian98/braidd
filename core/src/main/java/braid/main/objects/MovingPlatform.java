package braid.main.objects;

import braid.main.Braid;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MovingPlatform extends InteractiveGameObject {

    private final float rangeX;
    private final float speed;
    private final float startX;
    private boolean movingRight = true;
    private final Rectangle boundary;
    private final Sprite sprite;
    private Player player;

    public MovingPlatform(World world, TiledMap map, TextureRegion region, Rectangle boundary, float rangeX, float speed) {
        super(world, boundary, region,false);
        this.boundary = boundary;
        this.rangeX = rangeX / 2;
        this.speed = speed;
        this.startX = b2body.getPosition().x;

        sprite = new Sprite(region);
        sprite.setBounds(0,0,region.getRegionWidth()/2f/ Braid.PPM, region.getRegionHeight()/2f/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        fixture.setUserData(new UserData("MovingPlatform", this));
    }

    @Override
    public void defineBody() {
        BodyDef bdef = new BodyDef();
        bdef.position.set((boundary.getX() + boundary.getWidth() / 2) / Braid.PPM,
            (boundary.getY() + boundary.getHeight() / 2) / Braid.PPM);
        bdef.type = BodyDef.BodyType.KinematicBody;

        b2body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprite.getWidth() / 2 / Braid.PPM, sprite.getHeight() / 2 / Braid.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        //fixture = b2body.createFixture(fdef);
        shape.dispose();
    }

    public void update(float dt) {
        float currentX = b2body.getPosition().x;
        if (movingRight && currentX > startX + rangeX) {
            movingRight = false;
        } else if (!movingRight && currentX < startX - rangeX) {
            movingRight = true;
        }

        float velocityX = movingRight ? speed : -speed;
        b2body.setLinearVelocity(velocityX*0.01f, 0);

        sprite.setPosition(b2body.getPosition().x-sprite.getWidth()/2, b2body.getPosition().y-sprite.getHeight()/2);

        if (player != null) {
            if (player.getPlatformVelocity() != b2body.getLinearVelocity().x)
                player.setPlatformVelocity(b2body.getLinearVelocity().x);
        }


        //System.out.println("CurrentX: " + currentX + ", StartX: " + startX + ", RangeX: " + rangeX + ", MovingRight: " + movingRight);
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }
}
