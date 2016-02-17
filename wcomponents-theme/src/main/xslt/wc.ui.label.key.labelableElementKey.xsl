<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ui="https://github.com/bordertech/wcomponents/namespace/ui/v1.0" xmlns:html="http://www.w3.org/1999/xhtml" version="1.0">
	<!--
		Key to find the component a label is for. Pass in the labels for attribute to 
		get the component. The match for this key must contain all elements which 
		transform to labelable content. If the labelled component is one of these
		and is not in a readOnly state then the WLabel is transformed to a HTML label.
	-->
	<xsl:key name="labelableElementKey" match="//ui:button|//ui:checkbox|//ui:datefield|//ui:dropdown|//ui:emailfield|//ui:fileupload[@async='false']|//ui:listbox|//ui:numberfield|//ui:passwordfield|//ui:phonenumberfield|//ui:printbutton|//ui:progressbar|//ui:radiobutton|//ui:selecttoggle[@renderAs='control']|//ui:textarea|//ui:textfield" use="@id"/>
</xsl:stylesheet>
