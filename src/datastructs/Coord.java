/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import file.BedAbstract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author bickhart
 */
public abstract class Coord extends BedAbstract{
    protected HashMap<String, AnnotationData> annotation = new HashMap<>();
    
    public String formatOutStrVal(ArrayList<String> sorteddbs){
        return null;
    }
    
    public ArrayList<String> formatOutArrayVal(ArrayList<String> sorteddbs){
        return null;
    }
    
    public boolean annotationExists(String key){
        return this.annotation.containsKey(key);
    }
    
    public AnnotationData annotationEntry(String key){
        return this.annotation.get(key);
    }
    
    public Set<String> annotationKeyset(){
        return this.annotation.keySet();
    }
    
    public void addAnnotation(AnnotationData data, String db){
        if(!annotationExists(db)){
            this.annotation.put(db, data);
        }else{
            System.out.println("Collision in Annotation db entry: " + db + ". Consider different db name");
        }
    }
}
