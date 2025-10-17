package com.tp.album;

import com.tp.album.console.AdminConsoleMenu;
import com.tp.album.service.impl.AlbumService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AlbumApp {

	public static void main(String[] args) {
		SpringApplication.run(AlbumApp.class, args);
	}

}
