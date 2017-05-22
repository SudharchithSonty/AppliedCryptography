package com;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.text.DecimalFormat;
public class ReadDataset{
	static ArrayList<String[]> file_array = new ArrayList<String[]>();
    static ArrayList<File> files = new ArrayList<File>();
    static ArrayList<String> unique_terms;
	static ArrayList<double[]> vector = new ArrayList<double[]>();
	static DecimalFormat format = new DecimalFormat("#.###");
public static void clear(){
	file_array.clear();
	files.clear();
	if(unique_terms != null){
		unique_terms.clear();
	}
	vector.clear();
}
public static void readFile(File folder)throws Exception{
	clear();
	File list[] = folder.listFiles();
	for(int i=0;i<list.length;i++){
		files.add(list[i]);
	}
	HashSet<String> hs = new HashSet<String>();
	for(File file : files){
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder buffer = new StringBuilder();
		while((line = br.readLine())!=null){
			buffer.append(line);
		}
		br.close();
		String[] tokens = buffer.toString().replaceAll("[\\W&&[^\\s]]","").split("\\W+");
		for(String terms : tokens){
			hs.add(terms);
		}
		file_array.add(tokens);
	}
	unique_terms = new ArrayList<String>(hs);
}

public static void buildVector(){
	for(String[] tokens : file_array){
		double[] tf_idf = new double[unique_terms.size()];
		for(int i=0;i<unique_terms.size();i++){
			double value = getTermFrequency(tokens,unique_terms.get(i)) * getInverseDocument(file_array,unique_terms.get(i));
			tf_idf[i] = Double.parseDouble(format.format(value));
		}
		vector.add(tf_idf);
	}
}
public static double getTermFrequency(String[] doc, String word){
	double counts = 0;
	for(String terms : doc){
		if(terms.equalsIgnoreCase(word))
			counts++;
	}
    return counts/doc.length;
}
public static double getInverseDocument(ArrayList<String[]> docs, String word){
	double counts = 0;
	for(String[] doc : docs){
		for(String terms : doc){
			if(terms.equalsIgnoreCase(word)){
				counts++;
				break;
			}
		}
	}
    return Math.log(docs.size()/counts);
}
public static void saveVector(){
	try{
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<vector.size();i++){
			double data[] = vector.get(i);
			sb.append(files.get(i).getName()+",");
			for(int j=0;j<data.length;j++){
				sb.append(data[j]+",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(System.getProperty("line.separator"));
		}
		FileWriter fw = new FileWriter("vector.txt");
		fw.write(sb.toString());
		fw.close();
	}catch(Exception e){
		e.printStackTrace();
	}
}
}