package demo.configuration;

import org.springframework.context.annotation.*;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
//ThreadPoolTaskExecutor java 5 환경에서만 사용가능, concurrentTaskExecutor로 변경 필요
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected Logger errorLogger = LoggerFactory.getLogger("error");

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Spring_Executor-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.initialize();
        return new HandlingExecutor(taskExecutor); // HandlingExecutor로 wrapping 합니다.
        // return taskExecutor;
    }

    @Bean(name = "singleThreadPoolTaskExecutor")
    public Executor singleThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return new HandlingExecutor(taskExecutor); // HandlingExecutor로 wrapping 합니다.
        // return taskExecutor;
    }

    @Bean(name = "libSnarkThreadPoolTaskExecutor")
    public Executor libSnarkThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor libSnarkExecutor = new ThreadPoolTaskExecutor();
        libSnarkExecutor.setCorePoolSize(1);
        libSnarkExecutor.setMaxPoolSize(1);
        libSnarkExecutor.setQueueCapacity(10000);
        libSnarkExecutor.setThreadNamePrefix("LibSnark_Executor-");
        libSnarkExecutor.setWaitForTasksToCompleteOnShutdown(true);
        libSnarkExecutor.initialize();
        return new HandlingExecutor(libSnarkExecutor);// HandlingExecutor로 wrapping 합니다.
        // return taskExecutor;
    }

    public class HandlingExecutor implements AsyncTaskExecutor {
        private AsyncTaskExecutor executor;

        public HandlingExecutor(AsyncTaskExecutor executor) {
            this.executor = executor;
        }

        @Override
        public void execute(Runnable task) {
            executor.execute(createWrappedRunnable(task));
        }

        @Override
        public void execute(Runnable task, long startTimeout) {
            executor.execute(createWrappedRunnable(task), startTimeout);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return executor.submit(createWrappedRunnable(task));
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return executor.submit(createCallable(task));
        }

        private <T> Callable<T> createCallable(final Callable<T> task) {
            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    try {
                        return task.call();
                    } catch (Exception ex) {
                        handle(ex);
                        throw ex;
                    }
                }
            };
        }

        private Runnable createWrappedRunnable(final Runnable task) {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        task.run();
                    } catch (Exception ex) {
                        handle(ex);
                    }
                }
            };
        }

        private void handle(Exception ex) {
            errorLogger.info("Failed to execute task. : {}", ex.getMessage());
            errorLogger.error("Failed to execute task. ",ex);
        }

        // public void destroy() {
        //     if(executor instanceof ThreadPoolTaskExecutor){
        //         ((ThreadPoolTaskExecutor) executor).shutdown();
        //     }
        // }
    }

}