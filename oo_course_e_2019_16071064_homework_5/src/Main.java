import com.oocourse.TimableOutput;
import com.oocourse.elevator1.PersonRequest;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/1 13:36
 */
class Main {

    public static void output(String str) {
        if (false) {
            System.out.println(str);
        }
    }

    public static void main(String[] args) throws Exception {
        ArrayBlockingQueue<PersonRequest> queue =
            new ArrayBlockingQueue<PersonRequest>(30);
        Elevator elevator = new Elevator("ele1", 30);
        Scheduler scheduler = new Scheduler(queue);
        scheduler.addElevator(elevator);
        Submission submission = new Submission(queue);

        TimableOutput.initStartTimestamp();
        elevator.start();
        submission.start();
        scheduler.start();
    }
}
