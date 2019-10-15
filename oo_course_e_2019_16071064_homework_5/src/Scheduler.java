import com.oocourse.elevator1.PersonRequest;
import shareclass.ElevatorState;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/1 21:13
 */
public class Scheduler extends Thread {
    private static Boolean canExit = false;
    private final ArrayBlockingQueue<PersonRequest> queue;
    private HashMap<String, ElevatorState> statesMap;
    private HashMap<String, ArrayBlockingQueue<PersonRequest>> missionMap;

    public Scheduler(ArrayBlockingQueue<PersonRequest> queue) {
        this.queue = queue;
        this.statesMap = new HashMap<>();
        this.missionMap = new HashMap<>();
    }

    public void addElevator(Elevator ele) {
        statesMap.put(ele.getName(), ele.getElevatorState());
        missionMap.put(ele.getName(), ele.getElevatorMission());
    }

    @Override public void run() {
        PersonRequest request;
        while (true) {
            if (queue.isEmpty()) {
                if (canExit) {
                    break;
                } else {
                    yield();
                }
            } else {
                try {
                    request = queue.take();
                    String name = this.determine(request);
                    try {
                        this.missionMap.get(name).put(request);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        Main.output("Scheduler exit");
        Elevator.setCanExit(true);
    }

    private String determine(PersonRequest request) {
        return this.missionMap.keySet().iterator().next();
    }

    public static void setCanExit(Boolean canExit) {
        Scheduler.canExit = canExit;
    }
}
