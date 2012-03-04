package yapto.datasource.sqlfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import yapto.datasource.IPicture;
import yapto.datasource.sqlfile.config.ISQLFileDataSourceConfiguration;
import yapto.datasource.tag.Tag;

/**
 * Object holding the connection to the database and the prepared statements.
 * 
 * @author benobiwan
 * 
 */
public final class SQLFileListConnection
{
	/**
	 * Configuration for this {@link SQLFileDataSource}.
	 */
	private final ISQLFileDataSourceConfiguration _conf;

	// tag table
	/**
	 * Name for the 'tag' table.
	 */
	public static final String TAG_TABLE_NAME = "tag";

	/**
	 * Name for the 'id' column of the 'tag' table.
	 */
	public static final String TAG_ID_COLUMN_NAME = "id";

	/**
	 * Name for the 'name' column of the 'tag' table.
	 */
	public static final String TAG_NAME_COLUMN_NAME = "name";

	/**
	 * Name for the 'description' column of the 'tag' table.
	 */
	public static final String TAG_DESCRIPTION_COLUMN_NAME = "description";

	/**
	 * Name for the 'parentId' column of the 'tag' table.
	 */
	public static final String TAG_PARENT_ID_COLUMN_NAME = "parentId";

	/**
	 * Name for the 'selectable' column of the 'tag' table.
	 */
	public static final String TAG_SELECTABLE_COLUMN_NAME = "selectable";

	// picture table
	/**
	 * Name for the 'picture' table.
	 */
	public static final String PICTURE_TABLE_NAME = "picture";

	/**
	 * Name for the 'id' column of the 'picture' table.
	 */
	public static final String PICTURE_ID_COLUMN_NAME = "id";

	/**
	 * Name for the 'grade' column of the 'picture' table.
	 */
	public static final String PICTURE_GRADE_COLUMN_NAME = "grade";

	/**
	 * Name for the 'width' column of the 'picture' table.
	 */
	public static final String PICTURE_WIDTH_COLUMN_NAME = "width";

	/**
	 * Name for the 'mark' column of the 'picture' table.
	 */
	public static final String PICTURE_HEIGTH_COLUMN_NAME = "height";

	/**
	 * Name for the 'timestamp' column of the 'picture' table.
	 */
	public static final String PICTURE_TIMESTAMP_COLUMN_NAME = "timestamp";

	/**
	 * Name for the 'path' column of the 'picture' table.
	 */
	public static final String PICTURE_PATH_COLUMN_NAME = "path";

	// picture_tag table
	/**
	 * Name for the 'picture_tag' table.
	 */
	public static final String PICTURE_TAG_TABLE_NAME = "picture_tag";

	/**
	 * Name for the 'tagId' column of the 'picture_tag' table.
	 */
	public static final String PICTURE_TAG_TAG_ID_COLUMN_NAME = "tagId";

	/**
	 * Name for the 'pictureId' column of the 'picture_tag' table.
	 */
	public static final String PICTURE_TAG_PICTURE_ID_COLUMN_NAME = "pictureId";

	/**
	 * Connection to the database.
	 */
	private final Connection _connection;

	/**
	 * Statement to insert a {@link Tag}.
	 */
	private final PreparedStatement _psInsertTag;

	/**
	 * Statement to count the number of {@link IPicture}s having a given
	 * {@link Tag}.
	 */
	private final PreparedStatement _psCountPicturesByTag;

	/**
	 * Statement to count the total number of {@link IPicture}s in this
	 * {@link SQLFileDataSource}.
	 */
	private final PreparedStatement _psCountPictures;

	/**
	 * Statement to select all the {@link IPicture}s having a given {@link Tag}.
	 */
	private final PreparedStatement _psSelectPicturesByTag;

	/**
	 * Statement to insert an {@link IPicture} in the database.
	 */
	private final PreparedStatement _psInsertPicture;

	/**
	 * Statement to update the mark and the timestamp of the {@link IPicture}.
	 */
	private final PreparedStatement _psUpdatePictureMarkAndTimestamp;

	/**
	 * Statement to insert a {@link Tag} for an {@link IPicture}.
	 */
	private final PreparedStatement _psInsertTagForPicture;

	/**
	 * Statement to remove all the {@link Tag}s of an {@link IPicture}.
	 */
	private final PreparedStatement _psRemoveTagsForPicture;

	/**
	 * Statement to load a picture.
	 */
	private final PreparedStatement _psLoadPicture;

