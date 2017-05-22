package com;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.File;
import java.awt.Cursor;
import java.awt.Cursor;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.math.BigInteger;
public class Main extends JFrame{
	JPanel p1;
	JPanel p2;
	JLabel title;
	JButton b1,b2,b3,b4,b5;
	Font f1;
	JFileChooser chooser;
	File file;
	ServerSocket server;
	ProcessThread thread;
public void start(){
	try{
		server = new ServerSocket(1111);
		while(true){
			Socket socket = server.accept();
			socket.setKeepAlive(true);
			thread=new ProcessThread(socket);
			thread.start();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
public Main(){
	super("Kclustering");
	p1 = new JPanel();
	f1 = new Font("Courier New",Font.BOLD,14);
	p1.setBackground(new Color(204, 110, 155));
	title = new JLabel("<HTML><BODY><CENTER>A New Privacy-Preserving Distributed k-Clustering Algorithm</CENTER></BODY></HTML>".toUpperCase());
	title.setForeground(Color.white);
	title.setFont(new Font("Times New Roman",Font.BOLD,16));
	p1.add(title);

	chooser = new JFileChooser(new File("."));
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	p2 = new JPanel();
	p2.setLayout(null);
	b1 = new JButton("Upload Dataset");
	b1.setFont(f1);
	b1.setBounds(320,50,400,50);
	p2.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			int option = chooser.showOpenDialog(Main.this);
			if(option == chooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				try{
					ReadDataset.readFile(file);
					ReadDataset.buildVector();
				}catch(Exception e){
					e.printStackTrace();
				}
				Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(normalCursor);
				JOptionPane.showMessageDialog(Main.this,"Dataset loaded");
			}
		}
	});

	b2 = new JButton("Generate Vector");
	b2.setFont(f1);
	b2.setBounds(320,130,400,50);
	p2.add(b2);
	b2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			ReadDataset.saveVector();
			Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normalCursor);
			JOptionPane.showMessageDialog(Main.this,"Vector generated");
		}
	});

	b3 = new JButton("Run Recluster Algorithm");
	b3.setFont(f1);
	b3.setBounds(320,210,400,50);
	p2.add(b3);
	b3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Recluster.readVector();
			Recluster.recluster();
			ViewClusters vc = new ViewClusters();
			int id = 1;
			System.out.println("te =="+Recluster.mergemap.size());
			ArrayList<String> dup = new ArrayList<String>();
			for(Map.Entry<double[],MergeCluster> me : Recluster.map.entrySet()){
				MergeCluster mc = me.getValue();
				String arr[] = mc.getCluster().split(",");
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<arr.length;i++){
					if(!dup.contains(arr[i])){
						dup.add(arr[i]);
						sb.append(arr[i]+",");
					}
				}
				if(sb.length() > 0){
					sb.deleteCharAt(sb.length()-1);
					mc.setCluster(sb.toString());
				}
				vc.area.append("Cluster id : "+id+"\n\n");
				vc.area.append("Cluster data : "+mc.getCluster()+"\n\n");
				vc.area.append("====================================\n");
				id = id + 1;
			}
			vc.setSize(600,400);
			vc.setVisible(true);
		}
	});
	
	b4 = new JButton("Share with Bob");
	b4.setFont(f1);
	b4.setBounds(320,290,400,50);
	p2.add(b4);
	b4.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			send();
			Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normalCursor);
		}
	});

	
	b5 = new JButton("Exit");
	b5.setFont(f1);
	b5.setBounds(320,370,400,50);
	p2.add(b5);
	b5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			System.exit(0);
		}
	});

	
	
	getContentPane().add(p1,BorderLayout.NORTH);
	getContentPane().add(p2,BorderLayout.CENTER);
}
public static void main(String a[])throws Exception{
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	Main main = new Main();
	main.setVisible(true);
	main.setExtendedState(JFrame.MAXIMIZED_BOTH);
	new ServerThread(main);
}
public int getRandomCenter(){
	java.util.Random r = new java.util.Random();
	return r.nextInt(ReadDataset.vector.size());//1
}
public void send(){
	try{
		Homomorphic.KeyGeneration();
		int random = getRandomCenter();
		ArrayList<double[]> keys = new ArrayList<double[]>(Recluster.map.keySet());
		double cent[] = keys.get(random);
		String file = Recluster.map.get(cent).getCluster();
		String enc[] = new String[cent.length];
		for(int i=0;i<cent.length;i++){
			String value = Double.toString(cent[i]);
			BigInteger encrypt = new BigInteger(value.getBytes());
			encrypt = Homomorphic.Encryption(encrypt);
			enc[i] = encrypt.toString();
		}
		Socket socket=new Socket("localhost",2222);
        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in=new ObjectInputStream(socket.getInputStream());
        Object req[]={"centers",enc,file};
        out.writeObject(req);
        out.flush();
        Object res[]=(Object[])in.readObject();
		String msg = res[0].toString();
		String match = (String)res[1];
		String total = (String)res[2];
		BigInteger encr = new BigInteger(match);
		BigInteger dec = Homomorphic.Decryption(encr);
		System.out.println(msg);
		System.out.println("Total Centroids : "+total);
		System.out.println("Total matches : "+(Integer.parseInt(total)-Integer.parseInt(dec.toString())));
	}catch(Exception e){
		e.printStackTrace();
	}
}
}
