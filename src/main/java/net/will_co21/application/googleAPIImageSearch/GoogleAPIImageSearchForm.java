package net.will_co21.application.googleAPIImageSearch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.awt.GridBagLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleAPIImageSearchForm extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = -2617295185731416329L;
	private JPanel contentPane;
	private JTextField searchKeyword;
	private JTextField logWindow;
	private final IGoogleAPIRequester apiRequester;
	private final IDownloadService downloader;
	protected final ExecutorService loggingExecutor;
	public final RunnerApplicative shutDownInvoker;
	public final Optional<Runnable> canselRequestInvoker;
	protected volatile boolean isClosed = false;
	protected volatile String lastKeyword = "";
	protected volatile String originalSearchButtonText = "検索";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			GoogleAPIImageSearchForm frame = null;

			public void run() {
				try {
					frame = new GoogleAPIImageSearchForm();
					frame.setVisible(true);
				} catch (InvalidSettingException e) {
					JOptionPane.showMessageDialog(frame, e.getMessage());
					System.exit(ERROR);
				} catch (Exception e) {
					e.printStackTrace();
					if(frame != null && frame.shutDownInvoker != null) frame.shutDownInvoker.runIfImplemented();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public GoogleAPIImageSearchForm() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		setTitle("google api image search");
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (UnsupportedLookAndFeelException e) {
			System.out.println(e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println(e.getMessage());
		} catch (InstantiationException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		ISettings settings = new GoogleAPIImageSearchSettings(new File("settings.json"), str -> JOptionPane.showMessageDialog(this, str));
		settings.validate();

		setBounds(new Rectangle(100, 100, 800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setAlignmentY(0.0f);
		contentPane.setAlignmentX(0.0f);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JPanel headerPanel = new JPanel();
		headerPanel.setBounds(new Rectangle(0, 0, 0, 20));
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		searchKeyword = new JTextField();
		searchKeyword.setToolTipText("検索するキーワードを入力してください...");
		searchKeyword.setColumns(48);
		headerPanel.add(searchKeyword);
		JPanel logPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) logPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		logWindow = new JTextField();
		logWindow.setEditable(false);
		logWindow.setSize(new Dimension(0, 20));

		logPanel.add(logWindow);
		logWindow.setColumns(82);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(headerPanel);

		JScrollPane imageScrollPanel = new JScrollPane();
		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new GridBagLayout());
		JPanel imageOuterPanel = new JPanel();
		((FlowLayout)imageOuterPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		imageOuterPanel.add(imagePanel);
		imageScrollPanel.setPreferredSize(new Dimension(768, 498));
		imageScrollPanel.setViewportView(imageOuterPanel);

		IEnvironment environment = new GoogleAPIImageSearchEnvironment(settings);

		environment.setSafeMode(settings.getEnableSafeSearch());

		RunnerApplicative onSearchRequestCancelled = new RunnerApplicative();

		shutDownInvoker = new RunnerApplicative();
		loggingExecutor = Executors.newSingleThreadExecutor();

		LoggingWorker loggingWorker;

		if(settings.getLoggingMode() == LoggingMode.file)
		{
			loggingWorker = new LoggingWorker(new FileLogger(new File(settings.getLogFilePath()),
												settings.getIsAppendLogging()));
		}
		else
		{
			loggingWorker = new LoggingWorker(new ConsoleLogger());
		}

		loggingExecutor.submit(loggingWorker);

		ILogger logger = new ILogger() {
			public void write(String s)
			{
				loggingWorker.post(s);
			}

			public void write(Exception e)
			{
				write(StackTraceString.toString(e));
			}

			public void write(Error e)
			{
				write(StackTraceString.toString(e));
			}

			public void write(Throwable t)
			{
				write(StackTraceString.toString(t));
			}
		};

		ISwingLogPrinter logPrinter = s -> {
			EventQueue.invokeLater(() -> {
				logWindow.setText(s);
			});
		};

		IImageReader imageReader = (String url, File originalImagelPath, File resizedImagePath, File thumbnailPath, int w, int h) -> {
			EventQueue.invokeLater(() -> {
				JLabel img = new JLabel(new ImageIcon(thumbnailPath.getAbsolutePath()));
				img.setBackground(Color.BLACK);
				img.setPreferredSize(new Dimension(ThumbnailSize.width, ThumbnailSize.height));
				img.setToolTipText(url);
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.ipadx = 5;
				gbc.ipady = 5;
				gbc.gridx = imagePanel.getComponentCount() % 5;
				gbc.gridy = imagePanel.getComponentCount() / 5;

				((GridBagLayout)imagePanel.getLayout()).setConstraints(img, gbc);
				imagePanel.add(img);
				img.addMouseListener(new MouseListener() {

					@Override
					public void mouseReleased(MouseEvent e) {
						// TODO 自動生成されたメソッド・スタブ

					}

					@Override
					public void mousePressed(MouseEvent e) {
						// TODO 自動生成されたメソッド・スタブ

					}

					@Override
					public void mouseExited(MouseEvent e) {
						// TODO 自動生成されたメソッド・スタブ

					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO 自動生成されたメソッド・スタブ

					}

					@Override
					public void mouseClicked(MouseEvent e) {
						ImageWindow imageWindow = new ImageWindow(url);
						imageWindow.displayImage(resizedImagePath, w, h);
					}
				});

				this.revalidate();
			});
		};

		JButton searchButton = new JButton("検索");
		JButton canselButton = new JButton("検索を中止");

		apiRequester = new HttpsGoogleAPIRequester(settings, logPrinter, logger,
				(str) -> {
					EventQueue.invokeLater(() -> {
						JOptionPane.showMessageDialog(this, str);
					});
				}, imageReader);

		downloader = new HttpDownloadService(cancelled -> {
			EventQueue.invokeLater(() -> {
				if(!cancelled)
				{
					searchButton.setEnabled(true);
					canselButton.setEnabled(false);
				}
				apiRequester.onSearchRequestCompleted(cancelled);
				if(isClosed)
				{
					shutDownInvoker.run();
				}
			});
		}, () -> {
			onSearchRequestCancelled.run();
		}, imageReader
		, (String url, String originalImagelPath, String resizedImagePath, String thumbnailPath, int w, int h) -> {
			apiRequester.addCacheImage(new SavedImageInfo(url, originalImagelPath, resizedImagePath, thumbnailPath, w, h));
		}, logPrinter, logger, environment, settings);

		shutDownInvoker.setImplements(() -> {
			if(apiRequester != null) apiRequester.shutdown();
			if(downloader != null) downloader.shutdown();
			if(loggingWorker != null) loggingWorker.shutdown();
			if(loggingExecutor != null) loggingExecutor.shutdown();
		});

		canselRequestInvoker = Optional.of(() -> {
			apiRequester.cancel();
			downloader.cansel();
		});

		canselButton.setEnabled(false);

		canselButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				EventQueue.invokeLater(() -> {
					try {
						apiRequester.cancel();
						downloader.cansel();
						canselButton.setEnabled(false);
					} catch (Exception ex) {
						logger.write(ex);
					}
				});
			}
		});

		Runnable searchRunner = () -> {
			boolean changeKeyword = false;

			if(searchKeyword.getText().equals("")) return;
			else if(!searchKeyword.getText().equals(lastKeyword))
			{
				imagePanel.removeAll();
				this.revalidate();
				apiRequester.reset();
				downloader.resetAlreadyDownloads();
				downloader.resetRequestedUrls();
				apiRequester.setKeyword(searchKeyword.getText());
				lastKeyword = searchKeyword.getText();
				searchButton.setText("検索");
				changeKeyword = true;
			}

			try {
				searchButton.setEnabled(false);
				apiRequester.request(downloader, changeKeyword);
				canselButton.setEnabled(true);
				searchButton.setText("次を検索");
				originalSearchButtonText = searchButton.getText();
			} catch (Exception e) {
				logger.write(e);
			}
		};

		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				EventQueue.invokeLater(searchRunner);
			}
		});

		onSearchRequestCancelled.setImplements(() -> {
			EventQueue.invokeLater(() -> searchButton.setEnabled(true));
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				EventQueue.invokeLater(() -> {
					isClosed = true;
					canselRequestInvoker.get().run();
					try {
						settings.save();
					} catch (IOException ex) {
						logger.write(ex);
					}
					if(downloader.getCounter().getCount() == 0) shutDownInvoker.run();
					dispose();
				});
			}
		});

		headerPanel.add(searchButton);
		searchButton.setPreferredSize(new Dimension(100, 24));

		searchKeyword.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				setButtonText();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setButtonText();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				setButtonText();
			}

			protected void setButtonText()
			{
				EventQueue.invokeLater(() -> {
					if(searchKeyword.getText().equals(lastKeyword))
					{
						searchButton.setText(originalSearchButtonText);
						GoogleAPIImageSearchForm.this.revalidate();
					}
					else
					{
						searchButton.setText("検索");
						GoogleAPIImageSearchForm.this.revalidate();
					}
				});
			}
		});

		searchKeyword.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					EventQueue.invokeLater(searchRunner);
				}
			}
		});

		headerPanel.add(canselButton);

		JCheckBox enableSafeMode = new JCheckBox("セーフモード");

		enableSafeMode.addChangeListener(e -> environment.setSafeMode(((JCheckBox)e.getSource()).isSelected()));

		enableSafeMode.setSelected(environment.getSafeMode());

		headerPanel.add(enableSafeMode);

		contentPane.add(imageScrollPanel);
		contentPane.add(logPanel);
		this.pack();
	}
}
