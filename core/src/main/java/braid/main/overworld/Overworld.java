package braid.main.overworld;

import braid.main.Braid;

import braid.main.screens.levels.TestLevel;
import braid.main.screens.menus.PauseMenu;
import braid.main.tools.KeyBindings;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Objects;

public class Overworld implements Screen {
    private final Braid game;
    private final TextureAtlas atlas;
    public static boolean gameIsPaused;
    private InputProcessor inputProcessor;

    // Camera and Map variables
    private final OverworldCamera camera;
    private final TmxMapLoader mapLoader;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private int railLayerIndex;
    public Array<OverworldNode> nodes;


    private Stage stage;
    private OverworldPlayer player;


    public Overworld(Braid game) {
        this.game = game;
        atlas = new TextureAtlas("packedimages/sprites.atlas");

        // Load current keybindings
        KeyBindings.standardKeybindings();
        KeyBindings.loadKeyBindings();

        // Setup level map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/overworld-map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Braid.PPM);

        railLayerIndex = map.getLayers().getIndex("rails-img");


        setupMapNodes();

        stage = new Stage();

        // find start node -> currently just hard set to uni
        OverworldNode uni = nodes.first();
        for (OverworldNode n : nodes) {
            if (Objects.equals(n.getName(), "UNI"))
                uni = n;
        }

        player = new OverworldPlayer(this, uni.getPosition().x, uni.getPosition().y);
        player.setPreviousNode(uni);
        player.setCurrentNode(uni);
        stage.addActor(player);

        // Final setup steps
        camera = new OverworldCamera(35*16, 35*9, player);
        camera.setMap(map);

        setupInput();

    }

    private void setupInput() {
        inputProcessor = new InputAdapter() {
            @Override
            public boolean keyDown (int keycode) {

                // Pause game
                if (keycode == KeyBindings.getKey("ESC") && !gameIsPaused) {
                    new Thread(() -> {
                        long time = System.currentTimeMillis();
                        while (System.currentTimeMillis() < time + 1){}
                        Gdx.app.postRunnable(() -> pause() );
                    }).start();
                    return true;
                }

                // Enter level
                if (keycode == KeyBindings.getKey("INTERACT") || keycode == Input.Keys.ENTER) {
                    OverworldNode node = player.getCurrentNode();
                    if (node instanceof LevelNode && ((LevelNode) node).isUnlocked()) {
                        enterLevel(node.getName());
                        return true;
                    }
                }

                // Handle overworld movement only if the player is not currently moving
                if (player.isMoving())
                    return true;

                handleMovement(keycode);
                return true;
            }
        };

        Gdx.input.setInputProcessor(inputProcessor);
    }

    private void enterLevel(String levelName) {
        // get next level based on current node
        Screen newScreen = switch (levelName) {
            case "UNI" -> new TestLevel(game);
            // case "HBF" ->
            // case "FREUDENBERG" ->
            // case "LUISENVIERTEL" ->
            // case "ARKADEN" ->
            // case "OBERBARMEN" ->
            // case "WEGZURUNI" ->
            // case "SCHLOSSBURG" ->
            // case "BAYER" ->
            // case "ZOO" ->
            default -> throw new IllegalStateException("Unexpected level name: " + levelName);
        };

        // Zoom transition
        final float targetZoom = 0.5f;
        final float zoomSpeed = 0.01f;
        final float targetRotation = -20f;
        final float rotationSpeed = -.4f;

        new Thread(() -> {
            float currentRotation = 0f;

            while (camera.getCamera().zoom > targetZoom && currentRotation > targetRotation) {
                camera.getCamera().zoom -= zoomSpeed;
                if (camera.getCamera().zoom < targetZoom) {
                    camera.getCamera().zoom = targetZoom;
                }

                camera.getCamera().rotate(rotationSpeed);  // Rotate the camera
                currentRotation += rotationSpeed;         // Track total rotation
                if (currentRotation < targetRotation) {
                    currentRotation = targetRotation;     // Clamp to target rotation
                }

                camera.getCamera().update();

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Transition to the new level once zoom completes
            Gdx.app.postRunnable(() -> game.setScreen(newScreen));
        }).start();

        // Remove overworld input processor
        Gdx.input.setInputProcessor(null);
    }

    private void handleMovement(int keycode) {
        String newNode = "";

        OverworldNode node = player.getCurrentNode();
        LevelNode check = (LevelNode) node;

        if (keycode == KeyBindings.getKey("UP_KEY")) {
            if (check.hasNeighborNorth())
                newNode = check.getNeighborNorth();
        } else if (keycode == KeyBindings.getKey("RIGHT_KEY")) {
            if (check.hasNeighborEast())
                newNode = check.getNeighborEast();
        } else if (keycode == KeyBindings.getKey("DOWN_KEY")) {
            if (check.hasNeighborSouth())
                newNode = check.getNeighborSouth();
        } else if (keycode == KeyBindings.getKey("LEFT_KEY")) {
            if (check.hasNeighborWest())
                newNode = check.getNeighborWest();
        }

        newNode = newNode.toUpperCase();

        for (OverworldNode n : nodes) {
            if (Objects.equals(n.getName(), newNode))
                node = n;
        }

        if (!Objects.equals(node.getName(), player.getCurrentNode().getName())) {
            player.setPreviousNode(player.getCurrentNode());
            player.setCurrentNode(node);
            player.moveToCurrentNode();
        }
    }

    private void setupMapNodes() {
        MapLayer nodeLayer = map.getLayers().get("nodes");

        nodes = new Array<>();

        for (EllipseMapObject ellipse : nodeLayer.getObjects().getByType(EllipseMapObject.class)) {
            /*LevelNode node = new LevelNode(ellipse);
            // AnchorSouth ist nur der Ankerpunkt zwischen Bayer und dem Zoo, daher ist
            //   der isUnlocked wert nicht gegeben
            if (!Objects.equals(node.getName(), "ANCHORSOUTH")) {
                node.isUnlocked(savegame.getUnlockStatus(node.getName()));
            }
            nodes.add(node);*/

            nodes.add(new LevelNode(ellipse));
        }

        MapLayer transitionLayer = map.getLayers().get("transitions");

        for (RectangleMapObject rect : transitionLayer.getObjects().getByType(RectangleMapObject.class)) {
            nodes.add(new TransitionNode(rect));
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        KeyBindings.loadKeyBindings();
        if(!gameIsPaused) {
            delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());

            stage.act(delta);
            player.update();

            updateCamera();

            clearScreen();
            renderWorld();
        }
    }

    private void clearScreen() {
        ScreenUtils.clear(0, 0, 0, 1);
    }

    private void renderWorld() {
        renderer.setView(camera.getCamera());
        for (int i = 0; i < railLayerIndex; i++)
            renderer.render(new int[] {i});
        game.batch.setProjectionMatrix(camera.getCamera().combined);

        // draw game objects
        game.batch.begin();
        player.getSprite().draw(game.batch);
        game.batch.end();

        for (int i = railLayerIndex; i < map.getLayers().getCount(); i++) {
            renderer.render(new int[] { i });
        }

    }

    private void updateCamera() {
        camera.followTarget();
        camera.getCamera().update();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    @Override
    public void pause() {
        gameIsPaused = true;
        Gdx.input.setInputProcessor(null);
        game.setScreen(new PauseMenu(game, this,true));
    }

    @Override
    public void resume() {
        KeyBindings.loadKeyBindings();
        setupInput();
        gameIsPaused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    // Getters and setters


    public TextureAtlas getAtlas() {
        return atlas;
    }
}
