package com.github.dibp.wcomponents; 

import com.github.dibp.wcomponents.WTable.BeanBoundTableModel;

/** 
 * The BeanTableDataModel provides a link between a bean (bound to a table),
 * and the table model API. 
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 * 
 * @deprecated Use {@link WTable} and {@link BeanBoundTableModel} instead.
 */
@Deprecated
public interface BeanTableDataModel extends TableDataModel, BeanProviderBound
{
    
}
