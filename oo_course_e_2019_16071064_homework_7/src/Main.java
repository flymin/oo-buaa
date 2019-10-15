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
 * @since 2019/4/1 13:36
 */
class Main {
    private static Scheduler scheduler;

    public static void output(String str) {
        if (false) {
            System.err.println(str);
        }
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Object lock = new Object();
        ArrayList<Mission> queue = new ArrayList<>(40);
        Main.scheduler = new Scheduler(queue, lock);
        List listA = Arrays.asList(-3,-2,-1,1,15,16,17,18,19,20);

        List listB = Arrays.asList(-2,-1,1,2,4,5,6,7,8,9,10,11,12,13,14,15);

        List listC = Arrays.asList(1,3,5,7,9,11,13,15);

        Elevator elevatorA = new Elevator("A", 40, listA, 400, 6, lock);
        Main.scheduler.addElevator(elevatorA);
        Elevator elevatorB = new Elevator("B", 40, listB, 500, 8, lock);
        Main.scheduler.addElevator(elevatorB);
        Elevator elevatorC = new Elevator("C", 40, listC, 600, 7, lock);
        Main.scheduler.addElevator(elevatorC);

        elevatorA.start();
        elevatorB.start();
        elevatorC.start();
        Submission submission = new Submission(queue);
        submission.start();
        Main.scheduler.start();
    }
}
