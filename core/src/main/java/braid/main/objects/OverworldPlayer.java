package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.Overworld;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class OverworldPlayer extends Actor {
    private Sprite sprite;

    private TextureRegion north, northEast, east, southEast, south, southWest, west, northWest;

    public OverworldPlayer(Overworld overworld) {
        TextureAtlas atlas = overworld.getAtlas();

        north = new TextureRegion(atlas.findRegion("schwebimini_n"));
        northEast = new TextureRegion(atlas.findRegion("schwebimini_ne"));
        east = new TextureRegion(atlas.findRegion("schwebimini_e"));
        southEast = new TextureRegion(atlas.findRegion("schwebimini_se"));
        south = new TextureRegion(atlas.findRegion("schwebimini_s"));
        southWest = new TextureRegion(atlas.findRegion("schwebimini_sw"));
        west = new TextureRegion(atlas.findRegion("schwebimini_w"));
        northWest = new TextureRegion(atlas.findRegion("schwebimini_nw"));

        sprite = new Sprite(north);
        sprite.setBounds(0,0,16/ Braid.PPM, 16/Braid.PPM);

        sprite.setPosition(getX(), getY());
    }

    public Sprite getSprite() {
        return sprite;
    }
}
