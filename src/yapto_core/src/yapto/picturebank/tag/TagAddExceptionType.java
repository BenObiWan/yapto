package yapto.picturebank.tag;

import yapto.picturebank.PictureAddException;

/**
 * Enum describing the different types of {@link PictureAddException}.
 * 
 * @author benobiwan
 */
public enum TagAddExceptionType
{
	/**
	 * There is already a tag with this name.
	 */
	DUPLICATE_TAG_NAME("There is already a tag with this name."),

	/**
	 * SQL error during the insertion of the tag in the database.
	 */
	SQL_INSERT_ERROR(
			"SQL error during the insertion of the tag in the database."),

	/**
	 * No more tag ids available.
	 */
	NO_MORE_IDS("No more tag ids available."),

	/**
	 * The tag name is malformed.
	 */
	MALFORMED_TAG_NAME("The tag name is malformed."),

	/**
	 * The tag is uneditable.
	 */
	UNEDITABLE_TAG("The tag is uneditable."),

	/**
	 * SQL error during the removal of the tag from the database.
	 */
	SQL_REMOVAL_ERROR(
			"SQL error during the removal of the tag from the database"),

	/**
	 * The specified id isn't a valid tag id, or is root tag id.
	 */
	ILLEGAL_TAG_ID("The specified id isn't a valid tag id, or is root tag id.");

	/**
	 * Message for this exception type.
	 */
	private final String _strMessage;

	/**
	 * Creates a new TagAddExceptionType.
	 * 
	 * @param strMessage
	 *            message for this exception type.
	 */
	private TagAddExceptionType(final String strMessage)
	{
		_strMessage = strMessage;
	}

	/**
	 * Get the message for this exception type.
	 * 
	 * @return the message for this exception type.
	 */
	public String getMessage()
	{
		return _strMessage;
	}
}
