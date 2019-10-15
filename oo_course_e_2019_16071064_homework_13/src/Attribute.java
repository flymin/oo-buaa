import com.oocourse.uml1.models.common.Visibility;
import com.oocourse.uml1.models.elements.UmlAttribute;

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
    private String className;
    private String classId;

    Attribute(UmlAttribute attribute, AssociationEnd endElement) {
        this.element = attribute;
        this.classId = endElement.getId();
        this.className = endElement.getName();
    }

    public UmlAttribute getElement() {
        return element;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public Visibility getVisibility() {
        return this.element.getVisibility();
    }
}
