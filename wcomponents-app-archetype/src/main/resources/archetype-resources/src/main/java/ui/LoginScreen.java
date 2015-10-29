package ${package}.ui;

import java.util.List;

import ${package}.util.SecurityUtils;
import ${package}.util.UiUtils;
import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WFieldSet;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessages;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WPasswordField;
import com.github.bordertech.wcomponents.WTextField;
import com.github.bordertech.wcomponents.validation.Diagnostic;
import com.github.bordertech.wcomponents.validation.ValidatingAction;

/**
 * An example application entry/login screen.
 */
public class LoginScreen extends WPanel
{
    /** The field used to enter in the user id. */
    private final WTextField userIdField;

    /** The field used to enter in the password. */
    private final WPasswordField passwordField = new WPasswordField();

    /**
     * Creates a log-in screen.
     *
     * @param loginSuccessAction the action to execute on a successful log-in.
     */
    public LoginScreen(final Action loginSuccessAction)
    {
        WPanel panel = new WPanel();
		panel.setMargin(UiUtils.getBigMargin());
        add(panel);

        WFieldSet fieldSet = new WFieldSet("Login");
        panel.add(fieldSet);

        WFieldLayout fieldLayout = new WFieldLayout();
        fieldLayout.setLabelWidth(UiUtils.LABEL_WIDTH);
        fieldSet.add(fieldLayout);

        userIdField = new WTextField();
        userIdField.setMandatory(true);
		userIdField.setColumns(40);
        fieldLayout.addField("User Name", userIdField);

        passwordField.setMandatory(true);
		passwordField.setColumns(40);
        fieldLayout.addField("Password", passwordField);

        WButton loginButton = new WButton("Login");
        fieldLayout.addField((WLabel) null, loginButton);
        setDefaultSubmitButton(loginButton);

        loginButton.setAction(new ValidatingAction(WMessages.getInstance(this).getValidationErrors(), this) {
            @Override
            public void executeOnValid(final ActionEvent event) {
                loginSuccessAction.execute(event);
            }
        });
    }

    /**
     * @return The user id which was entered into the text field.
     */
    protected String getUserId() {
        return userIdField.getText();
    }

    /**
     * Override validateComponent to validate the userId / password.
     * @param diags the list of diagnostics to add validation errors to.
     */
    @Override
    protected void validateComponent(final List<Diagnostic> diags) {
        if (!SecurityUtils.authenticate(userIdField.getText(), passwordField.getText())) {
            diags.add(createErrorDiagnostic(this, "Invalid user id / password"));
        }
    }
}