import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.RailwaySystem;
import com.oocourse.specs3.models.PathNotFoundException;
import com.oocourse.specs3.models.PathIdNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/15 12:06
 */
public class MyRailwaySystem extends MyGraph implements RailwaySystem {
    private HashMap<TicketNode, TicketNode> nodeSetMap;
    private HashMap<Integer, LinkedList<TicketNode>> pathNodeMap;
    private HashMap<Integer, LinkedList<TicketNode>> repeatNode;
    private boolean needCalTicket;
    private boolean needCalChange;
    private boolean needCalUnpleasant;
    private HashMap<IntPair, Integer> ticketRecord;
    private HashMap<IntPair, Integer> changeRecord;
    private HashMap<IntPair, Integer> unpleasantRecord;

    public MyRailwaySystem() {
        super();
        this.nodeSetMap = new HashMap<>();
        this.pathNodeMap = new HashMap<>();
        this.repeatNode = new HashMap<>();
        this.needCalChange = true;
        this.needCalTicket = true;
        this.needCalUnpleasant = true;
        this.ticketRecord = new HashMap<>();
        this.changeRecord = new HashMap<>();
        this.unpleasantRecord = new HashMap<>();
    }

    private void checkNodeId(int... nodeIds) throws NodeIdNotFoundException {
        for (int nodeId : nodeIds) {
            if (!this.repeatNode.containsKey(nodeId)) {
                throw new NodeIdNotFoundException(nodeId);
            }
        }
    }