	/**
	 * Statement to load all the {@link Tag}s of an {@link IPicture}.
	 */
	private final PreparedStatement _psLoadTagsOfPicture;

	/**
	 * creates a new SQLFileListConnection.
	 * 
	 * @param conf
	 *            configuration for this {@link SQLFileListConnection}.
	 * @throws SQLException
	 *             if an SQL error occurred during the connection to the
	 *             database.
	 * @throws ClassNotFoundException
	 *             if the database driver class can't be found.
	 */
	public SQLFileListConnection(final ISQLFileDataSourceConfiguration conf)
			throws ClassNotFoundException, SQLException
	{
		_conf = conf;
		Class.forName("org.sqlite.JDBC");
		_connection = DriverManager.getConnection("jdbc:sqlite:"
				+ _conf.getDatabaseFileName());
		_psInsertTag = _connection.prepareStatement("insert into "
				+ TAG_TABLE_NAME + " (" + TAG_ID_COLUMN_NAME + ", "
				+ TAG_NAME_COLUMN_NAME + ", " + TAG_DESCRIPTION_COLUMN_NAME
				+ ", " + TAG_PARENT_ID_COLUMN_NAME + ", "
				+ TAG_SELECTABLE_COLUMN_NAME + ") values(?, ?, ?, ?, ?)");
		_psCountPicturesByTag = _connection.prepareStatement("select count("
				+ PICTURE_TAG_PICTURE_ID_COLUMN_NAME + ") FROM "
				+ PICTURE_TAG_TABLE_NAME + " WHERE "
				+ PICTURE_TAG_TAG_ID_COLUMN_NAME + "=?");
		_psSelectPicturesByTag = _connection.prepareStatement("select "
				+ PICTURE_TAG_PICTURE_ID_COLUMN_NAME + " FROM "
				+ PICTURE_TAG_TABLE_NAME + " WHERE "
				+ PICTURE_TAG_TAG_ID_COLUMN_NAME + "=?");
		_psCountPictures = _connection.prepareStatement("select count("
				+ PICTURE_ID_COLUMN_NAME + ") FROM " + PICTURE_TABLE_NAME);
		_psInsertPicture = _connection.prepareStatement("insert into "
				+ PICTURE_TABLE_NAME + " (" + PICTURE_ID_COLUMN_NAME + ", "
				+ PICTURE_GRADE_COLUMN_NAME + ", " + PICTURE_WIDTH_COLUMN_NAME
				+ ", " + PICTURE_HEIGTH_COLUMN_NAME + ", "
				+ PICTURE_TIMESTAMP_COLUMN_NAME + ", "
				+ PICTURE_PATH_COLUMN_NAME + ") values(?, ?, ?, ?, ?, ?)");
		_psUpdatePictureMarkAndTimestamp = _connection
				.prepareStatement("update " + PICTURE_TABLE_NAME + " set "
						+ PICTURE_GRADE_COLUMN_NAME + "=?, "
						+ PICTURE_TIMESTAMP_COLUMN_NAME + "=? where "
						+ PICTURE_ID_COLUMN_NAME + "=?");
		_psInsertTagForPicture = _connection.prepareStatement("insert into "
				+ PICTURE_TAG_TABLE_NAME + " ("
				+ PICTURE_TAG_TAG_ID_COLUMN_NAME + ", "
				+ PICTURE_TAG_PICTURE_ID_COLUMN_NAME + ") values(?, ?)");
		_psRemoveTagsForPicture = _connection.prepareStatement("delete from "
				+ PICTURE_TAG_TABLE_NAME + " where "
				+ PICTURE_TAG_TAG_ID_COLUMN_NAME + "=?");
		_psLoadPicture = _connection.prepareStatement("select "
				+ PICTURE_GRADE_COLUMN_NAME + ", " + PICTURE_WIDTH_COLUMN_NAME
				+ ", " + PICTURE_HEIGTH_COLUMN_NAME + ", "
				+ PICTURE_TIMESTAMP_COLUMN_NAME + ", "
				+ PICTURE_PATH_COLUMN_NAME + " from " + PICTURE_TABLE_NAME
				+ " where " + PICTURE_ID_COLUMN_NAME + " =?");
		_psLoadTagsOfPicture = _connection.prepareStatement("select "
				+ PICTURE_TAG_TAG_ID_COLUMN_NAME + " from "
				+ PICTURE_TAG_TABLE_NAME + " where "
				+ PICTURE_TAG_PICTURE_ID_COLUMN_NAME + " =?");
	}

