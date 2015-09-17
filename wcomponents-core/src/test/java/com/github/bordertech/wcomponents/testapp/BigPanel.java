package com.github.bordertech.wcomponents.testapp;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.RadioButtonGroup;
import com.github.bordertech.wcomponents.TestLookupTable;
import com.github.bordertech.wcomponents.WButton;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WCheckBoxSelect;
import com.github.bordertech.wcomponents.WCollapsible;
import com.github.bordertech.wcomponents.WColumnLayout;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WDateField;
import com.github.bordertech.wcomponents.WDropdown;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WHeading;
import com.github.bordertech.wcomponents.WLabel;
import com.github.bordertech.wcomponents.WMessageBox;
import com.github.bordertech.wcomponents.WMultiSelect;
import com.github.bordertech.wcomponents.WMultiSelectPair;
import com.github.bordertech.wcomponents.WRadioButton;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WTextArea;
import com.github.bordertech.wcomponents.WTextField;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel with several components.
 *
 * @author Kishan Bisht
 * @since 1.0.0
 */
public class BigPanel extends WContainer {

	/**
	 * Creates a BigPanel.
	 */
	public BigPanel() {
		WColumnLayout mainLayout = new WColumnLayout();
		WFieldLayout leftColumn = new WFieldLayout();
		WFieldLayout rightColumn = new WFieldLayout();

		// Add left and right column layouts to main layout
		mainLayout.setLeftColumn(leftColumn);
		mainLayout.setRightColumn(rightColumn);

		List<String> progTypeList = getProgrammerTypeList();

		add(new WHeading(WHeading.MAJOR, "Programmer social network"));
		leftColumn.addField("Name", new WTextField());
		leftColumn.addField("Date Of birth", new WDateField());
		leftColumn.addField("Day of week", new WDropdown(TestLookupTable.DayOfWeekTable.class));
		leftColumn.addField("U a nerd", new WCheckBox());
		leftColumn.addField("What kind of programmer u", new WDropdown(progTypeList));

		leftColumn.addField("Gender", new WCollapsible(new WText("Male"), "Heading"));

		leftColumn.addField("Preferences", new WMessageBox(WMessageBox.INFO, "Game programmers"));

		// Add radio button type display
		RadioButtonGroup radioButtonGroup = new RadioButtonGroup();

		WRadioButton stunner = radioButtonGroup.addRadioButton("Stunner");
		WRadioButton average = radioButtonGroup.addRadioButton("Average");
		WRadioButton acquired = radioButtonGroup.addRadioButton("Acquired");
		WRadioButton revolting = radioButtonGroup.addRadioButton("Revolting");

		WContainer radioButtonPanel = new WContainer();

		// Add labels and radios
		WLabel stunnerLabel = new WLabel("Stunner", stunner);
		radioButtonPanel.add(stunnerLabel);
		radioButtonPanel.add(stunner);

		radioButtonPanel.add(radioButtonGroup);

		WLabel averageLabel = new WLabel("Average", average);
		radioButtonPanel.add(averageLabel);
		radioButtonPanel.add(average);

		WLabel acquiredLabel = new WLabel("Acquired", acquired);
		radioButtonPanel.add(acquiredLabel);
		radioButtonPanel.add(acquired);

		WLabel revoltingLabel = new WLabel("Revolting", revolting);
		radioButtonPanel.add(revoltingLabel);
		radioButtonPanel.add(revolting);

		leftColumn.addField("How good looking are u", radioButtonPanel);

		WTextArea textArea = new WTextArea();
		textArea
				.setText(
						"Hey, being a Gandalf would be wicked cool for a Tolkien fan (but who isn't, post-Jackson?) - unfortunately I have trouble to grow a dense, long beard even at 34 (and my wife wouldn't let me anyway). I once tried to be a Ninja, which is the second coolest position, it worked for a while (have the tons of SVN commits to prove, hehehe) but my company was not mature for telecommuting and I had to go back working with my pants on. (If anybody is willing to send me a monthly paycheck and never see my face, just call: 055-HACKFORFOOD.) So I guess I'm just a Workaholic again, except perhaps when posting and blogging, as evangelism and even fanboying help alleviate the stress... ");
		textArea.setMandatory(true);
		leftColumn.addField("Describe yourself: ", textArea);

		ArrayList<String> drugList = new ArrayList<>();
		drugList.add("tea");
		drugList.add("coffee");
		drugList.add("coke");
		drugList.add("red bull");
		drugList.add("not legal");

		WCheckBoxSelect cbSelect = new WCheckBoxSelect(drugList);
		leftColumn.addField("Drug choice", cbSelect);

		ArrayList<String> favList = new ArrayList<>();
		favList.add("Java");
		favList.add(".net");
		favList.add("PHP");
		favList.add("Ruby");
		favList.add("other");
		leftColumn.addField("Favorite programming platform", new WMultiSelect(favList));

		final WButton button2 = new WButton("Click here to make yourself happy", 'C');
		button2.setRenderAsLink(true);
		button2.setAction(new Action() {
			@Override
			public void execute(final ActionEvent event) {
				if (button2.isDisabled()) {
					button2.setDisabled(false);
				} else {
					button2.setDisabled(true);
				}
			}
		});

		leftColumn.addField("Your state of mind", button2);

		List<String> sites = new ArrayList<>();
		sites.add("infoq.com");
		sites.add("facebook.com");
		sites.add("myspace.com");
		sites.add("youtube.com");
		sites.add("hackerhack.com");
		sites.add("gamingdeath.com");
		sites.add("upeekageek.com");
		sites.add("donateurorgansforcharity.com");
		sites.add("blogandletothersuffer.com");
		sites.add("burn.in.globalwarming.com");
		sites.add("downloadilllegalmusicwithrisk.com");

		WMultiSelectPair pair = new WMultiSelectPair(sites);
		rightColumn.addField("Sites u visit", pair);

		add(mainLayout);
	}

	/**
	 * @return a list of "programmer types".
	 */
	private List<String> getProgrammerTypeList() {
		List<String> programmerTypeList = new ArrayList<>();
		programmerTypeList.add("Gandalf");
		programmerTypeList.add("Ninja");
		programmerTypeList.add("Fanboy");
		programmerTypeList.add("Mediocre");
		programmerTypeList.add("Cowboy");
		programmerTypeList.add("Evangelist");
		programmerTypeList.add("Paratrooper");
		programmerTypeList.add("Martyr");
		programmerTypeList.add("Leader");
		programmerTypeList.add("Junior");
		programmerTypeList.add("Fault finder");
		programmerTypeList.add("Googler");
		programmerTypeList.add("Business fighter");
		programmerTypeList.add("Perfectionist");
		programmerTypeList.add("Nobody");
		programmerTypeList.add("Sick of programming");
		programmerTypeList.add("Inventor");
		programmerTypeList.add("Who cares");
		return programmerTypeList;
	}
}
