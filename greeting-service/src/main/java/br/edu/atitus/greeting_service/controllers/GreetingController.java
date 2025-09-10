package br.edu.atitus.greeting_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.greeting_service.configs.GreetingConfig;
import java.util.Map;

@RestController
@RequestMapping("greeting")
public class GreetingController {
	
//	@Value("${greeting-service.greeting}")
//	private String greeting;
//	@Value("${greeting-service.default-name}")
//	private String defaultName;
	
	private final GreetingConfig config;
	
	public GreetingController(GreetingConfig config) {
		super();
		this.config = config;
	}

	@GetMapping
	public ResponseEntity<String> greet(@RequestParam(required = false) String name){
		String greetingReturn = config.getGreeting(); //Era greeting
		String nameReturn = name != null ? name : config.getDefaultName(); //Era defaultName
		String textReturn = String.format("%s,  %s !!!", greetingReturn, nameReturn);
		
		return ResponseEntity.ok(textReturn);
	}
	
    @GetMapping("/{name}")
    public ResponseEntity<String> greetPath(@PathVariable String name) {
        String textReturn = String.format("%s, %s !!!", config.getGreeting(), name);
        return ResponseEntity.ok(textReturn);
    }

    
    @PostMapping
    public ResponseEntity<String> greetPost(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", config.getDefaultName());
        String textReturn = String.format("%s, %s !!!", config.getGreeting(), name);
        return ResponseEntity.ok(textReturn);
    }
}