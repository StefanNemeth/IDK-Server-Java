/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.threadpools;

public abstract class ServerTask implements Runnable {
    @Override
    public abstract void run();
}
