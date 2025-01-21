package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.EventListener;
import braid.main.tools.Subtitle;
import braid.main.tools.SubtitleManager;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;

import java.util.Objects;

import static braid.main.Braid.PPM;

public class NPC extends InteractiveGameObject{
    private final Rectangle npcTrigger;
    private final Subtitle subtitle;
    private final String type;
    private final String name;
    private boolean trigger = false;
    protected Body body;
    private EventListener event;
    SubtitleManager subtitleManager;

    public NPC(World world, LevelScreen screen, SubtitleManager subtitleManager, String type, Rectangle boundary, Rectangle npcTrigger, String text, String name) {
        super(world, boundary, true);
        this.subtitleManager = subtitleManager;
        this.subtitle = new Subtitle(text);
        subtitleManager.addSubtitle(subtitle);

        this.type = type;
        this.npcTrigger = (npcTrigger != null) ? npcTrigger : boundary;
        this.name = name;

        defineBody();
        fixture.setUserData(new UserData("NPC", this));
        setSprite(screen.getAtlas());
    }

    public void setEvent(EventListener event) {
        this.event = event;
    }

    public void setSprite(TextureAtlas atlas) {
        if(this.name == null) return;
        switch (this.name) {
            case "PublicBus" -> {
            }
            case "HoodedOne" -> {
            }
        }
    }


    public void trigger(boolean trigger) {

        if (this.trigger != trigger) {
            switch (type) {
                case "TalkingNPC" -> setShowing(trigger);
                case "FinalNPC" -> this.event.hasTriggeredNPC(trigger);
                case "DoorNPC" -> this.event.hasTriggeredNPC(trigger);
                case "MusicNPC" -> this.event.hasChangedMusic(name);

            }
            this.trigger = trigger;
        }
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

    public String getType() {
        return this.type;
    }
}
