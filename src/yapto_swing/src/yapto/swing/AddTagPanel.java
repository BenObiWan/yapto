package yapto.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import yapto.picturebank.IPicture;
import yapto.picturebank.IPictureBank;
import yapto.picturebank.PictureBankList;
import yapto.picturebank.tag.EditableTag;
import yapto.picturebank.tag.ITag;
import yapto.picturebank.tag.TagAddException;

/**
 * Panel used to enter all the information of a new {@link ITag} and create it,
 * also used to edit an existing {@link ITag}.
 * 
 * @author benobiwan
 * 
 */
public final class AddTagPanel extends JPanel implements ActionListener
{
	/**
	 * serialVersionUID for Serialization.
	 */
	private static final long serialVersionUID = 1479318453431238470L;

	/**
	 * Action command for the create tag action.
	 */
	private static final String CREATE_ACTION_COMMAND = "cr";

	/**
	 * Action command for the cancel action.
	 */
	private static final String CANCEL_ACTION_COMMAND = "ca";

	/**
	 * Action command for the edit action.
	 */
	private static final String EDIT_ACTION_COMMAND = "ed";

	/**
	 * Text for the "create tag" button.
	 */
	private static final String CREATE_LABEL = "Create";

	/**
	 * Text for the "edit tag" button.
	 */
	private static final String EDIT_LABEL = "Edit";

	/**
	 * Title for the error dialog box.
	 */
	private static final String ERROR_DIALOG_TITLE = "Tag add error";

	/**
	 * Text field used to enter the name of the new {@link ITag}.
	 */
	private final JTextField _tagNameField = new JTextField();

	/**
	 * Check box used to specify if the new {@link ITag} is selectable or not.
	 */
	private final JCheckBox _tagSelectableField = new JCheckBox();

	/**
	 * Text area used to enter the description of the new {@link ITag}.
	 */
	private final JTextArea _tagDescriptionField = new JTextArea();

	/**
	 * Tree used to specify the parent of the new {@link ITag}.
	 */
	private final ParentingTreeTagPanel _tagParent;

	/**
	 * Parent {@link JDialog} of this Panel.
	 */
	private final JDialog _parent;

	/**
	 * The {@link PictureBankList} used to load the {@link IPictureBank} used as
	 * source for the {@link IPicture}.
	 */
	private final PictureBankList _bankList;

	/**
	 * {@link JButton} used to create or edit the {@link ITag}.
	 */
	final JButton _buttonCreate = new JButton();

	/**
	 * Id of the tag to edit when in edit mode.
	 */
	private int _iIdOfTagToEdit = -1;

	/**
	 * Creates a new AddTagPanel.
	 * 
	 * @param parent
	 *            parent {@link JDialog} of this Panel.
	 * @param bankList
	 *            the {@link PictureBankList} used to load the
	 *            {@link IPictureBank} used as source for the {@link IPicture}.
	 */
	public AddTagPanel(final JDialog parent, final PictureBankList bankList)
	{
		super(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		_parent = parent;
		_bankList = bankList;
		_tagParent = new ParentingTreeTagPanel(_bankList);

		final JPanel panelHeadField = new JPanel(new GridLayout(3, 2, 5, 5));
		panelHeadField.add(new JLabel("Name "));
		panelHeadField.add(_tagNameField);
		panelHeadField.add(new JLabel("Selectable "));
		panelHeadField.add(_tagSelectableField);
		_tagSelectableField.setSelected(true);
		panelHeadField.add(new JLabel("Description"));

		final JPanel panelHead = new JPanel(new GridLayout(2, 1, 5, 5));
		panelHead.add(panelHeadField);
		panelHead.add(new JScrollPane(_tagDescriptionField),
				BorderLayout.CENTER);

		final JPanel panelParent = new JPanel(new BorderLayout(5, 5));
		panelParent.add(new JLabel("Parent"), BorderLayout.PAGE_START);
		panelParent.add(_tagParent, BorderLayout.CENTER);

		final JPanel panelButton = new JPanel(new GridLayout(1, 2, 10, 10));
		_buttonCreate.addActionListener(this);
		final JButton buttonCancel = new JButton("Cancel");
		buttonCancel.setActionCommand(CANCEL_ACTION_COMMAND);
		buttonCancel.addActionListener(this);
		panelButton.add(_buttonCreate);
		panelButton.add(buttonCancel);

		add(panelHead, BorderLayout.PAGE_START);
		add(panelParent, BorderLayout.CENTER);
		add(panelButton, BorderLayout.PAGE_END);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		if (CREATE_ACTION_COMMAND.equals(e.getActionCommand()))
		{
			final boolean bSelectable = _tagSelectableField.isSelected();
			final String strName = _tagNameField.getText();
			final String strDescription = _tagDescriptionField.getText();
			final int iParentTagId = _tagParent.getSelectedTagId();
			try
			{
				// TODO handle multiple picture bank
				_bankList
						.getSelectedPictureBank()
						.first()
						.addTag(iParentTagId, strName, strDescription,
								bSelectable);
				_parent.setVisible(false);
			}
			catch (final TagAddException ex)
			{
				JOptionPane.showMessageDialog(_parent, ex.getExceptionType()
						.getMessage(), ERROR_DIALOG_TITLE,
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (EDIT_ACTION_COMMAND.equals(e.getActionCommand()))
		{
			final boolean bSelectable = _tagSelectableField.isSelected();
			final String strName = _tagNameField.getText();
			final String strDescription = _tagDescriptionField.getText();
			final int iParentTagId = _tagParent.getSelectedTagId();
			try
			{
				// TODO handle multiple picture bank
				_bankList
						.getSelectedPictureBank()
						.first()
						.editTag(_iIdOfTagToEdit, iParentTagId, strName,
								strDescription, bSelectable);
				_parent.setVisible(false);
			}
			catch (final TagAddException ex)
			{
				JOptionPane.showMessageDialog(_parent, ex.getExceptionType()
						.getMessage(), ERROR_DIALOG_TITLE,
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (CANCEL_ACTION_COMMAND.equals(e.getActionCommand()))
		{
			_parent.setVisible(false);
		}
	}

	/**
	 * Initialize the AddTagPanel before displaying it.
	 * 
	 * @param bIsEdit
	 *            true if we are editing a tag.
	 * @param tag
	 *            the tag to edit if we are in edit mode, the tag to use as
	 *            parent otherwise.
	 */
	public void initialize(final boolean bIsEdit, final EditableTag tag)
	{
		if (bIsEdit && tag != null)
		{
			_buttonCreate.setActionCommand(EDIT_ACTION_COMMAND);
			_buttonCreate.setText(EDIT_LABEL);
			_tagParent.setSelectedTag(tag.getParentId());
			_tagNameField.setText(tag.getName());
			_tagSelectableField.setSelected(tag.isSelectable());
			_tagDescriptionField.setText(tag.getDescription());
			_iIdOfTagToEdit = tag.getTagId();
		}
		else
		{
			_buttonCreate.setActionCommand(CREATE_ACTION_COMMAND);
			_buttonCreate.setText(CREATE_LABEL);
			_tagNameField.setText("");
			_tagSelectableField.setSelected(true);
			_tagDescriptionField.setText("");
			_iIdOfTagToEdit = -1;
			if (tag == null)
			{
				_tagParent.setSelectedTag(0);
			}
			else
			{
				_tagParent.setSelectedTag(tag.getTagId());
			}
		}
	}
}
