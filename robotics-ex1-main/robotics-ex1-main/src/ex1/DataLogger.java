package ex1;
import ex1.robot.StatisticsPoint;
import lejos.nxt.LCD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataLogger {
    private StringBuffer buffer;
    private final int bufferSize;
    private final String fileName;

    public DataLogger(String fileName, int bufferSize) {
        this.fileName = fileName;
        this.bufferSize = bufferSize;
        this.buffer = new StringBuffer();
    }

    public synchronized void log(StatisticsPoint point) {
        // Append the new data point to the buffer
    	
        buffer.append(point.toString()).append("\n");
        
        // Check if buffer needs to be flushed
        if (buffer.length() >= bufferSize) {
        	buffer.append("dddddd");
            flush();
        }
    }

    public synchronized void flush() {
    	try{
    		if (buffer.length() == 0 ) { 
    			 return ;
    		}
    		File f = new File(fileName);
    		FileOutputStream fos = new  FileOutputStream(f,true);
            
    		byte[] byteText = getBytes(buffer);

            //Critic to add a useless character into file
            //byteText.length-1
            for(int i=0;i<byteText.length-1;i++){
                fos.write((int) byteText[i]);
            }	
            
            fos.close();
            this.buffer = new StringBuffer();

        }catch(IOException e){
            LCD.drawString(e.getMessage(),0,0);
        }
    	
    }
    
    
    
    static private byte[] getBytes(StringBuffer inputText){
    	
        byte[] nameBytes = new byte[inputText.length()+1];
        
        for(int i=0;i<inputText.length();i++){
            nameBytes[i] = (byte) inputText.charAt(i);
        }
        nameBytes[inputText.length()] = 0;
 
        return nameBytes;
    }
    
}
