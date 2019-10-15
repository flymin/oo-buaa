import com.oocourse.specs3.models.NodeNotConnectedException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/8 16:33
 */
public class Node {
    private int nodeId;
    private HashMap<Node, Integer> reachable;
    private HashMap<Node, Integer> neighbor;

    public Node(int id) {
        this.nodeId = id;
        this.reachable = new HashMap<>();
        this.neighbor = new HashMap<>();
    }

    /**
     * neighbor
     * @param node
     */
    public void addNeighbor(Node node) {
        if (this.neighbor.containsKey(node)) {
            int time = this.neighbor.get(node) + 1;
            this.neighbor.replace(node, time);
        } else {
            this.neighbor.put(node, 1);
        }
    }

    public void removeNeighbor(Node... nodes) {
        for (Node node : nodes) {
            int time = this.neighbor.get(node);
            if (time == 1) {
                this.neighbor.remove(node);
            } else {
                time = time - 1;
                this.neighbor.replace(node, time);
            }
        }
    }

    /**
     * reachable
     * @param node
     * @param len
     */
    public void addreachable(Node node, int len) {
        this.reachable.put(node, len);
    }

    public void cleanReachable() {
        this.reachable.clear();
    }

    public int getShortestPathLen(Node another)
        throws NodeNotConnectedException {
        if (!this.reachable.containsKey(another)) {
            throw new NodeNotConnectedException(this.nodeId, another.nodeId);
        }
        return this.reachable.get(another);
    }

    public boolean isNeighbor(Node another) {
        if (this.neighbor.containsKey(another)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean reachable(Node another) {
        if (this.reachable.containsKey(another)) {
            return true;
        } else {
            return false;
        }
    }

    public HashSet<Node> getNeighbor() {
        return (HashSet<Node>)neighbor.keySet();
    }

    public Set<Node> getReachable() {
        return reachable.keySet();
    }

    public int getNodeId() {
        return nodeId;
    }

    public boolean removable() {
        if (this.neighbor.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node another = (Node)obj;
            if (this.nodeId == another.nodeId) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        return Math.abs(this.nodeId % 120);
    }
}
