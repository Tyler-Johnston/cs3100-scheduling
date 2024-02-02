import java.util.LinkedList;
import java.util.Queue;

public class SchedulerRR extends Scheduler {

    int timeQuantum;
    Logger logger;
    Queue<Process> queue = new LinkedList<>();
    public SchedulerRR(Logger logger, int timeQuantum) {
        this.logger = logger;
        this.timeQuantum = timeQuantum;
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
                contextSwitches ++; // changing to a new process
                queue.add(process);
                process = queue.poll();
                contextSwitches++; // a process is finishing
                logger.log("CPU " + cpu + " > Scheduled " + process.getName());
            }
            else if (process.getElapsedBurst() % timeQuantum == 0) {
                logger.log("Time quantum complete for process " + process.getName());
                contextSwitches++; // changing to a new process
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
