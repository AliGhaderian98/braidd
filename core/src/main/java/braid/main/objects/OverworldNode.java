package braid.main.objects;

import braid.main.Braid;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class OverworldNode {
    private enum Type {LEVEL, ANCHOR};
    private enum LevelName {
        HBF,
        UNI,
        FREUDENBERG,
        LUISENVIERTEL,
        ARKADEN,
        OBERBARMEN,
        WEGZURUNI,
        SCHLOSSBURG,
        BAYER,
        ZOO,
        ANCHORSOUTH,
        NONE
    };

    private LevelName name;
    private Vector2 position;
    private Type type;
    private boolean isUnlocked;

    private boolean hasNeighborNorth;
    private boolean hasNeighborEast;
    private boolean hasNeighborSouth;
    private boolean hasNeighborWest;

    private LevelName neighborNorth;
    private LevelName neighborEast;
    private LevelName neighborSouth;
    private LevelName neighborWest;

    public OverworldNode(EllipseMapObject base) {
        name = stringToLevelName(base.getName());

        position = new Vector2((base.getEllipse().x + base.getEllipse().width/2) / Braid.PPM,
                                (base.getEllipse().y + base.getEllipse().height/2) / Braid.PPM);

        if (name == LevelName.ANCHORSOUTH)
            type = Type.ANCHOR;
        else
            type = Type.LEVEL;

        isUnlocked = (boolean) base.getProperties().get("isUnlocked");


        hasNeighborNorth = (boolean) base.getProperties().get("hasNeighborNorth");
        hasNeighborEast = (boolean) base.getProperties().get("hasNeighborEast");
        hasNeighborSouth = (boolean) base.getProperties().get("hasNeighborSouth");
        hasNeighborWest = (boolean) base.getProperties().get("hasNeighborWest");

        MapObject temp = (MapObject) base.getProperties().get("neighborNorth");
        if (temp != null)
            neighborNorth = stringToLevelName(temp.getName());
        else neighborNorth = LevelName.NONE;

        temp = (MapObject) base.getProperties().get("neighborEast");
        if (temp != null)
            neighborEast = stringToLevelName(temp.getName());
        else neighborEast = LevelName.NONE;

        temp = (MapObject) base.getProperties().get("neighborSouth");
        if (temp != null)
            neighborSouth = stringToLevelName(temp.getName());
        else neighborSouth = LevelName.NONE;

        temp = (MapObject) base.getProperties().get("neighborWest");
        if (temp != null)
            neighborWest = stringToLevelName(temp.getName());
        else neighborWest = LevelName.NONE;
    }



    // Getters and Setters
    public String getName() { return name.toString(); }

    private LevelName stringToLevelName(String string) {
        return switch (string.toUpperCase()) {
            case "HBF" -> LevelName.HBF;
            case "UNI" -> LevelName.UNI;
            case "FREUDENBERG" -> LevelName.FREUDENBERG;
            case "LUISENVIERTEL" -> LevelName.LUISENVIERTEL;
            case "ARKADEN" -> LevelName.ARKADEN;
            case "OBERBARMEN" -> LevelName.OBERBARMEN;
            case "WEGZURUNI" -> LevelName.WEGZURUNI;
            case "SCHLOSSBURG" -> LevelName.SCHLOSSBURG;
            case "BAYER" -> LevelName.BAYER;
            case "ZOO" -> LevelName.ZOO;
            case "ANCHORSOUTH" -> LevelName.ANCHORSOUTH;
            case "" -> LevelName.NONE;
            case "NONE" -> LevelName.NONE;
            default -> throw new IllegalStateException("Unexpected value: " + string);
        };
    }

    public boolean nameEquals(String value) { return stringToLevelName(value) == name; }

    public Vector2 getPosition() { return position; }

    public boolean isUnlocked() { return isUnlocked; }
    public void isUnlocked(boolean value) { isUnlocked = value; }

    public boolean hasNeighborNorth() { return hasNeighborNorth; }
    public boolean hasNeighborEast() { return hasNeighborEast; }
    public boolean hasNeighborSouth() { return hasNeighborSouth; }
    public boolean hasNeighborWest() { return hasNeighborWest; }

    public String getNeighborNorth() { return neighborNorth.toString(); }
    public String getNeighborEast() { return neighborEast.toString(); }
    public String getNeighborSouth() { return neighborSouth.toString(); }
    public String getNeighborWest() { return neighborWest.toString(); }
}
