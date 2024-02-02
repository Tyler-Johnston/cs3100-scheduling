import java.util.Comparator;
import java.util.PriorityQueue;

public class SchedulerSJF extends Scheduler{

    Logger logger;
    PriorityQueue<Process> queue = new PriorityQueue<>(1, new CompareProcessSJF());
    public SchedulerSJF(Logger logger) {
        this.logger = logger;
    }

    @Override
    void notifyNewProcess(Process process) {
        queue.add(process);
    }

    @Override
    Process update(Process process, int cpu) {
        if (process != null) {
            if (process.isExecutionComplete()) {
                logger.log("CPU " + cpu + " > Process " + process.getName() + " Burst Complete");
                logger.log("CPU " + cpu + " > Process " + process.getName() + " Execution Complete");
                contextSwitches ++; // changing to a new process
                process = queue.poll();
                if (process != null) {
                    contextSwitches++; // a process is finishing
                    logger.log("CPU " + cpu + " > Scheduled " + process.getName());
                }
            }
            else if (process.isBurstComplete()) {
                logger.log("CPU " + cpu + " > Process " + process.getName() + " Burst Complete");
                contextSwitches++; // changing to a new process & a process is finishing
                queue.add(process);
                process = queue.poll();
                contextSwitches++; // a process is finishing
                logger.log("CPU " + cpu + " > Scheduled " + process.getName());
            }
        }
        if (process == null) {
            process = queue.poll();
            if (process != null) {
                logger.log("CPU " + cpu + " > Scheduled " + process.getName());
                contextSwitches++; // just scheduling 1 new process, only 1 context switch added
            }
        }
        return process;
    }
}

class CompareProcessSJF implements Comparator<Process> {
    public int compare(Process p1, Process p2) {

        if (p1.getBurstTime() < p2.getBurstTime()) {
            return -1;
        }
        else if (p1.getBurstTime() > p2.getBurstTime()) {
            return 1;
        }
        // Process 1 burst time == Process 2 burst time
        return 0;
    }
}
