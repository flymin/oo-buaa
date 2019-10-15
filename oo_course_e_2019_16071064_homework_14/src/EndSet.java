import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.models.common.ElementType;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 16:21
 */
public class EndSet {
    private HashMap<String, AssociationEnd> idMap;
    private HashMap<String, LinkedList<AssociationEnd>> classNameMap;
    private HashMap<String, AssociationEnd> classIdMap;
    private static EndSet endSet = null;
    private LinkedList<AssociationEnd> iterList;

    public static EndSet getSet() {
        if (endSet == null) {
            endSet = new EndSet();
        }
        return endSet;
    }

    EndSet() {
        this.idMap = new HashMap<>();
        this.classNameMap = new HashMap<>();
        this.iterList = new LinkedList<>();
        this.classIdMap = new HashMap<>();
    }

    public void addElement(AssociationEnd element) {
        if (!this.idMap.containsKey(element.getId())) {
            this.idMap.put(element.getId(), element);
        }
        if (element.getType().equals(ElementType.UML_CLASS)) {
            if (!this.classNameMap.containsKey(element.getName())) {
                this.classNameMap.put(element.getName(), new LinkedList<>());
            }
            this.classNameMap.get(element.getName()).add(element);
            this.classIdMap.put(element.getId(), element);
        }
    }

    public AssociationEnd getById(String id) {
        return this.idMap.get(id);
    }

    public AssociationEnd getByName(String name) throws ClassNotFoundException,
        ClassDuplicatedException {
        if (!this.classNameMap.containsKey(name)) {
            throw new ClassNotFoundException(name);
        } else {
            LinkedList<AssociationEnd> list = this.classNameMap.get(name);
            if (list.size() > 1) {
                throw new ClassDuplicatedException(name);
            } else {
                return list.getFirst();
            }
        }
    }

    public Integer getClassNum() {
        return this.classIdMap.values().size();
    }

    public void iterClear(ElementType type) {
        this.iterList.clear();
        for (AssociationEnd end : this.idMap.values()) {
            if (end.getType().equals(type)) {
                this.iterList.add(end);
            }
        }
    }

    public AssociationEnd iterNext() {
        return this.iterList.getFirst();
    }

    public void iterRemove(AssociationEnd end) {
        this.iterList.remove(end);
    }

    public boolean iterEmpty() {
        return this.iterList.isEmpty();
    }
}
