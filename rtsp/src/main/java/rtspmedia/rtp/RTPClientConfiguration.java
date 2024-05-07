package rtspmedia.rtp;

import java.io.InvalidObjectException;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class RTPClientConfiguration implements JSONSerializable {
    private String serverAddress;

    public RTPClientConfiguration(JSONObject cdata) throws InvalidObjectException {
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

        if (config.size() > 1)
            throw new InvalidObjectException("Superflous fields -- invalid configuration object.");

    }

    public String serialize() {
        return toJSONType().getFormattedJSON();
    }

    public JSONType toJSONType() {
        JSONObject obj = new JSONObject();
        obj.put("server-address", serverAddress);
        return obj;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    @Override
    public String toString() {
        return "Server Address: " + this.serverAddress;
    }

}