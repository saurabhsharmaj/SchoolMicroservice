package com.ebit.school.studentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ebit.school.Student;

@EnableDiscoveryClient
@SpringBootApplication
public class StudentServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(StudentServiceApp.class, args);		
		
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
	    return new RestTemplate();
	}

	@RestController
	class StudentController {

	    @Autowired
	    private DiscoveryClient discoveryClient;

	    @Autowired
	    @LoadBalanced
	    RestTemplate restTemplate;
	    
	    @RequestMapping("/{applicationName}")
	    public @ResponseBody String getClientsByApplicationName(@PathVariable String applicationName) {
	        return this.discoveryClient.getInstances(applicationName).get(0).getUri().toString();
	    }
	    
	    @RequestMapping("/students")
	    public @ResponseBody String getStudents() {
	        return new Student().toString();
	    }
	    
	    @RequestMapping("/teachers")
	    public @ResponseBody String getTeachers() {
	    	ResponseEntity<String> teachers = restTemplate.exchange("http://TEACHERSERVICE/teachers", HttpMethod.GET, null, String.class);
	    	return teachers.getBody();
	    }
	}
}
