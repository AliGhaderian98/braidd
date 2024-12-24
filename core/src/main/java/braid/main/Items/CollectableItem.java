package braid.main.Items;

import braid.main.Braid;
import braid.main.objects.Player;
import braid.main.screens.huds.LevelHUD;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class CollectableItem extends Item {

    public static Boolean isCollected;

    public CollectableItem(LevelScreen screen, float x, float y) {
        super(screen, x, y, 6);

        fixture.setUserData(new UserData("item", this));

        sprite = new Sprite(screen.getAtlas().findRegion("page"));
        sprite.setBounds(0,0,24/ Braid.PPM, 24/Braid.PPM);
        sprite.setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y-getHeight()/2);
    }


    @Override
    public void use(Player player) {
        if (!toDestroy) {
            LevelHUD.addScore(1);
            destroy();
        }
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
