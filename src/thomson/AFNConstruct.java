/**
* Universidad Del Valle Guatemala
* Pablo Díaz 13203
* Descripción: Clase que implementa el algoritmo de Thomson
*/

package thomson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/**
 * Clase que implementa el algoritmo de Thomson
 * @author Pablo
 * @param <T>
 */
public class AFNConstruct<T> {
    
   
    private AFN afn;
    private final String regex;
   
    
    public AFNConstruct(String regex) {
        RegexConverter converter = new RegexConverter();
        this.regex = converter.infixToPostfix(regex);
       
        
    }
    
   
    /**
     * Metodo que construye el autómata
     */
    public void construct(){
        Stack pilaAFN = new Stack();
        
        for (Character c : this.regex.toCharArray()) {
            switch(c){
                case '*':
                     AFN kleene = cerraduraKleene((AFN) pilaAFN.pop());
                     pilaAFN.push(kleene);
                     this.afn=kleene;
                    break;
                case '.':
                    AFN concat_param1 = (AFN)pilaAFN.pop();
                    AFN concat_param2 = (AFN)pilaAFN.pop();
                    AFN concat_result = concatenacion(concat_param1,concat_param2);
                    System.out.println("-----");
                    for (Estado e: concat_result.getEstados()){
                        for (Transicion tran : e.getTransiciones()){
                            System.out.println(tran.getFin());
                            System.out.println( tran.getFin().getTransiciones());
                        }
                       
                        
                       
                    }
                    System.out.println("---------");
                    pilaAFN.push(concat_result);
                    this.afn=concat_result;
                    break;
                    
                case '|':
                    
                    AFN union_param1 = (AFN)pilaAFN.pop();
                    AFN union_param2 = (AFN)pilaAFN.pop();
                    AFN union_result = union(union_param1,union_param2);
                   
                    System.out.println("-----");
                    for (Estado e: union_result.getEstados()){
                        for (Transicion tran : e.getTransiciones()){
                            System.out.println(tran.getFin());
                            System.out.println( tran.getFin().getTransiciones());
                        }
                       
                        
                       
                    }
                    pilaAFN.push(union_result);
                    System.out.println("---------");
                    this.afn = union_result;
                    break;
                    
                default:
                    //crear un automata con cada simbolo
                    AFN simple = afnSimple((T) Character.toString(c));
                    pilaAFN.push(simple);
                    this.afn=simple;
                    
                   
                    
            }
        }
        this.afn.setAlfabeto(regex);
      
       
       
        
        
       
       
                
    }
    
    public AFN afnSimple(T simboloRegex)
    {
        AFN automataFN = new AFN();
        //definir los nuevos estados
        Estado inicial = new Estado(0);
        Estado aceptacion = new Estado(1);
        //crear una transicion unica con el simbolo
        Transicion tran = new Transicion(inicial, aceptacion,  simboloRegex);
        inicial.setTransiciones(tran);
        //agrega los estados creados
        automataFN.addEstados(inicial);
        automataFN.addEstados(aceptacion);
        
        automataFN.setEstadoInicial(inicial);
        automataFN.setEstadoFinal(aceptacion);
        return automataFN;
       
    }   
    
