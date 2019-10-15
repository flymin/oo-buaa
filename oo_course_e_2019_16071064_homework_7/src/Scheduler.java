import shareclass.ElevatorState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;

/**
 * 应用模块名称<p>
 * 代码描述<p>调度器负责管理电梯，统一视图，任务不可直接访问电梯信息</p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/4/1 21:13
 */
public class Scheduler extends Thread {
    private Boolean canExit = false;
    private ArrayList<Mission> queue;
    private HashMap<String, ElevatorState> statesMap;
    private HashMap<String, Vector<Mission>> missionMap;
    private Object idleLock;

    public Scheduler(ArrayList<Mission> queue, Object lock) {
        this.queue = queue;
        statesMap = new HashMap<>();
        missionMap = new HashMap<>();
        this.idleLock = lock;
    }

    public void addElevator(Elevator ele) {
        statesMap.put(ele.getName(), ele.getElevatorState());
        missionMap.put(ele.getName(), ele.getElevatorMission());
    }

    @Override public void run() {
        Mission request = null;
        Boolean exit = false;
        while (true) {
            synchronized (this.queue) {
                while (this.queue.isEmpty()) {
                    if (canExit) {
                        exit = true;
                        break;
                    } else {
                        Main.output("Scheduler trying to take");
                        try {
                            this.queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (exit) {
                    break;
                } else {
                    Main.output("Scheduler can take");
                    request = this.queue.get(0);
                    this.queue.remove(request);
                    Main.output("Scheduler taken, notify submission");
                    this.queue.notifyAll();
                }
            }
            addMission(request);
        }
        Boolean idle = true;
        while (true) {
            idle = true;
            for (ElevatorState state : this.statesMap.values()) {
                idle &= state.isIdle();
            }
            if (!idle) {
                synchronized (this.idleLock) {
                    try {
                        this.idleLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                break;
            }
        }
        Main.output("Scheduler exit");
        Elevator.setCanExit();
        for (Vector<Mission> missionQueue :
            missionMap.values()) {
            synchronized (missionQueue) {
                missionQueue.notifyAll();
            }
        }
    }

    private void addMission(Mission mission) {
        String name = this.determine(mission);
        Main.output("determine for " + name);
        Vector<Mission> missions = missionMap.get(name);
        synchronized (missions) {
            missions.add(mission);
            Main.output("Scheduler notify elevator");
            missions.notifyAll();
        }
    }

    /**
     * 考虑：电梯运行状态+电梯选项+目标电梯
     *      写入Mission：目标换乘楼层（需要换乘时）
     *      1. 如果选择只有一个，直接上
     *      2. 如果选择有多个，基于“换乘点是一致的”的条件
     *         分支树判定（方向、捎带、远近、人多少）
     * @param request
     * @return 目标电梯名
     */
    private String determine(Mission request) {
        Main.output(request.toString());
        Boolean goingUp;
        String resultEle;
        ArrayList<String> choice = request.getElevatorChoice();
        if (choice.size() == 1) {
            resultEle = choice.get(0);
        } else {
            ArrayList<String> result = new ArrayList<>();
            for (String eleName : choice) {
                ElevatorState state = this.statesMap.get(eleName);
                goingUp = state.getGoingUp();
                if (goingUp && state.getFloor() < request.getFromFloor() ||
                    (!goingUp && state.getFloor() > request.getFromFloor())) {
                    result.add(eleName);
                }
            }

            // 没有可捎带的情况
            if (result.size() == 0) {
                result = choice;
            }
            if (result.size() > 1) {
                resultEle = findMinPeople(result);
            } else {
                resultEle = result.get(0);
            }
        }
        this.getTransFloor(request, resultEle);
        return resultEle;
    }

    private String findMinPeople(ArrayList<String> list) {
        String result = list.get(0);
        int people = Integer.MAX_VALUE;
        for (String eleName : list) {
            int peopleNum = this.statesMap.get(eleName).getCurrentPeople();
            if (peopleNum < people) {
                people = peopleNum;
                result = eleName;
            }
        }
        return result;
    }

    private void getTransFloor(Mission mission, String fromEle) {
        //运行方向根据电梯当前位置与人位置（目标位置）决定
        ElevatorState stateFrom = this.statesMap.get(fromEle);
        //TODO 目前只取电梯to队列中的第一个
        ArrayList<String> toList = mission.getTransToElevator();
        if (toList == null) {
            return;
        }
        ElevatorState stateTo = this.statesMap.get(toList.get(0));
        Set<Integer> transferA = new HashSet<>();
        Set<Integer> transferB = new HashSet<>();
        transferA.addAll(stateFrom.getAvailableFloor());
        transferB.addAll(stateTo.getAvailableFloor());
        transferA.retainAll(transferB);
        int resultFloor = transferA.iterator().next();
        int floorDiff = Math.abs(resultFloor - mission.getFromFloor());
        for (int floor : transferA) {
            if (Math.abs(floor - mission.getFromFloor()) < floorDiff) {
                floorDiff = Math.abs(floor - mission.getFromFloor());
                resultFloor = floor;
            }
        }
        mission.setTransferFloor(resultFloor);
    }

    /**
     * 由电梯调用，在任务完成时归还任务
     * 需要missions锁，要求电梯不占有锁
     * 既然调用此函数，一定是操作其他电梯的mission列表
     * @param mission
     */
    public void returnMission(Mission mission) {
        if (!mission.isFinish()) {
            mission.reDeter();
            addMission(mission);
        }
    }

    public ArrayList<String> directDeliver(int from, int to) {
        Set<String> fromSet = new HashSet<>();
        Set<String> toSet = new HashSet<>();
        fromSet.addAll(whichElevator(from));
        toSet.addAll(whichElevator(to));
        Main.output("from:" + from + "to:" + to +
            fromSet.toString() + toSet.toString());
        fromSet.retainAll(toSet);
        if (fromSet.size() == 0) {
            return null;
        } else {
            ArrayList<String> result = new ArrayList<>();
            result.addAll(fromSet);
            return result;
        }
    }

    public ArrayList<String> whichElevator(int floor) {
        ArrayList<String> result = new ArrayList<>();
        for (String eleName : statesMap.keySet()) {
            if (statesMap.get(eleName).canDeliver(floor)) {
                result.add(eleName);
            }
        }
        return result;
    }

    public void setCanExit() {
        canExit = true;
    }
}
