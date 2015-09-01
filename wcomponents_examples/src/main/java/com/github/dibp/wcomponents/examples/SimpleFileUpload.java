package com.github.dibp.wcomponents.examples;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.dibp.wcomponents.Action;
import com.github.dibp.wcomponents.ActionEvent;
import com.github.dibp.wcomponents.WButton;
import com.github.dibp.wcomponents.WContainer;
import com.github.dibp.wcomponents.WFieldLayout;
import com.github.dibp.wcomponents.WFileWidget;
import com.github.dibp.wcomponents.WLabel;
import com.github.dibp.wcomponents.WText;
import com.github.dibp.wcomponents.WTextArea;
import com.github.dibp.wcomponents.util.Util;

/**
 * This example demonstrates the use of WFileWidget to select and upload a file.
 * The contents of the uploaded file in displayed in the "console" component
 * when the "Upload" button is pressed.
 *
 * @author Martin Shevchenko
 */
public class SimpleFileUpload extends WContainer
{
    private final WFileWidget fileWidget;
    private final WFileWidget imageWidget;
    private final WTextArea console;

    /**
     * Creates a SimpleFileUpload example.
     */
    public SimpleFileUpload()
    {
        fileWidget = new WFileWidget();
        WLabel fileLabel = new WLabel("Select a file to upload", fileWidget);


        WFieldLayout layout = new WFieldLayout();
        layout.setLabelWidth(25);
        add(layout);
        layout.addField(fileLabel, fileWidget);

        imageWidget = new WFileWidget();

        imageWidget.setFileTypes(new ArrayList<String>(Arrays.asList("image/jpeg",
                                                                        "image/png",
                                                                        "image/gif",
                                                                        "image/jpg")));
        layout.addField("Image file upload", imageWidget);


        WFileWidget constrainedWidget = new WFileWidget();
        constrainedWidget.setMaxFileSize(2048);
        layout.addField("FIle up to 2k", constrainedWidget);
        
        console = new WTextArea();
        console.setColumns(80);
        console.setRows(12);

        layout.addField("Output", console);

        WButton uploadBtn = new WButton("Upload");
        uploadBtn.setAction(new Action()
        {
            public void execute(final ActionEvent event)
            {
                String fileText;

                if (Util.empty(fileWidget.getFileName()))
                {
                    fileText = "nothing uploaded";
                }
                else
                {
                    byte[] fileBytes = fileWidget.getBytes();
                    fileText = new String(fileBytes);
                }

                console.setText(fileText);
            }
        });


        // TODO: This is bad - use a layout instead
        add(uploadBtn);
    }
}
