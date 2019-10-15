import com.oocourse.specs3.models.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/30 16:42
 */
public class MyPath implements Path {
    private ArrayList<Integer> nodes;
    private HashSet<Integer> distinctNodes;

    public MyPath(Path path) {
        this.distinctNodes = new HashSet<>();
        this.nodes = new ArrayList<>();
        Iterator<Integer> iterList = path.iterator();
        while (iterList.hasNext()) {
            int nodeId = iterList.next();
            this.nodes.add(nodeId);
            this.distinctNodes.add(nodeId);
        }
    }

    public MyPath(int... nodeList) {
        this.distinctNodes = new HashSet<>();
        this.nodes = new ArrayList<>();
        for (int nodeId : nodeList) {
            this.nodes.add(nodeId);
            this.distinctNodes.add(nodeId);
        }
    }

    @Override
    public int size() {
        return this.nodes.size();
    }

    @Override public int getNode(int index) {
        return this.nodes.get(index);
    }

    @Override public boolean containsNode(int nodeId) {
        if (this.distinctNodes.contains(nodeId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override public int getDistinctNodeCount() {
        return this.distinctNodes.size();
    }

    @Override public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Path)) {
            return false;
        }
        Path another = (Path)obj;
        if (another.size() != this.size()) {
            return false;
        }
        for (int index = 0; index < this.size(); index++) {
            if (this.getNode(index) != another.getNode(index)) {
                return false;
            }
        }
        return true;
    }

    @Override public boolean isValid() {
        return this.nodes.size() >= 2;
    }

    @Override public int getUnpleasantValue(int nodeId) {
        if (this.containsNode(nodeId)) {
            return (int)Math.pow(4, (nodeId % 5 + 5) % 5);
        } else {
            return 0;
        }
    }

    @Override public int compareTo(Path another) {
        for (int index = 0; index < this.size(); index++) {
            if (this.getNode(index) < another.getNode(index)) {
                return -1;
            } else if (this.getNode(index) > another.getNode(index)) {
                return 1;
            }
        }
        if (this.size() < another.size()) {
            return -1;
        } else if (this.size() > another.size()) {
            return 1;
        }
        return 0;
    }

    @Override public Iterator<Integer> iterator() {
        return this.nodes.iterator();
    }
}
