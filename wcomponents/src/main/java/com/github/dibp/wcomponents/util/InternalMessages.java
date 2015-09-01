package com.github.dibp.wcomponents.util; 

/** 
 * This class declares constants for internal messages issued by the
 * WComponent framework itself. The default messages themeselves are contained
 * in a WComponent configuration properties file. Developers who are 
 * internationalising an application should also internationalise these 
 * messages.
 * 
 * @author Yiannis Paschalidis 
 * @since 1.0.0
 */
public final class InternalMessages
{
    /** The key for the mandatory field validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MANDATORY = "wcomponent.message.fieldMandatory";

    /** The key for the default invalid field validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_INVALID = "wcomponent.message.fieldInvalid";

    /** The key for the miniumum value validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MIN_VALUE = "wcomponent.message.fieldMinValue";

    /** The key for the miniumum length validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MIN_LENGTH = "wcomponent.message.fieldMinLength";
    
    /** The key for the maximum value validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MAX_VALUE = "wcomponent.message.fieldMaxValue";

    /** The key for the miniumum length validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MAX_LENGTH = "wcomponent.message.fieldMaxLength";
    
    /** The key for the maximum decimal places validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MAX_DECIMAL_PLACES = "wcomponent.message.fieldMaxDecimalPlaces";
    
    /** The key for the default invalid date error text. */
    public static final String DEFAULT_VALIDATION_ERROR_INVALID_DATE = "wcomponent.message.fieldDateInvalid";
    
    /** The key for the default invalid partial date error text. */
    public static final String DEFAULT_VALIDATION_ERROR_INVALID_PARTIAL_DATE = "wcomponent.message.fieldPartialDateInvalid";
            
    /** The key for the default date before pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_BEFORE = "wcomponent.message.fieldDateBefore";
    
    /** The key for the default date before or equal to pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_BEFORE_OR_EQUAL = "wcomponent.message.fieldDateBeforeOrEqual";
    
    /** The key for the default date equal pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_EQUAL = "wcomponent.message.fieldDateEqual";
    
    /** The key for the default date after or equal to pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_AFTER_OR_EQUAL = "wcomponent.message.fieldDateAfterOrEqual";
    
    /** The key for the default date after pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_AFTER = "wcomponent.message.fieldDateAfter";
    
    /** The key for the default date before today pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_BEFORE_TODAY = "wcomponent.message.fieldDateBeforeToday";
    
    /** The key for the default date before or equal to today pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_BEFORE_OR_EQUAL_TODAY = "wcomponent.message.fieldDateBeforeOrEqualToday";
    
    /** The key for the default date equal to today pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_EQUAL_TODAY = "wcomponent.message.fieldDateEqualToday";
    
    /** The key for the default date after or equal to today pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_AFTER_OR_EQUAL_TODAY = "wcomponent.message.fieldDateAfterOrEqualToday";
    
    /** The key for the default date after today pivot validator error text. */
    public static final String DEFAULT_VALIDATION_ERROR_DATE_AFTER_TODAY = "wcomponent.message.fieldDateAfterToday";
    
    /** The key for the minimum select validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MIN_SELECT = "wcomponent.message.fieldMinSelect";

    /** The key for the maximum select validation error text. */
    public static final String DEFAULT_VALIDATION_ERROR_MAX_SELECT = "wcomponent.message.fieldMaxSelect";

    /** The key for the invalid pattern error text. */
    public static final String DEFAULT_VALIDATION_ERROR_INVALID_PATTERN = "wcomponent.message.fieldInvalidPattern";
    
    /** The key for the system error error text. */
    public static final String DEFAULT_SYSTEM_ERROR = "wcomponent.message.defaultSystemError";

    /** The key for the system error error text, where the session is irrecoverable. */
    public static final String DEFAULT_SYSTEM_ERROR_SEVERE = "wcomponent.message.defaultSystemErrorSevere";
    
    /** The key for the missing lookup table message error text. */
    public static final String DEFAULT_LOOKUP_TABLE_MESSAGE_ERROR = "wcomponent.message.defaultLookupTableMessage";    
    
    /** The key for the step error text. */
    public static final String DEFAULT_STEP_ERROR = "wcomponent.message.stepError";

    /** The key for the ajax error text. */
    public static final String DEFAULT_AJAX_ERROR = "wcomponent.message.ajaxError";

    /** The key for the content error text. */
    public static final String DEFAULT_CONTENT_ERROR = "wcomponent.message.contentError";
    
    /** The key for the session token error text. */
    public static final String DEFAULT_SESSION_TOKEN_ERROR = "wcomponent.message.sessionTokenError";

    /** The key for the confirmation prompt text. */
    public static final String DEFAULT_CONFIRMATION_PROMPT = "wcomponent.message.confirmationPrompt";
    
    /** The key for the no table data message text. */
    public static final String DEFAULT_NO_TABLE_DATA = "wcomponent.message.noTableData";
    
    /** The key for the print button text. */
    public static final String DEFAULT_PRINT_BUTTON_TEXT = "wcomponent.message.printButton";
    
    /** The key for the cancel button text. */
    public static final String DEFAULT_CANCEL_BUTTON_TEXT = "wcomponent.message.cancelButton";
    
    /** The key for the multi-select-pair options list heading. */
    public static final String DEFAULT_MULTI_SELECT_PAIR_OPTIONS_LIST_HEADING = "wcomponent.message.multiSelectPair.options";
    
    /** The key for the multi-select-pair selections list heading. */
    public static final String DEFAULT_MULTI_SELECT_PAIR_SELECTIONS_LIST_HEADING = "wcomponent.message.multiSelectPair.selections";
    
    /** Prevent instantiation of this class. */
    private InternalMessages() 
    {
    }
}
