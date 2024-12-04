package braid.main.tools;

import braid.main.Braid;
import braid.main.objects.Player;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/***********
 Die GameCamera ist dafür da, um die Spielwelt und die Position des Spielers
 automatisch der Kamera anzupassen
 ***********/

public class GameCamera {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float worldWidth;
    private final float worldHeight;
    private float mapWidth;
    private float mapHeight;
    private Player player;


    public GameCamera(float worldWidth, float worldHeight, Player player) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.player = player;

        camera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth / Braid.PPM, worldHeight / Braid.PPM, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }

    public void setMap(TiledMap map) {
        int mapTileWidth = map.getProperties().get("width", Integer.class);
        int mapTileHeigth = map.getProperties().get("height", Integer.class);
        int tileSize = map.getProperties().get("tilewidth", Integer.class);
        mapWidth = (mapTileWidth * tileSize) / Braid.PPM;
        mapHeight = (mapTileHeigth * tileSize) / Braid.PPM;
    }

    public void followTarget() {
        float targetX = player.b2body.getPosition().x;
        float targetY = player.b2body.getPosition().y;

        float minX = viewport.getWorldWidth() / 2;
        float maxX = mapWidth - viewport.getWorldWidth() / 2;
        float minY = viewport.getWorldHeight() / 2;
        float maxY = mapHeight - viewport.getWorldHeight() / 2;


        camera.position.x = Math.max(minX,Math.min(targetX,maxX));
        camera.position.y = Math.max(minY,Math.min(targetY,maxY));
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
