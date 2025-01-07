package braid.main.tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.XmlReader;

public class TemplateTmxMapLoader extends TmxMapLoader {
    private FileHandle tmxFile;
    private final XmlReader xml = new XmlReader();

    @Override
    protected TiledMap loadTiledMap(FileHandle tmxFile, Parameters parameter, ImageResolver imageResolver) {
        this.tmxFile = tmxFile;
        return super.loadTiledMap(tmxFile, parameter, imageResolver);
    }

    @Override
    protected void loadObject(TiledMap map, MapObjects objects, XmlReader.Element element, float heightInPixels) {
        if (element.getName().equals("object") && element.hasAttribute("template")) {
            FileHandle templateFile = getRelativeFileHandle(tmxFile, element.getAttribute("template"));
            XmlReader.Element templateRoot = xml.parse(templateFile);
            XmlReader.Element templateObject = templateRoot.getChildByName("object");

            if (templateObject != null) {
                mergeTemplateObject(element, templateObject);
                super.loadObject(map, objects, templateObject, heightInPixels);
            }
        } else {
            super.loadObject(map, objects, element, heightInPixels);
        }
    }

    /**
     * Merges properties from a template object into a specific object element,
     * giving precedence to the properties of the specific object element.
     */
    private void mergeTemplateObject(XmlReader.Element element, XmlReader.Element templateObject) {
        // Copy coordinates and ID to template object
        templateObject.setAttribute("x", element.getAttribute("x", "0"));
        templateObject.setAttribute("y", element.getAttribute("y", "0"));
        templateObject.setAttribute("id", element.getAttribute("id", "0"));

        // Merge or overwrite properties
        XmlReader.Element templateProperties = templateObject.getChildByName("properties");
        XmlReader.Element objectProperties = element.getChildByName("properties");

        if (objectProperties == null) {
            // Use template properties if object properties are missing
            if (templateProperties != null) {
                templateObject.removeChild(templateProperties);
                templateObject.addChild(templateProperties);
            }
        } else {
            // Merge properties: overwrite template properties with object-specific ones
            if (templateProperties == null) {
                templateObject.addChild(objectProperties);
            } else {
                for (XmlReader.Element prop : objectProperties.getChildrenByName("property")) {
                    String name = prop.getAttribute("name");
                    XmlReader.Element existingProp = findProperty(templateProperties, name);
                    if (existingProp != null) {
                        templateProperties.removeChild(existingProp);
                    }
                    templateProperties.addChild(prop);
                }
            }
        }
    }

    /**
     * Finds a property by name in the given properties element.
     */
    private XmlReader.Element findProperty(XmlReader.Element propertiesElement, String name) {
        if (propertiesElement == null) return null;
        for (XmlReader.Element property : propertiesElement.getChildrenByName("property")) {
            if (property.getAttribute("name").equals(name)) {
                return property;
            }
        }
        return null;
    }
}
