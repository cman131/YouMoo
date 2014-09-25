package Selenium;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class youMu
{
	// Bys
	private final static By SEARCH_FIELD_BY = By.id("masthead-search-term");
	private final static By SEARCH_BUTTON_BY = By.id("search-btn");
	private final static By RESULT_LIST_BY = By.id("search-results");
	private final static By RESULT_LINK_BY = By
			.cssSelector("a[class*='yt-uix-tile-link']");
	private final static By RESULT_BY = By
			.cssSelector("li[class*='yt-lockup'][class*='yt-lockup-video']");

	private final static By RELATED_VIDEO_LINK_BY = By
			.cssSelector("a[class*='related-video']");
	private final static By VIDEO_BY = By.id("movie_player");
	private final static By VIDEO_TIME_BY = By.className("video-time");

	// Swing
	private final JFrame win = new JFrame();
	private final JButton button = new JButton("Start");
	private final JTextField field = new JTextField();
	private final JRadioButton radio1 = new JRadioButton("Chrome");
	private final JRadioButton radio2 = new JRadioButton("FireFox");

	// Data
	private final String settingsFilePath = "settings.yoho";
	private String searchTerm = "music";
	private boolean isChrome = true;
	private boolean GUI_ON = true;

	public static void main(final String[] args) throws InterruptedException,
			IOException
	{
		final youMu dis = new youMu();
		dis.populate();

		if (dis.GUI_ON)
		{
			JFrame.setDefaultLookAndFeelDecorated(true);
			dis.win.setLayout(new BorderLayout());
			dis.win.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dis.win.setSize(300, 200);

			final JPanel pane = new JPanel();
			pane.add(new JLabel("Set Your Search Term"), BorderLayout.NORTH);
			dis.field.setBounds(0, 0, 100, 30);
			dis.field.setText("ajfhgaslkdfhglshdflgkjhslfhgskl");
			pane.add(dis.field, BorderLayout.CENTER);

			final JPanel lowPane = new JPanel();
			lowPane.setLayout(new BorderLayout());
			lowPane.add(dis.button, BorderLayout.SOUTH);
			final JPanel gridPane = new JPanel();
			gridPane.setLayout(new GridLayout(1, 2));
			final ButtonGroup group = new ButtonGroup();
			group.add(dis.radio1);
			group.add(dis.radio2);
			gridPane.add(dis.radio1);
			gridPane.add(dis.radio2);
			lowPane.add(gridPane, BorderLayout.CENTER);

			dis.button.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					dis.win.dispose();
					dis.searchTerm = dis.field.getText().trim();
					dis.isChrome = dis.radio1.isSelected();
					dis.setDefaults();
					try
					{
						dis.execute();
					}
					catch (final IOException e1)
					{
						e1.printStackTrace();
					}
				}
			});
			dis.field.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					dis.win.dispose();
					dis.searchTerm = dis.field.getText().trim();
					dis.isChrome = dis.radio1.isSelected();
					dis.setDefaults();
					try
					{
						dis.execute();
					}
					catch (final IOException e1)
					{
						e1.printStackTrace();
					}
				}
			});
			dis.win.add(lowPane, BorderLayout.SOUTH);
			dis.win.add(pane, BorderLayout.CENTER);
			dis.win.setVisible(true);
			dis.field.setText(dis.searchTerm);
			dis.radio1.setSelected(true);
		}
		else
		{
			dis.execute();
		}
	}

	public void execute() throws IOException
	{
		WebDriver driver = null;
		if (isChrome)
		{
			final File file = new File("./drivers/adblock.crx");
			final ChromeOptions options = new ChromeOptions();
			options.addExtensions(file);
			// driver = getChromeDriver();
			driver = getChromeDriverFromNearby(options);
		}
		else
		{
			final File file = new File("./drivers/adblock.xpi");
			final FirefoxProfile firefoxProfile = new FirefoxProfile();
			firefoxProfile.addExtension(file);
			driver = new FirefoxDriver(firefoxProfile);
		}

		try
		{
			driver.manage().window().setSize(new Dimension(0, 0));
			final Object[] handles = driver.getWindowHandles().toArray();
			for (final Object handlee : handles)
			{
				System.out.println(handlee);
			}
			for (int i = 1; i < handles.length; i++)
			{
				final String handle = (String) handles[i];
				driver.switchTo().window(handle);
				driver.close();
			}
			driver.switchTo().window((String) handles[0]);
			driver.get("http://www.youtube.com");

			driver.findElement(youMu.SEARCH_FIELD_BY).click();
			driver.findElement(youMu.SEARCH_FIELD_BY).clear();
			driver.findElement(youMu.SEARCH_FIELD_BY).sendKeys(searchTerm);
			driver.findElement(youMu.SEARCH_BUTTON_BY).click();

			waitForElementVisible(driver, youMu.RESULT_LIST_BY, 5000);

			final List<WebElement> items = driver.findElement(youMu.RESULT_LIST_BY)
					.findElements(youMu.RESULT_BY);
			WebElement item = items.get(new Random().nextInt(items.size() - 1));
			while (item.findElement(youMu.RESULT_LINK_BY).getAttribute("class")
					.contains("g-hovercard"))
			{
				item = items.get(new Random().nextInt(items.size() - 1));
			}
			String[] unparsedTime = item.findElement(youMu.VIDEO_TIME_BY).getText()
					.trim().split(":");
			int time = 300;
			item.findElement(youMu.RESULT_LINK_BY).click();

			while (true)
			{
				if (unparsedTime.length == 2)
				{
					time = 5 + Integer.parseInt(unparsedTime[1])
							+ Integer.parseInt(unparsedTime[0]) * 60;
				}
				else
				{
					time = 5 + Integer.parseInt(unparsedTime[2])
							+ Integer.parseInt(unparsedTime[1]) * 60
							+ Integer.parseInt(unparsedTime[0]) * 3600;
				}
				// driver.manage().timeouts().implicitlyWait(300000,
				// TimeUnit.SECONDS);
				final WebDriverWait wait = new WebDriverWait(driver, 5);
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(youMu.VIDEO_BY));
				// String url = driver.getCurrentUrl();
				// url = url.subSequence(0, url.indexOf('?'))
				// + "?enablejsapi=1&version=3&playerapiid=ytplayer";
				// driver.get(url);
				System.out.println("Found");
				Thread.sleep(time * 1000);
				System.out.println("Movin on");
				item = driver.findElements(youMu.RELATED_VIDEO_LINK_BY).get(
						new Random().nextInt(5));
				unparsedTime = item.findElement(youMu.VIDEO_TIME_BY).getText()
						.trim().split(":");
				item.click();
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			try
			{
				driver.close();
			}
			catch (final Exception ee)
			{

			}
			driver.quit();
			System.exit(0);
		}
	}

	private void populate() throws FileNotFoundException,
			UnsupportedEncodingException
	{
		FileInputStream fos = null;
		try
		{
			fos = new FileInputStream(settingsFilePath);
			final String[] defaults = getDefaults(fos);
			if (defaults[1].equals("FF"))
			{
				radio2.setSelected(true);
			}
			if (defaults[2].equals("FALSE"))
			{
				GUI_ON = false;
			}
			searchTerm = defaults[0];
			fos.close();
		}
		catch (final IOException e1)
		{
			try
			{
				fos.close();
			}
			catch (final Exception e)
			{
			}
			final PrintWriter writer = new PrintWriter(settingsFilePath, "UTF-8");
			writer.println("//Search Term");
			writer.println("DefaultSearchTerm: \"music\"");
			writer.println("//Browser{ Chrome:\"CH\", Firefox:\"FF\" }");
			writer.println("DefaultBrowser: \"CH\"");
			writer.println("//Whether the GUI should be on or not { yes:\"TRUE\", no:\"FALSE\" }");
			writer.println("GUI_ON: \"TRUE\"");
			writer.close();
		}
	}

	private String buildSettingsFile()
	{
		final StringBuilder s = new StringBuilder();
		s.append("//Search Term\n");
		s.append("DefaultSearchTerm: \"" + searchTerm + "\"\n");
		s.append("\n");
		s.append("//Browser{ Chrome:\"CH\", Firefox:\"FF\" }\n");
		s.append("DefaultBrowser: \"" + (isChrome ? "CH" : "FF") + "\"\n");
		s.append("\n");
		s.append("//Whether the GUI should be on or not { yes:\"TRUE\", no:\"FALSE\" }\n");
		s.append("GUI_ON: \"TRUE\"");
		return s.toString();
	}

	private void setDefaults()
	{
		try
		{
			final FileOutputStream fos = new FileOutputStream(settingsFilePath);
			fos.write(buildSettingsFile().getBytes());
			fos.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	private String[] getDefaults(final FileInputStream fos)
	{
		final String searchTermLocator = "DefaultSearchTerm";
		final String browserLocator = "DefaultBrowser";
		final java.util.Scanner scan = new java.util.Scanner(fos);
		final java.util.Scanner s = scan.useDelimiter("\\n");
		final String[] ret =
		{ "Music", "CH", "TRUE" };
		while (s.hasNext())
		{
			final String disHereString = s.next();
			if (disHereString.startsWith(searchTermLocator))
			{
				ret[0] = disHereString.substring(disHereString.indexOf("\"") + 1,
						disHereString.lastIndexOf("\""));
			}
			else if (disHereString.startsWith(browserLocator))
			{
				ret[1] = disHereString.substring(disHereString.indexOf("\"") + 1,
						disHereString.lastIndexOf("\""));
			}
			else if (disHereString.startsWith("guiOnLocator"))
			{
				ret[2] = disHereString.substring(disHereString.indexOf("\"") + 1,
						disHereString.lastIndexOf("\""));
			}
		}
		scan.close();
		s.close();
		return ret;
	}

	public WebDriver getChromeDriver(final ChromeOptions options)
	{
		final String path = getClass().getResource("drivers/chromedriver.exe")
				.getPath();
		try
		{
			System.setProperty("webdriver.chrome.driver", path);
			return new ChromeDriver(options);
		}
		catch (final Exception e)
		{
			System.out.println(path.substring(path.lastIndexOf("YouMoo")));
			System.setProperty("webdriver.chrome.driver",
					path.substring(path.lastIndexOf("YouMoo")));
			return new ChromeDriver(options);
		}
	}

	public WebDriver getChromeDriverFromNearby(final ChromeOptions options)
	{
		try
		{
			System.setProperty("webdriver.chrome.driver",
					"./drivers/chromedriver.exe");
			return new ChromeDriver(options);
		}
		catch (final Exception e)
		{
			final String path = getClass().getResource("drivers/chromedriver.exe")
					.getPath();
			System.out.println(path.substring(path.lastIndexOf("YouMoo")));
			System.setProperty("webdriver.chrome.driver",
					path.substring(path.lastIndexOf("YouMoo")));
			return new ChromeDriver(options);
		}
	}

	private boolean waitForElementVisible(final WebDriver driver,
			final By elementBy, final int implicitWait) throws InterruptedException
	{
		for (int i = 0; i < implicitWait; i++)
		{
			try
			{
				if (driver.findElement(elementBy).isDisplayed())
				{
					return true;
				}
			}
			catch (final Exception e)
			{

			}
			Thread.sleep(1);
		}
		return false;
	}
}
