package com.example.application.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;

public class CookieDialog extends Dialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6700087733093662545L;
	private static final String SET_PROPERTY_IN_OVERLAY_JS = "this.$.overlay.$.overlay.style[$0]=$1";

	public CookieDialog() {
		setPosition(new Position("69%", "16px"));
		this.setModal(false);
		this.setHeaderTitle("Cookie information");
		this.setMaxWidth("57%");
		this.add("We only use cookies to store your session ID and theme preference."
				+ "We do not store or process any personal data."
				+ "jku.at may have access to your activity on this site if you have previously accepted their cookies. "
				+ "All files uploaded to and generated on this site will be removed within 80 seconds of you leaving the site.");

		Button okButton = new Button("Ok", e ->this.close());
		okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.getFooter().add(okButton);
	}

	private void setPosition(Position position) {
		enablePositioning(true);
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "left", position.getLeft());
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "top", position.getTop());
	}

	private void enablePositioning(boolean positioningEnabled) {
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "align-self", positioningEnabled ? "flex-start" : "unset");
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "position", positioningEnabled ? "absolute" : "relative");
	}

	public static class Position {
		private String top;
		private String left;

		public Position(String top, String left) {
			this.top = top;
			this.left = left;
		}

		public String getTop() {
			return top;
		}

		public void setTop(String top) {
			this.top = top;
		}

		public String getLeft() {
			return left;
		}

		public void setLeft(String left) {
			this.left = left;
		}
	}
}
