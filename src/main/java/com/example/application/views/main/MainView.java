package com.example.application.views.main;


import com.example.application.views.icons.CustomIcon;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;

@JsModule("icons/custom-iconset.js")
@PageTitle("Travart Online")
public class MainView extends AppLayout{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4920006999153529869L;
	private Tabs tabs;
	
	MainView(){
		createHeader();
		createDrawer();
	}
	
	private void createHeader() {
		Icon image = CustomIcon.LOGO.create();
		
		H1 banner = new H1("Travart Online");
		banner.getStyle()
	      .set("font-size", "var(--lumo-font-size-l)")
	      .set("margin", "0");

		HorizontalLayout header = new HorizontalLayout(new DrawerToggle(),image, banner);

		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
		header.setWidth("100%");
		header.addClassNames("py-0", "px-m");
		
		addToNavbar(header);

	}

	private Tabs getTabs() {
		if (tabs == null)
			tabs = new Tabs();
		tabs.add(createTab(VaadinIcon.HOME, "Home"),
				createTab(CustomIcon.LOGO, "Converter"),
				createTab(VaadinIcon.SCALE, "Impressum"));
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		return tabs;
	}

	private Tab createTab(IconFactory viewIcon, String viewName) {
		Icon icon = viewIcon.create();
		icon.getStyle().set("box-sizing", "border-box").set("margin-inline-end", "var(--lumo-space-m)")
				.set("margin-inline-start", "var(--lumo-space-xs)").set("padding", "var(--lumo-space-xs)");
		Class<? extends VerticalLayout> c=null;
		switch(viewName) {
		case "Home": c=HomeView.class;break;
		case "Converter": c=ConvertView.class;break;
		case "Impressum": c=ImpressumView.class;break;
		default:
			c=HomeView.class;
		}
		RouterLink link = new RouterLink("",c);
		link.add(icon, new Span(viewName));
		link.setTabIndex(0);

		return new Tab(link);
	}

	private void createDrawer() {
		addToDrawer(getTabs());
	}
	
}
