package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.Button;
import braid.main.objects.NPC;
import com.badlogic.gdx.maps.MapLayer;

import java.util.Objects;

public class MountainMap extends LevelScreen{
    private final int instance;

    public MountainMap(Braid game, int instance) {
        super(game, "maps/tilesets/WEGZURUNI/QuantenEbene/maps/Mountain" + instance + ".tmx", "packedimages/sprites.atlas", "audio/music/WEGZURUNI/MountainMap" + instance + ".mp3");
        this.instance = instance;
        player.setPosition(272/Braid.PPM, 32/Braid.PPM);
    }
    @Override
    protected LevelScreen getNewInstance() {
        return new MountainMap(game, this.instance);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }
}
