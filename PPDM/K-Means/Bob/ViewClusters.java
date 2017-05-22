package com;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.BorderLayout;
public class ViewClusters extends JFrame{
	JPanel p1;
	JTextArea area;
	JScrollPane jsp;
	Font f1;
public ViewClusters(){
	super("View Clusters Data");
	p1 = new JPanel();
	p1.setLayout(new BorderLayout());
	f1 = new Font("Times New Roman",Font.BOLD,16);
	area = new JTextArea();
	area.setEditable(false);
	area.setLineWrap(true);
	area.setFont(f1);
	jsp = new JScrollPane(area);
	p1.add(jsp,BorderLayout.CENTER);

	getContentPane().add(p1,BorderLayout.CENTER);
}
}