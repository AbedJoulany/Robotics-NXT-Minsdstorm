package ex2;
import ex2.robot.StatisticsPoint;
import lejos.nxt.LCD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataLogger {
    private StringBuffer buffer;
    private final int bufferSize;
    private final String fileName;

    private final Thread flushThread;
    private volatile boolean running = true;
    
    public DataLogger(String fileName, int bufferSize) {
        this.fileName = fileName;
        this.bufferSize = bufferSize;
        this.buffer = new StringBuffer();
        this.flushThread = new Thread(new Runnable() {           
            public void run() {
                runFlush();
            }
        });
        this.flushThread.start();
    }

    public  void log(StatisticsPoint point) {
    	synchronized (this) {
    		buffer.append(point.toString()).append("\n");   
    	}
    }
    
    public void stop() {
    	running = false;
    	try {
    		flushThread.interrupt();
    		flushThread.join();			
		} catch (InterruptedException e) {				
			Thread.currentThread().interrupt();
		}             
    }

    private  synchronized void flush() {
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
    
    private void runFlush() {
        while (running) {
            try {
                Thread.sleep(1000); // Check every second
                synchronized (this) {
                    if (buffer.length() >= bufferSize) {
                        flush();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        flush(); // Ensure final flush when stopping
    }
}
