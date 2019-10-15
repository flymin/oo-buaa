/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/5/15 23:04
 */
public class IntPair {
    private int valueX;
    private int valueY;

    IntPair(int valueX, int valueY) {
        this.valueX = valueX;
        this.valueY = valueY;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof IntPair) {
            IntPair another = (IntPair) obj;
            if (this.valueX == another.valueX
                && this.valueY == another.valueY) {
                return true;
            } else if (this.valueX == another.valueY
                && this.valueY == another.valueX) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        return this.valueX % 120 + this.valueY % 120;
    }
}
