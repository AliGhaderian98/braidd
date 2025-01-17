package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.Subtitle;
import braid.main.tools.SubtitleManager;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

import static braid.main.Braid.PPM;

public class NPC extends InteractiveGameObject{
    private Player player;
    private final Rectangle npcTrigger;
    private final Subtitle subtitle;
    private String type;
    private final String name;
    private boolean trigger = false;
    protected Body body;
    SubtitleManager subtitleManager;

    public NPC(World world, LevelScreen screen, SubtitleManager subtitleManager, String type, Rectangle boundary, Rectangle npcTrigger, String text, String name) {
        super(world, boundary, true);
        this.subtitleManager = subtitleManager;
        this.subtitle = new Subtitle(text);
        subtitleManager.addSubtitle(subtitle);
        this.type = type;
        System.out.println(type);

        this.npcTrigger = (npcTrigger != null) ? npcTrigger : boundary;
        System.out.println(npcTrigger);
        this.name = name;

        defineBody();
        setSprite(screen.getAtlas());
    }

    public void setSprite(TextureAtlas atlas) {
        if(this.name == null) return;
        switch (this.name) {
            case "PublicBus" -> {
                System.out.println("hallo");
            }
            case "HoodedOne" -> {
                System.out.println("hallo2");
            }
        }
    }


    public void trigger(boolean trigger) {
        System.out.println(type);

        if(this.trigger != trigger) {

            switch (type) {
                case "TalkingNPC" -> setShowing(trigger);
                case "AttackingNPC" -> {

                }
                case "MovingNPC" -> {
                    float speed = 3f;
                    body.setLinearVelocity(speed, body.getLinearVelocity().y);
                }
            }
            this.trigger = trigger;
        }
    }

    public void move() {

    }
    public void setShowing(boolean showing) {
        subtitle.setShowing(showing);
    }

    public Subtitle getSubtitle() { return subtitle; }

    @Override
    public void defineBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((npcTrigger.x + npcTrigger.width / 2) / PPM,
            (npcTrigger.y + npcTrigger.height / 2) / PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(npcTrigger.width / 2 / PPM, npcTrigger.height / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData(new UserData("NPCTrigger", this));
        shape.dispose();
    }
}
