import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

public class OutLinePing extends JFrame {
	
	Object[][] tableData;
	int threadNum=0;
	

	
	public OutLinePing() {
		
		super("Angry IP Scanner");
		
		tableData = new Object[254][5];
		
		//menu
		JMenuBar menuBar = new JMenuBar();
		
		setJMenuBar(menuBar);
		
		JMenu scanMenu = new JMenu("Scan");
		JMenu goToMenu = new JMenu("Go to");
		JMenu commandsMenu = new JMenu("Commands");
		JMenu favoritesMenu = new JMenu("Favorites");
		JMenu toolsMenu = new JMenu("Tools");
		JMenu helpMenu = new JMenu("Help");
		
		menuBar.add(scanMenu);
		menuBar.add(goToMenu);
		menuBar.add(commandsMenu);
		menuBar.add(favoritesMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);
		
		JMenuItem loadFromFileAction = new JMenuItem("Load from file...");
		JMenuItem exportAllAction = new JMenuItem("Export all...");
		JMenuItem exportSelectionAction = new JMenuItem("Export selection...");
		JMenuItem quitAction = new JMenuItem("Quit");
		
		quitAction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		
		scanMenu.add(loadFromFileAction);
		scanMenu.add(exportAllAction);
		scanMenu.add(exportSelectionAction);
		scanMenu.addSeparator();
		scanMenu.add(quitAction);
		
		JMenuItem nextAliveHostAction = new JMenuItem("Next alive host");
		JMenuItem nextOpenPortAction = new JMenuItem("Next open host");
		JMenuItem nextDeadHostAction = new JMenuItem("Next dead host");
		JMenuItem previousAliveHostAction = new JMenuItem("Previous alive host");
		JMenuItem previousOpenPortAction = new JMenuItem("Previous open port");
		JMenuItem previousDeadhostAction = new JMenuItem("Previous dead host");
		JMenuItem findAction = new JMenuItem("Find...");
		
		goToMenu.add(nextAliveHostAction);
		goToMenu.add(nextOpenPortAction);
		goToMenu.add(nextDeadHostAction);
		goToMenu.addSeparator();
		goToMenu.add(previousAliveHostAction);
		goToMenu.add(previousOpenPortAction);
		goToMenu.add(previousDeadhostAction);
		goToMenu.addSeparator();
		goToMenu.add(findAction);
		
		JMenuItem showDetailsAction = new JMenuItem("Show details");
		JMenuItem rescanAction = new JMenuItem("Rescan IP(s)");
		JMenuItem deleteAction = new JMenuItem("Delete IP(s)");
		JMenuItem copyAction = new JMenuItem("Copy IP");
		JMenuItem copyDetailsAction = new JMenuItem("Copy details");
		JMenuItem openAction = new JMenuItem("Open");
		
		commandsMenu.add(showDetailsAction);
		commandsMenu.addSeparator();
		commandsMenu.add(rescanAction);
		commandsMenu.add(deleteAction);
		commandsMenu.addSeparator();
		commandsMenu.add(copyAction);
		commandsMenu.add(copyDetailsAction);
		commandsMenu.addSeparator();
		commandsMenu.add(openAction);
		
		JMenuItem addCurrentAction = new JMenuItem("Add current...");
		JMenuItem manageFavoritesAction = new JMenuItem("Manage favorites...");
		
		favoritesMenu.add(addCurrentAction);
		favoritesMenu.add(manageFavoritesAction);
		
		JMenuItem preferencesAction = new JMenuItem("Preferences...");
		JMenuItem fetchersAction = new JMenuItem("Fetchers...");
		JMenuItem selectionAction = new JMenuItem("Selection");
		JMenuItem scanStatisticsAction = new JMenuItem("Scan statistics");
		
		toolsMenu.add(preferencesAction);
		toolsMenu.add(fetchersAction);
		toolsMenu.addSeparator();
		toolsMenu.add(selectionAction);
		toolsMenu.add(scanStatisticsAction);
		
		JMenuItem gettingStartedAction = new JMenuItem("Getting Started");
		JMenuItem officialWebsiteAction = new JMenuItem("Official Website");
		JMenuItem fAQAction = new JMenuItem("FAQ");
		JMenuItem reportAnIssueFromFileAction = new JMenuItem("Report an issue");
		JMenuItem pluginsAction = new JMenuItem("Plugins");
		JMenuItem commandLineAction = new JMenuItem("Command-line usage");
		JMenuItem checkForAction = new JMenuItem("Check for newer version...");
		JMenuItem aboutAction = new JMenuItem("About");
		
		helpMenu.add(gettingStartedAction);
		helpMenu.addSeparator();
		helpMenu.add(officialWebsiteAction);
		helpMenu.add(fAQAction);
		helpMenu.add(reportAnIssueFromFileAction);
		helpMenu.add(pluginsAction);
		helpMenu.addSeparator();
		helpMenu.add(commandLineAction);
		helpMenu.addSeparator();
		helpMenu.add(checkForAction);
		helpMenu.addSeparator();
		helpMenu.add(aboutAction);
		
		//menu end
		
		//status
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusPanel, BorderLayout.SOUTH);
		
