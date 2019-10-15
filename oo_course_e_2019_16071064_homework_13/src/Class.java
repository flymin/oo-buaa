import com.oocourse.uml1.interact.common.AttributeClassInformation;
import com.oocourse.uml1.interact.common.OperationQueryType;
import com.oocourse.uml1.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml1.models.common.ElementType;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 13:43
 */
public class Class implements AssociationEnd {
    private UmlClass element;
    private HashMap<String, LinkedList<Operation>> operations;
    private HashMap<String, LinkedList<Attribute>> attributes;
    private HashMap<String, LinkedList<Attribute>> fatherAttributes;
    private Integer selfAttributeCount;
    private Integer fatherAttributeCount;
    private LinkedList<AttributeClassInformation> nonPrivate;
    private Class father;
    private Class topFather;
    /**
     * interfaces中只有父类和本类的直接实现接口，不包含接口继承关系
     */
    private HashMap<String, Interface> interfaces;
    private LinkedList<String> allImplementName;
    private Integer allAssociations;
    private LinkedList<Association> associations;
    private HashSet<String> associationClassEndId;
    private LinkedList<String> associationClassEndName;
    private boolean treeDone;
    private HashMap<OperationQueryType, Integer> operationQuery;

    Class(UmlClass element) {
        this.element = element;
        this.operations = new HashMap<>();
        this.attributes = new HashMap<>();
        this.fatherAttributes = new HashMap<>();
        this.selfAttributeCount = 0;
        this.fatherAttributeCount = 0;
        this.nonPrivate = new LinkedList<>();
        this.father = null;
        this.topFather = null;
        this.interfaces = new HashMap<>();
        this.allImplementName = null;
        this.allAssociations = -1;
        this.associations = new LinkedList<>();
        this.associationClassEndId = new HashSet<>();
        this.associationClassEndName = null;
        treeDone = false;
        this.operationQuery = new HashMap<>();
        for (OperationQueryType type : OperationQueryType.values()) {
            this.operationQuery.put(type, 0);
        }
    }

    /**
     * 仅涉及自己定义的部分，已满足条件
     * 添加时operation的param必须已经填好
     * @param operation
     */
    public void addOperation(Operation operation) {
        if (!this.operations.containsKey(operation.getName())) {
            this.operations.put(operation.getName(), new LinkedList<>());
        }
        this.operations.get(operation.getName()).add(operation);
        // for all
        OperationQueryType type = OperationQueryType.ALL;
        Integer num;
        num = this.operationQuery.get(type) + 1;
        this.operationQuery.replace(type, num);
        // param and non-param
        if (operation.hasParam()) {
            type = OperationQueryType.PARAM;
        } else {
            type = OperationQueryType.NON_PARAM;
        }
        num = this.operationQuery.get(type) + 1;
        this.operationQuery.replace(type, num);
        // return and non-return
        if (operation.hasReturn()) {
            type = OperationQueryType.RETURN;
        } else {
            type = OperationQueryType.NON_RETURN;
        }
        num = this.operationQuery.get(type) + 1;
        this.operationQuery.replace(type, num);
    }

    public Integer getOperationCount(OperationQueryType type) {
        return this.operationQuery.get(type);
    }

    public LinkedList<Operation> getOperationByName(String name) {
        return this.operations.get(name);
    }

    /**
     * 仅涉及自己定义的部分，已满足条件
     * @param element
     */
    public void addAttribute(Attribute element) {
        UmlAttribute attribute = element.getElement();
        if (!this.attributes.containsKey(attribute.getName())) {
            this.attributes.put(attribute.getName(), new LinkedList<>());
        }
        this.attributes.get(attribute.getName()).add(element);
        this.selfAttributeCount++;
        if (!attribute.getVisibility().equals(Visibility.PRIVATE)) {
            this.nonPrivate.add(
                new AttributeClassInformation(attribute.getName(),
                    element.getClassName())
            );
        }
    }

    public Integer getSelfAttributeCount() {
        return this.selfAttributeCount;
    }

    public Integer getFatherAttributeCount() {
        return this.fatherAttributeCount;
    }

    public Attribute getAttributeByName(String name)
        throws AttributeNotFoundException, AttributeDuplicatedException {
        if (this.attributes.containsKey(name)) {
            if (this.fatherAttributes.containsKey(name)) {
                throw new AttributeDuplicatedException(this.getName(), name);
            } else {
                LinkedList<Attribute> list = this.attributes.get(name);
                if (list.size() != 1) {
                    throw new
                        AttributeDuplicatedException(this.getName(), name);
                } else {
                    return list.getFirst();
                }
            }
        } else if (this.fatherAttributes.containsKey(name)) {
            LinkedList<Attribute> list = this.fatherAttributes.get(name);
            if (list.size() != 1) {
                throw new AttributeDuplicatedException(this.getName(), name);
            } else {
                return list.getFirst();
            }
        }
        throw new AttributeNotFoundException(this.getName(), name);
    }

