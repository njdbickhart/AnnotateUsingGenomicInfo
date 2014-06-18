/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author bickhart
 */
public class AnnotationData {
    protected ArrayList<String> names;
    protected ArrayList<Double> overlaps;
    protected int count;
    
    public AnnotationData(){
        this.count = 0;
        this.names = new ArrayList<>();
        this.overlaps = new ArrayList<>();
    }
    
    public void addToAnnotation(String name, double ovlp){
        name = name.replaceAll(";", ".");
        this.names.add(name);
        this.overlaps.add(ovlp);
        this.count++;
    }
    public String[] retStringRep(){
        String[] retstr = new String[3];
        retstr[0] = joinStr(this.names);
        retstr[1] = joinStr(this.overlaps);
        retstr[2] = String.valueOf(count);
        return retstr;
    }
    protected <T> String joinStr(ArrayList<T> n){
        if(n.size() == 1){
            return n.get(0).toString();
        }else if(n.isEmpty()){
            return null;
        }else{
            Collections.sort((ArrayList<String>)n);
            String retStr = n.get(0).toString();
            for(int x = 1; x < n.size(); x++){
                retStr += ";" + n.get(x).toString();
            }
            return retStr;
        }
    }
}