		JLabel readyLabel = new JLabel("                 Ready");
		readyLabel.setPreferredSize(new Dimension(300, 20));
		JLabel displayLabel = new JLabel("            Display: All");
		displayLabel.setPreferredSize(new Dimension(150, 20));
		JLabel threadLabel = new JLabel("             Threads: "+threadNum);
		threadLabel.setPreferredSize(new Dimension(150, 20));
		
		readyLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		displayLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		threadLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		
		statusPanel.add(readyLabel);
		statusPanel.add(displayLabel);
		statusPanel.add(threadLabel);
		
		//status end
		
		//table
		String[] Tabletitle = new String[] {
				"IP", "Ping", "TTL", "HostName", "Port"
		};
		
		
		
		Object[][] status = initTable();
		
		JTable table = new JTable(status,Tabletitle);
		JScrollPane tableScrollPane = new JScrollPane(table);
		add(tableScrollPane);
		//table end
		
		//toolbar
		String myIP;
		String myHostName;
		String fixedIP;
		
		Font font = new Font("Serif",Font.BOLD,16);
		JToolBar toolBar1 = new JToolBar();
		toolBar1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JToolBar toolBar2 = new JToolBar();
		toolBar2.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		try {
			
		
			InetAddress localIP = InetAddress.getLocalHost();
			myIP = localIP.getHostAddress();
			fixedIP = myIP.substring(0,myIP.lastIndexOf(".")+1);
			myHostName = localIP.getHostName();
			
			JLabel rangeStartLabel = new JLabel("IP Range:");
			JTextField rangeStartTextField = new JTextField(fixedIP+"1");
			JLabel rangeEndLabel = new JLabel("        to");
			JTextField rangeEndTextField = new JTextField(fixedIP+"254");
			JButton topnee = new JButton(new ImageIcon("D:\\새 폴더\\PingTester\\pictures\\topnee.png"));
			
			rangeStartLabel.setFont(font);
			rangeStartLabel.setPreferredSize(new Dimension(90, 30));
			rangeEndLabel.setFont(font);
			rangeEndLabel.setPreferredSize(new Dimension(90, 30));

			
			toolBar1.add(rangeStartLabel);
			toolBar1.add(rangeStartTextField);
			toolBar1.add(rangeEndLabel);
			toolBar1.add(rangeEndTextField);
			toolBar1.add(topnee);
			
			JLabel hostNameLabel = new JLabel("Hostname:");
			JTextField hostNameTextField = new JTextField(myHostName);
			JButton upButton = new JButton("IP ↑");
			JComboBox optionComboBox = new JComboBox();
			optionComboBox.addItem("/24");
			optionComboBox.addItem("/26");
			
			JButton startButton = new JButton("▶ Start");
	
			JButton menuButton = new JButton(new ImageIcon("D:\\새 폴더\\PingTester\\pictures\\menu.png"));
			
			
		
			
			hostNameLabel.setFont(font);
			hostNameTextField.setPreferredSize(new Dimension(90, 30));
			upButton.setPreferredSize(new Dimension(50, 30));
			optionComboBox.setPreferredSize(new Dimension(90, 30));
			startButton.setPreferredSize(new Dimension(90, 30));
			
			toolBar2.add(hostNameLabel);
			toolBar2.add(hostNameTextField);
			toolBar2.add(upButton);
			toolBar2.add(optionComboBox);
			toolBar2.add(startButton);
			toolBar2.add(menuButton);
			
			JPanel pane = new JPanel(new BorderLayout());
			pane.add(toolBar1,BorderLayout.NORTH);
			pane.add(toolBar2, BorderLayout.SOUTH);
			
			add(pane,BorderLayout.NORTH);
			
			
			//toolbar end
			
			//function
			
			startButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
				
						
						startButton.setText("■ Stop");
				
						
						
						for(int i = 1; i < 255; i++) {
							final int j = i;
							
							String ip = fixedIP + j;
							readyLabel.setText(ip);
							String msg[] = {null, null, null,null, null};
							msg[0] = ip;
							Thread thread = new Thread() {
								@Override
								public void run() {
								
									try {
										Runtime run = Runtime.getRuntime();
										Process p = run.exec("ping -a "+ip);

										InputStream is = p.getInputStream();
										BufferedReader br = new BufferedReader(new InputStreamReader(is));
										String line = null;
										while((line = br.readLine()) != null) {
											if(line.indexOf("[") >= 0) {
												Pattern pattern_hostname = Pattern.compile("(Ping)(\\s+)(.+)(\\s+)(\\[)");
												Matcher matcher_hostname = pattern_hostname.matcher(line);
												while(matcher_hostname.find())
													table.setValueAt(matcher_hostname.group(3), j-1, 3);
											} 
											if(line.indexOf("ms") >= 0) {
												Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=)(\\d+)");
												Matcher matcher = pattern.matcher(line);
												while(matcher.find()) {
													msg[2] = matcher.group(1);
													msg[3] = matcher.group(4);
													table.setValueAt(msg[2], j-1, 1);
													table.setValueAt(msg[3], j-1, 2);
												}
											break;
											}
										}
										InetAddress address = InetAddress.getByName(ip);
										boolean reachable = address.isReachable(200);
										
										if(reachable) {
											
											final ExecutorService es = Executors.newFixedThreadPool(20);//20개의 풀, 스레드를 만들겠다는 이야기
											final String ip = (String)msg[0];
											final int timeout = 20;
											final List<Future<ScanResult>> futures = new ArrayList<>();

											for (int port = 1; port <= 1024; port++) {

												futures.add(portlsOpen(es, ip, port, timeout));	
											}
											try {
												es.awaitTermination(200L, TimeUnit.MILLISECONDS);
											} catch (InterruptedException e1) {

												e1.printStackTrace();
											}
											int openPorts = 0;
											String openPortNumber = "";
											for(final Future<ScanResult>f : futures) {
												try {
													
													if(f.get().isOpen()) {
														
														openPorts++;
														
														if(msg[4]!=null)
														openPortNumber += ","+f.get().getPort(); 
														else
															openPortNumber =""+ f.get().getPort();
														msg[4] = openPortNumber;
													}
												} catch (InterruptedException e1) {

													e1.printStackTrace();
												} catch (ExecutionException e1) {

													e1.printStackTrace();
												}
											}
											

												table.setValueAt(msg[4], j-1, 4);
												table.setValueAt(msg[0], j-1, 0);
										}
										else {
											table.setValueAt(msg[0], j-1, 0);
											table.setValueAt("[n/a]", j-1, 1);
											table.setValueAt("[n/s]", j-1, 2);
											table.setValueAt("[n/s]", j-1, 3);
											table.setValueAt("[n/s]", j-1, 4);
											
										}
										
										
										if(msg[4] == null)
											{ 
												table.setValueAt("[n/a]", j-1, 3);
											}
										br.close();
										is.close();
									}catch(Exception e) {
												
											}
								
								};
							};
							thread.start();
						}
						
					startButton.setText("▶ Start");
					readyLabel.setText("Ready");
					}
				 
				});
			
			
			//function end
		} catch (UnknownHostException e1) {
			System.out.println("IP 오류");
		}
		
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(700,700);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private Object[][] initTable() {
		
		Object[][] result = new Object[254][5];
		return result;
	}
	
	
	public static Future<ScanResult>portlsOpen(final ExecutorService es, final String ip, final int port, final int timeout){
		return es.submit(new Callable<ScanResult>() { //submit은 스레드의 start와 비슷하다
			@Override
			public ScanResult call() {
				try {
					Socket socket = new Socket(); //소켓 사용해서 서버와 연결
					socket.connect(new InetSocketAddress(ip, port), timeout);
					socket.close();
					return new ScanResult(port, true);
				}catch(Exception ex) {
					return new ScanResult(port, false);
				}//순차적으로 하면 시간이 너무 오래걸려서 스레드를 사용해야한다.
			}
		});
	}
	
	
	public static void main(String[] args) {
		new OutLinePing();
		
		
		
	}	
}