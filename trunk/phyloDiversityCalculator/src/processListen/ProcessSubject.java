/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processListen;

import java.util.Vector;
import javax.swing.SwingUtilities;

/**
 *
 * @author jingliu5
 */
public class ProcessSubject extends javax.swing.SwingWorker implements ProcessSubjectInterface{
    private int currentPercentage;
    private String currentMessage;
    private Vector processListeners;
    
    public ProcessSubject(){
       currentPercentage = 0;
       processListeners = new Vector();  

    }

    public void setCurrentPercentage(int currentPercentage){
       this.currentPercentage = currentPercentage;
       notice();
    }
    public int getCurrentPercentage(){
       return currentPercentage;
    }
    
    public void setCurrentMessage(String currentMessage){
       this.currentMessage = currentMessage;
       notice();
    }
    
    public String getCurrentMessage(){
        return currentMessage;
    }

    
    @Override
    public void attach(ProcessListenerInterface o) {
        processListeners.add(o);
    }

    @Override
    public void detach(ProcessListenerInterface o) {
        processListeners.remove(o);
    }

    @Override
    public void notice() {
         for(int i=0;i<processListeners.size();i++)
           ((ProcessListenerInterface)processListeners.get(i)).update();
    }    

    @Override
    protected Object doInBackground() throws Exception {
        return this;
    }


}
