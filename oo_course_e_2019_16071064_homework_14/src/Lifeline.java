import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlMessage;

import java.util.LinkedList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/6/16 16:43
 */
public class Lifeline {
    private UmlElement element;
    private LinkedList<UmlMessage> sendMessage;
    private LinkedList<UmlMessage> recvMessage;

    Lifeline(UmlElement lifeline) {
        this.element = lifeline;
        this.sendMessage = new LinkedList<>();
        this.recvMessage = new LinkedList<>();
    }

    public void addSendMessage(UmlMessage umlMessage) {
        this.sendMessage.add(umlMessage);
    }

    public void addRecvMessage(UmlMessage umlMessage) {
        this.recvMessage.add(umlMessage);
    }

    public Integer getIncomingNum() {
        return this.recvMessage.size();
    }

    public String getName() {
        return this.element.getName();
    }

    public String getId() {
        return this.element.getId();
    }
}
