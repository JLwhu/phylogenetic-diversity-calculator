/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processListen;

/**
 *
 * @author jingliu5
 */
public interface ProcessSubjectInterface {
    public void attach(ProcessListenerInterface o);
    public void detach(ProcessListenerInterface o);
    public void notice();
}
