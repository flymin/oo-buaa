import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlRegion;
import com.oocourse.uml2.models.elements.UmlStateMachine;
import com.oocourse.uml2.models.elements.UmlTransition;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/6/16 17:13
 */
public class StateMachine {
    private UmlStateMachine element;
    private HashMap<String, Transition> transitionIdMap;
    private HashMap<String, State> stateIdMap;
    private HashMap<String, LinkedList<State>> stateNameMap;
    private UmlRegion region;

    StateMachine(UmlStateMachine umlStateMachine) {
        this.element = umlStateMachine;
        this.transitionIdMap = new HashMap<>();
        this.stateIdMap = new HashMap<>();
        this.stateNameMap = new HashMap<>();
    }

    public void addRegion(UmlRegion region) {
        this.region = region;
    }

    public void addState(UmlElement umlState) {
        State state = new State(umlState);
        this.stateIdMap.put(state.getId(), state);
        if (!this.stateNameMap.containsKey(state.getName())) {
            this.stateNameMap.put(state.getName(), new LinkedList<>());
        }
        this.stateNameMap.get(state.getName()).add(state);
    }

    public Integer getStateNum() {
        return this.stateIdMap.size();
    }

    public void addTransition(UmlTransition umlTransition) {
        Transition transition = new Transition(umlTransition);
        this.transitionIdMap.put(transition.getId(), transition);
        State source = this.stateIdMap.get(umlTransition.getSource());
        transition.setFrom(source);
        source.addTo(transition);
        State target = this.stateIdMap.get(umlTransition.getTarget());
        transition.setTo(target);
        target.addFrom(transition);
    }

    public Integer getTransitionNum() {
        return this.transitionIdMap.size();
    }

    public State getStateByName(String stateName)
        throws StateNotFoundException, StateDuplicatedException {
        if (!this.stateNameMap.containsKey(stateName)) {
            throw new StateNotFoundException(this.getName(), stateName);
        }
        LinkedList<State> states = this.stateNameMap.get(stateName);
        if (states.size() > 1) {
            throw new StateDuplicatedException(this.getName(), stateName);
        }
        return states.getFirst();
    }

    public String getId() {
        return this.element.getId();
    }

    public String getName() {
        return this.element.getName();
    }

    public UmlRegion getRegion() {
        return this.region;
    }
}
