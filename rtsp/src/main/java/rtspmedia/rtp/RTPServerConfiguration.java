package rtspmedia.rtp;

import java.io.InvalidObjectException;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class RTPServerConfiguration implements JSONSerializable {

    private int port;

    public RTPServerConfiguration(JSONObject cdata) throws InvalidObjectException {
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
        if (config.size() > 1)
            throw new InvalidObjectException("Superflous fields -- invalid configuration object.");

    }

    public String serialize() {
        return toJSONType().getFormattedJSON();
    }

    public JSONType toJSONType() {
        JSONObject obj = new JSONObject();
        obj.put("port", port);
        return obj;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public String toString() {
        return "Port: " + this.port;
    }

}
