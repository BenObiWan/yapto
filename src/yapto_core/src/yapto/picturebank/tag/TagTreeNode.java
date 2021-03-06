package yapto.picturebank.tag;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import common.ISelectableTreeNode;

/**
 * A {@link TreeNode} based on a {@link ITag}.
 * 
 * @author benobiwan
 * 
 */
public class TagTreeNode extends DefaultMutableTreeNode implements
		ISelectableTreeNode
{
	/**
	 * serialVersionUID for Serialization.
	 */
	private static final long serialVersionUID = 5771660204195299805L;

	/**
	 * The name of this {@link TagTreeNode}.
	 */
	private final String _strName;

	/**
	 * The description of this {@link TagTreeNode}.
	 */
	private final String _strDescription;

	/**
	 * Whether or not this {@link TagTreeNode} is selectable.
	 */
	private final boolean _bSelectable;

	/**
	 * Whether or not this {@link TagTreeNode} is selected.
	 */
	private boolean _bSelected = false;

	/**
	 * Lock protecting the access to _bSelected.
	 */
	private final Object _lockSelected = new Object();

	/**
	 * Creates a new {@link TagTreeNode}.
	 * 
	 * @param tag
	 *            the {@link ITag} to use.
	 */
	public TagTreeNode(final ITag tag)
	{
		this(tag.getName(), tag.getDescription(), tag.isSelectable(), false);
	}

	/**
	 * Creates a new {@link TagTreeNode}.
	 * 
	 * @param strName
	 *            the name of this {@link TagTreeNode}.
	 * @param strDescription
	 *            the description of this {@link TagTreeNode}.
	 * @param bSelectable
	 *            whether or not this {@link TagTreeNode} is selectable.
	 */
	public TagTreeNode(final String strName, final String strDescription,
			final boolean bSelectable)
	{
		this(strName, strDescription, bSelectable, false);
	}

	/**
	 * Creates a new {@link TagTreeNode}.
	 * 
	 * @param strName
	 *            the name of this {@link TagTreeNode}.
	 * @param strDescription
	 *            the description of this {@link TagTreeNode}.
	 * @param bSelectable
	 *            whether or not this {@link TagTreeNode} is selectable.
	 * @param bSelected
	 *            whether or not this {@link TagTreeNode} is selected.
	 */
	public TagTreeNode(final String strName, final String strDescription,
			final boolean bSelectable, final boolean bSelected)
	{
		super(strName);
		_strName = strName;
		_strDescription = strDescription;
		_bSelectable = bSelectable;
		setSelected(bSelected);
	}

	@Override
	public boolean isSelectable()
	{
		return _bSelectable;
	}

	@Override
	public boolean isSelected()
	{
		synchronized (_lockSelected)
		{
			return _bSelected;
		}
	}

	@Override
	public void setSelected(final boolean bSelected)
	{
		if (_bSelectable)
		{
			synchronized (_lockSelected)
			{
				_bSelected = bSelected;
			}
		}
	}

	/**
	 * Get the name of this {@link TagTreeNode}.
	 * 
	 * @return the name of this {@link TagTreeNode}.
	 */
	public String getName()
	{
		return _strName;
	}

	/**
	 * Get the description of this {@link TagTreeNode}.
	 * 
	 * @return the description of this {@link TagTreeNode}.
	 */
	public String getDescription()
	{
		return _strDescription;
	}
}
