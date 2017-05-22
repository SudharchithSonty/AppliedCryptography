package com;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
public class KCluster{
	static ArrayList<File> files = new ArrayList<File>();
    static ArrayList<double[]> vector = new ArrayList<double[]>();
	
public static double distance(double[] vector1, double[] vector2){
	double dot = 0, magnitude1 = 0, magnitude2=0;
	for(int i=0;i<vector1.length;i++){
		dot+=vector1[i]*vector2[i];
		magnitude1+=Math.pow(vector1[i],2);
    	magnitude2+=Math.pow(vector2[i],2);
	}
	magnitude1 = Math.sqrt(magnitude1);
    magnitude2 = Math.sqrt(magnitude2);
    double d = dot / (magnitude1 * magnitude2);
    return d == Double.NaN ? 0 : d;
}

public static void kcluster(){
	HashMap<double[],TreeSet<Integer>> clusters = new HashMap<double[],TreeSet<Integer>>();
	HashMap<double[],TreeSet<Integer>> step = new HashMap<double[],TreeSet<Integer>>();
	HashSet<Integer> random = new HashSet<Integer>();
	TreeMap<Double,HashMap<double[],TreeSet<Integer>>> clusters_sim = new TreeMap<Double,HashMap<double[],TreeSet<Integer>>>();
	int k = 2;
	int maxiter = 20;
	for(int i=0;i<20;i++){
		clusters.clear();
	    step.clear();
	    random.clear();
		while(random.size() < k){
			random.add((int)(Math.random()*vector.size()));
		}
		for(int r : random){
			double[] temparray = new double[vector.get(r).length];
			System.arraycopy(vector.get(r),0,temparray,0,temparray.length);
			step.put(temparray,new TreeSet<Integer>());
		}
		boolean flag = true;
		int iter = 0;
		while(flag){
			clusters = new HashMap<double[],TreeSet<Integer>>(step);
			for(int p=0;p<vector.size();p++){
				double[] centroid = null;
				double similarity = 0;
				for(double[] cent : clusters.keySet()){
					double csimilarity = distance(vector.get(p),cent);
					if(csimilarity > similarity){
						similarity = csimilarity;
						centroid = cent;
					}
				}
				if(clusters.get(centroid) != null)
				clusters.get(centroid).add(p);
			}
		    step.clear();
		    for(double[] centroid : clusters.keySet()){
				double[] change_centroid = new double[centroid.length];
				for(int d : clusters.get(centroid)){
					double[] doc = vector.get(d);
					for(int p=0;p<change_centroid.length;p++)
						change_centroid[p]+=doc[p];
				}
				for(int p=0;p<change_centroid.length;p++){
					change_centroid[p]/=clusters.get(centroid).size();
				}
		    	step.put(change_centroid,new TreeSet<Integer>());
			}
			//check break conditions
		    String oldcentroid = "", newcentroid="";
			for(double[] d : clusters.keySet())
				oldcentroid+=Arrays.toString(d);
			for(double[] d: step.keySet())
				newcentroid+=Arrays.toString(d);
		    	if(oldcentroid.equals(newcentroid)) 
					flag = false;
				if(++iter >= maxiter) 
					flag = false;
		    }
			double sumsim = 0;
		    for(double[] cent : clusters.keySet()){
				TreeSet<Integer> cls = clusters.get(cent);
				for(int value : cls){
		    			sumsim+=distance(cent,vector.get(value));
		    		}
		    	}
				clusters_sim.put(sumsim,new HashMap<double[],TreeSet<Integer>>(clusters));
	}
	for(double[] cent : clusters_sim.get(clusters_sim.lastKey()).keySet()){
		StringBuilder sb = new StringBuilder();
		for(int pts : clusters_sim.get(clusters_sim.lastKey()).get(cent)){
			if(!exists(files.get(pts).getName()))
				sb.append(files.get(pts).getName()+",");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length()-1);
			MergeCluster mc = new MergeCluster();
			mc.setCluster(sb.toString());
			mc.setId(Recluster.map.size()+1);
			Recluster.map.put(cent,mc);
		}
	}
}
public static boolean exists(String name){
	boolean flag = false;
	for(Map.Entry<double[],MergeCluster> me : Recluster.map.entrySet()){
		MergeCluster mc = me.getValue();
		String arr[] = mc.getCluster().split(",");
		for(int i=0;i<arr.length;i++){
			if(arr[i].trim().equals(name.trim())){
				flag = true;
				break;
			}
		}
	}
	return flag;
}
}

    