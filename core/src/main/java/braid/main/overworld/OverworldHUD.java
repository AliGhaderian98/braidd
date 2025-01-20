package braid.main.overworld;

import braid.main.Braid;
import braid.main.screens.huds.LevelHUD;
import braid.main.tools.Savemanager;
import braid.main.tools.TextFontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.awt.*;

public class OverworldHUD implements Disposable {
    private Stage stage;
    private Viewport viewport;

    private Label levelNameLabel;
    private Label pageLabel;
    private Image pageImage;
    private Image finishStar;

    private NinePatchDrawable textbox;
    private Table table;


    public OverworldHUD(TextureAtlas atlas) {
        viewport = new FitViewport(Braid.V_WIDTH/2f, Braid.V_HEIGHT/2f, new OrthographicCamera());
        stage = new Stage(viewport);

        Label.LabelStyle LevelFont = new Label.LabelStyle(TextFontManager.getBoldPixelFont(35), Color.BLACK);
        Label.LabelStyle TextFont = new Label.LabelStyle(TextFontManager.getBoldPixelFont(30), Color.BLACK);

        levelNameLabel = new Label(formatLevelName(Savemanager.currentsavegame.lastLevel), LevelFont);
        pageLabel = new Label(String.format("%d/5", countCollectables(Savemanager.currentsavegame.lastLevel)), TextFont);

        pageImage = new Image(atlas.findRegion("page"));
        pageImage.setScale(4);

        Table pageTable = new Table();
        pageTable.add(pageImage).expandX().padRight(90).padTop(70);
        pageTable.add(pageLabel).expandX();

        finishStar = new Image(atlas.findRegion("star"));
        finishStar.setScale(3);
        finishStar.setVisible(Savemanager.currentsavegame.FinishedLevels.get(Savemanager.currentsavegame.lastLevel));

        textbox = new NinePatchDrawable(atlas.createPatch("textbox2"));

        table = new Table();
        table.setBackground(textbox);

        float padding = 125f;
        table.bottom().left();
        table.setPosition(padding, padding);

        table.setSize(Braid.V_WIDTH / 4f - padding * 2,
            Braid.V_HEIGHT / 4f - padding*2);

        Table subTable = new Table();
        subTable.setFillParent(true);
        subTable.padLeft(30).padTop(45).padRight(120);

        subTable.add(levelNameLabel).expandX().align(Align.left);
        subTable.add(finishStar).align(Align.right).padTop(45);
        subTable.row();
        subTable.add(pageTable).align(Align.left).padTop(20).padLeft(20).colspan(2);

        table.add(subTable);

        stage.addActor(table);
    }

    private String formatLevelName(String levelName) {
        return switch (levelName) {
            case "UNI" -> "Uni Wuppertal";
            case "FREUDENBERG" -> "Freudenberg";
            case "ARKADEN" -> "City Arkaden";
            case "LUISENVIERTEL" -> "Luisenviertel";
            case "HBF" -> "Hauptbahnhof";
            case "OBERBARMEN" -> "Oberbarmen";
            case "BAYER" -> "Bayer-Fabrik";
            case "ZOO" -> "Wuppertaler Zoo";
            case "SCHLOSSBURG" -> "Schloss Burg";
            case "WEGZURUNI" -> "Weg hoch zur Uni";
            default -> throw new IllegalStateException("Unknown Level Name: " + levelName);
        };
    }

    private int countCollectables(String levelName) {
        int counter = 0;
        Array<Boolean> levelCollectables = Savemanager.currentsavegame.Collectables.get(levelName);
        for (int i = 0; i < 5; i ++) {
            if (levelCollectables.get(i))
                counter++;
        }
        return counter;
    }

    public void activate(String currentLevel) {
        levelNameLabel.setText(formatLevelName(currentLevel));
        pageLabel.setText(String.format("%d/5", countCollectables(currentLevel)));
        finishStar.setVisible(Savemanager.currentsavegame.FinishedLevels.get(currentLevel));

        table.setVisible(true);
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void hide() { table.setVisible(false); }

    @Override
    public void dispose() { stage.dispose(); }
}
