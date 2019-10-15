import com.oocourse.uml2.models.common.ElementType;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 16:13
 */
public interface AssociationEnd {
    String getName();

    String getId();

    ElementType getType();

    void addOperation(Operation operation);

    void addAttribute(Attribute element);

    void setAssociated(Association association, Boolean opt);

    void addExtended(AssociationEnd end);
}
