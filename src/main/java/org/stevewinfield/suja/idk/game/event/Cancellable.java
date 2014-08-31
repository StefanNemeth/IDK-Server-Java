package org.stevewinfield.suja.idk.game.event;

public interface Cancellable {
    public boolean isCancelled();
    public void setCancelled(boolean cancel);
}
