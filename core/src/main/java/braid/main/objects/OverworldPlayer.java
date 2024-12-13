package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.Overworld;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class OverworldPlayer extends Actor {
    private Sprite sprite;

    private TextureRegion north, northEast, east, southEast, south, southWest, west, northWest;

    private OverworldNode currentNode;

    public OverworldPlayer(Overworld overworld, float x, float y) {
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

        setPosition(x,y);
    }

    public void update() {
        sprite.setPosition(getX()-sprite.getWidth()/2, getY()-sprite.getHeight()/2);
    }

    public void move() {
        addAction(Actions.moveTo(currentNode.getPosition().x, currentNode.getPosition().y, 1f));
    }



    public OverworldNode getCurrentNode() { return currentNode; }
    public void setCurrentNode(OverworldNode node) { currentNode = node; }

    public Sprite getSprite() {
        return sprite;
    }
}
