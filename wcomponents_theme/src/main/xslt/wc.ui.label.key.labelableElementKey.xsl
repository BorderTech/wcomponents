<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/openborders/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Key to find the component a label is for. Pass in the labels for attribute to 
		get the component. The match for this key must contain all elements which 
		transform to labelable content. If the labelled component is one of these
		and is not in a readOnly state then the WLabel is transformed to a HTML label.
	-->
	<xsl:key name="labelableElementKey" match="//ui:button|//ui:checkBox|//ui:dateField|//ui:dropdown|//ui:emailField|//ui:fileUpload[@async='false']|//ui:listBox|//ui:numberField|//ui:passwordField|//ui:phoneNumberField|//ui:printButton|//ui:progressBar|//ui:radioButton|//ui:selectToggle[@renderAs='control']|//ui:textArea|//ui:textField" use="@id"/>
</xsl:stylesheet>
