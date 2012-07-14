package yapto.datasource;

/**
 * An object used to browse pictures from an {@link IDataSource}.
 * 
 * @author benobiwan
 * @param <PICTURE>
 *            type of {@link IPicture} of this {@link IPictureBrowser}.
 */
public interface IPictureBrowser<PICTURE extends IPicture> extends
		IPictureList<PICTURE>
{
	/**
	 * Get the current {@link IPicture}.
	 * 
	 * @return the current {@link IPicture}.
	 */
	PICTURE getCurrentPicture();

	/**
	 * Get the index of the current picture.
	 * 
	 * @return the index of the current picture.
	 */
	int getCurrentIndex();

	/**
	 * Returns true if this IPictureBrowser has more elements when traversing
	 * the list in the forward direction.
	 * 
	 * @return true if this IPictureBrowser has more elements when traversing
	 *         the list in the forward direction.
	 */
	boolean hasNext();

	/**
	 * Returns the next element in the list and advances the cursor position.
	 * 
	 * @return the next element in the list.
	 */
	PICTURE next();

	/**
	 * Returns the index of the next element.
	 * 
	 * @return the index of the next element.
	 */
	int nextIndex();

	/**
	 * Returns true if this IPictureBrowser has more elements when traversing
	 * the list in the reverse direction.
	 * 
	 * @return true if this IPictureBrowser has more elements when traversing
	 *         the list in the reverse direction.
	 */
	boolean hasPrevious();

	/**
	 * Returns the next previous in the list and advances the cursor position.
	 * 
	 * @return the next previous in the list.
	 */
	PICTURE previous();

	/**
	 * Returns the index of the previous element.
	 * 
	 * @return the index of the previous element.
	 */
	int previousIndex();

	/**
	 * Register an object to the listen for change in this
	 * {@link IPictureBrowser}.
	 * 
	 * @param object
	 *            the object to register.
	 */
	void register(Object object);

	/**
	 * Unregister an object to the listen for change in this
	 * {@link IPictureBrowser}.
	 * 
	 * @param object
	 *            the object to unregister.
	 */
	void unRegister(Object object);

	/**
	 * Get the {@link IDataSource} which created this {@link IPictureBrowser}.
	 * 
	 * @return the {@link IDataSource} which created this
	 *         {@link IPictureBrowser}.
	 */
	IDataSource<PICTURE> getDataSource();
}
