package com.tp.album;

import com.tp.album.console.AdminConsoleMenu;
import com.tp.album.service.impl.AlbumService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AlbumApp {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AlbumApp.class, args);

		AlbumService albumService = context.getBean(AlbumService.class);
		AdminConsoleMenu menu = new AdminConsoleMenu(albumService);
		menu.mostrarMenu();
	}

}
