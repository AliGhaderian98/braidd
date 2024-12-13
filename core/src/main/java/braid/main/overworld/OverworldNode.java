package braid.main.overworld;

import com.badlogic.gdx.math.Vector2;

public abstract class OverworldNode {
    protected Vector2 position;

    public abstract String getName();

    public Vector2 getPosition() { return position; }

}
