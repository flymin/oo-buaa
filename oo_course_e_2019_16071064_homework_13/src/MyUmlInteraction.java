import com.oocourse.uml1.interact.common.AttributeClassInformation;
import com.oocourse.uml1.interact.common.AttributeQueryType;
import com.oocourse.uml1.interact.common.OperationQueryType;
import com.oocourse.uml1.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml1.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml1.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml1.interact.format.UmlInteraction;
import com.oocourse.uml1.models.common.ElementType;
import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlClass;
import com.oocourse.uml1.models.elements.UmlElement;
import com.oocourse.uml1.models.elements.UmlGeneralization;
import com.oocourse.uml1.models.elements.UmlInterfaceRealization;
import com.oocourse.uml1.models.elements.UmlAssociationEnd;
import com.oocourse.uml1.models.elements.UmlAssociation;
import com.oocourse.uml1.models.elements.UmlAttribute;
import com.oocourse.uml1.models.elements.UmlOperation;
import com.oocourse.uml1.models.elements.UmlInterface;
import com.oocourse.uml1.models.elements.UmlParameter;

import java.util.HashMap;
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
public class MyUmlInteraction implements UmlInteraction {
    private HashMap<String, Association> associations;
    private HashMap<String, Operation> operations;
    private LinkedList<UmlGeneralization> generalizations;
    private LinkedList<UmlInterfaceRealization> realizations;

    public MyUmlInteraction(UmlElement... elements) {
        this.associations = new HashMap<>();
        this.operations = new HashMap<>();
        this.generalizations = new LinkedList<>();
        this.realizations = new LinkedList<>();
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
                    Attribute umlAttribute =
                        new Attribute((UmlAttribute)element, end);
                    end.addAttribute(umlAttribute);
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
            association.getEnd1().setAssociated(association);
            association.getEnd2().setAssociated(association);
        }
        EndSet.getSet().iterClear(ElementType.UML_CLASS);
        while (!EndSet.getSet().iterEmpty()) {
            Class umlClass = (Class)EndSet.getSet().iterNext();
            umlClass.finishFather();
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
