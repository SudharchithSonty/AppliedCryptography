package com;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
public class ProcessThread extends Thread{
	Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
	BigDecimal BigDecimalTWO = new BigDecimal(2);
public ProcessThread(Socket soc){
	socket=soc;
	try{
		out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }catch(Exception e){
        e.printStackTrace();
    }
}
public BigDecimal sqrt(BigDecimal number1, BigDecimal guess1, RoundingMode rounding1){
    BigDecimal result1=BigDecimal.ZERO;
    BigDecimal flipA1=result1;
    BigDecimal flipB1=result1;
    boolean first1=true;
    while(result1.compareTo(guess1) !=0){
      if(!first1)
		  guess1 = result1;
	  else
		  first1=false;
      result1 = number1.divide(guess1, rounding1).add(guess1).divide(BigDecimalTWO, rounding1);
      if(result1.equals(flipB1))
        return flipA1;

      flipB1 = flipA1;
      flipA1 = result1;
    }
    return result1;
}
public double distance(BigDecimal[] vector1, BigDecimal[] vector2){
	BigDecimal dot = new BigDecimal("0");
	BigDecimal magnitude1 = new BigDecimal("0");
	BigDecimal magnitude2 = new BigDecimal("0");
	int loop = vector1.length;
	for(int i=0;i<loop;i++){
		BigDecimal sum = vector1[i].multiply(vector2[i]);
		dot = dot.add(sum);
		BigDecimal p1 = vector1[i].pow(2);
		BigDecimal p2 = vector2[i].pow(2);
		magnitude1 = magnitude1.add(p1);
		magnitude2 = magnitude2.add(p2);
	}
	magnitude1 = sqrt(magnitude1,BigDecimal.ONE,RoundingMode.HALF_UP);
    magnitude2 = sqrt(magnitude2,BigDecimal.ONE,RoundingMode.HALF_UP);
	magnitude2 = magnitude1.multiply(magnitude2);
	dot = dot.divide(magnitude2,10,RoundingMode.HALF_UP);
	double d = dot.doubleValue();
    return d == Double.NaN ? 0 : d;
}

@Override
public void run(){
	try{
		Object input[]=(Object[])in.readObject();
        String type=(String)input[0];
		if(type.equals("centers")){
			Homomorphic.KeyGeneration();
			System.out.println("enter");
			String[] receive_centers = (String[])input[1];
			String file = (String)input[2];
			ArrayList<String[]> centers = new ArrayList<String[]>();
			BigDecimal d1[] = new BigDecimal[receive_centers.length];
			for(int i=0;i<receive_centers.length;i++){
				java.math.BigInteger bd = new java.math.BigInteger(receive_centers[i]);
				d1[i] = new BigDecimal(bd);	
			}
			StringBuilder sb = new StringBuilder();
			ViewShare vs = new ViewShare();
			for(Map.Entry<double[],MergeCluster> me : Recluster.map.entrySet()){
				double cent[] = me.getKey();
				String enc[] = new String[cent.length];
				for(int i=0;i<cent.length;i++){
					String value = Double.toString(cent[i]);
					BigInteger encrypt = new BigInteger(value.getBytes());
					encrypt = Homomorphic.Encryption(encrypt);
					enc[i] = encrypt.toString();
				}
				BigDecimal d2[] = new BigDecimal[enc.length];
				for(int i=0;i<enc.length;i++){
					java.math.BigInteger bd = new java.math.BigInteger(enc[i]);
					d2[i] = new BigDecimal(bd);
				}
				BigDecimal d3[] = new BigDecimal[enc.length];
				for(int i=0;i<d1.length;i++){
					d3[i] = d1[i];
				}
				for(int i=d1.length;i<d2.length;i++){
					d3[i] = new BigDecimal("0.0");
				}
				double distance = distance(d2,d3);
				if(distance > 0.05){
					System.out.println(file+" "+distance+" "+ me.getValue().getCluster());
					sb.append(file+" "+distance+" "+ me.getValue().getCluster()+"\n");
					Object row[] = {file,distance,me.getValue().getCluster()};
					vs.dtm.addRow(row);
				}
			}
			System.out.println("Total Centroids : "+Recluster.map.size());
			System.out.println("Total matches : "+vs.dtm.getRowCount());
			BigInteger m1 = new BigInteger(Recluster.map.size()+"");
			BigInteger m2 = new BigInteger(vs.dtm.getRowCount()+"");
			BigInteger enc1 = Homomorphic.Encryption(m1);
			BigInteger enc2 = Homomorphic.Encryption(m2);
			BigInteger total = enc1.multiply(enc2).mod(Homomorphic.nsquare);
			Object res[] = {sb.toString(),total.toString(),Recluster.map.size()+""};
			out.writeObject(res);
			out.flush();
			vs.setVisible(true);
			vs.setSize(600,400);
		}
		
	}catch(Exception e){
        e.printStackTrace();
    }
}
}
