import com.oocourse.uml2.interact.AppRunner;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/29 9:22
 */
public class Main {
    public static void main(String[] args) throws Exception {
        AppRunner appRunner =
            AppRunner.newInstance(MyUmlGeneralInteraction.class);
        appRunner.run(args);
    }
}
