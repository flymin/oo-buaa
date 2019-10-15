import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.common.AttributeQueryType;
import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.interact.format.UmlClassModelInteraction;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlClass;
import com.oocourse.uml2.models.elements.UmlClassOrInterface;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlGeneralization;
import com.oocourse.uml2.models.elements.UmlInterfaceRealization;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlInterface;
import com.oocourse.uml2.models.elements.UmlParameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/29 9:23
 */
public class MyClassModelInteraction implements UmlClassModelInteraction {
    private HashMap<String, Association> associations;
    private HashMap<String, Operation> operations;
    private LinkedList<UmlGeneralization> generalizations;
    private LinkedList<UmlInterfaceRealization> realizations;
    private LinkedList<LinkedList<Class>> classCycle;
    private HashSet<Class> duplicatedClasses;
    private HashSet<Interface> interfacesCycle;
    private HashSet<Interface> duplicatedInterfaces;

    public MyClassModelInteraction(UmlElement... elements) {
        this.associations = new HashMap<>();
        this.operations = new HashMap<>();
        this.generalizations = new LinkedList<>();
        this.realizations = new LinkedList<>();
        this.classCycle = new LinkedList<>();
        this.interfacesCycle = null;
        this.duplicatedClasses = null;
        this.duplicatedInterfaces = null;
        for (UmlElement element : elements) {
            switch (element.getElementType()) {
                case UML_CLASS:
                    Class umlClass = new Class((UmlClass)element);
                    EndSet.getSet().addElement(umlClass);
                    break;
                case UML_ASSOCIATION:
                    Association umlAssociation =
                        new Association((UmlAssociation)element);
                    this.associations.put(element.getId(), umlAssociation);
                    break;
                case UML_ASSOCIATION_END:
                    umlAssociation =
                        this.associations.get(element.getParentId());
                    umlAssociation.setEnd((UmlAssociationEnd)element);
                    break;
                case UML_ATTRIBUTE:
                    AssociationEnd end =
                        EndSet.getSet().getById(element.getParentId());
                    if (end != null) {
                        Attribute umlAttribute =
                            new Attribute((UmlAttribute)element, end);
                        end.addAttribute(umlAttribute);
                    }
                    break;
                case UML_OPERATION:
                    Operation umlOperation =
                        new Operation((UmlOperation)element);
                    this.operations.put(umlOperation.getId(), umlOperation);
                    break;
                case UML_PARAMETER:
                    umlOperation = this.operations.get(element.getParentId());
                    umlOperation.addParam((UmlParameter)element);
                    break;
                case UML_GENERALIZATION:
                    // 这里不添加
                    this.generalizations.add((UmlGeneralization)element);
                    break;
                case UML_INTERFACE_REALIZATION:
                    // 这里不添加
                    this.realizations.add((UmlInterfaceRealization)element);
                    break;
                case UML_INTERFACE:
                    AssociationEnd umlInterface =
                        new Interface((UmlInterface)element);
                    EndSet.getSet().addElement(umlInterface);
                    break;
                default:
            }
        }
        for (String id : this.operations.keySet()) {
            Operation operation = this.operations.get(id);
            AssociationEnd end =
                EndSet.getSet().getById(operation.getParentId());
            end.addOperation(operation);
        }
        for (UmlGeneralization generalization : this.generalizations) {
            AssociationEnd source =
                EndSet.getSet().getById(generalization.getSource());
            AssociationEnd target =
                EndSet.getSet().getById(generalization.getTarget());
            assert source.getType().equals(target.getType());
            source.addExtended(target);
        }
        for (UmlInterfaceRealization realization : this.realizations) {
            Class umlClass =
                (Class)EndSet.getSet().getById(realization.getSource());
            Interface umlInterface = (Interface)EndSet.getSet().
                getById(realization.getTarget());
            umlClass.setImplement(umlInterface);
        }
        for (String id : this.associations.keySet()) {
            Association association = this.associations.get(id);
            association.finishEnd();
            association.getEnd1().setAssociated(association, true);
            association.getEnd2().setAssociated(association, false);
        }
        EndSet.getSet().iterClear(ElementType.UML_CLASS);
        while (!EndSet.getSet().iterEmpty()) {
            Class umlClass = (Class)EndSet.getSet().iterNext();
            // 传入初始化链表，准备检查是否有环
            LinkedList<Class> trail = new LinkedList<>();
            umlClass.finishFather(trail);
            if (!trail.isEmpty()) {
                if (trail.getFirst().getFather() == trail.getLast()) {
                    // 这是一个环
                    this.classCycle.add(trail);
                }
            }
        }
    }

    private void checkInterface() {
        this.interfacesCycle = new HashSet<>();
        this.duplicatedInterfaces = new HashSet<>();
        EndSet.getSet().iterClear(ElementType.UML_INTERFACE);
        while (!EndSet.getSet().iterEmpty()) {
            Interface inter = (Interface)EndSet.getSet().iterNext();
            EndSet.getSet().iterRemove(inter);
            LinkedList<Interface> stack = new LinkedList<>();
            // 有自环会添加自己，没有不添加
            stack.addAll(inter.getExtendedInterfaces().values());
            int index = 0;
            for (; index < stack.size(); index++) {
                Interface now = stack.get(index);
                if (now == inter) {
                    this.interfacesCycle.add(inter);
                    continue;
                }
                for (Interface extended :
                    now.getExtendedInterfaces().values()) {
                    if (!stack.contains(extended)) {
                        stack.addLast(extended);
                    } else {
                        this.duplicatedInterfaces.add(inter);
                    }
                }
                // 如果已经确定既有环也有重复继承，不必继续遍历
                if (this.duplicatedInterfaces.contains(inter)
                    && this.interfacesCycle.contains(inter)) {
                    break;
                }
            }
        }
    }

