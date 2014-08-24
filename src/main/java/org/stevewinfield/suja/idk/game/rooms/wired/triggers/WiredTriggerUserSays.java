/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired.triggers;

import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredTrigger;

public class WiredTriggerUserSays extends WiredTrigger {

    public WiredTriggerUserSays(final RoomInstance room, final RoomItem item, final String[] data) {
        this.room = room;
        this.item = item;
        this.set(data);
    }

    @Override
    public int getWiredType() {
        return 1;
    }

    @Override
    public RoomItem getItem() {
        return item;
    }

    @Override
    public boolean onTrigger(final RoomPlayer player, final Object data) {
        return ((String) data).toLowerCase().contains(this.message) && this.message.length() > 0 && (!this.onlyRoomOwner || (room.hasRights(player.getSession(), true)));
    }

    @Override
    public String[] getObject(final MessageReader reader) {
        reader.readBoolean();
        final boolean onlyOwner_ = reader.readBoolean();
        String message_ = InputFilter.filterString(reader.readUTF(), true);
        message_ = message_.length() > 100 ? message_.substring(0, 100) : message_;
        return new String[]{message_, onlyOwner_ ? "1" : "0"};
    }

    @Override
    public void set(final String[] obj) {
        this.message = obj.length > 0 ? obj[0] : "";
        this.onlyRoomOwner = obj.length > 1 && obj[1].equals("1");
    }

    // fields
    private final RoomInstance room;
    private final RoomItem item;
    private String message;
    private boolean onlyRoomOwner;
}
