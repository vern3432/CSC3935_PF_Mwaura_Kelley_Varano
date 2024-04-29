package rtspmedia.Server.LibraryMangement;

import java.io.InvalidObjectException;
import java.util.ArrayList;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONType;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;

public class Album implements JSONSerializable {
    private ArrayList<Song> songs = new ArrayList<>();

    public Album() {
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    @Override
    public void deserialize(JSONType json) throws InvalidObjectException {
        if (!(json instanceof JSONObject))
            throw new InvalidObjectException("JSON object expected");

        Object jsonSongsObject = ((JSONObject) json).get("songs");
        if (jsonSongsObject instanceof JSONArray) {
            JSONArray jsonSongs = (JSONArray) jsonSongsObject;
            for (int i = 0; i < jsonSongs.size(); i++) {
                JSONObject jsonSong = (JSONObject) jsonSongs.get(i);
                Song song = new Song(
                    jsonSong.getString("name"),
                    jsonSong.getString("albumImage"),
                    jsonSong.getString("path")
                );
                songs.add(song);
            }
        }
    }

    @Override
    public String serialize() {
        return toJSONType().toJSON();
    }

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonSongs = new JSONArray();
        for (Song song : songs) {
            jsonSongs.add(song.toJSONType());
        }
        jsonObj.put("songs", jsonSongs);
        return jsonObj;
    }
}