    /**
     * UML基本标准预检查
     */
    public void checkForUml008() throws UmlRule008Exception {
        HashSet<UmlClassOrInterface> result = new HashSet<>();
        // 处理类的循环继承
        for (LinkedList<Class> cycle : this.classCycle) {
            for (Class cls : cycle) {
                result.add(cls.getElement());
            }
        }
        // 处理接口的循环继承
        if (this.interfacesCycle == null) {
            checkInterface();
        }
        Iterator<Interface> iter = this.interfacesCycle.iterator();
        while (iter.hasNext()) {
            result.add(iter.next().getElement());
        }
        if (!result.isEmpty()) {
            throw new UmlRule008Exception(result);
        }
    }

    public void checkForUml009() throws UmlRule009Exception {
        HashSet<UmlClassOrInterface> result = new HashSet<>();
        // 类对接口重复实现
        if (this.duplicatedClasses == null) {
            this.duplicatedClasses = new HashSet<>();
            EndSet.getSet().iterClear(ElementType.UML_CLASS);
            while (!EndSet.getSet().iterEmpty()) {
                Class cls = (Class)EndSet.getSet().iterNext();
                if (cls.getDuplicateImplement()) {
                    this.duplicatedClasses.add(cls);
                }
                EndSet.getSet().iterRemove(cls);
            }
        }
        for (Class cls : this.duplicatedClasses) {
            result.add(cls.getElement());
        }
        // 处理接口重复继承
        if (this.duplicatedInterfaces == null) {
            checkInterface();
        }
        for (Interface inter : this.duplicatedInterfaces) {
            result.add(inter.getElement());
        }
        if (!result.isEmpty()) {
            throw new UmlRule009Exception(result);
        }
    }

    public void checkForUml002() throws UmlRule002Exception {
        HashSet<AttributeClassInformation> result = new HashSet<>();
        EndSet.getSet().iterClear(ElementType.UML_CLASS);
        while (!EndSet.getSet().iterEmpty()) {
            Class cls = (Class)EndSet.getSet().iterNext();
            LinkedList<AttributeClassInformation> clsResult =
                cls.getDuplicateAttribute();
            result.addAll(clsResult);
            EndSet.getSet().iterRemove(cls);
        }
        if (!result.isEmpty()) {
            throw new UmlRule002Exception(result);
        }
    }

    @Override public int getClassCount() {
        return EndSet.getSet().getClassNum();
    }

    @Override public int getClassOperationCount(String name,
        OperationQueryType operationQueryType)
        throws ClassNotFoundException, ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(name);
        return umlClass.getOperationCount(operationQueryType);
    }

    @Override public int getClassAttributeCount(String name,
        AttributeQueryType attributeQueryType)
        throws ClassNotFoundException, ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(name);
        Integer num = 0;
        switch (attributeQueryType) {
            case SELF_ONLY:
                num = umlClass.getSelfAttributeCount();
                break;
            case ALL:
                num = umlClass.getSelfAttributeCount()
                    + umlClass.getFatherAttributeCount();
                break;
            default:
        }
        return num;
    }

    @Override public int getClassAssociationCount(String name)
        throws ClassNotFoundException, ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(name);
        return umlClass.getAllAssociations();
    }

    @Override public List<String> getClassAssociatedClassList(String name)
        throws ClassNotFoundException, ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(name);
        LinkedList<String> classNameList = umlClass.getAssociationIdClasses();
        return classNameList;
    }

    @Override
    public Map<Visibility, Integer> getClassOperationVisibility(
        String className, String operationName) throws ClassNotFoundException,
        ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(className);
        LinkedList<Operation> operationList =
            umlClass.getOperationByName(operationName);
        HashMap<Visibility, Integer> result = new HashMap<>();
        if (operationList != null) {
            for (Operation operation : operationList) {
                Visibility type = operation.getVisibility();
                if (result.containsKey(type)) {
                    Integer num = result.get(type) + 1;
                    result.replace(type, num);
                } else {
                    result.put(type, 1);
                }
            }
        }
        return result;
    }

    @Override public Visibility getClassAttributeVisibility(String className,
        String attributeName) throws ClassNotFoundException,
        ClassDuplicatedException, AttributeNotFoundException,
        AttributeDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(className);
        Attribute attribute = umlClass.getAttributeByName(attributeName);
        return attribute.getVisibility();
    }

    @Override public String getTopParentClass(String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(className);
        return umlClass.getTopFather().getName();
    }

    @Override public List<String> getImplementInterfaceList(String className)
        throws ClassNotFoundException, ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(className);
        return umlClass.getAllImplement();
    }

    @Override
    public List<AttributeClassInformation> getInformationNotHidden(
        String className) throws ClassNotFoundException,
        ClassDuplicatedException {
        Class umlClass = (Class)EndSet.getSet().getByName(className);
        return umlClass.getNonPrivate();
    }
}
