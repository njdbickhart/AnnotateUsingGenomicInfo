/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import file.BedAbstract;
import file.BedFileException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bickhart
 */
public class GeneBed extends BedAbstract{
    private ConcurrentHashMap<String, Double> cnvalues = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<>();
    private Double overlap;
    private String cnvroverlaps;
    
    public GeneBed(String chr, String start, String end, String name){
        try {
            this.initialVals(chr, start, end);
        } catch (BedFileException ex) {
            Logger.getLogger(GeneBed.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.name = name;
    }
    
    public void addCNValue(String indiv, double cn){        
        if(!counter.containsKey(indiv)){
            cnvalues.put(indiv, cn);
            counter.put(indiv, 1);
        }else{
            int num = counter.get(indiv);
            double cnsum = (cnvalues.get(indiv) * (double) num) + cn;
            num++;
            cnvalues.put(indiv, (cnsum / (double)num));
            counter.put(indiv, num);
        }
    }
    
    @Override
    public int compareTo(BedAbstract t) {
        return this.Start() - t.Start();
    }
    
    //returns: gene chr start end overlap% cnvrstr cn
    public ArrayList<String> formatOutArray(ArrayList<String> sorteddbs){
        ArrayList<String> values =  new ArrayList<>(cnvalues.keySet().size() + 5);
        values.add(name);
        values.add(chr);
        values.add(String.valueOf(start));
        values.add(String.valueOf(end));
        if(this.overlap != null){
            values.add(String.format("%.3f", overlap));
        }else{
            values.add("");
        }
        if(this.cnvroverlaps != null){
            values.add(cnvroverlaps);
        }else{
            values.add("");
        }
        
        for(String i : sorteddbs){
            values.add(String.format("%.3f", this.cnvalues.get(i)));
        }
        return values;
    }
    
    /*
     * Setters
     */
    public void setOverlap(int start, int end){
        double val = ovCount(start, end, this.start, this.end) / (double) (this.end - this.start);
        if(this.overlap == null){
            this.overlap = val;
        }else{
            this.overlap += val;
            if(this.overlap > 1.0d){
                this.overlap = 1.0d;
            }
        }
    }
    public void setCNVRStr(int counter){
        if(this.cnvroverlaps == null){
            this.cnvroverlaps = String.valueOf(counter);
        }else{
            this.cnvroverlaps += ";" + String.valueOf(counter);
        }
    }
    
    
    /*
     * Private methods
     */
    
    protected int ovCount (int start1, int end1, int start2, int end2){
        return soonest(end1, end2) - latest(start1, start2);
    }
    protected int soonest (int a, int b){
        return (a >= b)? b : a;
    }
    protected int latest (int a, int b){
        return (a >= b)? a : b;
    }
}
