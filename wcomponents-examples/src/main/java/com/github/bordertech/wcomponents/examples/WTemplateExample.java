package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.MessageContainer;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDecoratedLabel;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.WSection;
import com.github.bordertech.wcomponents.WTemplate;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.examples.common.ExplanatoryText;
import com.github.bordertech.wcomponents.template.TemplateRendererFactory;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import java.util.List;

/**
 * An example of how to use a WTemplate to center a login form.
 *
 * @author Mark Reeves
 * @since 2016-02-22
 */
public class WTemplateExample extends WContainer {
	/**
	 * A template used to center content.
	 */
	private final WTemplate template = new WTemplate("com/github/bordertech/wcomponents/examples/centerAll.moustache", TemplateRendererFactory.TemplateEngine.HANDLEBARS);

	/**
	 * Create the LoginViewExample.
	 */
	public WTemplateExample() {
		add(template);
		template.addTaggedComponent("content", new ContentSection());
	}

	/**
	 * A {@link WSection} which holds the guts of the example.
	 * This is added to the template.
	 */
	private final class ContentSection extends WSection implements MessageContainer {
		/**
		 * The view manager.
		 */
		private final WCardManager cardManager = new WCardManager();

		/**
		 * This section's content container.
		 */
		private WPanel contentPanel;

		/**
		 * A log in form.
		 */
		private final LoginFormCard loginForm;

		/**
		 * A new user form.
		 */
		private final NewUserCard newUserForm;

		/**
		 * A successful log in will show this card.
		 */
		private final LoggedInUserCard loggedIn;

		/**
		 * Adding a user will show this card.
		 */
		private final AddedUserCard added;

		/**
		 * Validation messages.
		 */
		private final WMessages messages = new WMessages();

		/**
		 * Create the main content and card manager for the example.
		 */
		public ContentSection() {
			super(new WPanel(), new WDecoratedLabel());

			loginForm = new LoginFormCard();
			newUserForm = new NewUserCard();
			loggedIn = new LoggedInUserCard();
			added = new AddedUserCard();

			initCard();
		}

