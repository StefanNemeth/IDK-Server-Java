/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.placeholder.services;

import org.json.simple.JSONObject;

public class ServerResult {
    public String getResponseCode() {
        return responseCode;
    }

    public JSONObject getResponse() {
        return response;
    }

    public ServerResult(final String responseCode, final JSONObject response) {
        this.responseCode = responseCode;
        this.response = response;
    }

    private final String responseCode;
    private final JSONObject response;
}
