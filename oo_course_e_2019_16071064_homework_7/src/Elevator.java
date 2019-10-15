import com.oocourse.TimableOutput;
import shareclass.ElevatorState;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

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
    private long time;
    private ElevatorState elevatorState;
    private Vector<Mission> elevatorMission;
    private HashMap<Integer, ArrayList<Mission>> handlingMission;
    private static final int OPENTIME = 200;
    private static final int CLOSETIME = 200;
    private Vector<Mission> finishList;

    Elevator(String str, int capacity, List floors, long time, int cap,
        Object lock) {
        super(str);
        this.time = time;
        this.elevatorState = new ElevatorState(floors, cap, lock);
        this.elevatorMission = new Vector<>(capacity);
        this.handlingMission = new HashMap<>();
        this.finishList = new Vector<>();
    }

    @Override public void run() {
        Boolean exit = false;
        while (true) {
            this.elevatorState.waiting();
            Main.output(this.getName() + " waiting");
            synchronized (this.elevatorMission) {
                while (this.elevatorMission.isEmpty()) {
                    if (canExit) {
                        exit = true;
                        break;
                    } else {
                        Main.output(this.getName() + " trying to take");
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
            this.elevatorState.running();
            Main.output(this.getName() + " running");
            this.go();
        }
        Main.output(this.getName() + " exit");
    }

    private void go() {
        while (true) {
            synchronized (this.elevatorMission) {
                Main.output(this.elevatorMission.toString());
                if (this.elevatorMission.isEmpty()) {
                    break;
                }
            }
            this.elevatorState.setTargetFloor(this.determineMission());
            while (!this.handlingMission.isEmpty()) {
                while (!this.elevatorState.arrive()) {
                    try {
                        this.elevatorState.moveDireciton(this.getName());
                        sleep(this.time);
                        TimableOutput.println(String.format("ARRIVE-%d-%s",
                            this.elevatorState.getFloor(), this.getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.changeTarget();
                }
                this.inAndOut();
            }
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
        this.iterMission(current, current, goingUp);
        ArrayList<String> output = new ArrayList<>();
        if (!this.handlingMission.containsKey(current)) {
            return output;
        }
        for (Mission req : this.handlingMission.get(current)) {
            if (current == req.getFromFloor()) {
                // 接人进电梯
                output.add(String.format("IN-%d-%d-%s",
                    req.getPersonId(), current, this.getName()));
                // 这个missionAdd是变更任务非添加任务
                this.missionAdd(req.getToFloor(), req, false);
            } else {
                // 放人出电梯
                output.add(
                    String.format("OUT-%d-%d-%s", req.getPersonId(), current,
                     this.getName()));
                this.finishList.add(req);
                this.elevatorState.inAndOut("out");
            }
        }
        return output;
    }

    private void checkAndRemove(int current) {
        if (this.handlingMission.containsKey(current)) {
            this.handlingMission.remove(current);
        }
    }

    /**
     * 1. 开门关门的输出信息
     * 2. 更新电梯下一个楼层
     */
    private void inAndOut() {
        int current = this.elevatorState.getFloor();
        Boolean goingUp = this.elevatorState.getGoingUp();
        ArrayList<String> output = this.getPerson(current, goingUp);
        checkAndRemove(current);
        if (!output.isEmpty()) {
            this.doorOutput(output, current, goingUp);
        }
        // double check for safe
        checkAndRemove(current);
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
                for (int floor : this.handlingMission.keySet()) {
                    if (floor > current) {
                        part.add(floor);
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
                for (int floor : this.handlingMission.keySet()) {
                    if (floor < current) {
                        part.add(floor);
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
        if (this.elevatorState.isFull()) {
            return targetFloor;
        }
        synchronized (this.elevatorMission) {
            Main.output(this.elevatorMission.toString());
            for (Mission req : this.elevatorMission) {
                if (this.elevatorState.isFull()) {
                    break;
                }
                if (targetFloor > currentFloor) {
                    if (req.getFromFloor() > currentFloor) {
                        this.missionAdd(req.getFromFloor(), req, true);
                        if (req.getFromFloor() < targetFloor) {
                            targetFloor = req.getFromFloor();
                        }
                    }
                } else if (targetFloor < currentFloor) {
                    if (req.getFromFloor() < currentFloor) {
                        this.missionAdd(req.getFromFloor(), req, true);
                        if (req.getFromFloor() > targetFloor) {
                            targetFloor = req.getFromFloor();
                        }
                    }
                }
                if (req.getFromFloor() == currentFloor) {
                    this.missionAdd(req.getFromFloor(), req, true);
                }
            }
        }
        synchronized (this.elevatorMission) {
            for (int key : this.handlingMission.keySet()) {
                for (Mission req : this.handlingMission.get(key)) {
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
    private void missionAdd(int floor, Mission request, Boolean aug) {
        if (this.elevatorState.isFull() && aug) {
            //throw new RuntimeException("电梯满");
            System.err.println("电梯满");
        }
        if (aug) {
            this.elevatorState.inAndOut("in");
        }
        if (this.handlingMission.containsKey(floor)) {
            this.handlingMission.get(floor).add(request);
        } else {
            this.handlingMission.put(floor, new ArrayList<>());
            this.handlingMission.get(floor).add(request);
        }
        Main.output("\t add " + request.toString() +
            this.elevatorState.getCurrentPeople() + " : " +
            this.elevatorState.getCapacity() + aug);
    }

    /**
     * 请求队列为空或者捎带情况结束时，重新组织此次执行的任务
     * @return 最近的目标楼层
     */
    private int determineMission() {
        Mission request;
        int targetFloor;
        int currentFloor = this.elevatorState.getFloor();
        synchronized (this.elevatorMission) {
            request = this.elevatorMission.get(0);
            this.missionAdd(request.getFromFloor(), request, true);
            this.elevatorMission.remove(request);
            targetFloor = request.getFromFloor();
            Boolean goingUp = this.elevatorState.getGoingUp();
            targetFloor = this.iterMission(targetFloor, currentFloor, goingUp);
            Main.output(String.format("with target %d", targetFloor));
        }
        return targetFloor;
    }

    private void doorOutput(ArrayList<String> output, int floor,
        Boolean goingUp) {
        long openTime = TimableOutput.println(
            String.format("OPEN-%d-%s", floor, this.getName()));
        for (String str : output) {
            TimableOutput.println(str);
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - openTime < OPENTIME + CLOSETIME) {
            try {
                sleep(OPENTIME + CLOSETIME - (currentTime - openTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // double check before closing
        ArrayList<String> temp = this.getPerson(floor, goingUp);
        for (String str : temp) {
            TimableOutput.println(str);
        }
        TimableOutput.println(
            String.format("CLOSE-%d-%s", floor, this.getName()));
        for (Mission mission : this.finishList) {
            Main.getScheduler().returnMission(mission);
        }
    }

    public ElevatorState getElevatorState() {
        return this.elevatorState;
    }

    public Vector<Mission> getElevatorMission() {
        return this.elevatorMission;
    }

    public static void setCanExit() {
        Elevator.canExit = true;
    }
}
