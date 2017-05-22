package com;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.LinkedHashMap;
import java.io.File;
public class Recluster{
	static ArrayList<String> dataset = new ArrayList<String>();
	static LinkedHashMap<double[],MergeCluster> map = new LinkedHashMap<double[],MergeCluster>();
	static LinkedHashMap<double[],MergeCluster> mergemap = new LinkedHashMap<double[],MergeCluster>();
	static int reader = 0;
	static int merge = 0;
public static void readVector(){
	try{
		reader = 0;
		merge = 0;
		map.clear();
		dataset.clear();
		mergemap.clear();
		BufferedReader br = new BufferedReader(new FileReader("vector.txt"));
		String line = null;
		while((line = br.readLine())!=null){
			line = line.trim();
			if(line.length() > 0)
				dataset.add(line);
		}
		br.close();
	}catch(Exception e){
		e.printStackTrace();
	}
}
public static void recluster(){
	int divide = dataset.size()/2;
	int first = divide;
	int start = 0;
	recursive(start,first);
	int second = first + divide;
	if(second != dataset.size())
		second = divide + 1;
	else
		second = divide;
	recursive(start,second);
}
public static void recursive(int start,int index){
	int divide = index/2;
	int first = divide;
	KCluster.files.clear();
	KCluster.vector.clear();
	for(int i=start;i<first;i++){
		String arr[] = dataset.get(reader).split(",");
		KCluster.files.add(new File(arr[0]));
		System.out.print(arr[0]+" ");
		double data[] = new double[arr.length-1];
		for(int k=1;k<arr.length;k++){
			data[k-1] = Double.parseDouble(arr[k]);
		}
		KCluster.vector.add(data);
		reader = reader + 1;
	}
	System.out.println();
	KCluster.kcluster();
	if(merge == 0){
		merge = 1;
	}else{
		mergeCluster();
	}
	start = first;
	int second = first + divide;
	if(second != index)
		second = first + divide + 1;
	else
		second = first + divide;
	for(int i=start;i<second;i++){
		String arr[] = dataset.get(reader).split(",");
		KCluster.files.add(new File(arr[0]));
		System.out.print(arr[0]+" ");
		double data[] = new double[arr.length-1];
		for(int k=1;k<arr.length;k++){
			data[k-1] = Double.parseDouble(arr[k]);
		}
		KCluster.vector.add(data);
		reader = reader + 1;
	}
	System.out.println();
	KCluster.kcluster();
	mergeCluster();
}
public static void mergeCluster(){
	ArrayList<double[]> vector = KCluster.vector;
	for(int k=0;k<vector.size();k++){
		double data[] = vector.get(k);
		double temp[] = null;
		double value = 0;
		for(Map.Entry<double[],MergeCluster> me : map.entrySet()){
			double cent[] = me.getKey();
			double sim = KCluster.distance(cent,data);
			if(value < sim){
				value = sim;
				temp = cent;
			}
		}
		if(temp != null){
			MergeCluster mc = map.get(temp);
			//if(!exists(mc.getCluster().split(","),KCluster.files.get(k).getName())){
				String str = "";
				if(mc.getCluster().split(",").length == 1)
					str = KCluster.files.get(k).getName();
				else
					str = mc.getCluster()+","+KCluster.files.get(k).getName();
				mc.setCluster(str);
				map.put(temp,mc);
				mergemap.put(temp,mc);
			//}
		}else{
			MergeCluster mc = new MergeCluster();
			mc.setCluster(KCluster.files.get(k).getName());
			map.put(data,mc);
			mergemap.put(temp,mc);
		}
	}
}

public static boolean exists(String arr[],String name){
	boolean flag = false;
	for(int i=0;i<arr.length;i++){
		if(arr.length > 1){
			if(arr[i].trim().equals(name.trim())){
				flag = true;
				break;
			}
		}
	}
	return flag;
}
}