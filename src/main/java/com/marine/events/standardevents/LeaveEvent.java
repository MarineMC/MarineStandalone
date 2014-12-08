package com.marine.events.standardevents;

import com.marine.events.MarineEvent;
import com.marine.player.Player;

/**
 * Created 2014-12-08 for MarineStandalone
 *
 * @author Citymonstret
 */
public class LeaveEvent extends MarineEvent {

    private final Player player;
    private final QuitReason reason;
    private String message;

    public LeaveEvent(final Player player, final QuitReason reason) {
        super("leave");
        this.player = player;
        this.reason = reason;
        this.message = reason.getMessage();
    }

    public Player getPlayer() {
        return this.player;
    }

    public QuitReason getReason() {
        return this.reason;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static enum QuitReason {
        KICKED("%plr got kicked"), NORMAL("%plr left the game"), TIMEOUT("%plr timed out");

        private final String message;
        QuitReason(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

}
