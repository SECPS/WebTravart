package com.example.application.views.main;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
	private Tabs tabs;

	public MainLayout() {
		createHeader();
		createDrawer();
		
	}

	private void createHeader() {
		H1 logo = new H1("Travart Online");
		logo.addClassNames("text-l", "m-m");

		HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		header.setWidth("100%");
		header.addClassNames("py-0", "px-m");

		addToNavbar(header);

	}

	private Tabs getTabs() {
		if (tabs == null)
			tabs = new Tabs();
		tabs.add(createTab(VaadinIcon.HOME, "Home"),
				createTab(VaadinIcon.MAGIC, "Converter"),
				createTab(VaadinIcon.SCALE, "Impressum"));
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		return tabs;
	}

	private Tab createTab(VaadinIcon viewIcon, String viewName) {
		Icon icon = viewIcon.create();
		icon.getStyle().set("box-sizing", "border-box").set("margin-inline-end", "var(--lumo-space-m)")
				.set("margin-inline-start", "var(--lumo-space-xs)").set("padding", "var(--lumo-space-xs)");
		Class<? extends VerticalLayout> c=null;
		switch(viewName) {
		case "Home": c=HomeView.class;break;
		case "Converter": c=UvlUpload.class;break;
		case "Impressum": c=ImpressumView.class;break;
		default:
			c=HomeView.class;
		}
		RouterLink link = new RouterLink("",c);
		link.add(icon, new Span(viewName));
		// Demo has no routes
		// link.setRoute(viewClass.java);
		link.setTabIndex(0);

		return new Tab(link);
	}

	private void createDrawer() {
//		RouterLink homeView = new RouterLink("Home", HomeView.class);
//		homeView.setHighlightCondition(HighlightConditions.sameLocation());
//
//		RouterLink uploadLink = new RouterLink("UVL Upload", UvlUpload.class);
//		uploadLink.setHighlightCondition(HighlightConditions.sameLocation());
//
//		RouterLink impressumLink = new RouterLink("Impressum", ImpressumView.class);
//		impressumLink.setHighlightCondition(HighlightConditions.sameLocation());

//		addToDrawer(new VerticalLayout(homeView, uploadLink, impressumLink));
		addToDrawer(getTabs());
	}
}
