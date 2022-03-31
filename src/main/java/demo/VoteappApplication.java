package demo;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VoteappApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(VoteappApplication.class);
	}

	public static void main(String[] args) throws LifecycleException {
		SpringApplication.run(VoteappApplication.class, args);

		// Tomcat tomcat = new Tomcat();
        // tomcat.setPort(8080);

        // Context context = tomcat.addContext("/", "/");

        // tomcat.start();

		// System.out.println(" Server started ") ;
	}

}
