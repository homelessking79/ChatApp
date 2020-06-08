package Client;

import javax.swing.JFrame;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.KeyAdapter;

public class ChatUI extends JFrame {
	
	private JTextField textField;
	private JScrollPane chatScrollPane;
	private JLabel lblNewLabel;
	private JButton btnAddFile;
	
	private HashMap<String, JTextPane> chatArea;
	private JScrollPane scrollPane;
	private JList<Object> list;
	private DefaultListModel<Object> DLM;
	
	private final String ServerStyle = "<html><body><p style='margin: 3px 0px; color: rgb(214, 71, 0);'>";
	private final String GroupStyle = "<html><body><p style='margin: 3px 0px; color: rgb(33, 137, 255);'>";
	private final String UserStyle = "<html><body><p style='margin: 3px 0px; color: rgb(0, 214, 36);'>";
	private final String EndStyle = "</p></body></html>";
	private final String exclamationMark = "<img width='25' height='25' alt='new' src='https://stickershop.line-scdn.net/sticonshop/v1/sticon/5c0f6869040ab1b3f8bec3af/iPhone/036.png'>";
	
	private ArrayList<String> ListUser;
	private HashMap<String, ArrayList<String> > ListGroup;
	private HashMap<String, ArrayList<Socket> > socketPane;
	private HashMap<String, ArrayList<Socket> > serverPane;
	
	private Thread read;
	private int PORT;
	private String name;
	private String paneName;
	private Server server;
	private BufferedReader input;
	private PrintWriter output;
	private Socket socket;
	
	/**
	 * Create the frame.
	 */
	public ChatUI(Socket sock, BufferedReader in, PrintWriter out, String name) {
		setBackground(Color.BLACK);
		this.socket = sock;
		this.input = in;
		this.output = out;
		this.name = name;
		this.socketPane = new HashMap<String, ArrayList<Socket> >();
		this.serverPane = new HashMap<String, ArrayList<Socket> >();
		
		try {
			this.PORT = Integer.valueOf(this.input.readLine());
			this.server = new Server(this.PORT);
			this.server.start();
		} catch (NumberFormatException | IOException e1) {
			try {
				this.socket.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		System.out.println(this.name + " has joined the server");
		setResizable(false);
		setBounds(100, 100, 600, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		this.chatScrollPane = new JScrollPane();
		this.chatArea = new HashMap<String, JTextPane>();
		this.scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		this.list = new JList<>();
		//list.setBorder(null);
		//list.setBackground(new Color(51, 102, 153));
		this.paneName = "SERVER";
		this.ListUser = new ArrayList<String>();
		this.ListGroup = new HashMap<String, ArrayList<String> >();
		
		
		scrollPane.setBounds(400,100,120,200);
		getContentPane().add(scrollPane);	
		JLabel lblName = new JLabel(this.name.toUpperCase());
		lblName.setForeground(Color.BLACK);
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(200, 10, 83, 23);
		getContentPane().add(lblName);
		
		JLabel lblChatInput = new JLabel("Enter message: ");
		lblChatInput.setBounds(10, 50, 100, 23);
		getContentPane().add(lblChatInput);
		
		JLabel lblChatOuput = new JLabel("Chat panel: ");
		lblChatOuput.setBounds(10, 100, 100, 23);
		getContentPane().add(lblChatOuput);
		
		this.list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if(!list.isSelectionEmpty()) {
						String selected = list.getSelectedValue().toString();
						String paneMsg = "SERVER";
						if(selected.contains(exclamationMark)) {
							String temp = selected.replace(exclamationMark, "");
							paneMsg = temp.substring(temp.indexOf(");'>") + 4, temp.indexOf("    </p>"));
							if(paneMsg.equals("SERVER")) {
								DLM.set(DLM.indexOf(selected), ServerStyle + "SERVER    " + EndStyle);
							}
							else if(ListGroup.containsKey(paneMsg)) {
								DLM.set(DLM.indexOf(selected), GroupStyle + paneMsg + "    " + EndStyle);		
							} else {
								DLM.set(DLM.indexOf(selected), UserStyle + paneMsg + "    " + EndStyle);		
							}
						} else {
							paneMsg = selected.substring(selected.indexOf(");'>") + 4, selected.indexOf("    </p>"));
						}
						if(!paneMsg.equals(name)) {
							changechatPane(paneMsg);
						}
					}
				}
			}
		});
		this.scrollPane.setViewportView(this.list);
		DefaultListCellRenderer renderer =  (DefaultListCellRenderer) this.list.getCellRenderer();  
		renderer.setHorizontalAlignment(JLabel.CENTER); 
		this.DLM = new DefaultListModel<>();
		this.list.setModel(this.DLM);
		this.list.setSelectedIndex(0);

