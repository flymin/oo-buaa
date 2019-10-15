import com.oocourse.elevator2.PersonRequest;
import shareclass.ElevatorState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

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
    private ArrayList<PersonRequest> queue;
    private HashMap<String, ElevatorState> statesMap;
    private HashMap<String, Vector<PersonRequest>> missionMap;

    public Scheduler(ArrayList<PersonRequest> queue) {
        this.queue = queue;
        this.statesMap = new HashMap<>();
        this.missionMap = new HashMap<>();
    }

    public void addElevator(Elevator ele) {
        statesMap.put(ele.getName(), ele.getElevatorState());
        missionMap.put(ele.getName(), ele.getElevatorMission());
    }

    @Override public void run() {
        PersonRequest request = null;
        Boolean exit = false;
        while (true) {
            synchronized (this.queue) {
                while (this.queue.isEmpty()) {
                    if (canExit) {
                        exit = true;
                        break;
                    } else {
                        Main.output("Scheduler trying to take");
                        try {
                            this.queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (exit) {
                    break;
                } else {
                    Main.output("Scheduler can take");
                    request = this.queue.get(0);
                    this.queue.remove(request);
                    Main.output("Scheduler taken");
                }
            }
            String name = this.determine(request);
            Vector<PersonRequest> missions = this.missionMap.get(name);
            synchronized (missions) {
                missions.add(request);
                Main.output("Scheduler notify");
                missions.notifyAll();
            }
        }
        Main.output("Scheduler exit");
        Elevator.setCanExit();
        for (Vector<PersonRequest> missionQueue :
            this.missionMap.values()) {
            synchronized (missionQueue) {
                missionQueue.notifyAll();
            }
        }
    }

    private String determine(PersonRequest request) {
        return this.missionMap.keySet().iterator().next();
    }

    public static void setCanExit() {
        Scheduler.canExit = true;
    }
}
