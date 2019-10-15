import com.oocourse.uml2.models.elements.UmlTransition;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/6/16 17:14
 */
public class Transition {
    private UmlTransition element;
    private State from;
    private State to;

    Transition(UmlTransition element) {
        this.element = element;
    }

    public void setFrom(State state) {
        this.from = state;
    }

    public void setTo(State to) {
        this.to = to;
    }

    public State getFrom() {
        return from;
    }

    public State getTo() {
        return to;
    }

    public String getId() {
        return this.element.getId();
    }
}
