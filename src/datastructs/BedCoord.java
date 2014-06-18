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
public class BedCoord extends Coord{
    protected Double value;
    protected int num;
    
    public BedCoord(String chr, String start, String end, String name){
        this.chr = chr;
        try{
            this.start = Integer.valueOf(start);
            this.end = Integer.valueOf(end);
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        this.name = name;
        this.type = 1;
    }
    public BedCoord(String chr, int start, int end, String name){
        this.chr = chr;
        this.start = start;
        this.end = end;
        this.name = name;
        this.type = 1;
    }
    public BedCoord(String chr, String start, String end, Double value){
        this.chr = chr;
        try{
            this.start = Integer.valueOf(start);
            this.end = Integer.valueOf(end);
        }catch(NumberFormatException ex){
            ex.printStackTrace();
        }
        this.value = value;
        this.type = 3;
    }
    
    public BedCoord(String chr, String start, String end, String name, Double value){
        this(chr, start, end, name);
        this.value = value;
        this.type = 2;
    }
    
    @Override
    public int compareTo(BedAbstract t) {
        return this.Start() - t.Start();
    }
    
    /*
     * Getters
     */
    
    @Override
    public String formatOutStrVal(ArrayList<String> sorteddbs){
        StringBuilder str = new StringBuilder();
        str.append(chr).append("\t").append(start).append("\t").append(end).append("\t");
        str.append(name).append("\t").append(String.format("%.6f", value));
        for(String db : sorteddbs){
            AnnotationData dat = annotation.get(db);
            String[] vals= dat.retStringRep();
            if(vals[2].equals("0")){
                str.append("\t").append("\t").append("\t");
            }else{
                str.append("\t").append(vals[2]).append("\t").append(vals[0]).append("\t").append(vals[1]);
            }
        }   
        str.append("\n");
        return str.toString();        
    }
    
    @Override
    public ArrayList<String> formatOutArrayVal(ArrayList<String> sorteddbs){
        ArrayList<String> vals = new ArrayList<>();
        vals.add(chr);
        vals.add(String.valueOf(start));
        vals.add(String.valueOf(end));
        if(type == 1 || type == 2){vals.add(name);}
        
        if(type != 1 ){
            vals.add(String.format("%.6f",value));
        }
        for(String db : sorteddbs){
            AnnotationData dat = annotation.get(db);
            String[] v= dat.retStringRep();
            if(v[2].equals("0")){
                vals.add("");
                vals.add("");
                vals.add("");
            }else{
                vals.add(v[2]);
                vals.add(v[0]);
                vals.add(v[1]);
            }
        }
        return vals;
    }
    
    // Returns: # chr start end #animals animals dbs
    public ArrayList<String> formatOutArrayCNVR(ArrayList<String> sorteddbs){
        ArrayList<String> vals = new ArrayList<>();
        vals.add(String.valueOf(this.num));
        vals.add(chr);
        vals.add(String.valueOf(start));
        vals.add(String.valueOf(end));
        
        String[] count = this.name.split(";");
        vals.add(String.valueOf(count.length));
        vals.add(name);
        
        for(String db : sorteddbs){
            AnnotationData dat = annotation.get(db);
            String[] v= dat.retStringRep();
            if(v[2].equals("0")){
                vals.add("");
                vals.add("");
                vals.add("");
            }else{
                vals.add(v[2]);
                vals.add(v[0]);
                vals.add(v[1]);
            }
        }
        return vals;
    }
    
    /*
     * Setters
     */
    public void setNum(int num){
        this.num = num;
    }
}
