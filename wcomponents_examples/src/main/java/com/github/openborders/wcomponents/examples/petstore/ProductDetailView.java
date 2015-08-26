package com.github.openborders.wcomponents.examples.petstore; 

import com.github.openborders.wcomponents.ActionEvent;
import com.github.openborders.wcomponents.Image;
import com.github.openborders.wcomponents.WBeanContainer;
import com.github.openborders.wcomponents.WButton;
import com.github.openborders.wcomponents.WHeading;
import com.github.openborders.wcomponents.WImage;
import com.github.openborders.wcomponents.WMessagesValidatingAction;
import com.github.openborders.wcomponents.WPanel;
import com.github.openborders.wcomponents.WStyledText;
import com.github.openborders.wcomponents.WText;
import com.github.openborders.wcomponents.layout.ColumnLayout;
import com.github.openborders.wcomponents.layout.FlowLayout;
import com.github.openborders.wcomponents.layout.FlowLayout.Alignment;

import com.github.openborders.wcomponents.examples.petstore.beanprovider.InventoryBeanProvider;
import com.github.openborders.wcomponents.examples.petstore.model.ProductBean;

/** 
 * ProductDetailView shows details for a particular product and
 * allows the user to add it to their shopping cart.
 * 
 * Expects the bean to be an InventoryBean.
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class ProductDetailView extends WBeanContainer
{
    /**
     * Creates a ProductDetailView.
     */
    public ProductDetailView()
    {
        WPanel panel = new WPanel();
        panel.setLayout(new ColumnLayout(new int[]{70, 30}));
        add(panel);        
        
        final UpdateCartComponent cartComponent = new UpdateCartComponent();
        cartComponent.setBeanProperty("productId");
        
        panel.add(new ProductDetailsPanel());
        
        WPanel cartPanel = new WPanel();
        cartPanel.setLayout(new FlowLayout(Alignment.LEFT, 5, 0));
        panel.add(cartPanel);

        WButton updateButton = new WButton("Update cart");
        
        cartPanel.add(cartComponent);
        cartPanel.add(updateButton);
        
        updateButton.setAction(new WMessagesValidatingAction(cartComponent)
        {
            @Override
            public void executeOnValid(final ActionEvent event)
            {
                cartComponent.updateCart();
            }
        });
        
        setBeanProvider(InventoryBeanProvider.getInstance());
    }

    /**
     * A sub-panel for displaying the product details. 
     * 
     * @author Yiannis Paschalidis
     */
    private static final class ProductDetailsPanel extends WPanel
    {
        /** Creates a ProductDetailsPanel. */
        public ProductDetailsPanel()
        {
            setLayout(new ColumnLayout(new int[]{15, 85}));
            
            // Product details on left
            WPanel textPanel = new WPanel();
            textPanel.setLayout(new FlowLayout(Alignment.VERTICAL, 0, 5));
            
            WHeading title = new WHeading(WHeading.MINOR, "No description available");
            title.setBeanProperty("product.shortTitle");
            
            WText cost = new CostRenderer();
            cost.setBeanProperty("unitCost");
            
            WPanel titlePanel = new WPanel();
            titlePanel.setLayout(new ColumnLayout(new int[]{80, 20}));
            titlePanel.add(title);
            titlePanel.add(cost);
            textPanel.add(titlePanel);            
            
            // Filter the text to replace line breaks with paragraph tags
            WStyledText description = new WStyledText();
            description.setWhitespaceMode(WStyledText.WhitespaceMode.PARAGRAPHS);
            
            description.setBeanProperty("product.description");
            textPanel.add(description);
            
            // We don't need to always specify the bean/bean provider for components,
            // as they can also obtain a bean from their parent component
            add(new DynamicWImage());
            
            add(textPanel);
        }
    }
    
    /**
     * A bean-bound image that uses the ProductImage to retrieve the image data. 
     * 
     * @author Yiannis Paschalidis
     */
    private static final class DynamicWImage extends WImage
    {
        /**
         * Creates a DynamicWImage.
         */
        public DynamicWImage()
        {
            setBeanProperty("product");
        }
        
        /**
         * Returns the image of the product being displayed by ProductDetailView.
         * 
         * @return the image to be displayed.
         */
        @Override
        public Image getImage()
        {
            return new ProductImage((ProductBean) getBeanValue());
        }
    }
}