    public LinkedList<AttributeClassInformation> getNonPrivate() {
        return this.nonPrivate;
    }

    private void addFatherAttribute(Attribute element) {
        UmlAttribute attribute = element.getElement();
        if (!this.fatherAttributes.containsKey(attribute.getName())) {
            this.fatherAttributes.put(attribute.getName(), new LinkedList<>());
        }
        this.fatherAttributes.get(attribute.getName()).add(element);
        this.fatherAttributeCount++;
        if (!attribute.getVisibility().equals(Visibility.PRIVATE)) {
            this.nonPrivate.add(
                new AttributeClassInformation(attribute.getName(),
                    element.getClassName())
            );
        }
    }

    private void addFatherImplement() {
        for (Interface fatherInter : this.father.interfaces.values()) {
            if (!this.interfaces.containsKey(fatherInter.getId())) {
                this.interfaces.put(fatherInter.getId(), fatherInter);
            }
        }
    }

    /**
     * 要求：父节点已经计算完毕
     * 设置时保证父类所有方法（包括自定义和继承）都被计算
     * @return 顶级父类
     */
    public Class finishFather() {
        if (this.father == null || this.father == this) {
            this.treeDone = true;
            this.allAssociations = this.associations.size();
            this.topFather = this;
            EndSet.getSet().iterRemove(this);
            return this;
        }
        if (!this.father.treeDone) {
            topFather = this.father.finishFather();
        } else {
            topFather = this.father.topFather;
        }
        Class father = this.father;
        // 考虑父类的属性及继承属性
        for (String name : father.attributes.keySet()) {
            for (Attribute element : father.attributes.get(name)) {
                this.addFatherAttribute(element);
            }
        }
        for (String name : father.fatherAttributes.keySet()) {
            for (Attribute element : father.fatherAttributes.get(name)) {
                this.addFatherAttribute(element);
            }
        }
        // 考虑父类的实现及继承实现
        this.addFatherImplement();
        // 完成标记
        this.treeDone = true;
        this.allAssociations =
            this.father.allAssociations + this.associations.size();
        this.associationClassEndId.addAll(this.father.associationClassEndId);
        EndSet.getSet().iterRemove(this);
        return this.topFather;
    }

    @Override public void addExtended(AssociationEnd end) {
        Class father = (Class)end;
        this.father = father;
    }

    public Class getTopFather() {
        return this.topFather;
    }

    /**
     * 仅涉及当前接口，随时可调用
     * @param inter
     */
    public void setImplement(Interface inter) {
        this.interfaces.put(inter.getId(), inter);
    }

    /**
     * 利用interfaces查找接口的继承关系
     */
    public LinkedList<String> getAllImplement() {
        if (this.allImplementName != null) {
            return this.allImplementName;
        }
        LinkedList<Interface> stack = new LinkedList<>();
        HashSet<String> allImplementId = new HashSet<>();
        allImplementId.addAll(this.interfaces.keySet());
        stack.addAll(this.interfaces.values());
        while (!stack.isEmpty()) {
            Interface now = stack.pop();
            for (Interface extended : now.getExtendedInterfaces().values()) {
                if (!allImplementId.contains(extended.getId())) {
                    allImplementId.add(extended.getId());
                    stack.add(extended);
                }
            }
        }
        Iterator<String> idIter = allImplementId.iterator();
        this.allImplementName = new LinkedList<>();
        while (idIter.hasNext()) {
            String name = EndSet.getSet().getById(idIter.next()).getName();
            this.allImplementName.add(name);
        }
        return this.allImplementName;
    }

    public Integer getAllAssociations() {
        return allAssociations;
    }

    public LinkedList<String> getAssociationIdClasses() {
        if (this.associationClassEndName == null) {
            LinkedList<String> result = new LinkedList<>();
            Iterator<String> idIter = this.associationClassEndId.iterator();
            while (idIter.hasNext()) {
                String id = idIter.next();
                result.add(EndSet.getSet().getById(id).getName());
            }
            this.associationClassEndName = result;
        }
        return (LinkedList<String>)this.associationClassEndName.clone();
    }

    @Override public void setAssociated(Association association) {
        this.associations.add(association);
        if (association.getEnd1().getId().equals(this.getId())) {
            if (association.getEnd2().getType().equals(ElementType.UML_CLASS)) {
                this.associationClassEndId.add(association.getEnd2().getId());
            }
        } else {
            if (association.getEnd1().getType().equals(ElementType.UML_CLASS)) {
                this.associationClassEndId.add(association.getEnd1().getId());
            }
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
}
