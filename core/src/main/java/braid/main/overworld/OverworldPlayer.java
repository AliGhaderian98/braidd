package braid.main.overworld;

import braid.main.Braid;
import braid.main.tools.Savemanager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.Objects;

public class OverworldPlayer extends Actor {
    private Sprite sprite;
    private final TextureRegion north, northEast, east, southEast, south, southWest, west, northWest;

    private Vector2 previousPosition;

    float horizontalOffset = 8f/Braid.PPM;
    float verticalOffset = 4f/Braid.PPM;
    float diagnoalOffset = 4f/Braid.PPM;
    float offset = horizontalOffset;

    private final Overworld overworld;
    private OverworldNode previousNode;
    private OverworldNode currentNode;

    private boolean isMoving = false;
    private OverworldHUD hud;

    public OverworldPlayer(Overworld overworld, float x, float y, OverworldHUD hud) {
        TextureAtlas atlas = overworld.getAtlas();
        this.overworld = overworld;
        this.hud = hud;

        north = new TextureRegion(atlas.findRegion("schwebimini_n"));
        northEast = new TextureRegion(atlas.findRegion("schwebimini_ne"));
        east = new TextureRegion(atlas.findRegion("schwebimini_e"));
        southEast = new TextureRegion(atlas.findRegion("schwebimini_se"));
        south = new TextureRegion(atlas.findRegion("schwebimini_s"));
        southWest = new TextureRegion(atlas.findRegion("schwebimini_sw"));
        west = new TextureRegion(atlas.findRegion("schwebimini_w"));
        northWest = new TextureRegion(atlas.findRegion("schwebimini_nw"));

        sprite = new Sprite(west);
        sprite.setBounds(0,0,(16/ Braid.PPM)*1.5f, (16/Braid.PPM)*1.5f);

        sprite.setPosition(getX(), getY());

        setPosition(x,y);
        previousPosition = new Vector2(x,y);
    }

    public void update() {
        sprite.setPosition(getX()-sprite.getWidth()/2, getY()-sprite.getHeight()/2 - offset);

        previousPosition.x = getX();
        previousPosition.y = getY();
    }

    private void updateSprite(float angle) {
        if (angle >= 22.5 && angle < 67.5) {
            sprite.setRegion(northEast);
            offset = diagnoalOffset;
        } else if (angle >= 67.5 && angle < 112.5) {
            sprite.setRegion(north);
            offset = verticalOffset;
        } else if (angle >= 112.5 && angle < 157.5) {
            sprite.setRegion(northWest);
            offset = diagnoalOffset;
        } else if (angle >= 157.5 && angle < 202.5) {
            sprite.setRegion(west);
            offset = horizontalOffset;
        } else if (angle >= 202.5 && angle < 247.5) {
            sprite.setRegion(southWest);
            offset = diagnoalOffset;
        } else if (angle >= 247.5 && angle < 292.5) {
            sprite.setRegion(south);
            offset = verticalOffset;
        } else if (angle >= 292.5 && angle < 337.5) {
            sprite.setRegion(southEast);
            offset = diagnoalOffset;
        } else {
            sprite.setRegion(east);
            offset = horizontalOffset;
        }
    }

    public void moveToCurrentNode() {
        isMoving = true;
        hud.hide();

        Vector2 position = new Vector2(getX(), getY());
        float distance = position.dst(currentNode.getPosition());
        updateSprite(currentNode.getPosition().cpy().sub(position).angleDeg());

        addAction(Actions.sequence(
            Actions.moveTo(currentNode.getPosition().x, currentNode.getPosition().y, 0.6f*distance),
            Actions.run(() -> {
                if (currentNode instanceof TransitionNode) {
                    String neighborA = ((TransitionNode) currentNode).getNeighborA();
                    String neighborB = ((TransitionNode) currentNode).getNeighborB();

                    String next = Objects.equals(previousNode.getName(), neighborA) ? neighborB : neighborA;

                    previousNode = currentNode;
                    for (OverworldNode n : overworld.nodes) {
                        if (Objects.equals(n.getName(), next))
                            currentNode = n;
                    }
                    moveToCurrentNode();
                } else if (Objects.equals(currentNode.getName(), "WEGZURUNI") && !((LevelNode) currentNode).isUnlocked()) {
                    String next;
                    if (previousNode instanceof TransitionNode) {
                        next = "HBF";
                    } else {
                        next = ((LevelNode) currentNode).getNeighborEast().toUpperCase();
                    }

                    previousNode = currentNode;
                    for (OverworldNode n : overworld.nodes) {
                        if (Objects.equals(n.getName(), next))
                            currentNode = n;
                    }
                    moveToCurrentNode();
                } else {
                    isMoving = false;
                    hud.activate(currentNode.getName());
                }

                // force enter the wegZurUni level if it was just unlocked
                if (Objects.equals(currentNode.getName(), "WEGZURUNI") && Savemanager.currentsavegame.wegZurUniJustUnlocked) {
                    overworld.enterLevel(currentNode.getName());
                    // reset flag that this level was just unlocked to false after starting level for the first time
                    if (Savemanager.currentsavegame.wegZurUniJustUnlocked)
                        Savemanager.currentsavegame.wegZurUniJustUnlocked = false;
                }
            })
        ));
    }



    public OverworldNode getCurrentNode() { return currentNode; }
    public void setCurrentNode(OverworldNode node) { currentNode = node; }

    public OverworldNode getPreviousNode() { return previousNode; }
    public void setPreviousNode(OverworldNode node) { previousNode = node; }

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isMoving() { return  isMoving; }
    public void isMoving(boolean value) { isMoving = value; }
}
