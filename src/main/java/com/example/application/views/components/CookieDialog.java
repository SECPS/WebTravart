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
		setPosition(new Position("16px", "16px"));
		this.setModal(false);
		this.setHeaderTitle("Cookie information");
		this.setMaxWidth("57%");
		enablePositioning(true);
		this.add("We only use cookies to store your session ID and theme preference. "
				+ "We do not store or process any personal data. "
				+ "jku.at may have access to your activity on this site if you have previously accepted their cookies. "
				+ "All files uploaded to and generated on this site will be removed within 30 minutes of you leaving the site.");

		Button okButton = new Button("Ok", e ->this.close());
		okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		this.getFooter().add(okButton);
	}

	private void setPosition(Position position) {
		enablePositioning(true);
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "left", position.getLeft());
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "bottom", position.getBottom());
	}

	private void enablePositioning(boolean positioningEnabled) {
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "align-self", positioningEnabled ? "flex-start" : "unset");
		getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "position", positioningEnabled ? "absolute" : "relative");
	}

	public static class Position {
		private String bottom;
		private String left;

		public Position(String bottom, String left) {
			this.bottom = bottom;
			this.left = left;
		}

		public String getBottom() {
			return bottom;
		}

		public void setBottom(String bottom) {
			this.bottom = bottom;
		}

		public String getLeft() {
			return left;
		}

		public void setLeft(String left) {
			this.left = left;
		}
	}
}
