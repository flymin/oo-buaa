package shareclass;

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
    private int floor;

    public ElevatorState() {
        this.floor = 1;
    }

    public synchronized int getFloor() {
        return this.floor;
    }

    public synchronized void setFloor(int floor) {
        this.floor = floor;
    }
}
