/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processListen;

import javax.swing.JProgressBar;

/**
 *
 * @author jingliu5
 */
public class ProcessListener  implements ProcessListenerInterface{
    private int currentPercentage;
    private ProcessSubject processSubject;
    private String currentMessage;
    public ProcessListener(ProcessSubject processSubject){
       this.processSubject = processSubject;
    }
    public int getCurrentPercentage(){
        return currentPercentage;
    }

    public String getCurrentMessage(){
        return currentMessage;
    }

    
    @Override
    public void update() {
         currentPercentage = processSubject.getCurrentPercentage();
         currentMessage = processSubject.getCurrentMessage();
    }
    
}

