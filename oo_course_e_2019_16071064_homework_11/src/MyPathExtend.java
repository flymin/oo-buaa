import com.oocourse.specs3.models.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/15 16:31
 */
public class MyPathExtend extends MyPath {
    private HashMap<Integer, TicketNode> distinctNodes;

    MyPathExtend(Path path) {
        super(path);
        this.distinctNodes = new HashMap<>();
        TicketNode prevNode = new TicketNode(path.getNode(0));
        TicketNode currentNode;
        this.distinctNodes.put(prevNode.getNodeId(), prevNode);
        Iterator<Integer> iterList = path.iterator();
        while (iterList.hasNext()) {
            int nodeId = iterList.next();
            if (!this.distinctNodes.containsKey(nodeId)) {
                currentNode = new TicketNode(nodeId);
                if (!currentNode.equals(prevNode)) {
                    int weight =
                        Math.max(this.getUnpleasantValue(prevNode.getNodeId()),
                            this.getUnpleasantValue(currentNode.getNodeId()));
                    currentNode.addNeighbor(prevNode, weight);
                    prevNode.addNeighbor(currentNode, weight);
                }
                this.distinctNodes.put(currentNode.getNodeId(), currentNode);
                prevNode = currentNode;
            }
        }
    }

    public Integer getShortPath(int fromIndex, int toIndex) {
        int fromNodeId = this.getNode(fromIndex);
        int toNodeId = this.getNode(toIndex);
        ArrayList<TicketNode> nodeList =
            new ArrayList<>(this.distinctNodes.values());
        int[][] distance = calForUnpleasant(nodeList);
        return distance[fromNodeId][toNodeId];
    }

    public static int[][] calForUnpleasant(ArrayList<TicketNode> nodeList) {
        int[][] distance = new int[nodeList.size()][nodeList.size()];

        // 初始化距离矩阵
        for (int i = 0; i < nodeList.size(); i++) {
            TicketNode nodeI = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                TicketNode nodeJ = nodeList.get(j);
                if (i == j) {
                    distance[i][j] = 0;
                } else if (nodeI.isNeighbor(nodeJ)) {
                    distance[i][j] = nodeI.getUnpleasantWeight(nodeJ);
                } else {
                    distance[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        //循环更新矩阵的值
        distance = MyGraph.floidPath(nodeList.size(), distance);
        return distance;
    }
}
