package rtspmedia.Client;

import java.io.InvalidObjectException;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class ClientConfiguration implements JSONSerializable {
    private String serverAddress;
    private int serverPort;

    public ClientConfiguration(JSONObject cdata) throws InvalidObjectException {
        deserialize(cdata);
    }

    
    /** 
     * @param obj
     * @throws InvalidObjectException
     */
    public void deserialize(JSONType obj) throws InvalidObjectException {
        JSONObject config = null;

        if (!(obj instanceof JSONObject))
            throw new InvalidObjectException("JSONObject expected.");

        config = (JSONObject) obj;

        if (config.containsKey("server-address"))
            serverAddress = config.getString("server-address");
        else
            throw new InvalidObjectException("Missing server-address field -- invalid configuration object.");

        if (config.containsKey("server-port"))
            serverPort = config.getInt("server-port");
        else
            throw new InvalidObjectException("Missing server-port field -- invalid configuration object.");

        if (config.size() > 2)
            throw new InvalidObjectException("Superflous fields -- invalid configuration object.");

    }

    public String serialize() {
        return toJSONType().getFormattedJSON();
    }

    public JSONType toJSONType() {
        JSONObject obj = new JSONObject();
        obj.put("server-address", serverAddress);
        obj.put("server-port", serverPort);
        return obj;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    @Override
    public String toString() {
        return "Server Address: " + this.serverAddress + "\nServer Port: " + this.serverPort;
    }

}