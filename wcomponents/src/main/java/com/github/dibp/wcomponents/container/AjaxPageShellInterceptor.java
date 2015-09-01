package com.github.dibp.wcomponents.container;

import com.github.dibp.wcomponents.AjaxHelper;
import com.github.dibp.wcomponents.AjaxOperation;
import com.github.dibp.wcomponents.Headers;
import com.github.dibp.wcomponents.RenderContext;
import com.github.dibp.wcomponents.Request;
import com.github.dibp.wcomponents.UIContext;
import com.github.dibp.wcomponents.UIContextHolder;
import com.github.dibp.wcomponents.WebUtilities;
import com.github.dibp.wcomponents.XmlStringBuilder;
import com.github.dibp.wcomponents.servlet.WebXmlRenderContext;
import com.github.dibp.wcomponents.util.SystemException;
import com.github.dibp.wcomponents.util.Util;
import com.github.dibp.wcomponents.util.XMLUtil;

/**
 * This {@link InterceptorComponent} provides the XML wrapper necessary for serving
 * up an AJAX response.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class AjaxPageShellInterceptor extends InterceptorComponent
{
    /**
     * Override to set the content type of the response and reset the headers.
     * @param request The request being serviced.
     */
    @Override
    public void preparePaint(final Request request)
    {
        UIContext uic = UIContextHolder.getCurrent();
        Headers headers = uic.getUI().getHeaders();
        headers.reset();
        headers.setContentType(WebUtilities.CONTENT_TYPE_XML);

        super.preparePaint(request);
    }

    /**
     * Paints the targeted ajax regions. The format of the response is an agreement between
     * the server and the client side handling our AJAX response.
     *
     * @param renderContext the renderContext to send the output to.
     */
    @Override
    public void paint(final RenderContext renderContext)
    {
        WebXmlRenderContext webRenderContext = (WebXmlRenderContext) renderContext;
        XmlStringBuilder xml = webRenderContext.getWriter();
        AjaxOperation operation = AjaxHelper.getCurrentOperation();

        if (operation == null)
        {
            // the request attribute that we place in the ui contenxt in the action phase can't be null
            throw new SystemException("Can't paint AJAX response. Couldn't find the expected reference to the AjaxOperation.");
        }

        UIContext uic = UIContextHolder.getCurrent();
        String focusId = uic.getFocussedId();

        xml.append(XMLUtil.getXMLDeclarationWithThemeXslt(uic));
        xml.append(XMLUtil.DOC_TYPE);  // It is possible that the AJAX response contains XHTML including &nbsp;
        xml.appendTagOpen("ui:ajaxResponse");
        xml.append(XMLUtil.STANDARD_NAMESPACES);
        xml.appendOptionalAttribute("defaultFocusId", uic.isFocusRequired() && !Util.empty(focusId), focusId);
        xml.appendClose();

        getBackingComponent().paint(renderContext);

        xml.appendEndTag("ui:ajaxResponse");
    }
}
