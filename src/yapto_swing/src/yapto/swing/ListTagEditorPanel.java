package yapto.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yapto.picturebank.IPicture;
import yapto.picturebank.IPictureBank;
import yapto.picturebank.PictureBankList;
import yapto.picturebank.tag.ITag;

/**
 * Panel displaying the list of {@link ITag} that can be associated with an
 * {@link IPicture} using a {@link JList}.
 * 
 * @author benobiwan
 * 
 */
public final class ListTagEditorPanel extends AbstractTagEditorPanel
{
	/**
	 * serialVersionUID for Serialization.
	 */
	private static final long serialVersionUID = 1577570873903931403L;

	/**
	 * Logger object.
	 */
	protected static transient final Logger LOGGER = LoggerFactory
			.getLogger(ListTagEditorPanel.class);

	/**
	 * {@link JList} used to display all the {@link ITag}s.
	 */
	private final JList<ITag> _tagList;

	/**
	 * Vector of all {@link ITag}s.
	 */
	private final Vector<ITag> _vTags = new Vector<>();

	/**
	 * Boolean used to prevent the modification of the tags associated to an
	 * {@link IPicture} when the picture is changing.
	 */
	private volatile boolean _bDoNotSaveTags = false;

	/**
	 * Creates a new ListTagEditorPanel.
	 * 
	 * @param parent
	 *            parent {@link Frame}.
	 * @param bankList
	 *            the {@link PictureBankList} used to load the
	 *            {@link IPictureBank} used as source for the {@link IPicture}.
	 */
	public ListTagEditorPanel(final Frame parent, final PictureBankList bankList)
	{
		super(parent, bankList);
		_tagList = new JList<>();
		_tagList.setLayoutOrientation(JList.VERTICAL);
		_tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		final JScrollPane scrollPane = new JScrollPane(_tagList);
		add(scrollPane, BorderLayout.CENTER);
		changePictureBrowser();
		changePicture();
		_tagList.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				savePictureTags();
			}
		});
	}

	@Override
	protected void selectAppropriateTags()
	{
		_bDoNotSaveTags = true;
		synchronized (_lock)
		{
			if (_pictureBrowser != null)
			{
				_tagList.clearSelection();
				if (_picture != null)
				{
					final Set<ITag> tags = _picture.getTagSet();
					final int[] indices = new int[tags.size()];
					int i = 0;
					for (final ITag t : tags)
					{
						indices[i] = _vTags.indexOf(t);
						i++;
					}
					_tagList.setSelectedIndices(indices);
				}
			}
		}
		_bDoNotSaveTags = false;
	}

	@Override
	protected void updateAvailableTags()
	{
		synchronized (_lock)
		{
			if (_pictureBrowser != null)
			{
				_vTags.clear();
				for (final ITag t : _pictureBrowser.getTagSet())
				{
					if (t.isSelectable())
					{
						_vTags.add(t);
					}
				}
				_tagList.setListData(_vTags);
			}
		}
	}

	@Override
	protected void savePictureTags()
	{
		if (!_bDoNotSaveTags)
		{
			synchronized (_lock)
			{
				if (_pictureBrowser != null)
				{
					if (_picture != null)
					{
						_picture.setTagList(_tagList.getSelectedValuesList());
					}
				}
			}
		}
	}
}
