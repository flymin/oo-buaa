import com.oocourse.TimableOutput;
import com.oocourse.elevator2.PersonRequest;
import shareclass.ElevatorState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/1 21:31
 */
public class Elevator extends Thread {
    private static Boolean canExit = false;
    private ElevatorState elevatorState;
    private Vector<PersonRequest> elevatorMission;
    private HashMap<Integer, ArrayList<PersonRequest>> handlingMission;

    Elevator(String str, int capacity) {
        super(str);
        this.elevatorState = new ElevatorState();
        this.elevatorMission = new Vector<>(capacity);
        this.handlingMission = new HashMap<>();
    }

    @Override public void run() {
        Boolean exit = false;
        while (true) {
            synchronized (this.elevatorMission) {
                while (this.elevatorMission.isEmpty()) {
                    if (canExit) {
                        exit = true;
                        break;
                    } else {
                        Main.output("Elevator trying to take");
                        try {
                            this.elevatorMission.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (exit) {
                    break;
                }
            }
            this.go();
        }
        Main.output(this.getName() + " exit");
    }

    private void go() {
        this.elevatorState.setTargetFloor(this.determineMission());
        //Main.output(this.handlingMission.toString());
        while (!this.handlingMission.isEmpty()) {
            while (!this.elevatorState.arrive()) {
                try {
                    sleep(400);
                    this.elevatorState.moveDireciton();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Main.output(this.elevatorMission.toString());
                this.changeTarget();
            }
            this.inAndOut();
        }
    }

    /**
     * 1. 再次遍历任务队列
     * 2. 查找出入电梯的人
     * 3. 更改电梯中的人的请求的目标楼层（FROM->TO）
     * @param current 当前楼层
     * @param goingUp 上行？
     * @return output 输出字符串数组
     */
    private ArrayList<String> getPerson(int current, Boolean goingUp) {
        yield();
        this.iterMission(current, current, goingUp);
        ArrayList<String> output = new ArrayList<>();
        if (!this.handlingMission.containsKey(current)) {
            return output;
        }
        for (PersonRequest req : this.handlingMission.get(current)) {
            if (current == req.getFromFloor()) {
                // 接人进电梯
                output.add(
                    String.format("IN-%d-%d", req.getPersonId(), current));
                this.missionAdd(req.getToFloor(), req);
            } else {
                // 放人出电梯
                output.add(
                    String.format("OUT-%d-%d", req.getPersonId(), current));
            }
        }
        return output;
    }

    /**
     * 1. 开门关门的输出信息
     * 2. 更新电梯下一个楼层
     */
    private void inAndOut() {
        int current = this.elevatorState.getFloor();
        Boolean goingUp = this.elevatorState.getGoingUp();
        ArrayList<String> output = this.getPerson(current, goingUp);
        this.handlingMission.remove(current);
        if (!output.isEmpty()) {
            this.doorOutput(output, current, goingUp);
        }
        // double check for safe
        if (this.handlingMission.containsKey(current)) {
            this.handlingMission.remove(current);
        }
        if (!this.handlingMission.isEmpty()) {
            this.checkDirection(current);
        }
    }

    /**
     * 在某一层接送完乘客之后，决定下一次目标楼层
     * @param current 当前楼层
     */
    private void checkDirection(int current) {
        int targetFloor;
        Vector<Integer> part = new Vector<>();
        Boolean goingUp = this.elevatorState.getGoingUp();
        int min = Collections.min(this.handlingMission.keySet());
        int max = Collections.max(this.handlingMission.keySet());
        if (goingUp) {
            if (min > current) {
                targetFloor = min;
            } else if (max < current) {
                targetFloor = max;
            } else {
                // 介于min max之间
                for (int i : this.handlingMission.keySet()) {
                    if (i > current) {
                        part.add(i);
                    }
                }
                targetFloor = Collections.min(part);
            }
        } else {
            if (current > max) {
                targetFloor = max;
            } else if (current < min) {
                targetFloor = min;
            } else {
                // 介于max min之间
                for (int i : this.handlingMission.keySet()) {
                    if (i < current) {
                        part.add(i);
                    }
                }
                targetFloor = Collections.max(part);
            }
        }
        this.elevatorState.setTargetFloor(targetFloor);
    }

    /**
     * 遍历请求队列并构造handle字典
     * @param target 目标楼层
     * @param currentFloor 当前楼层
     * @param goingUp 上行？
     * @return targetFloor最近的停靠楼层
     */
    private int iterMission(int target, int currentFloor,
        Boolean goingUp) {
        int targetFloor = target;
        synchronized (this.elevatorMission) {
            for (PersonRequest req : this.elevatorMission) {
                if (targetFloor > currentFloor) {
                    if (req.getFromFloor() > currentFloor
                        && req.getFromFloor() < req.getToFloor()) {
                        this.missionAdd(req.getFromFloor(), req);
                        if (req.getFromFloor() < targetFloor) {
                            targetFloor = req.getFromFloor();
                        }
                    }
                } else if (targetFloor < currentFloor) {
                    if (req.getFromFloor() < currentFloor
                        && req.getFromFloor() > req.getToFloor()) {
                        this.missionAdd(req.getFromFloor(), req);
                        if (req.getFromFloor() > targetFloor) {
                            targetFloor = req.getFromFloor();
                        }
                    }
                } else {
                    if (req.getFromFloor() == currentFloor) {
                        this.missionAdd(req.getFromFloor(), req);
                    }
                }
            }
        }
        synchronized (this.elevatorMission) {
            for (int key : this.handlingMission.keySet()) {
                for (PersonRequest req : this.handlingMission.get(key)) {
                    this.elevatorMission.remove(req);
                }
            }
        }
        return targetFloor;
    }

    /**
     * 到达每一层后，检查
     * 1. 是否有新的捎带请求被加入
     * 2. 是否需要更改目标楼层——提前停靠
     */
    private void changeTarget() {
        int targetFloor = this.elevatorState.getTargetFloor();
        int currentFloor = this.elevatorState.getFloor();
        int targetFloorNew;
        synchronized (this.elevatorMission) {
            Boolean goingUp = this.elevatorState.getGoingUp();
            targetFloorNew =
                this.iterMission(targetFloor, currentFloor, goingUp);
        }
        if (targetFloor != targetFloorNew) {
            this.elevatorState.setTargetFloor(targetFloorNew);
        }
    }

    /**
     * 将目标楼层和请求二元组加入到字典列表中
     */
    private void missionAdd(int floor, PersonRequest request) {
        if (this.handlingMission.containsKey(floor)) {
            this.handlingMission.get(floor).add(request);
        } else {
            this.handlingMission.put(floor, new ArrayList<>());
            this.handlingMission.get(floor).add(request);
        }
    }

    /**
     * 请求队列为空或者捎带情况结束时，重新组织此次执行的任务
     * @return 最近的目标楼层
     */
    private int determineMission() {
        PersonRequest request = null;
        int targetFloor;
        int currentFloor = this.elevatorState.getFloor();
        synchronized (this.elevatorMission) {
            request = this.elevatorMission.get(0);
            this.missionAdd(request.getFromFloor(), request);
            this.elevatorMission.remove(request);
            targetFloor = request.getFromFloor();
            Boolean goingUp = this.elevatorState.getGoingUp();
            Main.output(String.format("with target %d first", targetFloor));
            targetFloor = this.iterMission(targetFloor, currentFloor, goingUp);
            Main.output(String.format("with target %d", targetFloor));
        }
        return targetFloor;
    }

    private void doorOutput(ArrayList<String> output, int floor,
        Boolean goingUp) {
        long openTime = TimableOutput.println(
            String.format("OPEN-%d", floor));
        for (String str : output) {
            TimableOutput.println(str);
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - openTime < 400) {
            try {
                sleep(400 - (currentTime - openTime));
            } catch (InterruptedException e) {
                // ignore
            }
        }
        // double check before closing
        ArrayList<String> temp = this.getPerson(floor, goingUp);
        for (String str : temp) {
            TimableOutput.println(str);
        }
        TimableOutput.println(
            String.format("CLOSE-%d", floor));
    }

    public ElevatorState getElevatorState() {
        return this.elevatorState;
    }

    public Vector<PersonRequest> getElevatorMission() {
        return this.elevatorMission;
    }

    public static void setCanExit() {
        Elevator.canExit = true;
    }
}