	/**
	 * Save the given {@link Tag} to the database.
	 * 
	 * @param tag
	 *            the {@link Tag} to save.
	 * @throws SQLException
	 *             if an SQL error occurred during the saving of the {@link Tag}
	 *             .
	 */
	public void saveTagToDatabase(final Tag tag) throws SQLException
	{
		_psInsertTag.clearParameters();
		_psInsertTag.setInt(1, tag.getTagId());
		_psInsertTag.setString(2, tag.getName());
		_psInsertTag.setString(3, tag.getDescription());
		if (tag.getParent() == null)
		{
			_psInsertTag.setInt(4, -1);
		}
		else
		{
			_psInsertTag.setInt(4, tag.getParent().getTagId());
		}
		_psInsertTag.setBoolean(5, tag.isSelectable());
		_psInsertTag.executeUpdate();
	}

	/**
	 * Create the tables of this database.
	 * 
	 * @throws SQLException
	 *             if an SQL error occurred during the creation of the tables.
	 */
	public void createTables() throws SQLException
	{
		final Statement statement = _connection.createStatement();
		// Tag table
		statement.executeUpdate("create table " + TAG_TABLE_NAME
				+ " if not exists (" + TAG_ID_COLUMN_NAME + " integer, "
				+ TAG_NAME_COLUMN_NAME + " text, "
				+ TAG_DESCRIPTION_COLUMN_NAME + " text, "
				+ TAG_PARENT_ID_COLUMN_NAME + " integer, "
				+ TAG_SELECTABLE_COLUMN_NAME + " boolean)");
		// picture table
		statement.executeUpdate("create table " + PICTURE_TABLE_NAME
				+ " if not exists (" + PICTURE_ID_COLUMN_NAME + " text, "
				+ PICTURE_GRADE_COLUMN_NAME + " integer, "
				+ PICTURE_WIDTH_COLUMN_NAME + " integer, "
				+ PICTURE_HEIGTH_COLUMN_NAME + " integer, "
				+ PICTURE_TIMESTAMP_COLUMN_NAME + " integer, "
				+ PICTURE_PATH_COLUMN_NAME + " text)");
		// picture_tag table
		statement.executeUpdate("create table " + PICTURE_TAG_TABLE_NAME
				+ " if not exists (" + PICTURE_TAG_TAG_ID_COLUMN_NAME
				+ " integer, " + PICTURE_TAG_PICTURE_ID_COLUMN_NAME
				+ " integer)");
	}

	/**
	 * Load the list of tags.
	 * 
	 * @return a {@link ResultSet} containing the list of tags.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public ResultSet loadTagList() throws SQLException
	{
		final Statement statementLoad = _connection.createStatement();
		return statementLoad
				.executeQuery("select " + TAG_ID_COLUMN_NAME + ", "
						+ TAG_NAME_COLUMN_NAME + ", "
						+ TAG_DESCRIPTION_COLUMN_NAME + ", "
						+ TAG_SELECTABLE_COLUMN_NAME + " from "
						+ TAG_TABLE_NAME);
	}

	/**
	 * Load the list of tag id and there parent id.
	 * 
	 * @return a {@link ResultSet} containing the list of tag id and there
	 *         parent id.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public ResultSet loadParents() throws SQLException
	{
		final Statement statementParent = _connection.createStatement();
		return statementParent.executeQuery("select " + TAG_ID_COLUMN_NAME
				+ ", " + TAG_PARENT_ID_COLUMN_NAME + " from " + TAG_TABLE_NAME);
	}

	/**
	 * Insert a {@link FsPicture} in the database.
	 * 
	 * @param picture
	 *            the {@link FsPicture} to insert.
	 * @return true if the picture was correctly inserted.
	 * @throws SQLException
	 *             if an SQL error occurred during the insertion in the
	 *             database.
	 */
	public boolean insertPicture(final FsPicture picture) throws SQLException
	{
		_psInsertPicture.clearParameters();
		_psInsertPicture.setString(1, picture.getId());
		_psInsertPicture.setInt(2, picture.getPictureGrade());
		_psInsertPicture.setInt(3, picture.getWidth());
		_psInsertPicture.setInt(4, picture.getHeight());
		_psInsertPicture.setLong(5, picture.getTimestamp());
		_psInsertPicture.setString(6, picture.getImagePath().toString());
		return _psInsertPicture.executeUpdate() > 0;
	}

