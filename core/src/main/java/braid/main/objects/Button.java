package braid.main.objects;

import braid.main.tools.EventListener;
import braid.main.tools.UserData;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

import static braid.main.Braid.PPM;

public class Button extends InteractiveGameObject {
    private enum ButtonState {
        ON, OFF;
    }
    private final TiledMap map;
    private ButtonState buttonState;
    private final String actionType;
    private final String targetName;
    private final EventListener event;
    private final Rectangle boundary;
    protected Body body;

    public Button(World world, Rectangle boundary, TiledMap map, String actionType, String targetName, EventListener event) {
        super(world, boundary, true);
        this.buttonState = ButtonState.OFF;
        this.map = map;
        this.actionType = actionType;
        this.event = event;
        this.targetName = targetName;
        this.boundary = boundary;

        System.out.println("spawnButton" + actionType);

        fixture.setUserData(new UserData("Button", this));
    }

    @Override
    public void defineBody() {

    }

    public void toggle() {
        if(buttonState != ButtonState.ON){
            this.buttonState = ButtonState.ON;
            performAction();
        }
    }

    private void performAction(){
        if ("toggleLayer".equals(actionType)) {
            MapLayer layer = map.getLayers().get(targetName);
            System.out.println(targetName + " " + actionType);
            if(layer != null){
                layer.setVisible(!layer.isVisible());
            }
            this.event.spawnStuff(targetName);
            this.event.hasPressedButton(this.buttonState == ButtonState.ON);
        } else if ("toggleNPCTrigger".equals(actionType)) {
            this.event.hasPressedButton(this.buttonState == ButtonState.ON);
        } else if ("repairButton".equals(actionType)) {
                this.event.hasPressedAllButtons();
        }
    }

}
