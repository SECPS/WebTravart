package com.example.application;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
@PWA(name = "TraVarT Online", shortName = "TraVarT Online", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@CssImport(value="./styles/global.css", themeFor="vaadin-grid-sorter")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5170116799200123108L;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
