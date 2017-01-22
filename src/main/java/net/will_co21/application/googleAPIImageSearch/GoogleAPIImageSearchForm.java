package net.will_co21.application.googleAPIImageSearch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Rectangle;
import java.awt.ComponentOrientation;
import java.awt.GridBagLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
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

	private JPanel contentPane;
	private JTextField searchKeyword;
	private JTextField logWindow;
	private final IGoogleAPIRequester apiRequester;
	private final IDownloadService downloader;
	protected final ExecutorService loggingExecutor;
	public final Optional<Runnable> shutDownInvoker;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			GoogleAPIImageSearchForm frame = null;

			public void run() {
				try {

					ExecutorService loggingExecutor = Executors.newSingleThreadExecutor();

					LoggingWorker loggingWorker = new LoggingWorker(new ConsoleLogger());

					loggingExecutor.submit(loggingWorker);

					ILogger logger = new ILogger() {
						public void write(String s)
						{
							loggingWorker.post(s);
						}

						public void write(Exception e)
						{
							write(LoggingExceptionMessageBuilder.build(e));
						}

						public void write(Error e)
						{
							write(LoggingExceptionMessageBuilder.build(e));
						}
					};

					try {
						frame = new GoogleAPIImageSearchForm(loggingExecutor, loggingWorker, logger);
						frame.setVisible(true);
					} catch (Exception e) {
						logger.write(e);
						if(frame != null) frame.shutDownInvoker.ifPresent(f -> f.run());
					}
				} catch (Exception e) {
					if(frame != null) frame.shutDownInvoker.ifPresent(f -> f.run());
					e.printStackTrace();
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
	public GoogleAPIImageSearchForm(ExecutorService loggingExecutor, LoggingWorker loggingWorker, ILogger logger) throws UnsupportedEncodingException, FileNotFoundException, IOException {
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

		ISettings settings = new GoogleAPIImageSearchSettings(new File("settings.json"));
		settings.validate();

		this.loggingExecutor = loggingExecutor;

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
		searchKeyword.setColumns(38);
		headerPanel.add(searchKeyword);
		JPanel logPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) logPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		logWindow = new JTextField();
		logWindow.setEditable(false);
		logWindow.setSize(new Dimension(0, 20));

		logPanel.add(logWindow);
		logWindow.setColumns(76);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(headerPanel);

		JScrollPane imageScrollPanel = new JScrollPane();
		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new GridLayout(3, 5, 3, 3));
		JPanel imageOuterPanel = new JPanel();
		((FlowLayout)imageOuterPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		imageOuterPanel.add(imagePanel);
		imageScrollPanel.setPreferredSize(new Dimension(768, 498));
		imageScrollPanel.setViewportView(imageOuterPanel);

		IEnvironment environment = new GoogleAPIImageSearchEnvironment(settings);

		RunnerApplicative onSearchRequestCompleted = new RunnerApplicative();
		RunnerApplicative onSearchRequestCancelled = new RunnerApplicative();

		downloader = new HttpDownloadService(() -> {
			onSearchRequestCancelled.run();
		}, () -> {
			onSearchRequestCancelled.run();
		},
			(File originaImagelPath, File resizedImagePath, File thumbnailPath, int w, int h) -> {
				EventQueue.invokeLater(() -> {
					JLabel img = new JLabel(new ImageIcon(thumbnailPath.getAbsolutePath()));
					img.setBackground(Color.BLACK);
					img.setPreferredSize(new Dimension(ThumbnailSize.width, ThumbnailSize.height));
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
							ImageWindow imageWindow = new ImageWindow(originaImagelPath.getName());
							imageWindow.displayImage(resizedImagePath, w, h);
						}
					});

					this.revalidate();
				});
		}, s -> {
			EventQueue.invokeLater(() -> {
				logWindow.setText(s);
			});
		}, logger, environment, settings);

		apiRequester = new MockGoogleAPIRequester(new File(String.join(File.separator, new String[] { "mockdata", "googleapi.json" })));

		shutDownInvoker = Optional.of(() -> {
			apiRequester.cancel();
			downloader.cansel();
		});

		JButton searchButton = new JButton("検索");
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				EventQueue.invokeLater(() -> {
					try {
						searchButton.setEnabled(false);
						apiRequester.request(downloader);
						EventQueue.invokeLater(()-> {
							searchButton.setText("次を検索");
						});
					} catch (Exception ex) {
						logger.write(ex);
					}
				});
			}
		});

		onSearchRequestCompleted.setImplements(() -> {
			EventQueue.invokeLater(() -> {
				searchButton.setEnabled(true);
				loggingWorker.shutdown();
			});
		});

		onSearchRequestCancelled.setImplements(() -> {
			searchButton.setEnabled(true);
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				EventQueue.invokeLater(() -> {
					shutDownInvoker.get().run();
					dispose();
				});
			}
		});

		headerPanel.add(searchButton);
		searchButton.setPreferredSize(new Dimension(100, 24));

		JButton canselButton = new JButton("検索を中止");

		canselButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				EventQueue.invokeLater(() -> {
					try {
						apiRequester.cancel();
						downloader.cansel();
					} catch (Exception ex) {
						logger.write(ex);
					}
				});
			}
		});

		headerPanel.add(canselButton);
		JCheckBox enableLocalSearch = new JCheckBox("ローカルを検索");
		headerPanel.add(enableLocalSearch);

		enableLocalSearch.addChangeListener(e -> {
			environment.setSearchMode(((((JCheckBox)e.getSource()).isSelected() ? SearchMode.localSearch : SearchMode.apiSearch)));
		});

		JCheckBox enableSafeMode = new JCheckBox("セーフモード");

		enableSafeMode.addChangeListener(e -> environment.setSafeMode(((JCheckBox)e.getSource()).isSelected()));

		headerPanel.add(enableSafeMode);

		contentPane.add(imageScrollPanel);
		contentPane.add(logPanel);
		this.pack();
	}
}
