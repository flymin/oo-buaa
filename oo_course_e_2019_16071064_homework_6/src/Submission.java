import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;

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
    private ArrayList<PersonRequest> queue;

    public Submission(ArrayList<PersonRequest> queue) {
        super();
        this.queue = queue;
        this.elevatorInput = new ElevatorInput(System.in);
    }

    @Override
    public void run() {
        while (true) {
            PersonRequest request = this.elevatorInput.nextPersonRequest();
            if (request == null) {
                break;
            } else {
                synchronized (this.queue) {
                    Main.output(request.toString());
                    Main.output("mission put");
                    this.queue.add(request);
                    this.queue.notifyAll();

                }
            }
        }
        try {
            elevatorInput.close();
            Main.output("Submission exit");
            synchronized (this.queue) {
                Scheduler.setCanExit();
                this.queue.notifyAll();
            }
        } catch (IOException e) {
            //ignore
        }
    }
}