		JButton btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createGroup();
			}
		});
		btnCreateGroup.setBounds(400, 300, 120, 20);
		getContentPane().add(btnCreateGroup);
			
		this.chatArea.put(this.paneName, createChatPane("SERVER"));
		this.chatArea.get(this.paneName).setContentType("text/html");
		this.chatArea.get(this.paneName).setEditable(false);
		chatScrollPane.setViewportView(chatArea.get(this.paneName));
	    
		chatScrollPane.setBounds(100,100,300,300);
		getContentPane().add(chatScrollPane);
		
	    JButton btnSend = new JButton("Send");
	    btnSend.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		sendMessage();
	    	}
	    });
	    btnSend.setBounds(400,50,80,20);
		getContentPane().add(btnSend);
	    
	    
	    this.textField = new JTextField();
	    textField.addKeyListener(new KeyAdapter() {
	    	@Override
	    	public void keyPressed(KeyEvent arg0) {
	    		if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
	    			sendMessage();
	    		}
	    	}
	    });
	    this.textField.setColumns(10);
	    textField.setBounds(100,50,300,20);
		getContentPane().add(textField);
	    
	    JButton btnOutButton = new JButton("Exit");
	    btnOutButton.setAlignmentY(Component.TOP_ALIGNMENT);
	    btnOutButton.setBackground(Color.RED);
	    btnOutButton.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent arg0) {
	    		output.println("IAMLOGGINGOUT");
	    		System.exit(0);
	    	}
	    });
	    btnOutButton.setBounds(540,0,60,60);
		getContentPane().add(btnOutButton);
	    
	    lblNewLabel = new JLabel("SERVER");
	    lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
	    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.read = new Read();
		this.read.start();
	}
	
	private void createGroup() {
		JFrame addGroupFrame = new JFrame("CHOOSE MEMBER");
		addGroupFrame.setBounds(100, 100, 400, 300);
		addGroupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPaneAddGroup = new JScrollPane();
		JList<String> listAddGroup = new JList<>();
		DefaultListCellRenderer renderer = (DefaultListCellRenderer)listAddGroup.getCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		DefaultListModel<String> DAGLM = new DefaultListModel<>();
		listAddGroup.setModel(DAGLM);
		
		JTextField groupNameTextField = new JTextField();
		groupNameTextField.setColumns(10);
		
		JButton addGroup = new JButton("ADD");
		addGroup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String groupName = groupNameTextField.getText();
				if(groupName.equals("") || groupName.equals("SERVER")) {
					JOptionPane.showMessageDialog(null, "Invalid Group Name!");
				} else if(ListUser.contains(groupName)) {
					JOptionPane.showMessageDialog(null, "Group Name must be different from UserName!");					
				} else if(ListGroup.keySet().contains(groupName)) {
					JOptionPane.showMessageDialog(null, "Group Name Existed!");					
				} else {
					if (listAddGroup.getSelectedValuesList().size() <= 2) {
						JOptionPane.showMessageDialog(null, "Need more than 2 people to create group!");
					} else {
						ListGroup.put(groupName, (ArrayList<String>) listAddGroup.getSelectedValuesList());
						output.println("REUPDATEUSERLISTANDGROUPLIST:" + name + ":" + groupName + ":" + ListGroup.get(groupName).toString());
						changechatPane(groupName);
						addGroupFrame.dispose();						
					}
				}
			}
		});
		
		listAddGroup.setModel(DAGLM);
		scrollPaneAddGroup.setViewportView(listAddGroup);
		
		GroupLayout groupLayout = new GroupLayout(addGroupFrame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(91)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(scrollPaneAddGroup, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(groupNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(addGroup, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(102, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPaneAddGroup, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(addGroup)
						.addComponent(groupNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		addGroupFrame.getContentPane().setLayout(groupLayout);
		
		for (String user : this.ListUser) {
			DAGLM.addElement(user);
		}
		
		listAddGroup.addMouseListener(new MouseAdapter() {
			Robot robot;
			{
				try {
					robot = new Robot();
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if (robot != null)
					robot.keyPress(KeyEvent.VK_CONTROL);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (robot != null)
					robot.keyRelease(KeyEvent.VK_CONTROL);
			}
		});
		listAddGroup.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					listAddGroup.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				}
			}
		});
		addGroupFrame.setResizable(false);
		addGroupFrame.setVisible(true);
	}
	
	private JTextPane createChatPane(String paneName) {
		JTextPane tempPane = new JTextPane();
		tempPane.setContentType("text/html");
		tempPane.setEditable(false);
		return tempPane;
	}
	
	private void changechatPane(String paneName) {
		if(paneName.equals("SERVER")) {
			this.paneName = "SERVER";
		}
		else {
			if(this.chatArea.keySet().contains(paneName)) {
				this.paneName = paneName;
			} else {
				this.paneName = paneName;
				if(ListGroup.keySet().contains(paneName)) {
					this.serverPane.put(this.paneName, new ArrayList<Socket>());
				} else {
					this.socketPane.put(this.paneName, new ArrayList<Socket>());
				}
				this.chatArea.put(this.paneName, createChatPane(this.paneName));
			}
		}
		if(!this.paneName.equals("SERVER") && !this.ListGroup.keySet().contains(paneName)) {
			this.output.println("CHATLISTENERREQUESTTOUSER:" + this.paneName);
		} else if(!this.paneName.equals("SERVER") && this.ListGroup.keySet().contains(paneName)) {
			this.output.println("CHATLISTENERREQUESTTOGROUP:" + this.paneName);
		}
		this.chatScrollPane.setViewportView(chatArea.get(this.paneName));
		lblNewLabel.setText(this.paneName);
	}
	
	private void updateListener(String condi, String paneName) {
		ArrayList<String> inVals = new ArrayList<String>();
		if(condi.contains(",")) {
			inVals = new ArrayList<String>(Arrays.asList(condi.split(",")));
			if(this.serverPane.get(paneName).isEmpty()) {
				for(int i=0; i<inVals.size(); i++) {
					String[] addr = inVals.get(i).split(":");
					Socket socket = null;
					try {
						socket = new Socket(addr[0], Integer.valueOf(addr[1]));
						new PrintWriter(socket.getOutputStream()).println("FROMPANE:" + paneName);
						this.serverPane.get(paneName).add(socket);
						new Peer(socket, paneName, addr[0], Integer.valueOf(addr[1])).start();
					} catch(Exception e) {
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						else System.out.println("invalid input");
					}
				}
			}
		} else {
			inVals.add(condi);
			if(this.socketPane.get(paneName).isEmpty()) {
				String[] addr = inVals.get(0).split(":");
				Socket socket = null;
				try {
					socket = new Socket(addr[0], Integer.valueOf(addr[1]));
					new PrintWriter(socket.getOutputStream()).println("FROMPANE:" + paneName);
					this.socketPane.get(paneName).add(socket);
					new Peer(socket, paneName, addr[0], Integer.valueOf(addr[1])).start();
				} catch(Exception e) {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					else System.out.println("invalid input");
				}
			}
		}
	}

	private void sendMessage() {
		try {
			String message = this.textField.getText().trim();
            if (message.equals("")) {
				return;
			}
			if(this.paneName.equals("SERVER")) {
				appendToPane(chatArea.get("SERVER"), "<p align='right' style='margin:0; color:rgb(180, 52, 235);'>" + this.name + ": " + message + "</p>");
				output.println(message);
				this.textField.requestFocus();
				this.textField.setText(null);
			} else {
				appendToPane(chatArea.get(this.paneName), "<p align='right' style='margin:0; color:rgb(180, 52, 235);'>" + this.name + ": " + message + "</p>");
				this.server.sendMessage("<p style='margin:0; color:rgb(250, 98, 27);'>" + this.name + ": " + message + "</p>");
				this.textField.requestFocus();
				this.textField.setText(null);

			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage());
			System.exit(0);
		}
	}

	// read new incoming messages
	// change
	class Read extends Thread {
		public void run() {
			String message = "";
			while(!Thread.currentThread().isInterrupted()){
				try {
					message = input.readLine();
					if(message.contains("-")) {
						String[] readInfo = message.split("-");
						if(readInfo[0].equals("LISTENERLIST")) {
							updateListener(readInfo[1], paneName);
							message = "";
						} else if(readInfo[0].equals("REQUESTLISTENERFROMUSER")) {
							String[] request = readInfo[1].split("@");
							if(!chatArea.keySet().contains(request[0])) {
								socketPane.put(request[0], new ArrayList<Socket>());
								chatArea.put(request[0], createChatPane(request[0]));
							}
							if(ListUser.contains(request[0]))
								updateListener(request[1], request[0]);
							message = "";
						} else if(readInfo[0].equals("REQUESTLISTENERFROMGROUP")) {
							String[] request = readInfo[1].split("@");
							if(!chatArea.keySet().contains(request[0])) {
								serverPane.put(request[0], new ArrayList<Socket>());
								chatArea.put(request[0], createChatPane(request[0]));
							}
							if(ListGroup.containsKey(request[0]))
								updateListener(request[1], request[0]);
							message = "";
						}
					}
					if(message != "") {
						if (message.charAt(0) == '[') {
							ListGroup.clear();
							if(!message.contains(":")) {
								message = message.substring(1, message.length()-1);
								ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
							} else {
								String groupTemp[] = message.split("-, ");
								ListUser = new ArrayList<String>(Arrays.asList(groupTemp[groupTemp.length-1].substring(1, groupTemp[groupTemp.length-1].length()-1).split(", ")));
								for (int i=0; i<groupTemp.length-1; i++) {
									String group[] = groupTemp[i].split(":");
									ListGroup.put(group[0].substring(1, group[0].length()-1), new ArrayList<String>(Arrays.asList(group[1].substring(1, group[1].length()-1).split(", "))));									
								}
							}
							DLM.removeAllElements();
							DLM.addElement(ServerStyle + "SERVER    " + EndStyle);
							if(!ListGroup.isEmpty()) {
								for(String groupName : ListGroup.keySet()) {
									DLM.addElement(GroupStyle + groupName + "    " + EndStyle);
								}
							}
							for(String user : ListUser) {
								DLM.addElement(UserStyle + user + "    " + EndStyle);
							}
						} else {
							if(!paneName.equals("SERVER")) {
								DLM.set(DLM.indexOf(ServerStyle + "SERVER    " + EndStyle), ServerStyle + "SERVER    " + exclamationMark + EndStyle);
							}
							appendToPane(chatArea.get("SERVER"), message);
						}
					}
				} catch (IOException ex) {
					System.err.println("Failed to parse incoming message");
					try {
						socket.close();
						output.close();
						input.close();
						System.exit(0);
						interrupt();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	  // send html to pane
	private void appendToPane(JTextPane tp, String msg) {
		HTMLDocument doc = (HTMLDocument) tp.getDocument();
		HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
		try {
	    	editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
	    	tp.setCaretPosition(doc.getLength());
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	// change
	class Peer extends Thread {
		private String msgPane;
		private Socket socket;
		private BufferedReader input;
		private String address;
		private int port;
		
		public Peer(Socket socket, String paneName, String addr, int por) {
			this.socket = socket;
			this.msgPane = paneName;
			this.address = addr;
			this.port = por;
			try {
				this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				PrintWriter output = new PrintWriter(this.socket.getOutputStream(), true);
				if(ListGroup.keySet().contains(this.msgPane)) {
					output.println("THISSOCKETFROMGROUP:" + this.msgPane);
				} else {
					output.println("THISSOCKETFROMUSER:" + name);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(true) {
				try {
					String msg = this.input.readLine();			
						if(!this.msgPane.equals(paneName)) {
							boolean hasMark = false;
							for (int i=0;i<DLM.getSize(); i++) {
								String temp = (String) DLM.get(i);
								if(temp.contains(exclamationMark) && temp.equals(this.msgPane)) {
									hasMark = true;
								}
							}
							if(!hasMark) {
								if(ListGroup.containsKey(this.msgPane)) {
									DLM.set(DLM.indexOf(GroupStyle + this.msgPane + "    " + EndStyle), GroupStyle + this.msgPane + "    " + exclamationMark + EndStyle);		
								} else {
									DLM.set(DLM.indexOf(UserStyle + this.msgPane + "    " + EndStyle), UserStyle + this.msgPane + "    " + exclamationMark + EndStyle);		
								}
							}
						}
						appendToPane(chatArea.get(this.msgPane), msg);
					//}
				} catch (IOException e) {
					if(ListGroup.keySet().contains(this.msgPane)) {
						serverPane.get(this.msgPane).remove(this.socket);
						if(serverPane.get(this.msgPane).size() <=2) {
							serverPane.remove(this.msgPane);
						}
					} else {
						socketPane.remove(this.msgPane);
					}
					break;
				}
			}
		}
	}	

	// change
	class Server extends Thread {
	    private ServerSocket serverSocket;
	    private HashSet<ServerSocketPair> serversocketPair;

	    public Server(int port) throws IOException {
	        this.serverSocket = new ServerSocket(port);
	        this.serversocketPair = new HashSet<ServerSocketPair>();
	    }

	    public void run() {
	        try {
	            while(true) {
	                ServerSocketPair serversocketPair = new ServerSocketPair(this.serverSocket.accept(), this);
	                this.serversocketPair.add(serversocketPair);
	                serversocketPair.start();
	            }
	        } catch (Exception ex) { ex.printStackTrace(); }
	    }

	    public void sendMessage(String message) {
	        try {
				for(ServerSocketPair serversocketPair : this.serversocketPair) {
	        		if(serversocketPair.checkPane().equals(paneName)) {
	    	            serversocketPair.getPrintWriter().println(message);
	        		}
	        	}
	        } catch (Exception ex) { ex.printStackTrace(); }
	    }

	    public Set<ServerSocketPair> getServerThreadThreads() {
	        return this.serversocketPair;
	    }
	    

	}
	
	// change
	class ServerSocketPair extends Thread {
	    private Server server;
	    private Socket socket;
	    private PrintWriter pr;
	    private String paneName;
	    
	    public ServerSocketPair(Socket socket, Server server){
	        this.socket = socket;
	        this.server = server;
	    }

		public void run(){
	        try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	            String msg = br.readLine();
	            if(msg.contains("THISSOCKETFROM")) {
		            this.paneName = msg.split(":")[1];
		            this.pr = new PrintWriter(this.socket.getOutputStream(), true);
	            }
	            
	        } catch(Exception ex) {
	        	this.server.getServerThreadThreads().remove(this);
	        }
	    }

	    public PrintWriter getPrintWriter() {
	        return this.pr;
	    }
	    
	    public Socket getSocket() {
	    	return socket;
	    }
	    public String checkPane() {
	    	return this.paneName;
	    }
	}
}

