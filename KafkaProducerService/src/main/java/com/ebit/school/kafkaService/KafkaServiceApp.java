package com.ebit.school.kafkaService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ebit.school.Student;

@EnableDiscoveryClient
@SpringBootApplication
public class KafkaServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(KafkaServiceApp.class, args);		
		
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
	    
	    @Autowired
	    private KafkaTemplate<String, Student> kafkaTemplate;

	    private static final String TOPIC = "test-stream";

	    
	    @RequestMapping("/{applicationName}")
	    public @ResponseBody String getClientsByApplicationName(@PathVariable String applicationName) {
	        return this.discoveryClient.getInstances(applicationName).get(0).getUri().toString();
	    }
	    
	    @RequestMapping("/push/{name}")
	    public @ResponseBody String pushDataToKafka(@PathVariable("name") final String name) {
	    	kafkaTemplate.send(TOPIC, new Student(name));

	        return "Published successfully";
	    }
	    
	    @RequestMapping("/teachers")
	    public @ResponseBody String getTeachers() {
	    	ResponseEntity<String> teachers = restTemplate.exchange("http://TEACHERSERVICE/teachers", HttpMethod.GET, null, String.class);
	    	return teachers.getBody();
	    }
	}
}