    public AFN cerraduraKleene(AFN automataFN)
    {
        AFN afn_kleene = new AFN();
                
        
        Estado nuevoInicio = new Estado(0);
        afn_kleene.addEstados(nuevoInicio);
        afn_kleene.setEstadoInicial(nuevoInicio);
        
        //agregar todos los estados intermedio
        for (int i=0; i < automataFN.getEstados().size(); i++) {
            Estado tmp = (Estado) automataFN.getEstados().get(i);
            tmp.setId(i + 1);
            afn_kleene.addEstados(tmp);
        }
        
        
        Estado nuevoFin = new Estado(automataFN.getEstados().size() + 1);
        afn_kleene.addEstados(nuevoFin);
        afn_kleene.setEstadoFinal(nuevoFin);
        
        //definir estados clave para realizar la cerraduras
        Estado anteriorInicio = automataFN.getEstadoInicial();
        
        ArrayList<Estado> anteriorFin    = automataFN.getEstadoFinal();
        
        // agregar transiciones desde el nuevo estado inicial
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, AFNThomsonMain.EPSILON));
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, nuevoFin, AFNThomsonMain.EPSILON));
        
        // agregar transiciones desde el anterior estado final
        for (int i =0; i<anteriorFin.size();i++){
            anteriorFin.get(i).getTransiciones().add(new Transicion(anteriorFin.get(i), anteriorInicio,AFNThomsonMain.EPSILON));
            anteriorFin.get(i).getTransiciones().add(new Transicion(anteriorFin.get(i), nuevoFin, AFNThomsonMain.EPSILON));
        }
        return afn_kleene;
    }

   public AFN concatenacion(AFN AFN1, AFN AFN2){
       
       AFN afn_concat = new AFN();
            

        int i=0;
        //agregar los estados del primer automata
        for (i=0; i < AFN2.getEstados().size(); i++) {
            Estado tmp = (Estado) AFN2.getEstados().get(i);
            tmp.setId(i);
            //se define el estado inicial
            if (i==0){
                afn_concat.setEstadoInicial(tmp);
            }
            //cuando llega al penultimo, concatena el ultimo con el primero del otro automata con un epsilon
            if (i == AFN2.getEstados().size()-1){
                for (int k = 0;k<AFN2.getEstadoFinal().size();k++){
                tmp.setTransiciones(new Transicion((Estado) AFN2.getEstadoFinal().get(k),AFN1.getEstadoInicial(),AFNThomsonMain.EPSILON));
                }
            }
            afn_concat.addEstados(tmp);

        }
        //termina de agregar los estados y transiciones del segundo automata
        for (int j =0;j<AFN1.getEstados().size();j++){
            Estado tmp = (Estado) AFN1.getEstados().get(j);
            tmp.setId(i);

            //define el ultimo con estado de aceptacion
            if (AFN1.getEstados().size()-1==j)
                afn_concat.setEstadoFinal(tmp);
             afn_concat.addEstados(tmp);
            i++;
        }
       
       return afn_concat;
   }
   
    
    public AFN union(AFN AFN1, AFN AFN2){
        AFN afn_union = new AFN();
        
        Estado nuevoInicio = new Estado(0);
        nuevoInicio.setTransiciones(new Transicion(nuevoInicio,AFN2.getEstadoInicial(),AFNThomsonMain.EPSILON));

        afn_union.addEstados(nuevoInicio);
        afn_union.setEstadoInicial(nuevoInicio);
        int i=0;//llevar el contador del identificador de estados
        //agregar los estados del segundo automata
        for (i=0; i < AFN1.getEstados().size(); i++) {
            Estado tmp = (Estado) AFN1.getEstados().get(i);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
        }
        //agregar los estados del primer automata
        for (int j=0; j < AFN2.getEstados().size(); j++) {
            Estado tmp = (Estado) AFN2.getEstados().get(j);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
            i++;
        }
        
        
        Estado nuevoFin = new Estado(AFN1.getEstados().size() +AFN2.getEstados().size()+ 1);
        afn_union.addEstados(nuevoFin);
        afn_union.setEstadoFinal(nuevoFin);
        
       
        Estado anteriorInicio = AFN1.getEstadoInicial();
        ArrayList<Estado> anteriorFin    = AFN1.getEstadoFinal();
        ArrayList<Estado> anteriorFin2    = AFN2.getEstadoFinal();
        
        // agregar transiciones desde el nuevo estado inicial
        nuevoInicio.getTransiciones().add(new Transicion(nuevoInicio, anteriorInicio, AFNThomsonMain.EPSILON));
        
         // agregar transiciones desde el anterior estado final
        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin.get(k).getTransiciones().add(new Transicion(anteriorFin.get(k), nuevoFin, AFNThomsonMain.EPSILON));
         // agregar transiciones desde el anterior estado final
        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin2.get(k).getTransiciones().add(new Transicion(anteriorFin2.get(k),nuevoFin,AFNThomsonMain.EPSILON));
        
       
        return afn_union;
    }
    
     public HashSet<Estado> eClosure(Estado eClosureEstado){
        Stack<Estado> pilaClosure = new Stack();
        Estado actual = eClosureEstado;
        HashSet<Estado> resultado = new HashSet();
        
        pilaClosure.push(actual);
        while(!pilaClosure.isEmpty()){
            actual = pilaClosure.pop();
           
            for (Transicion t: actual.getTransiciones()){
                
                if (t.getSimbolo().equals(AFNThomsonMain.EPSILON)){
                    resultado.add(t.getFin());
                    pilaClosure.push(t.getFin());
                }
            }
        }
        resultado.add(eClosureEstado); //la operacion e-Closure debe tener el estado aplicado
        return resultado;
    }
    
    public HashSet<Estado> move(HashSet<Estado> estados, String simbolo){
       
        HashSet<Estado> alcanzados = new HashSet();
        Iterator<Estado> iterador = estados.iterator();
        while (iterador.hasNext()){
            
            for (Transicion t: iterador.next().getTransiciones()){
                Estado siguiente = t.getFin();
                String simb = (String) t.getSimbolo();
                if (simb.equals(simbolo)){
                    alcanzados.add(siguiente);
                }
                
            }
            
        }
        return alcanzados;
        
    }
    
    public void simular(String regex)
    {
        
        HashSet<Estado> temp = new HashSet();
        HashSet<Estado> conjunto = eClosure(this.afn.getEstadoInicial());
       
        for (Character ch: regex.toCharArray()){
            conjunto = move(conjunto,ch.toString());
            System.out.println(conjunto);
            Iterator<Estado> iter = conjunto.iterator();
            
            while (iter.hasNext()){
               Estado siguiente = iter.next();
               /**
                * En esta parte es muy importante el metodo addAll
                * porque se tiene que agregar el eClosure de todo el conjunto
                * resultante del move y se utiliza un hashSet temporal porque
                * no se permite la mutacion mientras se itera
                */
                temp.addAll(eClosure(siguiente)); 
             
            }
            conjunto=temp;
        }
        if (conjunto.contains(this.afn.getEstadoFinal().get(0)))
            System.out.println("ACEPTADO");
      
    }
   
    
    
    
    public AFN getAfn() {
        return this.afn;
    }

    public void setAfn(AFN afn) {
        this.afn = afn;
    }
    
    
    
    

}