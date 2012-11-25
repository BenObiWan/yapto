package yapto.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import yapto.picturebank.IPictureBank;
import yapto.picturebank.PictureBankList;
import yapto.picturebank.PictureBankListChangedEvent;
import yapto.picturebank.config.IPictureBankConfiguration;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public final class PictureBankManagerPanel extends JPanel
{
	/**
	 * serialVersionUID for Serialization.
	 */
	private static final long serialVersionUID = 5033785594456871399L;

	/**
	 * Panel displaying the list of {@link IPictureBank} to load.
	 */
	private final JPanel _panelChooser = new JPanel(new GridLayout(0, 1));

	/**
	 * ButtonGroup used when only one {@link IPictureBank} can be selected.
	 */
	private final ButtonGroup _group = new ButtonGroup();

	/**
	 * CheckBox enabling or disabling the ability to open multiple
	 * {@link IPictureBank}.
	 */
	private final JCheckBox _checkBoxMulti = new JCheckBox("Multiple sources");

	/**
	 * The {@link PictureBankList}.
	 */
	private final PictureBankList _pictureBankList;

	/**
	 * Creates a new {@link PictureBankManagerPanel}.
	 * 
	 * @param pictureBankList
	 *            the {@link PictureBankList} to use.
	 */
	public PictureBankManagerPanel(final PictureBankList pictureBankList)
	{
		super(new BorderLayout(10, 10));
		_pictureBankList = pictureBankList;
		pictureBankList.register(this);

		setBorder(new EmptyBorder(10, 10, 10, 10));

		final JPanel panelBigChooser = new JPanel(new BorderLayout(5, 5));
		panelBigChooser.setBorder(new CompoundBorder(BorderFactory
				.createRaisedBevelBorder(), new EmptyBorder(10, 10, 10, 10)));
		final JScrollPane scrollPaneChooser = new JScrollPane(_panelChooser);
		scrollPaneChooser
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		final JPanel panelButtonsChooser = new JPanel(new GridLayout(1, 0, 10,
				0));
		final JPanel panelBottomChooser = new JPanel(new GridLayout(2, 1, 5, 0));
		panelBottomChooser.add(_checkBoxMulti);
		panelBottomChooser.add(panelButtonsChooser);
		panelBigChooser.add(scrollPaneChooser, BorderLayout.CENTER);
		panelBigChooser.add(panelBottomChooser, BorderLayout.PAGE_END);
		panelButtonsChooser.add(new JButton("Add"));
		panelButtonsChooser.add(new JButton("Remove"));

		_checkBoxMulti.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				reload();
			}
		});

		final JPanel panelButtons = new JPanel(new GridLayout(1, 0, 10, 0));

		panelButtons.add(new JButton("Open"));
		panelButtons.add(new JButton("Cancel"));

		add(panelBigChooser, BorderLayout.CENTER);
		add(panelButtons, BorderLayout.PAGE_END);

		reload();
	}

	/**
	 * Clear the list of registered {@link IPictureBank}.
	 */
	private void clearAll()
	{
		final Enumeration<AbstractButton> list = _group.getElements();
		while (list.hasMoreElements())
		{
			final AbstractButton but = list.nextElement();
			_group.remove(but);
		}
		_panelChooser.removeAll();
	}

	private void addPictureBank(
			final IPictureBankConfiguration pictureBankConfiguration)
	{
		if (_checkBoxMulti.isSelected())
		{
			final JCheckBox button = new JCheckBox(
					pictureBankConfiguration.getName());
			_panelChooser.add(button);
		}
		else
		{
			final JRadioButton radio = new JRadioButton(
					pictureBankConfiguration.getName());
			_panelChooser.add(radio);
			_group.add(radio);
		}
	}

	protected void reload()
	{
		_panelChooser.setVisible(false);
		clearAll();

		for (final IPictureBankConfiguration conf : _pictureBankList
				.getAllPictureBankConfiguration())
		{
			addPictureBank(conf);

		}

		for (final IPictureBank<?> sel : _pictureBankList
				.getSelectedPictureBank())
		{
			// TODO
		}

		_panelChooser.setVisible(true);
	}

	@Subscribe
	public void handlePictureBankListChangedEvent(
			@SuppressWarnings("unused") final PictureBankListChangedEvent ev)
	{
		reload();
	}

	public static void main(final String[] args)
	{
		final JFrame main = new JFrame("test");
		final EventBus _bus = new EventBus();
		final PictureBankList pictureBankList = new PictureBankList(_bus);

		main.setContentPane(new PictureBankManagerPanel(pictureBankList));
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.pack();
		main.setVisible(true);
	}
}
