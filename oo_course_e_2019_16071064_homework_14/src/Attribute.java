import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlElement;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 15:17
 */
public class Attribute {
    private UmlAttribute element;
    private UmlAssociationEnd endElement;
    private String className;
    private String classId;

    Attribute(UmlAttribute attribute, AssociationEnd endElement) {
        this.element = attribute;
        this.endElement = null;
        this.classId = endElement.getId();
        this.className = endElement.getName();
    }

    Attribute(UmlAssociationEnd associationEnd, AssociationEnd endElement) {
        this.endElement = associationEnd;
        this.element = null;
        this.classId = endElement.getId();
        this.className = endElement.getName();
    }

    public UmlElement getElement() {
        if (this.element != null) {
            return element;
        } else {
            return endElement;
        }
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public Visibility getVisibility() {
        if (element != null) {
            return this.element.getVisibility();
        } else {
            return this.endElement.getVisibility();
        }
    }
}
