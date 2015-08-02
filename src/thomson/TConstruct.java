/**
* Universidad Del Valle Guatemala
* Pablo Díaz 13203
*/

package thomson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Pablo
 */
public class TConstruct<T> {
    
   
    private AutomataFN afn;
    private final String epsilon = "ε";
    private String regex;
    private boolean control = false;
    
     public TConstruct(String regex) {
        RegexConverter convert = new RegexConverter();
        this.regex = convert.infixToPostfix(regex);
        char[] array = this.regex.toCharArray();
        String mod = new String(array);
        mod = mod.substring(0,1);
         System.out.println(mod);
        this.afn = afnBasico((T) mod);
    } 
    public void construct(){
        Stack pilaAFN = new Stack();
        
        for (Character c : this.regex.toCharArray()) {
            switch(c){
                case '*':
                     AutomataFN kleene = cerraduraKleene((AutomataFN) pilaAFN.pop());
                     pilaAFN.push(kleene);
                     System.out.println(kleene);
                     this.afn=kleene;
                    break;
                case '.':
                    AutomataFN concat_param1 = (AutomataFN)pilaAFN.pop();
                    AutomataFN concat_param2 = (AutomataFN)pilaAFN.pop();
                    AutomataFN concat_result = concatenacion(concat_param1,concat_param2,control);
                    control=true;
                    System.out.println(concat_result);
                    System.out.println("-----");
                    for (Estado e: concat_result.getEstados()){
                        for (Transicion tran : e.getTransiciones()){
                            System.out.println(tran.getFin());
                            System.out.println( tran.getFin().getTransiciones());
                        }
                       
                        
                       
                    }
                    System.out.println("---------");
                    pilaAFN.push(concat_result);
                    //concat_result.simular("abc");
                    this.afn=concat_result;
                    break;
                    
                case '|':
                    
                    AutomataFN union_param1 = (AutomataFN)pilaAFN.pop();
                    AutomataFN union_param2 = (AutomataFN)pilaAFN.pop();
                    AutomataFN union_result = union(union_param1,union_param2);
                    System.out.println(union_result);
                    
                      System.out.println("-----");
                    for (Estado e: union_result.getEstados()){
                        for (Transicion tran : e.getTransiciones()){
                            System.out.println(tran.getFin());
                            System.out.println( tran.getFin().getTransiciones());
                        }
                       
                        
                       
                    }
                    System.out.println("---------");
                    break;
                    
                default:
                   
                    AutomataFN simple = afnBasico((T) c);
                   // System.out.println(simple);
                    pilaAFN.push(simple);
                    //this.afn=simple;
                    
            }
        }
       //afn.simular("ab");
                
    }
    
    public AutomataFN afnBasico(T simboloRegex)
    {
        AutomataFN automataFN = new AutomataFN();
        Estado inicial = new Estado(0);
        Estado aceptacion = new Estado(1);
        
        Transicion tran = new Transicion(inicial, aceptacion,  simboloRegex);
        inicial.setTransiciones(tran);
        
        automataFN.setEstados(inicial);
        automataFN.setEstados(aceptacion);
        
        automataFN.setInicial(inicial);
        automataFN.setFin(aceptacion);
        return automataFN;
       
    }   
    
