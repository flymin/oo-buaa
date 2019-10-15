import com.oocourse.uml1.models.elements.UmlAssociation;
import com.oocourse.uml1.models.elements.UmlAssociationEnd;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/30 16:09
 */
public class Association {
    private UmlAssociation element;
    private UmlAssociationEnd umlEnd1;
    private AssociationEnd end1;
    private UmlAssociationEnd umlEnd2;
    private AssociationEnd end2;

    Association(UmlAssociation element) {
        this.element = element;
        this.end1 = null;
        this.end1 = null;
        this.umlEnd1 = null;
        this.umlEnd2 = null;
    }

    public void setEnd(UmlAssociationEnd end) {
        if (this.umlEnd1 == null) {
            this.umlEnd1 = end;
        } else {
            this.umlEnd2 = end;
        }
    }

    public void finishEnd() {
        this.end1 = EndSet.getSet().getById(umlEnd1.getReference());
        this.end2 = EndSet.getSet().getById(umlEnd2.getReference());
    }

    public AssociationEnd getEnd1() {
        return end1;
    }

    public AssociationEnd getEnd2() {
        return end2;
    }
}
