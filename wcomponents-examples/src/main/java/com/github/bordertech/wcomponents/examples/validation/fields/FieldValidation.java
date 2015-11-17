package com.github.bordertech.wcomponents.examples.validation.fields;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCardManager;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.validation.ValidatingAction;
import com.github.bordertech.wcomponents.validation.WValidationErrors;

/**
 * <p>
 * This is an example of a component that is used to validate a contained <code>component</code>. It supplies an
 * instance of {@link WValidationErrors} for displaying an error box containing all validation errors and provides the
 * validatable component with an extension of {@link ValidatingAction} used to perform the validation. If there are no
 * validation errors then visibility is given to a simple component to show the success. </p>
 *
 * <p>
 * Note the additional validation
 * </p>
 * <ul>
 * <li>WTextField will only accept alphabetic characters</li>
 * <li>WDateField ensures that the date enter is before today</li>
 * </ul>
 *
 *
 * @author Adam Millard
 */
public class FieldValidation extends WContainer {

	/**
	 * pages.
	 */
	private final WCardManager pages = new WCardManager();

	/**
	 * fields panel.
	 */
	private final WPanel basicFieldsPanel = new WPanel();

	/**
	 * errors display.
	 */
	private final WValidationErrors errors = new WValidationErrors();

	/**
	 * validation page.
	 */
	private final CoreFields basicFields = new CoreFields();

	/**
	 * success panel.
	 */
	private final WContainer successPanel = new WContainer();

	/**
	 * build the field validation card manager.
	 */
	public FieldValidation() {
		add(pages);
		basicFieldsPanel.add(errors);
		basicFieldsPanel.add(basicFields);

		pages.add(basicFieldsPanel);
		pages.add(successPanel);

		Action cancelAction = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				cancel();
			}
		};
		basicFields.setCancelAction(cancelAction);
		basicFields.setResetAction(cancelAction);

		Action submitAction = new ValidatingAction(errors, basicFields) {
			@Override
			public void executeOnValid(final ActionEvent event) {
				next();
			}
		};
		basicFields.setSubmitAction(submitAction);

		successPanel.add(new WMessageBox(WMessageBox.SUCCESS, "All is valid!"));
		WButton backBtn = new WButton("Back", 'B');
		successPanel.add(backBtn);

		Action back = new Action() {
			@Override
			public void execute(final ActionEvent event) {
				back();
			}
		};
		backBtn.setAction(back);
	}

	/**
	 * cancel button action.
	 */
	public void cancel() {
		this.reset();
	}

	/**
	 * Submit action.
	 */
	public void next() {
		pages.makeVisible(successPanel);
	}

	/**
	 * To return from the success panel.
	 */
	public void back() {
		pages.makeVisible(basicFieldsPanel);
	}
}
