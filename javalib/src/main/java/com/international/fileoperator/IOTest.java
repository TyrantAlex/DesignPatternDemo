package com.international.fileoperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class IOTest {

	public static void main(String[] args) {
		getFileStrByLine();
//		getFileStrAll();
	}
	
	//全部读取
	private static void getFileStrAll(){
	       File file = new File("D:\\strings.xml");    
	       
	       Reader reader = null;    
	       
	       String allStr = null;
	       
	       try {    
	  
	        System.out.println("以字符为单位读取文件内容，一次读一个字节：");    
	  
	        // 一次读一个字符    
	  
	        reader = new InputStreamReader(new FileInputStream(file));    
	  
	        int tempchar;    
//	        String[] tempHead = new String[2];
//	        String[] tempTail = new String[9];
	        String tempHead = null;
	        String tempTail = null;
	        Character tempOld = null;
	  
	        while ((tempchar = reader.read()) != -1){    
	  
	         //对于windows下，/r/n这两个字符在一起时，表示一个换行。    
	  
	         //但如果这两个字符分开显示时，会换两次行。    
	  
	         //因此，屏蔽掉/r，或者屏蔽/n。否则，将会多出很多空行。    
	  
	         if (((char)tempchar) != '\r'){    
	  
//	          System.out.print((char)tempchar);    
	          
//	          allStr += (char)tempchar;
	        	 if(tempOld != null){
	        		char[] tempcharArray = new char[2];
	        		tempcharArray[0] = tempOld;
	        		tempcharArray[1] = (char)tempchar;
	        		tempHead = tempcharArray.toString();
	        		if(tempHead == "\">"){
	        			
	        		}
	        	 }
	        	 
	        	 tempOld = (char)tempchar;
	         }  
	  
	        }    
	        
//	        System.out.print("String文件整体打印如下:"+ "/n" +allStr);    
	        reader.close();    
	  
	       } catch (Exception e) {    
	  
	        e.printStackTrace();    
	  
	       }    
	}

	//以行的形式读取
	private static void getFileStrByLine() {
		File file = new File("D:\\strings.xml");
		String result = null;
		FileReader fr = null;
		BufferedReader br = null;
		String line = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			line = br.readLine();
			while (line != null) {
//				System.out.println("已经读到 line = " + line);
				if (line.contains("\">") && line.contains("</string>")) {
					String[] split = line.split("\">");
					int indexOf = split[1].indexOf("/");
					int indexOf1 = split[1].indexOf("/string>");
//					System.out.println("indexOf 字符截取 = " + indexOf);
//					System.out.println("indexOf1111字符截取 = " + indexOf1);
					if(indexOf1 > 0){
						String substring = split[1].substring(0, indexOf1-1);
//						System.out.println("substring字符截取 = " + substring);
					}else{
						System.out.println("indexOf 小于或等于0？= " + split[1]);
					}
					result = line;
//					break;
//					System.out.println("result = " + result);
					line = br.readLine();
				} else {
					System.out.println("直接打印不包含的line = " + line);
					line = br.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
