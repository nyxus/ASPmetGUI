/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aspmetgui.Exceptions;

/**
 *
 * @author Gerco
 */
public class AlgorithmNotSet extends Exception {
     
    //Constructor that accepts a message
      public AlgorithmNotSet()
      {
         super("You havent selected an algorthm");
      }
    
    
    //Constructor that accepts a message
      public AlgorithmNotSet(String message)
      {
         super(message);
      }
    
}
