package com.bartz.analyzer;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BartzAnalyserApplication {

	public static void main(String[] args) {
		// Esta é a ponte necessária para rodar Spring + JavaFX
		Application.launch(JavafxApplication.class, args);
	}
}
