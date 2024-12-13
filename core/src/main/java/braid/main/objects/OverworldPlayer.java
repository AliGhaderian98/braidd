package braid.main.objects;

import braid.main.Braid;
import braid.main.screens.Overworld;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class OverworldPlayer extends Actor {
    private Sprite sprite;
    private TextureRegion north, northEast, east, southEast, south, southWest, west, northWest;

    private Vector2 previousPosition;

    float horizontalOffset = 8f/Braid.PPM;
    float verticalOffset = 4f/Braid.PPM;
    float diagnoalOffset = 4f/Braid.PPM;
    float offset = verticalOffset;

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
        sprite.setBounds(0,0,(16/ Braid.PPM)*1.5f, (16/Braid.PPM)*1.5f);

        sprite.setPosition(getX(), getY());

        setPosition(x,y);
        previousPosition = new Vector2(x,y);
    }

    public void update() {
        // update sprite
        if (previousPosition.x < getX()) {
            if (previousPosition.y < getY() - 0.0001) {
                sprite.setRegion(northEast);
                offset = diagnoalOffset;
            } else if (previousPosition.y > getY() + 0.0001) {
                sprite.setRegion(southEast);
                offset = diagnoalOffset;
            } else {
                sprite.setRegion(east);
                offset = horizontalOffset;
            }
        } else if (previousPosition.x > getX()) {
            if (previousPosition.y < getY() - 0.0001) {
                sprite.setRegion(northWest);
                offset = diagnoalOffset;
            } else if (previousPosition.y > getY() + 0.0001) {
                sprite.setRegion(southWest);
                offset = diagnoalOffset;
            } else {
                sprite.setRegion(west);
                offset = horizontalOffset;
            }
        } else {
            if (previousPosition.y < getY()) {
                sprite.setRegion(north);
                offset = verticalOffset;
            } else if (previousPosition.y > getY()) {
                sprite.setRegion(south);
                offset = verticalOffset;
            }
        }

        sprite.setPosition(getX()-sprite.getWidth()/2, getY()-sprite.getHeight()/2 - offset);

        previousPosition.x = getX();
        previousPosition.y = getY();
    }

    public void moveToCurrentNode() {
        addAction(Actions.moveTo(currentNode.getPosition().x, currentNode.getPosition().y, 1f));
    }



    public OverworldNode getCurrentNode() { return currentNode; }
    public void setCurrentNode(OverworldNode node) { currentNode = node; }

    public Sprite getSprite() {
        return sprite;
    }
}