	/**
	 * Update the {@link Tag} list of a {@link FsPicture}.
	 * 
	 * @param picture
	 *            the {@link FsPicture} to update.
	 * @throws SQLException
	 *             if an SQL error occurred during the insertion in the
	 *             database.
	 */
	public void updateTags(final FsPicture picture) throws SQLException
	{
		_psRemoveTagsForPicture.clearParameters();
		_psRemoveTagsForPicture.setString(1, picture.getId());
		_psRemoveTagsForPicture.execute();
		for (final Tag tag : picture.getTagSet())
		{
			_psInsertTagForPicture.clearParameters();
			_psInsertTagForPicture.setInt(1, tag.getTagId());
			_psInsertTagForPicture.setString(2, picture.getId());
			_psInsertTagForPicture.executeUpdate();
		}
	}

	/**
	 * Update the meta-informations of the specified picture.
	 * 
	 * @param picture
	 *            the {@link FsPicture} to update.
	 * @throws SQLException
	 *             if an SQL error occurred during the insertion in the
	 *             database.
	 */
	public void updatePicture(final FsPicture picture) throws SQLException
	{
		_psUpdatePictureMarkAndTimestamp.clearParameters();
		_psUpdatePictureMarkAndTimestamp.setInt(1, picture.getPictureGrade());
		_psUpdatePictureMarkAndTimestamp.setLong(2, picture.getTimestamp());
		_psUpdatePictureMarkAndTimestamp.setString(3, picture.getId());
		_psUpdatePictureMarkAndTimestamp.executeUpdate();
		updateTags(picture);
	}

	/**
	 * Count the number of pictures which have a given {@link Tag}.
	 * 
	 * @param tag
	 *            the {@link Tag} to consider.
	 * @return the number of pictures which have a given {@link Tag}.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public int countPictures(final Tag tag) throws SQLException
	{
		_psCountPicturesByTag.clearParameters();
		_psCountPicturesByTag.setInt(1, tag.getTagId());
		final ResultSet res = _psCountPicturesByTag.executeQuery();
		if (res.next())
		{
			return res.getInt(PICTURE_TAG_PICTURE_ID_COLUMN_NAME);
		}
		return 0;
	}

	/**
	 * Count the total number of pictures.
	 * 
	 * @return the total number of pictures.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public int countPictures() throws SQLException
	{
		final ResultSet res = _psCountPictures.executeQuery();
		if (res.next())
		{
			return res.getInt(PICTURE_ID_COLUMN_NAME);
		}
		return 0;
	}

	/**
	 * Select the list of pictures which have a given {@link Tag}.
	 * 
	 * @param tag
	 *            the {@link Tag} to consider.
	 * @return the list of pictures which have a given {@link Tag}.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public String[] selectPictures(final Tag tag) throws SQLException
	{
		_psSelectPicturesByTag.clearParameters();
		_psSelectPicturesByTag.setInt(1, tag.getTagId());
		final LinkedList<String> pictureList = new LinkedList<String>();
		final ResultSet response = _psSelectPicturesByTag.executeQuery();
		while (response.next())
		{
			pictureList.add(response
					.getString(PICTURE_TAG_PICTURE_ID_COLUMN_NAME));
		}
		final String[] res = new String[pictureList.size()];
		return pictureList.toArray(res);
	}

	/**
	 * Load the information about the specified picture.
	 * 
	 * @param strPictureId
	 *            the id of the picture.
	 * @return the information about the specified picture.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public ResultSet loadPicture(final String strPictureId) throws SQLException
	{
		_psLoadPicture.clearParameters();
		_psLoadPicture.setString(1, strPictureId);
		return _psLoadPicture.executeQuery();
	}

	/**
	 * Load all the {@link Tag}s of an {@link IPicture}.
	 * 
	 * @param strPictureId
	 *            the id of the picture.
	 * @return the list of {@link Tag}s of the picture.
	 * @throws SQLException
	 *             if an SQL error occurred during the interrogation of the
	 *             database.
	 */
	public String[] loadTagsOfPicture(final String strPictureId)
			throws SQLException
	{
		_psLoadTagsOfPicture.clearParameters();
		_psLoadTagsOfPicture.setString(1, strPictureId);
		final LinkedList<String> tagList = new LinkedList<String>();
		final ResultSet response = _psLoadTagsOfPicture.executeQuery();
		while (response.next())
		{
			tagList.add(response.getString(PICTURE_TAG_TAG_ID_COLUMN_NAME));
		}
		final String[] res = new String[tagList.size()];
		return tagList.toArray(res);
	}
}
