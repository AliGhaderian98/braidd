package braid.main.screens.huds;

import braid.main.tools.Savemanager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class FinishGameHUD extends FinishHUD {
    private Label notenLabel;

    public FinishGameHUD(SpriteBatch batch, TextureAtlas atlas) {
        super(batch, atlas);

        Savemanager.saveGame(true);

        finishLabel.setText("GAME FINISHED!");

        pageLabel.setText(String.format("collected pages: %d/50", Savemanager.AmountFoundCollectables(Savemanager.currentsavegame)));

        timeLabel.setText("total time: "+formatTime(Savemanager.currentsavegame.Playtime)+
            "\nbest times: "+formatTime(Savemanager.SumBestTimes(Savemanager.currentsavegame)));

        notenLabel = new Label(String.format("Note der Masterarbeit: %.1f", calculateNote()), TextFont);

        table.row();
        table.add(notenLabel).padTop(100);
    }

    private float calculateNote() {
        float note = 0.0f;
        int pages = Savemanager.AmountFoundCollectables(Savemanager.currentsavegame);

        if (pages < 25)
            note = 4.0f;
        else if (pages < 28)
            note = 3.7f;
        else if (pages < 31)
            note = 3.3f;
        else if (pages < 34)
            note = 3.0f;
        else if (pages < 37)
            note = 2.7f;
        else if (pages < 40)
            note = 2.3f;
        else if (pages < 43)
            note = 2.0f;
        else if (pages < 46)
            note = 1.7f;
        else if (pages < 49)
            note = 1.3f;
        else
            note = 1.0f;

        return note;
    }
}
