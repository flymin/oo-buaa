import com.oocourse.specs2.models.NodeIdNotFoundException;

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
 * @since 2019/5/8 17:01
 */
public class NodeSet implements Iterable<Node> {
    private static NodeSet nodeSet = null;
    private HashMap<Integer, Node> idMap;

    public NodeSet() {
        this.idMap = new HashMap<>();
    }

    public static NodeSet getNodeSet() {
        if (nodeSet == null) {
            nodeSet = new NodeSet();
        }
        return nodeSet;
    }

    public int size() {
        return this.idMap.size();
    }

    /**
     * 如果不存在则创建Node，如果存在返回已有Node
     * @param nodeId
     */
    public Node putNode(int nodeId) {
        if (!this.idMap.containsKey(nodeId)) {
            Node node = new Node(nodeId);
            this.idMap.put(nodeId, node);
        }
        return this.idMap.get(nodeId);
    }

    /**
     * 如果不存在exception，如果存在返回已有Node
     * @param nodeId
     */
    public Node getNode(int nodeId) throws NodeIdNotFoundException {
        if (this.idMap.containsKey(nodeId)) {
            return this.idMap.get(nodeId);
        } else {
            throw new NodeIdNotFoundException(nodeId);
        }
    }

    /**
     * 当节点的
     * @param node
     * @return
     */
    public Node safeRemoveNode(Node node) {
        if (node.removable()) {
            this.idMap.remove(node.getNodeId());
            return node;
        } else {
            return null;
        }
    }

    @Override public Iterator<Node> iterator() {
        return this.idMap.values().iterator();
    }

    public ArrayList<Node> getNodeList() {
        return new ArrayList<Node>(this.idMap.values());
    }
}
