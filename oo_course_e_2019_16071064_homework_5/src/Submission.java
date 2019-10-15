import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

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
    private final ArrayBlockingQueue<PersonRequest> queue;

    public Submission(ArrayBlockingQueue<PersonRequest> queue) {
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
                Main.output(request.toString());
                try {
                    this.queue.put(request);
                } catch (InterruptedException e) {
                    yield();
                }
            }
        }
        try {
            elevatorInput.close();
            Main.output("Submission exit");
            Scheduler.setCanExit(true);
        } catch (IOException e) {
            //ignore
        }
    }
}
