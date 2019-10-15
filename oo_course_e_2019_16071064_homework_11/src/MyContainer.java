import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathContainer;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.PathNotFoundException;

import java.util.ArrayList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/30 16:43
 */
public abstract class MyContainer implements PathContainer {
    private ArrayList<Path> pathList;
    private ArrayList<Integer> pidList;
    private static int allNum = 1;

    public MyContainer() {
        this.pidList = new ArrayList<>();
        this.pathList = new ArrayList<>();
    }

    @Override public int size() {
        return this.pathList.size();
    }

    @Override public boolean containsPath(Path path) {
        return this.pathList.contains(path);
    }

    @Override public boolean containsPathId(int pathId) {
        return this.pidList.contains(pathId);
    }

    @Override public Path getPathById(int i) throws PathIdNotFoundException {
        if (!this.containsPathId(i)) {
            throw new PathIdNotFoundException(i);
        }
        return this.pathList.get(this.pidList.indexOf(i));
    }

    @Override public int getPathId(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !containsPath(path)) {
            throw new PathNotFoundException(path);
        }
        return pidList.get(pathList.indexOf(path));
    }

    @Override public abstract int addPath(Path path);

    @Override public abstract int removePath(Path path)
        throws PathNotFoundException;

    @Override public abstract void removePathById(int pathId)
        throws PathIdNotFoundException;

    @Override public int getDistinctNodeCount() {
        return NodeSet.getNodeSet().size();
    }

    public ArrayList<Integer> getPidList() {
        return pidList;
    }

    public ArrayList<Path> getPathList() {
        return pathList;
    }

    public static int getAllNum() {
        return allNum++;
    }
}