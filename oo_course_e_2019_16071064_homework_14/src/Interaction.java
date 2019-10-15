import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.models.elements.UmlEndpoint;
import com.oocourse.uml2.models.elements.UmlInteraction;
import com.oocourse.uml2.models.elements.UmlLifeline;
import com.oocourse.uml2.models.elements.UmlMessage;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/6/16 16:46
 */
public class Interaction {
    private UmlInteraction element;
    private HashMap<String, LinkedList<Lifeline>> nameMap;
    private HashMap<String, Lifeline> idMap;
    private HashMap<String, Lifeline> endPointIdMap;
    private LinkedList<UmlMessage> messages;

    Interaction(UmlInteraction interaction) {
        this.element = interaction;
        this.nameMap = new HashMap<>();
        this.idMap = new HashMap<>();
        this.messages = new LinkedList<>();
        this.endPointIdMap = new HashMap<>();
    }

    public void addMessage(UmlMessage message) {
        Lifeline source = getLifelineById(message.getSource());
        source.addSendMessage(message);
        Lifeline target = getLifelineById(message.getTarget());
        target.addRecvMessage(message);
        this.messages.add(message);
    }

    public Integer getMessageNum() {
        return this.messages.size();
    }

    public void addLifeline(UmlLifeline umlLifeline) {
        Lifeline lifeline = new Lifeline(umlLifeline);
        this.idMap.put(lifeline.getId(), lifeline);
        if (!this.nameMap.containsKey(lifeline.getName())) {
            this.nameMap.put(lifeline.getName(), new LinkedList<>());
        }
        this.nameMap.get(lifeline.getName()).add(lifeline);
    }

    public void addEndPoint(UmlEndpoint umlEndpoint) {
        Lifeline endPoint = new Lifeline(umlEndpoint);
        this.endPointIdMap.put(umlEndpoint.getId(), endPoint);
    }

    public Integer getLifelineNum() {
        return this.idMap.size();
    }

    private Lifeline getLifelineById(String id) {
        if (this.idMap.containsKey(id)) {
            return this.idMap.get(id);
        } else {
            return this.endPointIdMap.get(id);
        }
    }

    public Lifeline getLifelineByName(String name) throws
        LifelineDuplicatedException, LifelineNotFoundException {
        if (!this.nameMap.containsKey(name)) {
            throw new LifelineNotFoundException(this.getName(), name);
        }
        LinkedList<Lifeline> lifelines = this.nameMap.get(name);
        if (lifelines.size() > 1) {
            throw new LifelineDuplicatedException(this.getName(), name);
        }
        return lifelines.getFirst();
    }

    public String getName() {
        return this.element.getName();
    }

    public String getId() {
        return this.element.getId();
    }
}
