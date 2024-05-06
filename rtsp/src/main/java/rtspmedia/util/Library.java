package rtspmedia.util;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONType;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;

public class Library implements JSONSerializable {
    private List<Song> songs = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();

    public Library() {
    }

    /**
     * @param song
     */
    public void addSong(Song song) {
        songs.add(song);
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    @Override
    public void deserialize(JSONType json) throws InvalidObjectException {
        if (!(json instanceof JSONObject))
            throw new InvalidObjectException("JSON object expected");

        JSONObject jsonObj = (JSONObject) json;

        // Handling songs
        Object jsonSongsObject = jsonObj.get("songs");
        if (jsonSongsObject instanceof JSONArray) {
            JSONArray jsonSongs = (JSONArray) jsonSongsObject;
            for (int i = 0; i < jsonSongs.size(); i++) {
                Object jsonSongObject = jsonSongs.get(i);
                if (jsonSongObject instanceof JSONObject) {
                    JSONObject jsonSong = (JSONObject) jsonSongObject;
                    String songName = jsonSong.getString("name");
                    String albumImage = jsonSong.getString("albumImage");
                    String path = jsonSong.getString("path");
                    Song song = new Song(songName, albumImage, path);
                    song.deserialize(jsonSong);
                    songs.add(song);
                }
            }
        }

        // Handling albums
        Object jsonAlbumsObject = jsonObj.get("albums");
        if (jsonAlbumsObject instanceof JSONArray) {
            JSONArray jsonAlbums = (JSONArray) jsonAlbumsObject;
            for (int i = 0; i < jsonAlbums.size(); i++) {
                Object jsonAlbumObject = jsonAlbums.get(i);
                if (jsonAlbumObject instanceof JSONObject) {
                    JSONObject jsonAlbum = (JSONObject) jsonAlbumObject;
                    Album album = new Album();
                    album.deserialize(jsonAlbum);
                    albums.add(album);
                }
            }
        }
    }

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonSongs = new JSONArray();
        for (Song song : songs) {
            jsonSongs.add(song.toJSONType());
        }
        jsonObj.put("songs", jsonSongs);

        JSONArray jsonAlbums = new JSONArray();
        for (Album album : albums) {
            jsonAlbums.add(album.toJSONType());
        }
        jsonObj.put("albums", jsonAlbums);

        return jsonObj;
    }

    @Override
    public String serialize() {
        return toJSONType().toJSON();
    }
}