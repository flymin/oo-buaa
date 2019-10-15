import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlClass;
import com.oocourse.uml2.models.elements.UmlElement;

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
    private LinkedList<AttributeClassInformation> duplicateAttribute;
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
    private Boolean duplicateImplement;

    Class(UmlClass element) {
        this.element = element;
        this.operations = new HashMap<>();
        this.attributes = new HashMap<>();
        this.fatherAttributes = new HashMap<>();
        this.selfAttributeCount = 0;
        this.fatherAttributeCount = 0;
        this.nonPrivate = new LinkedList<>();
        this.duplicateAttribute = new LinkedList<>();
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
        this.duplicateImplement = false;
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
        if (element.getElement() instanceof UmlAssociationEnd) {
            return;
        }
        UmlAttribute attribute = (UmlAttribute)element.getElement();
        if (!this.attributes.containsKey(attribute.getName())) {
            this.attributes.put(attribute.getName(), new LinkedList<>());
        } else if (this.attributes.get(attribute.getName()).size() == 1) {
            this.duplicateAttribute.add(
                new AttributeClassInformation(attribute.getName(),
                    element.getClassName()));
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
        if (element.getElement() instanceof UmlAssociationEnd) {
            return;
        }
        UmlAttribute attribute = (UmlAttribute)element.getElement();
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
            } else {
                // 考虑类继承带来的重复实现（直接实现关系）
                this.duplicateImplement = true;
            }
        }
    }

    /**
     * 要求：父节点已经计算完毕
     * 设置时保证父类所有方法（包括自定义和继承）都被计算
     * 类不可能重复继承类，如果重复只可能是形成了环
     * @return 顶级父类
     */
    public Class finishFather(LinkedList<Class> trail) {
        if (this.father == null || this.father == this) {
            if (this.father == this) {
                trail.add(this);
            }
            this.treeDone = true;
            this.allAssociations = this.associations.size();
            this.topFather = this;
            EndSet.getSet().iterRemove(this);
            return this;
        }
        if (trail != null && trail.contains(this.father)) {
            // 如果出现循环继承，向父节点的遍历过程会陷入死循环中，这里特殊处理
            // 树结构中出现环，先完成自己
            this.allAssociations = this.associations.size();
            this.topFather = this;
            this.treeDone = true;
            // 按照环中出现的顺序，依次完成
            LinkedList<Class> iter = (LinkedList<Class>)trail.clone();
            trail.clear();
            for (Class element : iter) {
                if (this.father != element.father) {
                    assert element.father.treeDone;
                    element.finishFather(null);
                    trail.add(element);
                } else {
                    break;
                }
            }
            // 这时trail已经删除了多余的元素
            trail.add(this);
            assert this.father.treeDone;
        }
        if (!this.father.treeDone) {
            trail.addFirst(this);
            this.topFather = this.father.finishFather(trail);
        } else {
            // 环结构中，if内的finishFather也执行这里
            // 出现环结构时，if结束一定到这里，第二次完成环中节点
            this.topFather = this.father.topFather;
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

    public Class getFather() {
        return father;
    }

    /**
     * 仅涉及当前接口，随时可调用
     * @param inter
     */
    public void setImplement(Interface inter) {
        if (this.interfaces.containsKey(inter.getId())) {
            this.duplicateImplement = true;
        } else {
            this.interfaces.put(inter.getId(), inter);
        }
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
            // 这里实际上考虑的是接口继承带来的实现关系
            for (Interface extended : now.getExtendedInterfaces().values()) {
                if (!allImplementId.contains(extended.getId())) {
                    allImplementId.add(extended.getId());
                    stack.add(extended);
                } else {
                    // 这里是接口的重复实现
                    this.duplicateImplement = true;
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

    private void addAssociatedEndAsAttribute(UmlElement ele) {
        String name = ele.getName();
        if (name == null) {
            return;
        }
        if (!this.attributes.containsKey(name)) {
            this.attributes.put(name, new LinkedList<>());
        } else if (this.attributes.get(name).size() == 1) {
            this.duplicateAttribute.add(
                new AttributeClassInformation(name, this.getName()));
        }
        this.attributes.get(name).add(
            new Attribute((UmlAssociationEnd)ele, this));
    }

    @Override public void setAssociated(Association association, Boolean opt) {
        this.associations.add(association);
        if (opt) {
            this.addAssociatedEndAsAttribute(association.getUmlEnd2());
            if (association.getEnd2().getType().equals(ElementType.UML_CLASS)) {
                this.associationClassEndId.add(association.getEnd2().getId());
            }
        } else {
            this.addAssociatedEndAsAttribute(association.getUmlEnd1());
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

    public UmlClass getElement() {
        return element;
    }

    public Boolean getDuplicateImplement() {
        assert this.treeDone;
        this.getAllImplement();
        return duplicateImplement;
    }

    public LinkedList<AttributeClassInformation> getDuplicateAttribute() {
        return duplicateAttribute;
    }
}
