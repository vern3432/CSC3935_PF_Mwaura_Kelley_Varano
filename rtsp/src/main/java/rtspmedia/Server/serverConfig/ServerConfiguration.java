package rtspmedia.Server.serverConfig;

import java.io.InvalidObjectException;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class ServerConfiguration implements JSONSerializable {
    private int port;
    private int maxConnections;

    public ServerConfiguration(JSONObject cdata) throws InvalidObjectException {
        deserialize(cdata);
    }

    public void deserialize(JSONType obj) throws InvalidObjectException {
        JSONObject config = null;

        if (!(obj instanceof JSONObject))
            throw new InvalidObjectException("JSONObject expected.");

        config = (JSONObject) obj;

        if (config.containsKey("port"))
            port = config.getInt("port");
        else
            throw new InvalidObjectException("Missing port field -- invalid configuration object.");
        if (config.containsKey("max-connections"))
            this.maxConnections = config.getInt("max-connections");
        else
            throw new InvalidObjectException("Missing max-connections field -- invalid configuration object.");

        if (config.size() > 2)
            throw new InvalidObjectException("Superflous fields -- invalid configuration object.");

    }

    public String serialize() {
        return toJSONType().getFormattedJSON();
    }

    public JSONType toJSONType() {
        JSONObject obj = new JSONObject();
        obj.put("port", port);
        obj.put("max-connections", maxConnections);
        return obj;
    }

    public int getPort() {
        return this.port;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    @Override
    public String toString() {
        return "Port: " + this.port + "Max Connections: " + this.maxConnections;
    }

}