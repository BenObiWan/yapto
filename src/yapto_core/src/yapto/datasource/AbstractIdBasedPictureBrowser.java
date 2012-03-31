package yapto.datasource;

import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yapto.datasource.sqlfile.FsPicture;

import com.google.common.eventbus.EventBus;

public abstract class AbstractIdBasedPictureBrowser<PICTURE extends IPicture>
		extends AbstractPictureBrowser<PICTURE>
{
	/**
	 * Logger object.
	 */
	protected static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractIdBasedPictureBrowser.class);

	/**
	 * {@link ListIterator} on the {@link FsPicture} id.
	 */
	protected final ListIterator<String> _idIterator;

	/**
	 * Creates a new AbstractIdBasedPictureBrowser.
	 * 
	 * @param sourcePictureList
	 *            the source {@link IPictureList} for this
	 *            {@link IPictureBrowser}.
	 * @param bus
	 *            the {@link EventBus} used to signal registered objects of
	 *            changes in this {@link AbstractPictureBrowser}.
	 * @param idIterator
	 */
	public AbstractIdBasedPictureBrowser(
			final IPictureList<PICTURE> sourcePictureList, final EventBus bus,
			final ListIterator<String> idIterator)
	{
		super(sourcePictureList, bus);
		_idIterator = idIterator;
	}

	@Override
	public boolean hasNext()
	{
		synchronized (_lock)
		{
			return _idIterator.hasNext();
		}
	}

	@Override
	public boolean hasPrevious()
	{
		synchronized (_lock)
		{
			if (_idIterator.hasPrevious())
			{
				return _idIterator.previousIndex() != 0;
			}
			return false;
		}
	}

	@Override
	public PICTURE next()
	{
		synchronized (_lock)
		{
			try
			{
				_currentPicture = getPicture(_idIterator.next());
				_bus.post(new PictureChangedEvent());
				return _currentPicture;
			}
			catch (final ExecutionException e)
			{
				LOGGER.error("can't load next picture.", e);
				return null;
			}
		}
	}

	@Override
	public PICTURE previous()
	{
		synchronized (_lock)
		{
			try
			{
				_currentPicture = getPicture(_idIterator.previous());
				_bus.post(new PictureChangedEvent());
				return _currentPicture;
			}
			catch (final ExecutionException e)
			{
				LOGGER.error("can't load previous picture.", e);
				return null;
			}
		}
	}

	@Override
	public int nextIndex()
	{
		synchronized (_lock)
		{
			return _idIterator.nextIndex();
		}
	}

	@Override
	public int previousIndex()
	{
		synchronized (_lock)
		{
			return _idIterator.previousIndex();
		}
	}

	/**
	 * Get the {@link IPicture} with the specified id.
	 * 
	 * @param pictureId
	 *            the id of the picture.
	 * @return the requested {@link IPicture}.
	 */
	protected abstract PICTURE getPicture(String pictureId)
			throws ExecutionException;
}