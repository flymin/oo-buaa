import com.oocourse.TimableOutput;
import com.oocourse.elevator1.PersonRequest;
import shareclass.ElevatorState;

import java.util.concurrent.ArrayBlockingQueue;

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
    private ArrayBlockingQueue<PersonRequest> elevatorMission;
    private Boolean running;

    Elevator(String str, int capacity) {
        super(str);
        this.elevatorState = new ElevatorState();
        this.elevatorMission =
            new ArrayBlockingQueue<PersonRequest>(capacity);
        this.running = false;
    }

    @Override public void run() {
        PersonRequest request;
        while (true) {
            if (this.elevatorMission.isEmpty()) {
                if (canExit) {
                    break;
                } else {
                    yield();
                }
            } else {
                try {
                    request = this.elevatorMission.take();
                    synchronized (this.running) {
                        this.running = true;
                    }
                    this.go(request);
                    synchronized (this.running) {
                        this.running = false;
                    }
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        Main.output(this.getName() + " exit");
    }

    private void go(PersonRequest request) {
        if (this.elevatorState.getFloor() == request.getFromFloor()) {
            this.letIn(request.getFromFloor(), request.getPersonId());
        } else {
            this.moveAndGet(request.getFromFloor(), request.getPersonId());
        }
        if (request.getFromFloor() == request.getToFloor()) {
            this.letOut(request.getToFloor(), request.getPersonId());
        }
        this.send(request.getToFloor(), request.getPersonId());
    }

    private void move(int floor) {
        int diff = Math.abs(floor - this.elevatorState.getFloor());
        try {
            sleep(500 * diff);
        } catch (InterruptedException e) {
            //ignore
        }
        this.elevatorState.setFloor(floor);
    }

    private void send(int floor, int id) {
        this.move(floor);
        this.letOut(floor, id);
    }

    private void moveAndGet(int floor, int id) {
        this.move(floor);
        this.letIn(floor, id);
    }

    private void letIn(int floor, int id) {
        TimableOutput.println(
            String.format("OPEN-%d", floor));
        try {
            sleep(250);
        } catch (InterruptedException e) {
            //ignore
        }
        TimableOutput.println(
            String.format("IN-%d-%d", id, floor));
        try {
            sleep(250);
        } catch (InterruptedException e) {
            // ignore
        }
        TimableOutput.println(
            String.format("CLOSE-%d", floor));
    }

    private void letOut(int floor, int id) {
        TimableOutput.println(
            String.format("OPEN-%d", floor));
        try {
            sleep(250);
        } catch (InterruptedException e) {
            //ignore
        }
        TimableOutput.println(
            String.format("OUT-%d-%d", id, floor));
        try {
            sleep(250);
        } catch (InterruptedException e) {
            // ignore
        }
        TimableOutput.println(
            String.format("CLOSE-%d", floor));
    }

    public ElevatorState getElevatorState() {
        return this.elevatorState;
    }

    public ArrayBlockingQueue<PersonRequest> getElevatorMission() {
        return this.elevatorMission;
    }

    public synchronized Boolean isRunning() {
        return this.running;
    }

    public static void setCanExit(Boolean canExit) {
        Elevator.canExit = canExit;
    }
}
