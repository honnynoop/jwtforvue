package com.ssafy.edu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ssafy.edu.service.BoardFileService;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ssafy.edu"})
public class BoardApiApplication implements CommandLineRunner{
	@Resource
	private  BoardFileService storageService;
	
	public static void main(String[] args) {
		SpringApplication.run(BoardApiApplication.class, args);
	}
    @Override
    public void run(String... arg) throws Exception {
    	storageService.init();
    }
}
