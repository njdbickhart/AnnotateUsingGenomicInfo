/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import java.util.ArrayList;

/**
 *
 * @author bickhart
 */
public class SimpleCount extends AnnotationData{
    public void SimpleCount(){
        this.overlaps = new ArrayList<>();
    }
    
    public void addToAnnotation(double ovlp){
        this.count++;
        this.overlaps.add(ovlp);
    }
    @Override
    public String[] retStringRep(){
        String[] retstr = new String[2];
        retstr[0] = String.format("%.3f", this.ovlpAvg());
        retstr[1] = String.valueOf(this.count);
        return retstr;
    }
    
    private double ovlpAvg(){
        double sum = 0.0d;
        for(double d : this.overlaps){
            sum += d;
        }
        return sum / (double) this.overlaps.size();
    }
}
