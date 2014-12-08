package com.marine.events.standardevents;

import com.marine.events.MarineEvent;
import com.marine.net.handshake.ListResponse;

/**
 * Created 2014-12-02 for MarineStandalone
 *
 * @author Citymonstret
 */
public class ListEvent extends MarineEvent {

    private ListResponse response;

    public ListEvent(ListResponse response) {
        super("list");
        this.response = response;
    }

    public ListResponse getResponse() {
        return this.response;
    }

    public void setResponse(ListResponse response) {
        this.response = response;
    }
}
