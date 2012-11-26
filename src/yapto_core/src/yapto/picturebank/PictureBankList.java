package yapto.picturebank;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yapto.picturebank.config.IPictureBankConfiguration;
import yapto.picturebank.sqlfile.SQLFilePictureBankLoader;
import yapto.picturebank.sqlfile.config.ISQLFilePictureBankConfiguration;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import common.config.InvalidConfigurationException;

/**
 * Keeps track of all {@link IPictureBankConfiguration}s known to the
 * application, and also of which {@link IPictureBank} are selected and loaded.
 * 
 * @author benobiwan
 * 
 */
public final class PictureBankList
{
	/**
	 * Logger object.
	 */
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(PictureBankList.class);

	/**
	 * Set of all {@link IPictureBankConfiguration}.
	 */
	private final SortedSet<IPictureBankConfiguration> _allPictureBankConfSet = new TreeSet<>();

	/**
	 * Map of {@link IPictureBankConfiguration} by id.
	 */
	private final Map<Integer, IPictureBankConfiguration> _configurationByIdMap = new HashMap<>();

	/**
	 * Map with selected {@link IPictureBankConfiguration} and the corresponding
	 * {@link IPictureBank} object as value.
	 */
	private final Map<IPictureBankConfiguration, IPictureBank<?>> _selectedPictureBankMap = new HashMap<>();

	/**
	 * {@link EventBus} used to signal registered objects of changes in this
	 * {@link PictureBankList}.
	 */
	private final EventBus _bus;

	/**
	 * The {@link IPictureBankLoader}.
	 */
	private final SQLFilePictureBankLoader _loader;

	/**
	 * Creates a new {@link PictureBankList}.
	 * 
	 * @param bus
	 *            the {@link EventBus} used to signal registered objects of
	 *            changes in this {@link PictureBankList}.
	 * @throws InvalidConfigurationException
	 */
	public PictureBankList(final EventBus bus)
			throws InvalidConfigurationException
	{
		_bus = bus;
		_loader = new SQLFilePictureBankLoader();
		// TODO add check for duplicate ids
		for (final IPictureBankConfiguration conf : _loader
				.getAllConfiguration())
		{
			addPictureBank(conf);
		}
	}

	/**
	 * Add a new {@link IPictureBank}.
	 * 
	 * @param pictureBankConfiguration
	 *            {@link IPictureBankConfiguration} of the new
	 *            {@link IPictureBank}.
	 */
	public void addPictureBank(
			final IPictureBankConfiguration pictureBankConfiguration)
	{
		if (pictureBankConfiguration != null)
		{
			_allPictureBankConfSet.add(pictureBankConfiguration);
			_configurationByIdMap.put(Integer.valueOf(pictureBankConfiguration
					.getPictureBankId()), pictureBankConfiguration);
			_bus.post(new PictureBankListChangedEvent());
		}
	}

	/**
	 * Remove a {@link IPictureBank}.
	 * 
	 * @param pictureBankConfiguration
	 *            {@link IPictureBankConfiguration} of the {@link IPictureBank}
	 *            to remove.
	 */
	public void removePictureBank(
			final IPictureBankConfiguration pictureBankConfiguration)
	{
		_allPictureBankConfSet.remove(pictureBankConfiguration);
		_configurationByIdMap.remove(Integer.valueOf(pictureBankConfiguration
				.getPictureBankId()));
		final IPictureBank<?> bank = _selectedPictureBankMap
				.remove(pictureBankConfiguration);
		if (bank != null)
		{
			bank.close();
		}
		_bus.post(new PictureBankListChangedEvent());
	}

	/**
	 * Select {@link IPictureBank} by id.
	 * 
	 * @param iIds
	 *            ids of the {@link IPictureBank} to select.s
	 */
	public void selectPictureBankById(final int... iIds)
	{
		final Set<IPictureBankConfiguration> confToLoad = new HashSet<>();
		for (final int iId : iIds)
		{
			final IPictureBankConfiguration conf = _configurationByIdMap
					.get(Integer.valueOf(iId));
			confToLoad.add(conf);
		}
		selectPictureBank(confToLoad);
	}

	/**
	 * Select {@link IPictureBank}s by {@link IPictureBankConfiguration}.
	 * 
	 * @param confList
	 *            {@link IPictureBankConfiguration} of the {@link IPictureBank}
	 *            to select.
	 */
	public void selectPictureBank(final IPictureBankConfiguration... confList)
	{
		final Set<IPictureBankConfiguration> confToLoad = Sets
				.newHashSet(confList);
		selectPictureBank(confToLoad);
	}

	/**
	 * Select {@link IPictureBank}s by {@link IPictureBankConfiguration}.
	 * 
	 * @param confToLoad
	 *            {@link IPictureBankConfiguration} of the {@link IPictureBank}
	 *            to select.
	 */
	private void selectPictureBank(
			final Set<IPictureBankConfiguration> confToLoad)
	{
		// Closing no longer used {@link IPictureBank}.
		for (final IPictureBankConfiguration bankToClose : Sets.difference(
				_selectedPictureBankMap.keySet(), confToLoad))
		{
			final IPictureBank<?> bank = _selectedPictureBankMap
					.remove(bankToClose);
			if (bank != null)
			{
				bank.close();
			}
		}
		// Opening new {@link IPictureBank}.
		for (final IPictureBankConfiguration bankToOpen : Sets.difference(
				confToLoad, _selectedPictureBankMap.keySet()))
		{
			try
			{
				final IPictureBank<?> bank = open(bankToOpen);
				if (bank != null)
				{
					_selectedPictureBankMap.put(bankToOpen, bank);
				}
			}
			catch (ClassNotFoundException | SQLException | IOException e)
			{
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Open an {@link IPictureBank}.
	 * 
	 * @param conf
	 *            {@link IPictureBankConfiguration} describing the
	 *            {@link IPictureBank} to load.
	 * @return the loaded {@link IPictureBank}.
	 * @throws ClassNotFoundException
	 *             TODO
	 * @throws SQLException
	 * @throws IOException
	 */
	private IPictureBank<?> open(final IPictureBankConfiguration conf)
			throws ClassNotFoundException, SQLException, IOException
	{
		if (conf instanceof ISQLFilePictureBankConfiguration)
		{
			return _loader
					.getPictureBank((ISQLFilePictureBankConfiguration) conf);
		}
		return null;
	}

	/**
	 * Get the selected {@link IPictureBank}s.
	 * 
	 * @return the selected {@link IPictureBank}s.
	 */
	public Set<IPictureBank<?>> getSelectedPictureBank()
	{
		return Collections.unmodifiableSet(Sets
				.newHashSet(_selectedPictureBankMap.values()));
	}

	/**
	 * Get all {@link IPictureBank}s.
	 * 
	 * @return all {@link IPictureBank}s.
	 */
	public Set<IPictureBankConfiguration> getAllPictureBankConfiguration()
	{
		return Collections.unmodifiableSet(_allPictureBankConfSet);
	}

	/**
	 * Register an object to the listen for change in this
	 * {@link PictureBankList}.
	 * 
	 * @param object
	 *            the object to register.
	 */
	public void register(final Object object)
	{
		_bus.register(object);
	}
}
