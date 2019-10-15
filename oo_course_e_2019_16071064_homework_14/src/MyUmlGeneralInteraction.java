import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.interact.format.UmlGeneralInteraction;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlEndpoint;
import com.oocourse.uml2.models.elements.UmlInteraction;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;
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
 * @since 2019/6/16 10:14
 */
public class MyUmlGeneralInteraction extends MyClassModelInteraction
    implements UmlGeneralInteraction {
    private HashMap<String, Interaction> interactionIdMap;
    private HashMap<String, LinkedList<Interaction>> interactionNameMap;
    private HashMap<String, StateMachine> stateMachineIdMap;
    private HashMap<String, LinkedList<StateMachine>> stateMachineNameMap;

    public MyUmlGeneralInteraction(UmlElement... elements) {
        super(elements);
        this.interactionIdMap = new HashMap<>();
        this.interactionNameMap = new HashMap<>();
        this.stateMachineNameMap = new HashMap<>();
        this.stateMachineIdMap = new HashMap<>();
        LinkedList<UmlElement> umlTransitions = new LinkedList<>();
        LinkedList<UmlElement> umlMessages = new LinkedList<>();
        for (UmlElement element : elements) {
            switch (element.getElementType()) {
                case UML_INTERACTION:
                    Interaction interaction =
                        new Interaction((UmlInteraction)element);
                    this.addInteraction(interaction);
                    break;
                case UML_LIFELINE:
                    this.getInteractionById(element.getParentId()).
                        addLifeline((UmlLifeline)element);
                    break;
                case UML_MESSAGE:
                    umlMessages.add(element);
                    break;
                case UML_CLASS:
                case UML_ASSOCIATION:
                case UML_ASSOCIATION_END:
                case UML_ATTRIBUTE:
                case UML_OPERATION:
                case UML_PARAMETER:
                case UML_GENERALIZATION:
                case UML_INTERFACE_REALIZATION:
                case UML_INTERFACE:
                    break;
                case UML_STATE_MACHINE:
                    StateMachine stateMachine =
                        new StateMachine((UmlStateMachine)element);
                    this.addStateMachine(stateMachine);
                    break;
                case UML_REGION:
                    if (this.stateMachineIdMap.
                        containsKey(element.getParentId())) {
                        this.getStateMachineById(element.getParentId()).
                            addRegion((UmlRegion)element);
                        this.changeMachineId(element.getParentId(),
                            element.getId());
                    }
                    break;
                case UML_STATE:
                case UML_FINAL_STATE:
                case UML_PSEUDOSTATE:
                    this.getStateMachineById(element.getParentId()).
                        addState(element);
                    break;
                case UML_TRANSITION:
                    umlTransitions.add(element);
                    break;
                case UML_EVENT:
                    break;
                case UML_OPAQUE_BEHAVIOR:
                    break;
                case UML_ENDPOINT:
                    this.getInteractionById(element.getParentId()).
                        addEndPoint((UmlEndpoint)element);
                    break;
                default:
            }
        }
        for (UmlElement message : umlMessages) {
            this.getInteractionById(message.getParentId()).
                addMessage((UmlMessage)message);
        }
        for (UmlElement transition : umlTransitions) {
            this.getStateMachineById(transition.getParentId()).
                addTransition((UmlTransition)transition);
        }
    }

    /**
     * 顺序图构建
     */
    private void addInteraction(Interaction interaction) {
        this.interactionIdMap.put(interaction.getId(), interaction);
        if (!this.interactionNameMap.containsKey(interaction.getName())) {
            this.interactionNameMap.put(interaction.getName(),
                new LinkedList<>());
        }
        this.interactionNameMap.get(interaction.getName()).add(interaction);
    }

    private Interaction getInteractionById(String id) {
        return this.interactionIdMap.get(id);
    }

    private Interaction getInteractionByName(String name) throws
        InteractionNotFoundException, InteractionDuplicatedException {
        if (!this.interactionNameMap.containsKey(name)) {
            throw new InteractionNotFoundException(name);
        }
        LinkedList<Interaction> interactions =
            this.interactionNameMap.get(name);
        if (interactions.size() > 1) {
            throw new InteractionDuplicatedException(name);
        }
        return interactions.getFirst();
    }

    /**
     * 顺序图交互
     */
    @Override public int getParticipantCount(String name)
        throws InteractionNotFoundException, InteractionDuplicatedException {
        return this.getInteractionByName(name).getLifelineNum();
    }

    @Override public int getMessageCount(String name)
        throws InteractionNotFoundException, InteractionDuplicatedException {
        return this.getInteractionByName(name).getMessageNum();
    }

    @Override public int getIncomingMessageCount(String interactionName,
        String lifelineName)
        throws InteractionNotFoundException, InteractionDuplicatedException,
        LifelineNotFoundException, LifelineDuplicatedException {
        return this.getInteractionByName(interactionName).
            getLifelineByName(lifelineName).getIncomingNum();
    }

    /**
     * UML状态图构建
     */
    private void addStateMachine(StateMachine stateMachine) {
        this.stateMachineIdMap.put(stateMachine.getId(), stateMachine);
        if (!this.stateMachineNameMap.containsKey(stateMachine.getName())) {
            this.stateMachineNameMap.put(stateMachine.getName(),
                new LinkedList<>());
        }
        this.stateMachineNameMap.get(stateMachine.getName()).add(stateMachine);
    }

    private void changeMachineId(String old, String n) {
        StateMachine machine = this.stateMachineIdMap.remove(old);
        this.stateMachineIdMap.put(n, machine);
    }

    private StateMachine getStateMachineById(String id) {
        return this.stateMachineIdMap.get(id);
    }

    private StateMachine getStateMachineByName(String name) throws
        StateMachineNotFoundException, StateMachineDuplicatedException {
        if (!this.stateMachineNameMap.containsKey(name)) {
            throw new StateMachineNotFoundException(name);
        }
        LinkedList<StateMachine> stateMachines =
            this.stateMachineNameMap.get(name);
        if (stateMachines.size() > 1) {
            throw new StateMachineDuplicatedException(name);
        }
        return stateMachines.getFirst();
    }

    /**
     * UML状态图交互
     */
    @Override public int getStateCount(String name)
        throws StateMachineNotFoundException, StateMachineDuplicatedException {
        return this.getStateMachineByName(name).getStateNum();
    }

    @Override public int getTransitionCount(String name)
        throws StateMachineNotFoundException, StateMachineDuplicatedException {
        return this.getStateMachineByName(name).getTransitionNum();
    }

    @Override public int getSubsequentStateCount(String stateMachineName,
        String stateName)
        throws StateMachineNotFoundException, StateMachineDuplicatedException,
        StateNotFoundException, StateDuplicatedException {
        State state = this.getStateMachineByName(stateMachineName).
            getStateByName(stateName);
        return state.getSubsequentState().size();
    }
}
