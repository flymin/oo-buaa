package shareclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/1 21:19
 */
public class ElevatorState {
    private int currentFloor;
    private int targetFloor;
    private ArrayList<Integer> floors;
    private ArrayList<Integer> availableFloor;
    private Boolean goingUp;
    private int capacity;
    private int currentPeople;
    private Boolean idle;
    private Object idleLock;

    public ElevatorState(List floors, int capacity, Object lock) {
        this.floors = new ArrayList<>();
        List list = Arrays.asList(-3,-2,-1,
            1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
        this.floors.addAll(list);
        this.availableFloor = new ArrayList<>();
        this.availableFloor.addAll(floors);
        this.currentFloor = this.floors.indexOf(1);
        this.targetFloor = this.floors.indexOf(1);
        this.goingUp = true;
        this.capacity = capacity;
        this.currentPeople = 0;
        this.idle = true;
        this.idleLock = lock;
    }

    public synchronized Boolean isIdle() {
        return idle;
    }

    public synchronized void running() {
        this.idle = false;
    }

    public void waiting() {
        synchronized (this.idleLock) {
            this.idle = true;
            this.idleLock.notifyAll();
        }
    }

    public synchronized Boolean isFull() {
        if (this.currentPeople >= this.capacity) {
            return true;
        } else {
            return false;
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getCurrentPeople() {
        return currentPeople;
    }

    public synchronized void inAndOut(String phrase) {
        if (phrase.equals("in")) {
            this.currentPeople++;
            assert this.currentPeople <= this.capacity;
        } else if (phrase.equals("out")) {
            this.currentPeople--;
            assert this.currentPeople >= 0;
        } else {
            //throw new RuntimeException("状态方向错误");
            System.err.println("状态方向错误");
        }
    }

    public synchronized int getFloor() {
        return this.floors.get(this.currentFloor);
    }

    public synchronized void setTargetFloor(int target) {
        if (!this.availableFloor.contains(target)) {
            //throw new RuntimeException("target: " + target + " 不停靠此楼层");
            System.err.println("target: " + target + " 不停靠此楼层");
        }
        this.targetFloor = this.floors.indexOf(target);
        if (this.targetFloor > this.currentFloor) {
            this.goingUp = true;
        } else {
            this.goingUp = false;
        }
    }

    public synchronized Boolean getGoingUp() {
        return goingUp;
    }

    public synchronized int getTargetFloor() {
        return this.floors.get(this.targetFloor);
    }

    public synchronized Boolean arrive() {
        if (this.targetFloor == currentFloor) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized void moveDireciton(String name) {
        if (this.goingUp) {
            this.currentFloor++;
            if (this.currentFloor > this.floors.size()) {
                //throw new RuntimeException("电梯运行超过上限");
                System.err.println("电梯运行超过上限");
            }
        } else {
            this.currentFloor--;
            if (this.currentFloor < 0) {
                //throw new RuntimeException("电梯运行超过下限");
                System.err.println("电梯运行超过下限");
            }
        }
        //TimableOutput.println(
        //String.format("ARRIVE-%d-%s", this.getFloor(), name));
    }

    public Boolean canDeliver(int floor) {
        if (!this.availableFloor.contains(floor)) {
            return false;
        }
        return true;
    }

    public ArrayList<Integer> getAvailableFloor() {
        return (ArrayList<Integer>)this.availableFloor.clone();
    }
}
