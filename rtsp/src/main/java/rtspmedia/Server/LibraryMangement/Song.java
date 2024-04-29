package rtspmedia.Server.LibraryMangement;

import java.io.InvalidObjectException;
import java.util.Base64;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONType;
import merrimackutil.json.types.JSONObject;

public class Song implements JSONSerializable {
    private String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String albumImage; // Base64 encoded image
    private String path; // Absolute path to the song file

    public Song(String name, String albumImage, String path) {
        this.name = name;
        this.albumImage = albumImage;
        this.path = path;
    }

    @Override
    public void deserialize(JSONType json) throws InvalidObjectException {
        if (!(json instanceof JSONObject))
            throw new InvalidObjectException("JSON object expected");

        JSONObject jsonObj = (JSONObject) json;
        this.name = jsonObj.getString("name");
        this.albumImage = jsonObj.getString("albumImage");
        this.path = jsonObj.getString("path");
    }

    @Override
    public String serialize() {
        return toJSONType().toJSON();
    }

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name", name);
        jsonObj.put("albumImage", albumImage);
        jsonObj.put("path", path);
        return jsonObj;
    }
}
