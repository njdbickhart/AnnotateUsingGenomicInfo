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
public class CnvnatorCoord extends Coord{
    protected String svtype;
    protected double svsup;
    /*
    * CNVnator: 9
    * duplication	chr1:1-76000	76000	2.72997	0	1.98253e+08	0	2.127e+08	1
    * */
    
    public CnvnatorCoord(String[] segs){
        try {
            String[] coords = utils.UCSCToStringArray.UCSCToArray(segs[1]);
            this.initialVals(coords[0], coords[1], coords[2]);
        } catch (BedFileException ex) {
            Logger.getLogger(CnvnatorCoord.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.svtype = segs[0];
        this.svsup = Double.valueOf(segs[4]);
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
    public int compareTo(BedAbstract t) {
        return this.Start() - t.Start();
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
