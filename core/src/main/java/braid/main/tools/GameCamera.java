package braid.main.tools;

import braid.main.Braid;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


// Die GameCamera ist dafür da, um die Spielwelt und die Position des Spielers automatisch der Kamera anzupassen
public class GameCamera {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float worldWidth;
    private final float worldHeight;
    private float mapWidth;
    private float mapHeight;



    public GameCamera(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        camera = new OrthographicCamera();
        viewport = new FitViewport(worldWidth / Braid.PPM , worldHeight / Braid.PPM, camera);
        camera.position.set((float) viewport.getWorldWidth() / 2, (float) viewport.getWorldHeight() / 2, 0);
    }

    public void setMap(TiledMap map) {
        int mapTileWidth = map.getProperties().get("width", Integer.class);
        int mapTileHeigth = map.getProperties().get("height", Integer.class);
        int tileSize = map.getProperties().get("tilewidth", Integer.class);
        mapWidth = (mapTileWidth * tileSize) / Braid.PPM;
        mapHeight = (mapTileHeigth * tileSize) / Braid.PPM;
    }

    public void followTarget(float targetX) {
        targetX /= Braid.PPM;

        float minX = viewport.getWorldWidth() / 2;
        float maxX = mapWidth - viewport.getWorldWidth() / 2;

        float newX = camera.position.x + (targetX - camera.position.x);
        camera.position.x = Math.max(minX, Math.min(newX, maxX));

        camera.update();
    }

    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
