import com.oocourse.specs1.models.Path;
import com.oocourse.specs1.models.PathContainer;
import com.oocourse.specs1.models.PathIdNotFoundException;
import com.oocourse.specs1.models.PathNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/30 16:43
 */
public class MyPathContainer implements PathContainer {
    private ArrayList<Path> pathList;
    private ArrayList<Integer> pidList;
    private static int allNum = 1;

    public MyPathContainer() {
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

    @Override public int addPath(Path path) {
        if (path == null || !path.isValid()) {
            return 0;
        }
        int pathId;
        try {
            pathId = this.getPathId(path);
        } catch (PathNotFoundException e) {
            // add new path
            this.pathList.add(path);
            pathId = allNum++;
            this.pidList.add(pathId);
        }
        return pathId;
    }

    @Override public int removePath(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid() || !this.containsPath(path)) {
            throw new PathNotFoundException(path);
        }
        int removeIndex = this.pathList.indexOf(path);
        int removeId = this.pidList.get(removeIndex);
        this.pidList.remove(removeIndex);
        this.pathList.remove(removeIndex);
        return removeId;
    }

    @Override public void removePathById(int pathId)
        throws PathIdNotFoundException {
        if (!this.containsPathId(pathId)) {
            throw new PathIdNotFoundException(pathId);
        }
        int removeIndex = this.pidList.indexOf(pathId);
        this.pathList.remove(removeIndex);
        this.pidList.remove(removeIndex);
    }

    @Override public int getDistinctNodeCount() {
        HashSet<Integer> result = new HashSet<>();
        for (Path path : this.pathList) {
            for (int node : path) {
                result.add(node);
            }
        }
        return result.size();
    }
}
