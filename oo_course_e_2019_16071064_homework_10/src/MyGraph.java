import com.oocourse.specs2.models.Graph;
import com.oocourse.specs2.models.NodeIdNotFoundException;
import com.oocourse.specs2.models.Path;
import com.oocourse.specs2.models.PathNotFoundException;
import com.oocourse.specs2.models.NodeNotConnectedException;
import com.oocourse.specs2.models.PathIdNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/8 16:31
 */
public class MyGraph extends MyContainer implements Graph {
    private boolean needCalShortest;

    public MyGraph() {
        super();
        this.needCalShortest = true;
    }

    @Override public boolean containsNode(int nodeId) {
        try {
            NodeSet.getNodeSet().getNode(nodeId);
        } catch (NodeIdNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * 仅表示neighbor关系，考虑自环（没有自环不连通）
     * @param fromNodeId
     * @param toNodeId
     * @return
     */
    @Override public boolean containsEdge(int fromNodeId, int toNodeId) {
        Node fromNode;
        Node toNode;
        try {
            fromNode = NodeSet.getNodeSet().getNode(fromNodeId);
            toNode = NodeSet.getNodeSet().getNode(toNodeId);
        } catch (NodeIdNotFoundException e) {
            return false;
        }
        if (fromNode.isNeighbor(toNode)) {
            return true;
        } else {
            return false;
        }
    }

    @Override public boolean isConnected(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException {
        try {
            this.getShortestPathLength(fromNodeId, toNodeId);
        } catch (NodeNotConnectedException e) {
            return false;
        }
        return true;
    }

    @Override public int getShortestPathLength(int fromNodeId, int toNodeId)
        throws NodeIdNotFoundException, NodeNotConnectedException {
        Node fromNode;
        Node toNode;
        this.calShortestPathLength();
        fromNode = NodeSet.getNodeSet().getNode(fromNodeId);
        toNode = NodeSet.getNodeSet().getNode(toNodeId);
        return fromNode.getShortestPathLen(toNode);
    }

    private void setNeighborNodes(Path path) {
        Node nodeLeft;
        Node nodeRight;
        Iterator<Integer> nodeIter = path.iterator();
        nodeRight = NodeSet.getNodeSet().putNode(nodeIter.next());
        while (nodeIter.hasNext()) {
            nodeLeft = nodeRight;
            nodeRight = NodeSet.getNodeSet().putNode(nodeIter.next());
            nodeLeft.addNeighbor(nodeRight);
            nodeRight.addNeighbor(nodeLeft);
        }
    }

    private void unsetNeighborNodes(Path path) {
        Node nodeLeft;
        Node nodeRight = null;
        Iterator<Integer> nodeIter = path.iterator();
        try {
            nodeRight = NodeSet.getNodeSet().getNode(nodeIter.next());
        } catch (NodeIdNotFoundException e) {
            e.printStackTrace();
        }
        while (nodeIter.hasNext()) {
            nodeLeft = nodeRight;
            try {
                nodeRight = NodeSet.getNodeSet().getNode(nodeIter.next());
            } catch (NodeIdNotFoundException e) {
                e.printStackTrace();
            }
            nodeLeft.removeNeighbor(nodeRight);
            nodeRight.removeNeighbor(nodeLeft);
            // try to remove
            NodeSet.getNodeSet().safeRemoveNode(nodeLeft);
        }
        NodeSet.getNodeSet().safeRemoveNode(nodeRight);
    }

    private void calShortestPathLength() {
        if (!this.needCalShortest) {
            return;
        } else {
            this.needCalShortest = false;
        }
        //计算全局最短路
        ArrayList<Node> nodeList = NodeSet.getNodeSet().getNodeList();
        int[][] distance = new int[nodeList.size()][nodeList.size()];

        // 初始化距离矩阵
        for (int i = 0; i < nodeList.size(); i++) {
            Node nodeI = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                Node nodeJ = nodeList.get(j);
                if (i == j) {
                    distance[i][j] = 0;
                } else if (nodeI.isNeighbor(nodeJ)) {
                    distance[i][j] = 1;
                } else {
                    distance[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        //循环更新矩阵的值
        for (int k = 0; k < nodeList.size(); k++) {
            for (int i = 0; i < nodeList.size(); i++) {
                for (int j = 0; j < nodeList.size(); j++) {
                    int temp;
                    if (distance[i][k] == Integer.MAX_VALUE
                        || distance[k][j] == Integer.MAX_VALUE) {
                        temp = Integer.MAX_VALUE;
                    } else {
                        temp = distance[i][k] + distance[k][j];
                    }
                    if (distance[i][j] > temp) {
                        distance[i][j] = temp;
                    }
                }
            }
        }
        for (int i = 0; i < nodeList.size(); i++) {
            Node nodeI = nodeList.get(i);
            nodeI.cleanReachable();
            for (int j = 0; j < nodeList.size(); j++) {
                if (distance[i][j] < Integer.MAX_VALUE) {
                    Node nodeJ = nodeList.get(j);
                    nodeI.addreachable(nodeJ, distance[i][j]);
                }
            }
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
            // add new path
            super.getPathList().add(path);
            pathId = MyContainer.getAllNum();
            super.getPidList().add(pathId);
            this.setNeighborNodes(path);
            this.needCalShortest = true;
        }
        return pathId;
    }

    @Override public int removePath(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !this.containsPath(path)) {
            throw new PathNotFoundException(path);
        }
        int removeIndex = this.getPathList().indexOf(path);
        final int removeId = this.getPidList().get(removeIndex);
        this.getPathList().remove(removeIndex);
        this.getPidList().remove(removeIndex);
        this.unsetNeighborNodes(path);
        this.needCalShortest = true;
        return removeId;
    }

    @Override public void removePathById(int pathId)
         throws PathIdNotFoundException {
        if (!this.containsPathId(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        int removeIndex = this.getPidList().indexOf(pathId);
        Path path = this.getPathList().get(removeIndex);
        this.getPathList().remove(removeIndex);
        this.getPidList().remove(removeIndex);
        this.unsetNeighborNodes(path);
        this.needCalShortest = true;
    }
}
