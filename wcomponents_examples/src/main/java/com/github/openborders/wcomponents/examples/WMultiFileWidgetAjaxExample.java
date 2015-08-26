package com.github.openborders.wcomponents.examples;

import java.awt.Dimension;

import com.github.openborders.wcomponents.Action;
import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.HeadingLevel;
import com.github.openborders.wcomponents.Margin;
import com.github.openborders.wcomponents.Request;
import com.github.openborders.wcomponents.WAjaxControl;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WCheckBox;
import com.github.openborders.wcomponents.WContainer;
import com.github.openborders.wcomponents.WFieldLayout;
import com.github.openborders.wcomponents.WFigure;
import com.github.openborders.wcomponents.WHeading;
import com.github.openborders.wcomponents.WImage;
import com.github.openborders.wcomponents.WMultiFileWidget;
import com.github.openborders.wcomponents.WNumberField;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.WMultiFileWidget.FileWidgetUpload;
import com.github.openborders.wcomponents.layout.ColumnLayout;
import com.github.openborders.wcomponents.validation.ValidatingAction;
import com.github.openborders.wcomponents.validation.WValidationErrors;

/**
 * Multifile upload with AJAX trigger.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public class WMultiFileWidgetAjaxExample extends WContainer
{

    WNumberField cols = new WNumberField();
    WNumberField size = new WNumberField();
    WNumberField maxfiles = new WNumberField();
    WNumberField previewHeight = new WNumberField();
    WCheckBox showThumnails = new WCheckBox();
    WCheckBox mandatory = new WCheckBox();
    WCheckBox readonly = new WCheckBox();
    WFigure imageHolder;

    /**
     * Construct.
     */
    public WMultiFileWidgetAjaxExample()
    {
        WValidationErrors errors = new WValidationErrors();
        add(errors);

        WFieldLayout paramsLayout = new WFieldLayout();
        paramsLayout.setLabelWidth(25);
        paramsLayout.setMargin(new Margin(0,0,12,0));
        add(paramsLayout);
        cols.setMinValue(0);
        cols.setMaxValue(8);
        paramsLayout.addField("How many columns of files?", cols);

        previewHeight.setMinValue(1);
        previewHeight.setMaxValue(150);
        paramsLayout.addField("Max height of preview images", previewHeight);

        size.setMinValue(1);
        paramsLayout.addField("Max height of viewed image", size);

        maxfiles.setMinValue(0);
        paramsLayout.addField("Max number of files to allow", maxfiles);

        showThumnails.setSelected(true);
        paramsLayout.addField("show thumbnails", showThumnails);

        paramsLayout.addField("Mandatory", mandatory);

        paramsLayout.addField("Readonly", readonly);

        WPanel split = new WPanel();
        split.setLayout(new ColumnLayout(new int[] { 50, 50 }, 12, 0));
        add(split);

        // Left
        WFieldLayout layout = new WFieldLayout(WFieldLayout.LAYOUT_STACKED);
        split.add(layout);

        final WMultiFileWidget widget = new WMultiFileWidget()
        {
            @Override
            protected void preparePaintComponent(Request request) {
                super.preparePaintComponent(request);
                if(cols.getValue() != null)
                {
                    setColumns(cols.getValue().intValue());
                }
                else
                {
                	setColumns(1);
                }
                if(maxfiles.getValue() != null)
                {
                    setMaxFiles(maxfiles.getValue().intValue());
                }
                setMandatory(mandatory.isSelected(), "This field is required");
                setReadOnly(readonly.isSelected());
                if(previewHeight.getValue() != null)
                {
                    if(previewHeight.getValue().intValue() == -1)
                    {
                        setThumbnailSize(null);
                    }
                    else
                    {
                        setThumbnailSize(new Dimension(-1, previewHeight.getValue().intValue()));
                    }
                }
                else {
                    setThumbnailSize(null);
                }
            }
        };
//        widget.setColumns(2);
        widget.setUseThumbnails(true);
        widget.setDropzone(split);
        widget.setFileTypes(new String[] { "image/*" });

        layout.addField("Upload", widget);

        previewHeight.setActionOnChange(new Action() {
            @Override
            public void execute(ActionEvent event) {
                // TODO Auto-generated method stub
                widget.clearThumbnails();
            }
        });

        showThumnails.setActionOnChange(new Action() {
            @Override
            public void execute(ActionEvent event) {
                // TODO Auto-generated method stub
                widget.setUseThumbnails(showThumnails.isSelected());
            }
        });

        mandatory.setActionOnChange(new Action() {
            @Override
            public void execute(ActionEvent event) {
                widget.setMandatory(mandatory.isSelected(), "This field is required");
            }
        });

        readonly.setActionOnChange(new Action() {
            @Override
            public void execute(ActionEvent event) {
                widget.setReadOnly(readonly.isSelected());
            }
        });

        maxfiles.setActionOnChange(new Action() {
            @Override
            public void execute(ActionEvent event) {
                if(maxfiles.getValue() != null)
                {
                    widget.setMaxFiles(maxfiles.getValue().intValue());
                }
                else
                {
                    widget.setMaxFiles(0);
                }
            }
        });

        WButton apply = new WButton("Apply");
        apply.setAction(new ValidatingAction(errors, widget)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                System.out.print(cols);
            }
        });

        add(apply);

        // Right
        final WPanel contentPanel = new WPanel();
        split.add(contentPanel);

        contentPanel.add(new WHeading(HeadingLevel.H2, "File View"));

        final WImage image = new WImage()
        {
            @Override
            public String getImageUrl()
            {
                String fileId = (String) getAttribute("image-fileid");
                if (fileId != null)
                {
                    // Get the url each time to allow for step count in the URL
                    return widget.getFileUrl(fileId);
                }
                return null;
            }

            @Override
            protected void preparePaintComponent(Request request) {
            	super.preparePaintComponent(request);

                if(size.getValue() != null)
                {
                	setSize(new Dimension(-1, size.getValue().intValue()));
                }
            }
        };

        imageHolder = new WFigure(image, "")
        {
            @Override
            public boolean isHidden() {
                return image.getImageUrl() == null;
            }
        };
        contentPanel.add(imageHolder);

        // File AJAX action (ie selected)
        widget.setFileAjaxAction(new Action()
        {
            @Override
            public void execute(final ActionEvent event)
            {
                String fileId = (String) event.getActionObject();
                FileWidgetUpload file = widget.getFile(fileId);
                String url = widget.getFileUrl(fileId);
                image.reset();
                image.setAlternativeText(file.getFile().getDescription());
                image.setImageUrl(url);
                image.setAttribute("image-fileid", fileId);

                if(imageHolder.getDecoratedLabel() != null)
                {
                    if(!"".equals(image.getAlternativeText()))
                    {
                        imageHolder.getDecoratedLabel().setBody(new WText(image.getAlternativeText()));
                    }
                    else
                    {
                        imageHolder.getDecoratedLabel().setBody(new WText("Unnamed Image."));
                    }
                }
            }
        });

        // File changed action (removed from list)
        widget.setActionOnChange(new Action()
        {
            @Override
            public void execute(final ActionEvent event)
            {
                String fileId = (String) image.getAttribute("image-fileid");
                if (fileId != null)
                {
                    // Check file id still in list
                    FileWidgetUpload file = widget.getFile(fileId);
                    if (file == null)
                    {
                        contentPanel.reset();
                    }
                }
            }
        });

        add(new WAjaxControl(cols, layout));
        add(new WAjaxControl(previewHeight, widget));
        add(new WAjaxControl(showThumnails, widget));
        add(new WAjaxControl(mandatory, layout));
        add(new WAjaxControl(readonly, layout));
        add(new WAjaxControl(maxfiles, layout));
        add(new WAjaxControl(size, contentPanel));
        add(new WAjaxControl(maxfiles, layout));
        add(new WAjaxControl(widget, imageHolder));

    }
}
