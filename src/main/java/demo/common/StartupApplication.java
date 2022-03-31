package demo.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class StartupApplication implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
	private SchedulerUnit schedulerUnit;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
       // tomcat 실행 시 수행 할 작업
        
    }
}
