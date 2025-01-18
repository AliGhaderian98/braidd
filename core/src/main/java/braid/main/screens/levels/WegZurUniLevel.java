package braid.main.screens.levels;

import braid.main.Braid;
import braid.main.objects.Button;
import braid.main.objects.MovingPlatform;
import braid.main.objects.NPC;
import braid.main.rewind.RewindController;
import braid.main.rewind.RewindableBody;
import braid.main.objects.Enemy;
import braid.main.tools.EventListener;
import braid.main.tools.Savemanager;
import com.badlogic.gdx.math.Rectangle;

public class WegZurUniLevel extends LevelScreen {
    private final EventListener event;

    public WegZurUniLevel(Braid game) {
        super(game, "maps/tilesets/WEGZURUNI/Present/maps/WEGZURUNI.tmx", "packedimages/sprites.atlas", "audio/music/background_music.mp3");
        levelName = "WEGZURUNI";
        event = new EventListener(game, this);

        player.setPosition(64/Braid.PPM, 160/Braid.PPM);
    }

    @Override
    public void spawnNPC(String type, Rectangle npcBoundary, Rectangle npcTrigger, String text, String spriteName){
        NPC npc = new NPC(world, this, getSubtitleManager(), type, npcBoundary, npcTrigger, text, spriteName);
        if(type == "")
        npc.setEvent(this.event);
        addNPC(npc);
    }

    @Override
    public void spawnButton(Rectangle boundary, String actionType, String targetName) {
        Button button = new Button(world, boundary, map, actionType, targetName);
        button.setEvent(this.event);
        addButton(button);
    }


    @Override
    protected LevelScreen getNewInstance() {
        return new WegZurUniLevel(game);
    }


    @Override
    public void show() {
        // Custom behavior for showing TestScreen

    }

    @Override
    public void hide() {

    }
}

