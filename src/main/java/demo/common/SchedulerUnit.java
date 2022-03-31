package demo.common;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.TaskScheduler;

@Component
public class SchedulerUnit {
    private Map<Integer, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    @Autowired
    private TaskScheduler TaskScheduler;

    public void Scheduler(int voteId, Date executeTime) {
        ScheduledFuture<?> task = TaskScheduler.schedule(
            ()->{
               // 스케줄러 작업
            }, executeTime);
        
        scheduledTasks.put(voteId, task);
    }

    public void remove(int voteId) {
        System.out.println(voteId + "  timeScheduler 를 종료합니다.");
        scheduledTasks.get(voteId).cancel(true);
    }
}