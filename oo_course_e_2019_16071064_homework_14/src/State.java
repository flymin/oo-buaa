import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.common.ElementType;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/6/16 17:14
 */
public class State {
    private UmlElement element;
    private LinkedList<Transition> transitionsFrom;
    private LinkedList<Transition> transitionsTo;

    State(UmlElement state) {
        this.element = state;
        this.transitionsFrom = new LinkedList<>();
        this.transitionsTo = new LinkedList<>();
    }

    public void addFrom(Transition transition) {
        assert transition.getTo().getId().equals(this.getId());
        this.transitionsFrom.add(transition);
    }

    public void addTo(Transition transition) {
        assert transition.getFrom().getId().equals(this.getId());
        this.transitionsTo.add(transition);
    }

    public HashSet<State> getSubsequentState() {
        HashSet<State> states = new HashSet<>();
        LinkedList<Transition> stack = (LinkedList<Transition>)
            this.getTransitionsTo().clone();
        while (!stack.isEmpty()) {
            State state = stack.pop().getTo();
            if (!states.contains(state)) {
                states.add(state);
                stack.addAll(state.getTransitionsTo());
            }
        }
        return states;
    }

    private LinkedList<Transition> getTransitionsTo() {
        return transitionsTo;
    }

    public String getName() {
        return this.element.getName();
    }

    public String getId() {
        return this.element.getId();
    }

    public ElementType getStateType() {
        return this.element.getElementType();
    }

    @Override public int hashCode() {
        return this.element.hashCode();
    }

    @Override public boolean equals(Object obj) {
        return this.element.equals(obj);
    }
}
