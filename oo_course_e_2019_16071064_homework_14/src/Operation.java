import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlParameter;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 13:49
 */
public class Operation {
    private UmlOperation element;
    private HashMap<String, LinkedList<UmlParameter>> inParam;
    private HashMap<String, LinkedList<UmlParameter>> outParam;
    private HashMap<String, LinkedList<UmlParameter>> inoutParam;
    private HashMap<String, LinkedList<UmlParameter>> returnParam;

    Operation(UmlOperation element) {
        this.element = element;
        this.inParam = new HashMap<>();
        this.outParam = new HashMap<>();
        this.inoutParam = new HashMap<>();
        this.returnParam = new HashMap<>();
    }

    public void addParam(UmlParameter param) {
        if (!this.element.getId().equals(param.getParentId())) {
            System.err.println("Wrong attribute for " + param);
        } else {
            HashMap<String, LinkedList<UmlParameter>> container = null;
            switch (param.getDirection()) {
                case IN:
                    container = this.inParam;
                    break;
                case INOUT:
                    container = this.outParam;
                    break;
                case OUT:
                    container = this.inoutParam;
                    break;
                case RETURN:
                    container = this.returnParam;
                    break;
                default:
            }
            if (container.containsKey(param.getName())) {
                container.get(param.getName()).add(param);
            } else {
                container.put(param.getName(), new LinkedList<>());
                container.get(param.getName()).add(param);
            }
        }
    }

    public Visibility getVisibility() {
        return this.element.getVisibility();
    }

    public Boolean hasParam() {
        if (!this.inParam.isEmpty() || !this.inoutParam.isEmpty()
            || !this.outParam.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean hasReturn() {
        if (!this.returnParam.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return this.element.getName();
    }

    public String getId() {
        return this.element.getId();
    }

    public String getParentId() {
        return this.element.getParentId();
    }
}
