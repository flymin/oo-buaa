import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/1 19:58
 */
public class Submission extends Thread {
    private ElevatorInput elevatorInput;
    private ArrayList<Mission> queue;

    public Submission(ArrayList<Mission> queue) {
        super();
        this.queue = queue;
        this.elevatorInput = new ElevatorInput(System.in);
    }

    @Override
    public void run() {
        while (true) {
            Main.output("mission iter");
            PersonRequest request;
            this.elevatorInput.hashCode();
            request = this.elevatorInput.nextPersonRequest();
            if (request == null) {
                break;
            } else {
                Main.output("mission want lock");
                synchronized (this.queue) {
                    Main.output(request.toString());
                    Main.output("mission put");
                    Mission mission = new Mission(request);
                    this.queue.add(mission);
                    this.queue.notifyAll();
                }
            }
        }
        try {
            elevatorInput.close();
            Main.output("Submission exit");
            synchronized (this.queue) {
                Main.getScheduler().setCanExit();
                this.queue.notifyAll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
