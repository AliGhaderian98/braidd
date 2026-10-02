package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.levels.LevelScreen;
import braid.main.tools.EventListener;
import braid.main.tools.Subtitle;
import braid.main.tools.SubtitleManager;
import braid.main.tools.UserData;
import com.badlogic.gdx.graphics.g2d.*;
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
    private final String text;
    private final String name;

    private Sprite sprite;
    private Animation<TextureRegion> idle;
    private float stateTimer = 0f;

    private boolean trigger = false;
    private boolean hasBeenPlayed = false;
    protected Body body;
    private EventListener event;
    SubtitleManager subtitleManager;

    public NPC(World world, LevelScreen screen, SubtitleManager subtitleManager, String type, Rectangle boundary, Rectangle npcTrigger, String text, String name) {
        super(world, boundary, true);
        this.subtitleManager = subtitleManager;
        this.text = text;
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
            case "Marcel" -> {
                sprite = new Sprite(atlas.findRegion("marcel"));
                idle = new Animation<>(0.1f, atlas.findRegions("marcel"), Animation.PlayMode.LOOP);
                sprite.setRegion(idle.getKeyFrame(0));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.6f/ Braid.PPM,
                    sprite.getRegionHeight()*0.6f/Braid.PPM);
                sprite.setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/3.4f);
            }
            case "Guru" -> {
                sprite = new Sprite(atlas.findRegion("guru"));
                idle = new Animation<>(0.1f, atlas.findRegions("guru"), Animation.PlayMode.LOOP);
                sprite.setRegion(idle.getKeyFrame(0));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*0.6f/ Braid.PPM,
                    sprite.getRegionHeight()*0.6f/Braid.PPM);
                sprite.setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/3f);
            }
            case ("henry") -> {
                sprite = new Sprite(atlas.findRegion("henry-idle"));
                idle = new Animation<>(0.2f, atlas.findRegions("henry-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(idle.getKeyFrame(0));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*.75f/ Braid.PPM,
                    sprite.getRegionHeight()*.75f/ Braid.PPM);
                sprite.setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
            }
            case ("alessa") -> {
                sprite = new Sprite(atlas.findRegion("alessa-idle"));
                idle = new Animation<>(0.2f, atlas.findRegions("alessa-idle"), Animation.PlayMode.LOOP);
                sprite.setRegion(idle.getKeyFrame(0));
                sprite.setBounds(0,0,
                    sprite.getRegionWidth()*.75f/ Braid.PPM,
                    sprite.getRegionHeight()*.75f/ Braid.PPM);
                sprite.setPosition(b2body.getPosition().x - sprite.getWidth()/2, b2body.getPosition().y - sprite.getHeight()/2);
            }
        }
    }


    public void trigger(boolean trigger) {

        if (this.trigger != trigger) {
            switch (type) {

                case "TalkingNPC" -> setShowing(trigger);
                case "DoorNPC" -> this.event.hasTriggeredNPC(trigger);
                case "MusicNPC" -> {
                    if (!hasBeenPlayed) {
                        this.event.hasChangedMusic(name);
                        setHasBeenPlayed(true);
                    }
                }
                case "CountingNPC" -> {
                    int remaining = event.getRepairButton();
                    String[] messages = text.split("\\|");

                    String newText = remaining > 5 ? String.format(messages[0], remaining) : String.format(messages[1], remaining);
                    subtitle.setFullText(newText);
                    subtitle.setCurrentText("");
                    setShowing(trigger);
                }

            }
            this.trigger = trigger;
        }
    }

    public void setHasBeenPlayed(boolean hasBeenPlayed) {
        this.hasBeenPlayed = hasBeenPlayed;
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

    public void draw(SpriteBatch batch, float dt) {
        if (sprite != null) {
            stateTimer += dt;
            sprite.setRegion(idle.getKeyFrame(stateTimer));
            sprite.draw(batch);
        }
    }

    public String getType() {
        return this.type;
    }

    @Override
    public float getStateTimer() { return stateTimer; }

    @Override
    public void setStateTimer(float value) { stateTimer = value; }
}