    public AutomataFN cerraduraKleene(AutomataFN automataFN)
    {
        AutomataFN afn_kleene = new AutomataFN();
                
        
        Estado nuevoInicio = new Estado(0);
        afn_kleene.setEstados(nuevoInicio);
        afn_kleene.setInicial(nuevoInicio);
        
        
        for (int i=0; i < automataFN.getEstados().size(); i++) {
            Estado tmp = (Estado) automataFN.getEstados().get(i);
            tmp.setId(i + 1);
            afn_kleene.setEstados(tmp);
        }
        
        
        Estado nuevoFin = new Estado(automataFN.getEstados().size() + 1);
        afn_kleene.setEstados(nuevoFin);
        afn_kleene.setFin(nuevoFin);
        
       
        Estado anteriorInicio = automataFN.getInicial();
        Estado anteriorFin    = automataFN.getFin();
        
        // agregar transiciones adicionales desde el nuevo estado inicial
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, epsilon));
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, nuevoFin, epsilon));
        
        // agregar transiciones adicionales desde el anterior estado final
        anteriorFin.getTransiciones().add(new Transicion(anteriorFin, anteriorInicio,epsilon));
        anteriorFin.getTransiciones().add(new Transicion(anteriorFin, nuevoFin, epsilon));
        
        System.out.println(afn_kleene.getFin().getId()+"id");
        return afn_kleene;
    }

   
    public AutomataFN concatenacion (AutomataFN automataFN1, AutomataFN automataFN2, boolean control){
        AutomataFN afn_concat = new AutomataFN();
        
      
//       Character simb =  (Character) fin.getTransiciones().get(0).getSimbolo();
//       Character simb2 =  (Character) nuevoInicio.getTransiciones().get(0).getSimbolo();
//       nuevoInicio.getTransiciones().get(0).setSimbolo(simb);
//       fin.getTransiciones().get(0).setSimbolo(simb2);
        //System.out.println(nuevoInicio+"init");
        
       // System.out.println(automataFN1);
       // System.out.println("d");
        //System.out.println(automataFN2);
     
        
//        if (control == true){
//            int i=0;
//            for (i=0; i < automataFN2.getEstados().size(); i++) {
//               
//               Estado tmp = (Estado) automataFN2.getEstados().get(i);
//               System.out.println(tmp.getTransiciones()+"temp");
//               tmp.setId(i);
//               if (i==automataFN2.getEstados().size()-1){
//                    automataFN1.getInicial().setId(i);
//                    automataFN1.getFin().setId(i+1);
//                    
//                    afn_concat.setEstados(automataFN1.getInicial());
//                    afn_concat.setFin(automataFN1.getFin());
//                     afn_concat.setEstados(automataFN1.getFin());
//                }
//               else
//               
//               afn_concat.setEstados(tmp);
//            }
//           
//          
//            
//            afn_concat.setInicial(automataFN2.getInicial());
//            
//        }
//        else{
            
            Estado nuevoInicio = new Estado(0);
          
            afn_concat.setEstados(nuevoInicio);
            afn_concat.setInicial(nuevoInicio);
           
            
            
            int i=0;
            for (i=0; i < automataFN2.getEstados().size(); i++) {
                Estado tmp = (Estado) automataFN2.getEstados().get(i);
                tmp.setId(i + 1);
                if (i == automataFN2.getEstados().size()-1){
                    tmp.setTransiciones(new Transicion(automataFN2.getFin(),automataFN1.getInicial(),epsilon));
                }
                afn_concat.setEstados(tmp);
                
            }
            
            Estado nuevoFin = new Estado(automataFN2.getEstados().size()+automataFN1.getEstados().size()+ 1);
           
            for (int j =0;j<automataFN1.getEstados().size();j++){
                Estado tmp = (Estado) automataFN1.getEstados().get(j);
                tmp.setId(i + 1);
                afn_concat.setEstados(tmp);
                if (automataFN1.getEstados().size()-1==j)
                     tmp.setTransiciones(new Transicion(tmp,nuevoFin,epsilon));
                i++;
            }
            afn_concat.setEstados(nuevoFin);
            afn_concat.setFin(nuevoFin);
           /* Estado primerFin = automataFN2.getFin();
            Estado inicioIntermedio = automataFN1.getInicial();
            Transicion tran = new Transicion(primerFin,inicioIntermedio,epsilon);
            afn_concat.setEstados(primerFin);
            afn_concat.setEstados(inicioIntermedio);*/
            Estado anteriorInicio = automataFN2.getInicial();
           /*for (int j=0;j<automataFN1.getEstados().size();j++){
               i++;
               Estado tmp = (Estado) automataFN1.getEstados().get(j);
               System.out.println(tmp.getTransiciones());
               tmp.setId(i+1);
               afn_concat.setEstados(tmp);
           }
          
            */
            
        
       
            
            Estado anteriorFin    = afn_concat.getFin();
           // System.out.println(anteriorFin);
            //afn_concat.setEstados(anteriorFin);*/
            
            nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, epsilon));
            System.out.println(anteriorFin);
            
            
            
//            Estado nuevoInicio = automataFN2.getInicial();
//             nuevoInicio.setId(0);
//             afn_concat.setEstados(nuevoInicio);
//             afn_concat.setInicial(nuevoInicio);
//             int i=0;
//             
//            for (i=0; i < automataFN1.getEstados().size()-1; i++) {
//
//               Estado tmp = (Estado) automataFN1.getEstados().get(i);
//               System.out.println(tmp+"temp");
//               tmp.setId(i+1);
//               afn_concat.setEstados(tmp);
//            
//            }
//            Estado tmp = (Estado) automataFN1.getEstados().get(i);
//            tmp.setId(i+1);
//            afn_concat.setEstados(tmp);
//            afn_concat.setFin(tmp);
        //}
//         
//        for (int i=0; i < automataFN2.getEstados().size(); i++) {
//            Estado tmp = (Estado) automataFN2.getEstados().get(i);
//            tmp.setId(i + 1);
//            afn_concat.setEstados(tmp);
//        }
        
      
      
//        
////        for (int i = 0;i<automataFN1.getEstados().size();i++){
//            afn_concat.setEstados(automataFN1.getEstados().get(i));
//        }
        
        
        return afn_concat;
        
    }
    
    public AutomataFN union(AutomataFN AFN1, AutomataFN AFN2){
        AutomataFN afn_union = new AutomataFN();
        
        Estado nuevoInicio = new Estado(0);
        nuevoInicio.setTransiciones(new Transicion(nuevoInicio,AFN2.getInicial(),epsilon));

        afn_union.setEstados(nuevoInicio);
        afn_union.setInicial(nuevoInicio);
        int i=0;
        for (i=0; i < AFN1.getEstados().size(); i++) {
            Estado tmp = (Estado) AFN1.getEstados().get(i);
            tmp.setId(i + 1);
            afn_union.setEstados(tmp);
        }
        
        for (int j=0; j < AFN2.getEstados().size(); j++) {
            Estado tmp = (Estado) AFN2.getEstados().get(j);
            tmp.setId(i + 1);
            afn_union.setEstados(tmp);
            i++;
        }
        
        
        Estado nuevoFin = new Estado(AFN1.getEstados().size() +AFN2.getEstados().size()+ 1);
        afn_union.setEstados(nuevoFin);
        afn_union.setFin(nuevoFin);
        
       
        Estado anteriorInicio = AFN1.getInicial();
        Estado anteriorFin    = AFN1.getFin();
        Estado anteriorFin2    = AFN2.getFin();
        
        // agregar transiciones adicionales desde el nuevo estado inicial
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, epsilon));
        
       
        
       
        anteriorFin.getTransiciones().add(new Transicion(anteriorFin, nuevoFin, epsilon));
        anteriorFin2.getTransiciones().add(new Transicion(anteriorFin2,nuevoFin,epsilon));
        
       
        
      
         
       
        
        
        return afn_union;
    }
    
    public AutomataFN getAfn() {
        return afn;
    }

    public void setAfn(AutomataFN afn) {
        this.afn = afn;
    }
    
    
    
    

}
