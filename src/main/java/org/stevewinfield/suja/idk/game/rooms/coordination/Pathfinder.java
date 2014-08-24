/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.coordination;

import org.stevewinfield.suja.idk.IDK;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Pathfinder {
    public static class Node {
        public Node getParent() {
            return parent;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(final Object x) {
            if (x instanceof Node) {
                final Node l = (Node) x;
                return l.x == this.x && l.y == this.y;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (31 + this.x) * 31 + this.y;
        }

        private final Node parent;
        private final int x, y;
        private double f, g;

        private Node(final Node _parent, final int _x, final int _y) {
            parent = _parent;
            x = _x;
            y = _y;
        }
    }

    public static final Vector2[] DIRECTIONS = new Vector2[]{new Vector2(-1, 0), new Vector2(0, -1), new Vector2(1, 0), new Vector2(0, 1), new Vector2(-1, -1), new Vector2(-1, 1), new Vector2(1, 1), new Vector2(1, -1)};

    public static Node getPathInArray(final int[][] field, final double[][] heights, final Vector2 start, final Vector2 goal) {
        return getPathInArray(field, heights, start, goal, false);
    }

    private static void addToList(final Node node, final ArrayList<Node> list, final ConcurrentHashMap<Node, Double> fVals) {
        list.add(node);
        final Double checkF = fVals.get(node);
        if (checkF == null || checkF >= node.f) {
            fVals.put(node, node.f);
        }
    }

    public static Node getPathInArray(final int[][] field, final double[][] heights, final Vector2 start, final Vector2 goal, final boolean override) {
        final double startHeight = heights[start.getX()][start.getY()];
        ArrayList<Node> openList = new ArrayList<Node>();
        ArrayList<Node> closedList = new ArrayList<Node>();
        ConcurrentHashMap<Node, Double> fVals = new ConcurrentHashMap<Node, Double>();

        final Node stn = new Node(null, start.getX(), start.getY());
        addToList(stn, openList, fVals);

        while (!openList.isEmpty()) {
            final Node q = getLeastF(openList);
            openList.remove(q);
            final LinkedList<Node> successors = new LinkedList<Node>();
            final int qx = q.x;
            final int qy = q.y;

            for (final Vector2 p : DIRECTIONS) {
                final int x = qx + p.getX();
                final int y = qy + p.getY();
                if (x < 0 || y < 0 || heights.length <= x || heights[x].length <= y || field.length <= x || field[x].length <= y || heights[x][y] - (q.getParent() == null ? startHeight : heights[q.getParent().getX()][q.getParent().getY()]) >= IDK.ROOM_MAX_WALK_ALTITUDE_DIFFERENCE) {
                    continue;
                }
                final int f = field[x][y];
                boolean walkOn = f == TileState.OPEN || override;
                if (x == goal.getX() && y == goal.getY()) {
                    walkOn = walkOn || f == TileState.DOOR || f == TileState.LAYABLE || f == TileState.SITABLE;
                } else {
                    walkOn = walkOn || f == TileState.PLAYER;
                }
                if (walkOn) {
                    final Node n = new Node(q, x, y);
                    n.g = calcG(n, start);
                    n.f = n.g + calcH(n, goal) + calcC(n);
                    successors.add(n);
                }
            }

            for (final Node n : successors) {
                if (n.x == goal.getX() && n.y == goal.getY()) {
                    closedList = null;
                    openList = null;
                    fVals = null;
                    return n;
                }
                boolean add = true;
                if (fVals.containsKey(n) && fVals.get(n) <= n.f) {
                    add = false;
                }
                if (add) {
                    addToList(n, openList, fVals);
                }
            }
            addToList(q, closedList, fVals);
        }
        closedList = null;
        openList = null;
        fVals = null;
        return null;
    }

    private static Node getLeastF(final List<Node> l) {
        Node least = null;
        for (final Node n : l) {
            if ((least == null) || (n.f < least.f)) {
                least = n;
            }
        }
        return least;
    }

    private static double calcG(final Node n, final Vector2 goal) {
        return n.parent.g + 1;
    }

    private static double calcC(final Node act) {
        return (act.parent.x != act.x) && (act.parent.y != act.y) ? 1 : 0;
    }

    public static double calcH(final Node act, final Vector2 goal) {
        return calcH(act.getX(), goal.getX(), act.getY(), goal.getY());
    }

    public static double calcH(final Vector2 act, final Vector2 goal) {
        return calcH(act.getX(), goal.getX(), act.getY(), goal.getY());
    }

    public static double calcH(final int startX, final int goalX, final int startY, final int goalY) {
        return Math.abs(startX - goalX) + Math.abs(startY - goalY);
    }
}
