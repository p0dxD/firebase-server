package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@SpringBootApplication
@RestController
public class ServerfirebaseApplication {
	//Stores current tokens
	private static HashMap<String, String> map;
	
	public static void main(String[] args) {
		SpringApplication.run(ServerfirebaseApplication.class, args);
		
		map = new HashMap<>();
		try {
			File file = new ClassPathResource("android-performance-dashboard-firebase-adminsdk-nunng-d0834e3400.json").getFile();
			FileInputStream serviceAccount =
					  new FileInputStream(file);

					FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
					  .setDatabaseUrl("https://android-performance-dashboard.firebaseio.com")
					  .build();

					FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found");
			e.printStackTrace();
		}
	}

    @RequestMapping(value="/token", method = RequestMethod.GET)
    public String token(@RequestHeader(value="token") String token, 
    		@RequestHeader(value="employeeId") String employeeId) {
    	// This registration token comes from the client FCM SDKs.
    	map.put(employeeId, token);
    	// Response is a message ID string.
    	System.out.println("Token set: " + map.get(employeeId));
        return "Token set: " + map.get(employeeId);
    }
    
    @RequestMapping(value="/gettoken/{employeeId}", method = RequestMethod.GET)
    public String getToken(@PathVariable(value="employeeId") String employeeId) {
        return map.get(employeeId);
    }
    
    @RequestMapping(value="/notify/{id}", method = RequestMethod.GET)
    public String message(@PathVariable(value="id") String id) {
    	// This registration token comes from the client FCM SDKs.
    	if (!map.containsKey(id)) {
    		return "";
    	}
    	String registrationToken = map.get(id);
    	// See documentation on defining a message payload.
    	Message message = Message.builder()
    	    .putData("message", "Job failed")
    	    .setToken(registrationToken)
    	    .build();

    	// Send a message to the device corresponding to the provided
    	// registration token.
    	String response = "";
		try {
			response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Successfully sent message: " + response);
		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Response failed: " + response);
			response = "failed";
		}
    	// Response is a message ID string.
    
        return response;
    }
}
