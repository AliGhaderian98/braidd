package braid.main.overworld;

import braid.main.Braid;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Vector2;

public class LevelNode extends OverworldNode {
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

    private boolean isUnlocked;

    private boolean hasNeighborNorth;
    private boolean hasNeighborEast;
    private boolean hasNeighborSouth;
    private boolean hasNeighborWest;

    private String neighborNorth;
    private String neighborEast;
    private String neighborSouth;
    private String neighborWest;


    public LevelNode(EllipseMapObject base) {
        name = stringToLevelName(base.getName());

        position = new Vector2((base.getEllipse().x + base.getEllipse().width/2) / Braid.PPM,
                                (base.getEllipse().y + base.getEllipse().height/2) / Braid.PPM);

        isUnlocked = (boolean) base.getProperties().get("isUnlocked");


        hasNeighborNorth = (boolean) base.getProperties().get("hasNeighborNorth");
        hasNeighborEast = (boolean) base.getProperties().get("hasNeighborEast");
        hasNeighborSouth = (boolean) base.getProperties().get("hasNeighborSouth");
        hasNeighborWest = (boolean) base.getProperties().get("hasNeighborWest");

        MapObject temp = (MapObject) base.getProperties().get("neighborNorth");
        if (temp != null)
            neighborNorth = temp.getName();
        else neighborNorth = "NONE";

        temp = (MapObject) base.getProperties().get("neighborEast");
        if (temp != null)
            neighborEast = temp.getName();
        else neighborEast = "NONE";

        temp = (MapObject) base.getProperties().get("neighborSouth");
        if (temp != null)
            neighborSouth = temp.getName();
        else neighborSouth = "NONE";

        temp = (MapObject) base.getProperties().get("neighborWest");
        if (temp != null)
            neighborWest = temp.getName();
        else neighborWest = "NONE";
    }



    // Getters and Setters
    @Override
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
