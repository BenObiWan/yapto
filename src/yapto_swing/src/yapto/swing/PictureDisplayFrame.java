package yapto.swing;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;

import yapto.datasource.IDataSource;
import yapto.datasource.IPicture;
import yapto.datasource.IPictureBrowser;
import yapto.datasource.dummy.DummyDataSource;
import yapto.datasource.dummy.DummyPicture;
import yapto.datasource.dummy.DummyPictureBrowser;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

/**
 * {@link JFrame} used to display an picture and it's information. Main frame of
 * the application and starting class.
 * 
 * @author benobiwan
 * 
 */
public final class PictureDisplayFrame extends JFrame
{
	/**
	 * serialVersionUID for Serialization.
	 */
	private static final long serialVersionUID = -4401831166047624407L;

	/**
	 * Creates a new PictureDisplayFrame.
	 * 
	 * @param pictureBrowser
	 *            the {@link IPictureBrowser} used as source for the
	 *            {@link IPicture}.
	 */
	public PictureDisplayFrame(final IPictureBrowser<?> pictureBrowser)
	{
		super("yapto");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		BasicConfigurator.configure();

		final MainPictureDisplayPanel contentPane = new MainPictureDisplayPanel(
				pictureBrowser);
		setContentPane(contentPane);
	}

	/**
	 * Main function.
	 * 
	 * @param args
	 *            command line parameters.
	 */
	public static void main(final String[] args)
	{
		final IDataSource<DummyPicture> dataSource = new DummyDataSource();
		final Executor exec = Executors.newFixedThreadPool(10);
		final EventBus bus = new AsyncEventBus(exec);

		final IPictureBrowser<?> pictureBrowser = new DummyPictureBrowser(
				dataSource, bus);
		final PictureDisplayFrame main = new PictureDisplayFrame(pictureBrowser);
		main.pack();
		main.setVisible(true);
	}
}
