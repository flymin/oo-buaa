import com.oocourse.specs1.AppRunner;


/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/30 16:45
 */
public class Main {
    public static void main(String[] args) throws Exception {
        AppRunner runner = AppRunner.newInstance(MyPath.class,
            MyPathContainer.class);
        //String[] strs = {"PATH_ADD 1 2 3 5\n" + "PATH_ADD 1 2 4 3 4\n"
        //        + "PATH_ADD 1 2 3 5\n" + "PATH_DISTINCT_NODE_COUNT 2\n"
        //        + "DISTINCT_NODE_COUNTCONTAINS_PATH 1 2 3\n"
        //        + "CONTAINS_PATH 1 2 3 5\n" + "PATH_REMOVE 1 2 4 3 4\n"
        //        + "DISTINCT_NODE_COUNT"};
        runner.run(args);
    }
}
