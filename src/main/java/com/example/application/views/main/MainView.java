package com.example.application.views.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.example.application.views.icons.CustomIcon;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinService;

@JsModule("icons/custom-iconset.js")
@PageTitle("Travart Online")
public class MainView extends AppLayout {
	/**
	 * 
	 */
	private VerticalLayout vert = new VerticalLayout();
	private Button tog = new Button();
	private Icon sun= new Icon(VaadinIcon.SUN_O);
	private Icon moon= new Icon(VaadinIcon.MOON_O);
	private boolean darkMode;
	
	private static final String COOKIE_THEME= "theme";
	private static final String THEME_DARK="dark";
	private static final String THEME_LIGHT="light";
	private static final int COOKIE_MAX_AGE=Integer.MAX_VALUE;
	
	private static final long serialVersionUID = 4920006999153529869L;
	private Map<String,Cookie> cookies=new HashMap<>(); 

	MainView() {		
		initCookies(cookies);
		createHeader();
		
		sun.setSize("30px");
		moon.setSize("30px");
	}
	
	private void initCookies(Map<String,Cookie> cooks) {
		for(Cookie cookie:VaadinService.getCurrentRequest().getCookies()) {
			cooks.put(cookie.getName(),cookie);
		}
		cooks.putIfAbsent(COOKIE_THEME, new Cookie(COOKIE_THEME,THEME_LIGHT));
		initTheme(cooks.get(COOKIE_THEME));
	}
	
	private void updateCookie(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
		// Make cookie expire in 2 minutes
		cookie.setMaxAge(COOKIE_MAX_AGE);

		// Set the cookie path.
		cookie.setPath(VaadinService.getCurrentRequest().getContextPath());

		// Save cookie
		VaadinService.getCurrentResponse().addCookie(cookie);
	}
	
	private void initTheme(Cookie cookie) {
		darkMode=cookie.getValue().equals(THEME_DARK);
		setDarkTheme(darkMode);
	}

	private void createHeader() {
		Icon image = CustomIcon.LOGO.create();
		image.setSize("60px");
		H1 banner = new H1("Travart Online");
		banner.getStyle().set("font-size", "var(--lumo-font-size-xxl)").set("margin", "0");
		banner.setWidth("30%");
		HorizontalLayout header = new HorizontalLayout(/*new DrawerToggle(),*/ image, banner,getThemeButton());
		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		
		header.setWidth("100%");
		header.addClassNames("py-0", "px-m","ml-l","mt-0","mb-0","mr-l");
		header.setMargin(true);
		addToNavbar(header);
	}

	private VerticalLayout getThemeButton() {
		tog.addThemeVariants(ButtonVariant.LUMO_ICON,ButtonVariant.LUMO_TERTIARY_INLINE);
		tog.addClickListener(evt -> setDarkTheme(darkMode));
		vert.setAlignItems(Alignment.END);
		vert.add(tog);
		vert.setMargin(false);
		return vert;
	}
	
	/**
	 * Sets theme  for the page. True sets dark theme, False sets light theme.
	 * @param darkMode2 
	 * @param b
	 */
	private void setDarkTheme(boolean dark) {
		Page page = UI.getCurrent().getPage();
		Cookie themeCookie=cookies.get(COOKIE_THEME);
		if (dark) {
			page.executeJs("document.querySelector('html').setAttribute(\"theme\",\"dark\")");
			themeCookie.setValue(THEME_DARK);
			tog.setIcon(sun);
		} else {
			page.executeJs("document.querySelector('html').setAttribute(\"theme\",\"light\")");
			themeCookie.setValue(THEME_LIGHT);
			tog.setIcon(moon);
		}
		darkMode=!darkMode;
		updateCookie(themeCookie);
	}

}
