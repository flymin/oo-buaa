import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.elements.UmlInterface;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 14:18
 */
public class Interface implements AssociationEnd {
    private UmlInterface element;
    /**
     * 建立时保持继承关系
     * 及父接口所继承的所有接口都要在这里有所记录
     */
    private HashMap<String, Interface> extendedInterfaces;
    private LinkedList<Association> associations;

    Interface(UmlInterface element) {
        this.element = element;
        this.extendedInterfaces = new HashMap<>();
        this.associations = new LinkedList<>();
    }

    @Override public void addAttribute(Attribute element) {
        // do nothing
    }

    @Override public void addOperation(Operation operation) {
        // do nothing
    }

    @Override public void addExtended(AssociationEnd end) {
        Interface father = (Interface)end;
        if (this.extendedInterfaces.containsKey(father.getId())) {
            // 这里只是创建类图时的直接继承关系，除非自己继承自己
            return;
        } else {
            this.extendedInterfaces.put(father.getId(), father);
        }
    }

    @Override public String getName() {
        return this.element.getName();
    }

    @Override public String getId() {
        return this.element.getId();
    }

    @Override public ElementType getType() {
        return this.element.getElementType();
    }

    @Override public void setAssociated(Association association, Boolean opt) {
        this.associations.add(association);
    }

    public UmlInterface getElement() {
        return element;
    }

    /**
     * 返回继承的接口数目
     * （不保证自己是不是在内）
     * @return extendedNum
     */
    public Integer getExtendedNum() {
        return this.extendedInterfaces.size();
    }

    public HashMap<String, Interface> getExtendedInterfaces() {
        return extendedInterfaces;
    }
}
