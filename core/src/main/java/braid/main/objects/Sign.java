package braid.main.objects;

import braid.main.tools.KeyBindings;
import braid.main.tools.Subtitle;
import braid.main.tools.SubtitleManager;
import braid.main.tools.UserData;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Sign extends InteractiveGameObject {
    SubtitleManager subtitleManager;
    private final Subtitle subtitle;

    public Sign(World world, Rectangle boundary, SubtitleManager subtitleManager, String text) {
        super(world, boundary, true);
        this.subtitleManager = subtitleManager;
        this.subtitle = new Subtitle(text);

        subtitleManager.addSubtitle(subtitle);

        fixture.setUserData(new UserData("Sign", this));
    }

    public Sign(World world, Rectangle boundary, SubtitleManager subtitleManager, String text, String key) {
        super(world, boundary, true);
        this.subtitleManager = subtitleManager;
        this.subtitle = new Subtitle(String.format(text,
            Input.Keys.toString(KeyBindings.getKey(key))));

        subtitleManager.addSubtitle(subtitle);

        fixture.setUserData(new UserData("Sign", this));
    }


    public void setShowing(boolean showing) {
        subtitle.setShowing(showing);
    }

    public Subtitle getSubtitle() { return subtitle; }
    @Override
    public void defineBody() {

    }
}
