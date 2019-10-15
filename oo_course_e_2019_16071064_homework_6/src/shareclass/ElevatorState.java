package shareclass;

import com.oocourse.TimableOutput;

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
    private Boolean goingUp;

    public ElevatorState() {
        this.floors = new ArrayList<>();
        List list = Arrays.asList(-3,-2,-1,
            1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16);
        this.floors.addAll(list);
        this.currentFloor = 3;
        this.targetFloor = 3;
        this.goingUp = true;
    }

    public synchronized int getFloor() {
        return this.floors.get(this.currentFloor);
    }

    public synchronized void setTargetFloor(int target) {
        if (!this.floors.contains(target)) {
            throw new RuntimeException("不存在此楼层");
        }
        this.targetFloor = this.floors.indexOf(target);
        if (this.targetFloor > this.currentFloor) {
            this.goingUp = true;
        } else {
            this.goingUp = false;
        }
    }

    public Boolean getGoingUp() {
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

    public synchronized void moveDireciton() throws Exception {
        if (this.goingUp) {
            this.currentFloor++;
            if (this.currentFloor > this.floors.size()) {
                throw new RuntimeException("电梯运行超过上限");
            }
        } else {
            this.currentFloor--;
            if (this.currentFloor < 0) {
                throw new RuntimeException("电梯运行超过下限");
            }
        }
        TimableOutput.println(
            String.format("ARRIVE-%d", this.getFloor()));
    }
}
