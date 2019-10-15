import com.oocourse.specs1.models.Path;

import java.util.ArrayList;
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

    public MyPath(int... nodeList) {
        this.nodes = new ArrayList<>();
        for (int node : nodeList) {
            this.nodes.add(node);
        }
    }

    @Override
    public int size() {
        return this.nodes.size();
    }

    @Override public int getNode(int index) {
        return this.nodes.get(index);
    }

    @Override public boolean containsNode(int node) {
        for (int i = 0; i < this.nodes.size(); i++) {
            if (this.nodes.get(i) == node) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer> getDistinctNodes() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int node : this.nodes) {
            if (!result.contains(node)) {
                result.add(node);
            }
        }
        return result;
    }

    @Override public int getDistinctNodeCount() {
        ArrayList<Integer> distinctNode = this.getDistinctNodes();
        return distinctNode.size();
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

    @Override public Iterator<Integer> iterator() {
        return this.nodes.iterator();
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
}
