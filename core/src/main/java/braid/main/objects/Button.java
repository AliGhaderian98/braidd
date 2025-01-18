package braid.main.objects;

import braid.main.tools.EventListener;
import braid.main.tools.UserData;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Button extends InteractiveGameObject {
    private enum ButtonState {
        ON, OFF;
    }
    private final TiledMap map;
    private ButtonState buttonState;
    private String actionType;
    private String targetName;
    private final EventListener event;

    public Button(World world, Rectangle boundary, TiledMap map, EventListener event) {
        super(world, boundary, true);
        this.buttonState = ButtonState.OFF;
        this.map = map;
        this.event = event;

        fixture.setUserData(new UserData("Button", this));
    }

    @Override
    public void defineBody() {

    }

    public void setActionType(String actionType){
        this.actionType = actionType;
    }

    public void setTargetName(String targetName){
        this.targetName = targetName;
    }


    public void toggle() {
        if(buttonState != ButtonState.ON){
            this.buttonState = ButtonState.ON;
            performAction();
        }
    }

    private void performAction(){
        if ("toggleLayer".equals(actionType)) {
            System.out.println("pasttoggle");

            MapLayer layer = map.getLayers().get(targetName);
            if(layer != null){
                layer.setVisible(!layer.isVisible());
            }
        } else if ("toggleNPCTrigger".equals(actionType)) {
            event.hasPressedButton(this.buttonState == ButtonState.ON);
        }
    }

}
