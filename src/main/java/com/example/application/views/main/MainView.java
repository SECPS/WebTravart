package com.example.application.views.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.example.application.views.icons.CustomIcon;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;

@JsModule("icons/custom-iconset.js")
@PageTitle("Travart Online")
public class MainView extends AppLayout {
	/**
	 * 
	 */
	private VerticalLayout vert = new VerticalLayout();
	private HorizontalLayout hor=new HorizontalLayout();
	private ToggleButton tog = new ToggleButton();
	private Icon sun= new Icon(VaadinIcon.SUN_O);
	private Icon moon= new Icon(VaadinIcon.MOON_O);
	
	private static final String COOKIE_THEME= "theme";
	private static final String THEME_DARK="dark";
	private static final String THEME_LIGHT="light";
	private static final int COOKIE_MAX_AGE=Integer.MAX_VALUE;
	
	private static final long serialVersionUID = 4920006999153529869L;
	private Tabs tabs;
	private Map<String,Cookie> cookies=new HashMap<>(); 

	MainView() {		
		initCookies(cookies);
		createHeader();
		createDrawer();
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
		setDarkTheme(cookie.getValue().equals(THEME_DARK));
	}

	private void createHeader() {
		Icon image = CustomIcon.LOGO.create();

		H1 banner = new H1("Travart Online");
		banner.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

		HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), image, banner);

		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		header.setWidth("100%");
		header.addClassNames("py-0", "px-m");
		addToNavbar(header);

	}

	private Tabs getTabs() {
		if (tabs == null)
			tabs = new Tabs();
		tabs.add(createTab(VaadinIcon.HOME, "Home"), createTab(CustomIcon.LOGO, "Converter"),
				createTab(VaadinIcon.SCALE, "Impressum"));
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.addSelectedChangeListener(e->setDrawerOpened(false));
		return tabs;
	}

	private Tab createTab(IconFactory viewIcon, String viewName) {
		Icon icon = viewIcon.create();
		icon.getStyle().set("box-sizing", "border-box").set("margin-inline-end", "var(--lumo-space-m)")
				.set("margin-inline-start", "var(--lumo-space-xs)").set("padding", "var(--lumo-space-xs)");
		Class<? extends VerticalLayout> c = null;
		switch (viewName) {
		case "Home":
			c = HomeView.class;
			break;
		case "Converter":
			c = ConvertView.class;
			break;
		case "Impressum":
			c = ImpressumView.class;
			break;
		default:
			c = HomeView.class;
		}
		RouterLink link = new RouterLink("", c);
		link.add(icon, new Span(viewName));
		link.setTabIndex(0);

		return new Tab(link);
	}

	private void createDrawer() {
		addToDrawer(getTabs());
		addToDrawer(getThemeButton());
	}

	private VerticalLayout getThemeButton() {
		vert.setHeight("80%");
		vert.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		tog.setLabel("Change theme");
		tog.addValueChangeListener(evt -> setDarkTheme(evt.getValue()));
		hor.add(tog);
		vert.add(hor);
		return vert;
	}
	
	/**
	 * Sets theme  for the page. True sets dark theme, False sets light theme.
	 * @param b
	 */
	private void setDarkTheme(boolean b) {
		Page page = UI.getCurrent().getPage();
		Cookie themeCookie=cookies.get(COOKIE_THEME);
		if (b) {
			page.executeJs("document.querySelector('html').setAttribute(\"theme\",\"dark\")");
			hor.remove(moon);
			hor.addComponentAsFirst(sun);
			themeCookie.setValue(THEME_DARK);
			tog.setValue(true);
		} else {
			page.executeJs("document.querySelector('html').setAttribute(\"theme\",\"light\")");
			hor.remove(sun);
			hor.addComponentAsFirst(moon);
			themeCookie.setValue(THEME_LIGHT);
		}
		updateCookie(themeCookie);
	}

}
