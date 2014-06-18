/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import file.BedAbstract;
import file.BedFileException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bickhart
 */
public class DellyCoord extends Coord{
    protected String svtype;
    protected double svsup;
    
    /*
     * Delly: 7
            chr1   32195  32949    754      4       37      >Deletion_xxx_00000000<
            * */
    
    public DellyCoord(String segs[]){
        try {
            this.initialVals(segs[0], segs[1], segs[2]);
        } catch (BedFileException ex) {
            Logger.getLogger(DellyCoord.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.svtype = segs[6];
        this.svsup = Double.valueOf(segs[5]);
    }
    
    @Override
    public int compareTo(BedAbstract t) {
        return this.Start() - t.Start();
    }
    
    @Override
    public String formatOutStrVal(ArrayList<String> sorteddbs) {
        StringBuilder str = new StringBuilder();
        str.append(chr).append("\t").append(start).append("\t").append(end).append("\t");
        str.append(svtype).append(";").append(String.format("%.6f", svsup));
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
        vals.add(svtype);
        vals.add(String.format("%.6f",svsup));
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
}