		/**
		 * Set up the content of the example.
		 */
		private void initCard() {
			contentPanel = getContent();
			contentPanel.add(messages);
			contentPanel.add(cardManager);

			/* The login form. */
			cardManager.add(loginForm);
			loginForm.getSubmitButton().setAction(new ValidatingAction(messages.getValidationErrors(), contentPanel) {
				@Override
				public void executeOnValid(final ActionEvent event) {
					cardManager.makeVisible(loggedIn);
					setTitle(loggedIn.getTitle());
					getDecoratedLabel().setTail(null);
				}
			});
			loginForm.getNavButton().setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					cardManager.makeVisible(newUserForm);
					setTitle(newUserForm.getTitle());
					getDecoratedLabel().setTail(newUserForm.getNavButton());
					messages.reset();
				}
			});

			/* The new user form. */
			cardManager.add(newUserForm);
			newUserForm.getSubmitButton().setAction(new ValidatingAction(messages.getValidationErrors(), contentPanel) {
				@Override
				public void executeOnValid(final ActionEvent event) {
					cardManager.makeVisible(added);
					setTitle(added.getTitle());
					getDecoratedLabel().setTail(null);
				}
			});
			newUserForm.getNavButton().setAction(new Action() {
				@Override
				public void execute(final ActionEvent event) {
					cardManager.makeVisible(loginForm);
					setTitle(loginForm.getTitle());
					getDecoratedLabel().setTail(loginForm.getNavButton());
					messages.reset();
				}
			});

			/* The logged in user card. */
			cardManager.add(loggedIn);

			/* The new user added card. */
			cardManager.add(added);

			/* The initial state of the card manager. */
			cardManager.makeVisible(loginForm);
			setTitle(loginForm.getTitle());
			getDecoratedLabel().setTail(loginForm.getNavButton());
		}

		/**
		 * Set the title of the WSection.
		 * @param title The title to set.
		 */
		public void setTitle(final String title) {
			this.getDecoratedLabel().setBody(new WText(title));
		}

		@Override
		public WMessages getMessages() {
			return messages;
		}
	}


	/**
	 * A card holding the logged in user message.
	 */
	private final class LoggedInUserCard extends WContainer {

		/**
		 * What to call this form.
		 */
		private static final String TITLE = "Log in successful";

		/**
		 * Create a logged in user message card.
		 */
		public LoggedInUserCard() {
			add(new ExplanatoryText("Log in successful"));
		}

		/**
		 * @return the card title.
		 */
		public String getTitle() {
			return TITLE;
		}
	}


	/**
	 * A card holding the added new user message.
	 */
	private final class AddedUserCard extends WContainer {

		/**
		 * What to call this form.
		 */
		private static final String TITLE = "Added successful";

		/**
		 * Create a logged in user message card.
		 */
		public AddedUserCard() {
			add(new ExplanatoryText("Successfully added user."));
		}

		/**
		 * @return the card title.
		 */
		public String getTitle() {
			return TITLE;
		}
	}

	/**
	 * A card holding the log in form.
	 */
	private final class LoginFormCard extends WContainer {
		/**
		 * A button to navigate to the "create a new user" view.
		 */
		private final WButton newUserButton = new WButton("Create a user");

		/**
		 * The log-in button.
		 */
		private final WButton submit = new WButton("Go");

		/**
		 * What to call this form.
		 */
		private static final String TITLE = "Log in form";

		/**
		 * Create the login form.
		 */
		public LoginFormCard() {
			final WFieldLayout layout = new WFieldLayout();
			add(layout);

			final WTextField userName = new WTextField();
			userName.setMandatory(true);
			userName.setDefaultSubmitButton(submit);

			final WPasswordField password = new WPasswordField();
			password.setMandatory(true);
			password.setDefaultSubmitButton(submit);

			layout.addField("User name", userName);
			layout.addField("Password", password);
			layout.addField((WLabel) null, submit);


			newUserButton.setImage("/image/user.png");
			newUserButton.setRenderAsLink(true);

		}

		/**
		 * @return the log in button.
		 */
		public WButton getSubmitButton() {
			return submit;
		}


		/**
		 * @return the button to go to the new user form.
		 */
		public WButton getNavButton() {
			return newUserButton;
		}

		/**
		 * @return the card title.
		 */
		public String getTitle() {
			return TITLE;
		}
	}



	/**
	 * Create a new user form.
	 */
	private final class NewUserCard extends WContainer {
		/**
		 * A button to return to the log in screen.
		 */
		private final WButton returnToLogInButton = new WButton("Return to log in screen");

		/**
		 * The create user button.
		 */
		private final WButton submit = new WButton("Add");

		/**
		 * What to call this form.
		 */
		private static final String TITLE = "Create account";

		/**
		 * Password field.
		 */
		private final WPasswordField newPassword = new WPasswordField();

		/**
		 * Repeat the password field.
		 */
		private final WPasswordField newPassword2 = new WPasswordField();

		/**
		 * Make the New user form.
		 */
		public NewUserCard() {
			WFieldLayout layout = new WFieldLayout();
			add(layout);

			WTextField newUserName = new WTextField();
			newUserName.setMandatory(true);
			newUserName.setMinLength(3);
			newUserName.setMaxLength(16);
			newUserName.setDefaultSubmitButton(submit);
			layout.addField("User name", newUserName).getLabel().setHint("User name must be between 3 and 16 characters.");

			newPassword.setMandatory(true);
			newPassword.setMinLength(3);
			newPassword.setMaxLength(16);
			newPassword.setDefaultSubmitButton(submit);
			layout.addField("Password", newPassword).getLabel().setHint("Password must be between 3 and 16 characters.");

			newPassword2.setMandatory(true);
			newPassword2.setMinLength(3);
			newPassword2.setMaxLength(16);
			newPassword2.setDefaultSubmitButton(submit);
			layout.addField("Repeat password", newPassword2);

			layout.addField((WLabel) null, submit);

			returnToLogInButton.setImage("/image/home.png");
			returnToLogInButton.setRenderAsLink(true);
		}

		@Override
		protected void validateComponent(final List<Diagnostic> diags) {
			super.validateComponent(diags); //To change body of generated methods, choose Tools | Templates.
			String text1 = newPassword.getText();
			String text2 = newPassword2.getText();

			if (text1 == null || !text1.equals(text2)) {
				diags.add(createErrorDiagnostic(newPassword2, "Passwords must be the same."));
			}
		}



		/**
		 * @return the add user button.
		 */
		public WButton getSubmitButton() {
			return submit;
		}

		/**
		 * @return the return to log-in screen button.
		 */
		public WButton getNavButton() {
			return returnToLogInButton;
		}

		/**
		 * @return the card title.
		 */
		public String getTitle() {
			return TITLE;
		}
	}
}
