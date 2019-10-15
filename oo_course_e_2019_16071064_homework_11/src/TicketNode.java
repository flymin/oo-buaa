import com.oocourse.specs3.models.NodeNotConnectedException;

import java.util.HashMap;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/15 17:05
 */
public class TicketNode extends Node {
    private int pathId;
    private HashMap<TicketNode, Integer> neighborWeightMap;
    private HashMap<TicketNode, Integer> leastChange;
    private HashMap<TicketNode, Integer> leastTicket;
    private HashMap<TicketNode, Integer> unpleasantWeightneighbor;
    private HashMap<TicketNode, Integer> leastUnpleasantWeight;

    TicketNode(int nodeId, int pathId) {
        super(nodeId);
        this.pathId = pathId;
        this.neighborWeightMap = new HashMap<>();
        this.leastChange = new HashMap<>();
        this.leastTicket = new HashMap<>();
        this.unpleasantWeightneighbor = new HashMap<>();
        this.leastUnpleasantWeight = new HashMap<>();
    }

    TicketNode(int nodeId) {
        super(nodeId);
        this.pathId = -1;
        this.neighborWeightMap = new HashMap<>();
        this.leastChange = new HashMap<>();
        this.leastTicket = new HashMap<>();
        this.unpleasantWeightneighbor = new HashMap<>();
        this.leastUnpleasantWeight = new HashMap<>();
    }

    public void addNeighbor(TicketNode node, int weight) {
        int putWeight;
        if (this.pathId == node.pathId) {
            this.neighborWeightMap.put(node, 1);
            putWeight = weight;
        } else {
            this.neighborWeightMap.put(node, 2);
            putWeight = 32;
        }
        if (this.unpleasantWeightneighbor.containsKey(node)) {
            this.unpleasantWeightneighbor.replace(node, putWeight);
        } else {
            this.unpleasantWeightneighbor.put(node, putWeight);
        }
    }

    public void removeNeighbor(TicketNode node) {
        this.neighborWeightMap.remove(node);
        if (this.unpleasantWeightneighbor.containsKey(node)) {
            this.unpleasantWeightneighbor.remove(node);
        }
    }

    /**
     * leastChange
     * @param node
     * @param time
     */
    public void addLeastChange(TicketNode node, int time) {
        this.leastChange.put(node, time);
    }

    public int getLeastChange(TicketNode node)
        throws NodeNotConnectedException {
        if (!this.leastChange.containsKey(node)) {
            throw new NodeNotConnectedException(this.getNodeId(),
                node.getNodeId());
        }
        return this.leastChange.get(node);
    }

    public void cleanLeastChange() {
        this.leastChange.clear();
    }

    public int getChangeWeight(TicketNode node) {
        return this.neighborWeightMap.get(node) - 1;
    }

    /**
     * leastTicket
     * @param node
     * @param time
     */
    public void addLeastTicket(TicketNode node, int time) {
        this.leastTicket.put(node, time);
    }

    public int getLeastTicket(TicketNode node)
        throws NodeNotConnectedException {
        if (!this.leastTicket.containsKey(node)) {
            throw new NodeNotConnectedException(this.getNodeId(),
                node.getNodeId());
        }
        return this.leastTicket.get(node);
    }

    public void cleanLeastTicket() {
        this.leastTicket.clear();
    }

    /**
     * unpleasent
     * @param node
     * @param len
     */
    public void addLeastUnpleasentWeight(TicketNode node, int len) {
        assert this.reachable(node);
        this.leastUnpleasantWeight.put(node, len);
    }

    public void cleanLeastUnpleasentWeight() {
        this.leastUnpleasantWeight.clear();
    }

    public int getLeastUnpleasentWeight(TicketNode another)
        throws NodeNotConnectedException {
        if (!this.leastUnpleasantWeight.containsKey(another)) {
            throw new NodeNotConnectedException(this.getNodeId(),
                another.getNodeId());
        }
        return this.leastUnpleasantWeight.get(another);
    }

    public int getUnpleasantWeight(TicketNode node) {
        return this.unpleasantWeightneighbor.get(node);
    }

    public int getTicketWeight(TicketNode node) {
        return this.neighborWeightMap.get(node);
    }

    public int getPathId() {
        return pathId;
    }

    public boolean isNeighbor(TicketNode another) {
        return this.neighborWeightMap.containsKey(another);
    }

    @Override public boolean removable() {
        return true;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof TicketNode) {
            TicketNode another = (TicketNode)obj;
            if (this.getNodeId() == another.getNodeId() &&
                this.pathId == another.pathId) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        return super.hashCode();
    }
}