    private void calTicket() {
        if (!this.needCalTicket) {
            return;
        } else {
            this.needCalTicket = false;
            this.ticketRecord.clear();
        }
        ArrayList<TicketNode> nodeList =
            new ArrayList<>(this.nodeSetMap.values());
        int[][] distance = new int[nodeList.size()][nodeList.size()];

        // 初始化距离矩阵
        for (int i = 0; i < nodeList.size(); i++) {
            TicketNode nodeI = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                TicketNode nodeJ = nodeList.get(j);
                if (i == j) {
                    distance[i][j] = 0;
                } else if (nodeI.isNeighbor(nodeJ)) {
                    distance[i][j] = nodeI.getTicketWeight(nodeJ);
                } else {
                    distance[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        //循环更新矩阵的值
        distance = MyGraph.floidPath(nodeList.size(), distance);
        for (int i = 0; i < nodeList.size(); i++) {
            TicketNode nodeI = nodeList.get(i);
            nodeI.cleanLeastTicket();
            for (int j = 0; j < nodeList.size(); j++) {
                if (distance[i][j] < Integer.MAX_VALUE) {
                    TicketNode nodeJ = nodeList.get(j);
                    nodeI.addLeastTicket(nodeJ, distance[i][j]);
                }
            }
        }
    }

    @Override public int getLeastTicketPrice(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        this.checkNodeId(fromNodeId, toNodeId);
        this.calTicket();
        if (this.ticketRecord.containsKey(new IntPair(fromNodeId, toNodeId))) {
            return this.ticketRecord.get(new IntPair(fromNodeId, toNodeId));
        }
        int result = Integer.MAX_VALUE;
        for (TicketNode fromNode : this.repeatNode.get(fromNodeId)) {
            for (TicketNode toNode : this.repeatNode.get(toNodeId)) {
                if (fromNode.getLeastTicket(toNode) < result) {
                    result = fromNode.getLeastTicket(toNode);
                }
            }
        }
        this.ticketRecord.put(new IntPair(fromNodeId, toNodeId), result);
        return result;
    }

    private void calChange() {
        if (!this.needCalChange) {
            return;
        } else {
            this.needCalChange = false;
            this.changeRecord.clear();
        }
        ArrayList<TicketNode> nodeList =
            new ArrayList<>(this.nodeSetMap.values());
        int[][] distance = new int[nodeList.size()][nodeList.size()];

        // 初始化距离矩阵
        for (int i = 0; i < nodeList.size(); i++) {
            TicketNode nodeI = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                TicketNode nodeJ = nodeList.get(j);
                if (i == j) {
                    distance[i][j] = 0;
                } else if (nodeI.isNeighbor(nodeJ)) {
                    distance[i][j] = nodeI.getChangeWeight(nodeJ);
                } else {
                    distance[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        //循环更新矩阵的值
        distance = MyGraph.floidPath(nodeList.size(), distance);
        for (int i = 0; i < nodeList.size(); i++) {
            TicketNode nodeI = nodeList.get(i);
            nodeI.cleanLeastChange();
            for (int j = 0; j < nodeList.size(); j++) {
                if (distance[i][j] < Integer.MAX_VALUE) {
                    TicketNode nodeJ = nodeList.get(j);
                    nodeI.addLeastChange(nodeJ, distance[i][j]);
                }
            }
        }
    }

    @Override public int getLeastTransferCount(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        this.checkNodeId(fromNodeId, toNodeId);
        this.calChange();
        if (this.changeRecord.containsKey(new IntPair(fromNodeId, toNodeId))) {
            return this.changeRecord.get(new IntPair(fromNodeId, toNodeId));
        }
        int result = Integer.MAX_VALUE;
        for (TicketNode fromNode : this.repeatNode.get(fromNodeId)) {
            for (TicketNode toNode : this.repeatNode.get(toNodeId)) {
                if (fromNode.getLeastChange(toNode) < result) {
                    result = fromNode.getLeastChange(toNode);
                }
            }
        }
        this.changeRecord.put(new IntPair(fromNodeId, toNodeId), result);
        return result;
    }

    @Override public int getUnpleasantValue(Path path, int fromIndex,
        int toIndex) {
        MyPathExtend extend = new MyPathExtend(path);
        return extend.getShortPath(fromIndex, toIndex);
    }

    private void calLeastUnpleasant() {
        if (!this.needCalUnpleasant) {
            return;
        } else {
            this.needCalUnpleasant = false;
            this.unpleasantRecord.clear();
        }
        //计算全局最短路
        ArrayList<TicketNode> nodeList =
            new ArrayList<>(this.nodeSetMap.values());
        int[][] distance = MyPathExtend.calForUnpleasant(nodeList);
        for (int i = 0; i < nodeList.size(); i++) {
            TicketNode nodeI = nodeList.get(i);
            nodeI.cleanLeastUnpleasentWeight();
            for (int j = 0; j < nodeList.size(); j++) {
                if (distance[i][j] < Integer.MAX_VALUE) {
                    TicketNode nodeJ = nodeList.get(j);
                    nodeI.addLeastUnpleasentWeight(nodeJ, distance[i][j]);
                }
            }
        }
    }

    @Override public int getLeastUnpleasantValue(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        this.checkNodeId(fromNodeId, toNodeId);
        this.calLeastUnpleasant();
        if (this.unpleasantRecord.containsKey(
            new IntPair(fromNodeId, toNodeId))) {
            return this.unpleasantRecord.get(new IntPair(fromNodeId, toNodeId));
        }
        int result = Integer.MAX_VALUE;
        for (TicketNode fromNode : this.repeatNode.get(fromNodeId)) {
            for (TicketNode toNode : this.repeatNode.get(toNodeId)) {
                if (fromNode.getLeastUnpleasentWeight(toNode) < result) {
                    result = fromNode.getLeastUnpleasentWeight(toNode);
                }
            }
        }
        this.unpleasantRecord.put(new IntPair(fromNodeId, toNodeId), result);
        return result;
    }

    @Override public int getConnectedBlockCount() {
        return super.getConnectedBlockCount();
    }

    private TicketNode getNewNode(int nodeId, int pathId) {
        TicketNode result = new TicketNode(nodeId, pathId);
        if (this.nodeSetMap.containsKey(result)) {
            return this.nodeSetMap.get(result);
        }
        this.nodeSetMap.put(result, result);
        if (!this.repeatNode.containsKey(nodeId)) {
            this.repeatNode.put(nodeId, new LinkedList<>());
        }
        for (TicketNode node : this.repeatNode.get(nodeId)) {
            if (!node.equals(result)) {
                node.addNeighbor(result, 32);
                result.addNeighbor(node, 32);
            }
        }
        this.repeatNode.get(nodeId).add(result);
        return result;
    }

    public void removeNode(TicketNode node) {
        if (!this.nodeSetMap.containsKey(node)) {
            return;
        }
        this.nodeSetMap.remove(node);
        if (this.repeatNode.get(node.getNodeId()).size() == 1) {
            this.repeatNode.remove(node.getNodeId());
        } else {
            for (TicketNode origin : this.repeatNode.get(node.getNodeId())) {
                node.removeNeighbor(origin);
                origin.removeNeighbor(node);
            }
            this.repeatNode.get(node.getNodeId()).remove(node);
        }
    }

    private void setNeighborNodes(Path path, int pathId) {
        LinkedList<TicketNode> pathSet = new LinkedList<>();
        TicketNode nodeLeft;
        TicketNode nodeRight;
        Iterator<Integer> nodeIter = path.iterator();
        nodeRight = this.getNewNode(nodeIter.next(), pathId);
        pathSet.add(nodeRight);
        while (nodeIter.hasNext()) {
            nodeLeft = nodeRight;
            nodeRight = this.getNewNode(nodeIter.next(), pathId);
            pathSet.add(nodeRight);
            int weight =
                        Math.max(path.getUnpleasantValue(nodeLeft.getNodeId()),
                            path.getUnpleasantValue(nodeRight.getNodeId()));
            nodeLeft.addNeighbor(nodeRight, weight);
            nodeRight.addNeighbor(nodeLeft, weight);
        }
        this.pathNodeMap.put(pathId, pathSet);
    }

    private void unsetNeighborNodes(int pathId) {
        LinkedList<TicketNode> pathSet = this.pathNodeMap.get(pathId);
        this.pathNodeMap.remove(pathId);
        Iterator<TicketNode> nodeIter = pathSet.iterator();
        TicketNode nodeRight = nodeIter.next();
        TicketNode nodeLeft;
        this.removeNode(nodeRight);
        while (nodeIter.hasNext()) {
            nodeLeft = nodeRight;
            nodeRight = nodeIter.next();
            this.removeNode(nodeRight);
            nodeLeft.removeNeighbor(nodeRight);
            nodeRight.removeNeighbor(nodeLeft);
        }
    }

    @Override public int addPath(Path path) {
        if (path == null || !path.isValid()) {
            return 0;
        }
        int pathId;
        try {
            pathId = this.getPathId(path);
        } catch (PathNotFoundException e) {
            pathId = super.addPath(path);
            this.setNeighborNodes(path, pathId);
            this.needCalChange = true;
            this.needCalTicket = true;
            this.needCalUnpleasant = true;
        }
        return pathId;
    }

    @Override public int removePath(Path path) throws PathNotFoundException {
        int pathId = super.removePath(path);
        this.unsetNeighborNodes(pathId);
        this.needCalChange = true;
        this.needCalTicket = true;
        this.needCalUnpleasant = true;
        return pathId;
    }

    @Override public void removePathById(int pathId)
        throws PathIdNotFoundException {
        Path path = super.getPathById(pathId);
        //super.removePathById(pathId); //need not to do this
        try {
            this.removePath(path);
        } catch (PathNotFoundException e) {
            e.printStackTrace();
        }
        //this.needCalChange = true;    //need not to do this
        //this.needCalTicket = true;
    }
}
