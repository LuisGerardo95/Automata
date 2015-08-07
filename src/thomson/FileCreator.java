/**
* Universidad Del Valle de Guatemala
* 04-ago-2015
* Pablo Díaz 13203
*/

package thomson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Pablo
 */
public class FileCreator {
    
    public FileCreator(String output){
        crearArchivo(output);
    }
     
    private void crearArchivo(String output){
        try {
            
                
                output += "\n"+"\n"+"\n"+leerArchivo();


                File file = new File("Automata.txt");

                // if FileCreator doesnt exists, then create it
                if (!file.exists()) {
                        file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
               
                bw.write(output+"\n");
                

                bw.close();

                System.out.println("Se ha creado el archivo exitosamente");

        } catch (IOException e) {
        }
    }
    private String leerArchivo(){
      
        int contador=0;
        int tamaño=0;
        String input=" ";
        BufferedReader br = null;
 
        try {

                String sCurrentLine;
                File file = new File("Automata.txt");
                br = new BufferedReader(new FileReader(file.getAbsoluteFile()));

               
               
               while ((sCurrentLine = br.readLine()) != null) {
                    
                    input+=sCurrentLine+"\n";
                
                }
             
                
        input+="\n";
                
        return input;
        } catch (IOException e) {
               
        } finally {
                try {
                        if (br != null)br.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
        }
        return null;
        
    }

}