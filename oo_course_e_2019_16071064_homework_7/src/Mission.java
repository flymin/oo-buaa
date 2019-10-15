import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/16 10:33
 */
public class Mission {
    private PersonRequest request;
    /**
     * true表示当前送达任务结束
     * false表示当前送达需要换乘
     */
    private Boolean finalStep;
    private ArrayList<String> fromElevator;
    private ArrayList<String> toElevator;
    private Integer fromFloor;
    private Integer toFloor;

    Mission(PersonRequest request) {
        this.toElevator =
            Main.getScheduler().directDeliver(request.getFromFloor(),
            request.getToFloor());
        if (this.toElevator != null) {
            this.finalStep = true;
            this.fromElevator = null;
            this.toFloor = request.getToFloor();
        } else {
            this.finalStep = false;
            this.fromElevator =
                Main.getScheduler().whichElevator(request.getFromFloor());
            this.toFloor = null;
            this.toElevator =
            Main.getScheduler().whichElevator(request.getToFloor());
        }
        this.request = request;
        this.fromFloor = request.getFromFloor();
    }

    public synchronized void reDeter() {
        this.toElevator =
            Main.getScheduler().directDeliver(this.fromFloor, this.toFloor);
        this.fromElevator = null;
    }

    public synchronized int getFromFloor() {
        return fromFloor;
    }

    public synchronized int getToFloor() {
        return toFloor;
    }

    public int getPersonId() {
        return this.request.getPersonId();
    }

    /**
     * 返回+判断
     * 需要换乘：返回目标电梯选项列表
     * 不需要换乘：返回null
     * @return
     */
    public synchronized ArrayList<String> getTransToElevator() {
        if (this.finalStep) {
            return null;
        } else {
            return (ArrayList<String>)this.toElevator.clone();
        }
    }

    /**
     * 返回当前需要上的电梯选项，由调度器具体判断上哪个
     * @return
     */
    public synchronized ArrayList<String> getElevatorChoice() {
        if (this.finalStep) {
            return (ArrayList<String>)this.toElevator.clone();
        } else {
            return (ArrayList<String>)this.fromElevator.clone();
        }
    }

    /**
     * 到达换乘点：切换起点、目的地，返回false
     * 到达终点：返回true
     * 外部：true任务结束，false则交还给调度器
     * @return isFinish
     */
    public synchronized Boolean isFinish() {
        // 保证之前设置过换乘点或者直达
        assert this.toFloor != null;
        if (!this.finalStep) {
            // 换乘情况
            this.fromFloor = this.toFloor;
            this.toFloor = this.request.getToFloor();
            this.finalStep = true;
            return false;
        } else {
            // 到达终点
            return true;
        }
    }

    /**
     * 设置换乘点，由scheduler完成
     * @param floor
     */
    public synchronized void setTransferFloor(int floor) {
        assert !this.finalStep;
        this.toFloor = floor;
    }

    @Override public String toString() {
        String result = "";
        result += this.request.toString() + ";";
        result += "finalStep:" + this.finalStep.toString() + ";";
        if (fromElevator == null) {
            result += "fromElevator: null;";
        } else {
            result += "fromElevator:" + this.fromElevator.toString() + ";";
        }
        if (this.toElevator == null) {
            result += "toElevator: null;";
        } else {
            result += "toElevator:" + this.toElevator.toString() + ";";
        }
        result += "fromFloor:" + this.fromFloor.toString() + ";";
        if (this.toFloor == null) {
            result += "toFloor: null;";
        } else {
            result += "toFloor:" + this.toFloor.toString() + ";";
        }
        return result;
    }
}
