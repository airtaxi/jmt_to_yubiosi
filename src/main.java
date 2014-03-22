import java.util.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.Manifest;
import java.util.prefs.BackingStoreException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;;

/*************************************************************************
 *  Compilation:  javac HelloWorld.java
 *  Execution:    java HelloWorld
 *
 *  Prints "Hello, World". By tradition, this is everyone's first program.
 *
 *  % java HelloWorld
 *  Hello, World
 *
 *  These 17 lines of text are comments. They are not part of the program;
 *  they serve to remind us about its properties. The first two lines tell
 *  us what to type to compile and test the program. The next line describes
 *  the purpose of the program. The next few lines give a sample execution
 *  of the program and the resulting output. We will always include such 
 *  lines in our programs and encourage you to do the same.
 *
 *************************************************************************/

public class main {

	public static void printline(String str) {
		System.out.println(str);
	}
	public static String readFile(String base,String folder) {
		String out = "";
		try{
    		InputStream mainfest = new FileInputStream(new File(base+folder+"\\manifest.txt"));
			BufferedInputStream buffer = new BufferedInputStream(mainfest);
			byte[] data = new byte[1024];
            int i = 0;
			while ((i = buffer.read(data)) != -1) {
            	out += new String(data, 0, i,"UTF-8");
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
		return out;
	}
    public static void main(String[] args) throws IOException {
    	String base = "C:\\songs\\";
    	File file = new File(base);
    	String[] directories = file.list(new FilenameFilter() {
    	  @Override
    	  public boolean accept(File current, String name) {
    	    return new File(current, name).isDirectory();
    	  }
    	});
    	(new File("./song")).mkdirs();
    	System.out.println(Arrays.toString(directories));
    	int index = 0;
    	for(String folder : directories) {
    		index++;
    		//printline(readfile(base,folder));
    		String manifest = readFile(base, folder);
    		String Name = manifest.split("Name=")[1].split("\r\n")[0];
    		//String fn = Name.replace("\\", "").replace("/","").replace("|", "").replace("?","").replace("*", "").replace("\"", "")
//			.replace("<","").replace(">", "").replace("%", "").replace("¡¹","").replace("¡¸","").replace("¢Ó", "");
    		String fn = Name.replaceAll("[^a-zA-Z0-9.-]", "_")+index;
    		//printline(fn);
    		if(!(new File("./song/"+fn+".mp3").canRead()) && (new File(base+folder+"\\song.mp3")).canRead()) {
	    		FileUtils.copyFile(new File(base+folder+"\\song.mp3"),new File("./song/"+fn+".mp3"));
	    		FileUtils.copyFile(new File(base+folder+"\\pic.jpg"),new File("./song/"+fn+".jpg"));
    		}
			String BPM = manifest.split("BPM=")[1].split("\r\n")[0];
			String Artist = manifest.split("Composer=")[1].split("\r\n")[0];
			String Level =  manifest.split("Level=")[1].split("\r\n")[0];
			//printline(Level);
			int bsclvl = Integer.parseInt(Level.substring(0,Level.indexOf(",")));
			int advlvl = Integer.parseInt(Level.substring(Level.indexOf(",")+1,Level.indexOf(",",Level.indexOf(",")+1)));
			int extlvl = Integer.parseInt(Level.substring(Level.lastIndexOf(",")+1,Level.length()));
			String out = "#TITLE_NAME:"+Name+"\n"+
					"#TITLE_READ:"+Name+"\n"+
					"#ARTIST:"+Artist+"\n"+
					"#BPM:15000\n#BPM_SHOW:"+BPM+"\n"+
					"#AUDIO_FILE:"+fn+".mp3\n"+
					"#JACKET:"+fn+".jpg\n#BGM_START:20000\n\n";
			

			List<String> bsc = null;
			try {
				if(new File(base+folder+"\\pt_bsc.jmt").canRead() && new File(base+folder+"\\pt_bsc.jmt").length() > 1000 && bsclvl != 0) {
						out += "[Notes:1]\n"+
						"#FILE:"+fn+"-bsc.ybh\n"+
						"#LEVEL:"+bsclvl+"\n#OFFSET:0\n";
						bsc = FileUtils.readLines(new File(base+folder+"\\pt_bsc.jmt"));
						int i = 0;
						List<Float> indexes = new ArrayList<Float>();
						for (String line : bsc){
							if(line != null && line.length() > 4 && !line.contains("Panel")) {
								int last = line.indexOf(".",line.indexOf(".")+1);
								if(last < 0 ) {
									last = line.length(); 
								}
		//						printline(line.substring(line.indexOf(".")+1,last));
		//						printline(line);
		//						printline(line.substring(1,line.indexOf(".")));
		//						printline(line.substring(line.indexOf(".")+1,last));
		//						printline(line.substring(1,line.indexOf(".")));
		//						System.out.println(i);
								int idx = (Integer.parseInt(line.substring(1,line.indexOf("."))));
		//						System.out.println(idx);
								indexes.add (Float.parseFloat(line.substring(line.indexOf(".")+1,last)) +(float) idx / (idx < 10 ? 10 : 100));
								//printline(Integer.parseInt(indexes[i]));
								//printline(Integer.parseInt(Integer.par(line.substring(line.indexOf(".")+1,last)) + Integer.valueOf(line.substring(1,line.indexOf("."))) / 100));
								Collections.sort(indexes);
								i = i + 1;
							}
						}
						String content = "";
						for(float k : indexes) {
							content += String.valueOf(k).replace(".", ",")+"\n";
						}
						FileUtils.write(new File("./song/"+fn+"-bsc.ybh"), content,false);
					}
				} catch(Exception e) {
					//printline("aa");
					printline(folder);
					e.printStackTrace();
				}

			List<String> adv = null;
			try {
				if(new File(base+folder+"\\pt_adv.jmt").canRead() && new File(base+folder+"\\pt_adv.jmt").length() > 1000 && advlvl != 0) {
					out += "[Notes:2]\n"+
					"#FILE:"+fn+"-adv.ybh\n"+
					"#LEVEL:"+advlvl+"\n#OFFSET:0\n";
					adv = FileUtils.readLines(new File(base+folder+"\\pt_adv.jmt"));
					int i = 0;
					List<Float> indexes = new ArrayList<Float>();
					for (String line : adv){
						if(line != null && line.length() > 4 && !line.contains("Panel")) {
							int last = line.indexOf(".",line.indexOf(".")+1);
							if(last < 0 ) {
								last = line.length(); 
							}
	//						printline(line.substring(line.indexOf(".")+1,last));
	//						printline(line);
	//						printline(line.substring(1,line.indexOf(".")));
	//						printline(line.substring(line.indexOf(".")+1,last));
	//						printline(line.substring(1,line.indexOf(".")));
	//						System.out.println(i);
							int idx = (Integer.parseInt(line.substring(1,line.indexOf("."))));
	//						System.out.println(idx);
							indexes.add (Float.parseFloat(line.substring(line.indexOf(".")+1,last)) +(float) idx / (idx < 10 ? 10 : 100));
							//printline(Integer.parseInt(indexes[i]));
							//printline(Integer.parseInt(Integer.par(line.substring(line.indexOf(".")+1,last)) + Integer.valueOf(line.substring(1,line.indexOf("."))) / 100));
							Collections.sort(indexes);
							i = i + 1;
						}
					}
					String content = "";
					
					for(float k : indexes) {
						content += String.valueOf(k).replace(".", ",")+"\n"; 
						//printline(String.valueOf(k).replace(".", ",")+"\n");
					}
					//FileUtils.write(new File("./song/"+fn+"-adv.ybh"), content,false);
					FileUtils.write(new File("./song/"+fn+"-adv.ybh"),content ,false);
				}
			} catch(Exception e) {
				//printline("aa");
				printline(folder);
				e.printStackTrace();
			}

			List<String> ext = null;
			try {
			if(new File(base+folder+"\\pt_ext.jmt").canRead() && new File(base+folder+"\\pt_ext.jmt").length() > 1000 && extlvl != 0) {
					out += "[Notes:3]\n"+
					"#FILE:"+fn+"-ext.ybh\n"+
					"#LEVEL:"+extlvl+"\n#OFFSET:0\n";
					ext = FileUtils.readLines(new File(base+folder+"\\pt_ext.jmt"));
					int i = 0;
					List<Float> indexes = new ArrayList<Float>();
					for (String line : ext){
						if(line != null && line.length() > 4 && !line.contains("Panel")) {
							int last = line.indexOf(".",line.indexOf(".")+1);
							if(last < 0 ) {
								last = line.length(); 
							}
	//						printline(line.substring(line.indexOf(".")+1,last));
	//						printline(line);
	//						printline(line.substring(1,line.indexOf(".")));
	//						printline(line.substring(line.indexOf(".")+1,last));
	//						printline(line.substring(1,line.indexOf(".")));
							//System.out.println(i);
							int idx = (Integer.parseInt(line.substring(1,line.indexOf("."))));
							//System.out.println(idx);
							indexes.add (Float.parseFloat(line.substring(line.indexOf(".")+1,last)) +(float) idx / (idx < 10 ? 10 : 100));
							//printline(Integer.parseInt(indexes[i]));
							//printline(Integer.parseInt(Integer.par(line.substring(line.indexOf(".")+1,last)) + Integer.valueOf(line.substring(1,line.indexOf("."))) / 100));
							Collections.sort(indexes);
							i = i + 1;
						}
					}
					String content = "";
					for(float k : indexes) {
						content += String.valueOf(k).replace(".", ",")+"\n";	
						//printline(String.valueOf(k).replace(".", ",")+"\n");
					}
					FileUtils.write(new File("./song/"+fn+"-ext.ybh"),content ,false);
				}
			} catch(Exception e) {
				printline(folder);
				//printline("aa");
			}
			FileUtils.write(new File("./song/"+fn+"-hoyo.ybi"),out ,false);
    	}
    	printline("end");
    }

}