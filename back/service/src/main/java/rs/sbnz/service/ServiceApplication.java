package rs.sbnz.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import rs.sbnz.service.util.DevServerInitializer;

@SpringBootApplication
@ComponentScan(basePackages = { "rs.sbnz.*" })
@EntityScan("rs.sbnz.*")
@EnableJpaRepositories("rs.sbnz.*") 
public class ServiceApplication extends SpringBootServletInitializer {
    @Autowired public DevServerInitializer devServerInitializer;

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

    @PostConstruct
    private void onInit() {
        System.out.println("Initializing data...");
        devServerInitializer.initData();
        System.out.println("Initializing data... DONE");
    }
}
