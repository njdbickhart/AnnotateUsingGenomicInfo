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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bickhart
 */
public class CNMOPSCoord extends Coord{
    protected int cn;
    /*
     * * cn.mops: 10
     * "1" "chr1" 93778988 93818957 39970 "*" "BTAN01" 1.58629269641922 1.60674959169292 "CN8"
     */
    
    public CNMOPSCoord(String[] segs){
        segs[1] = segs[1].replaceAll("\"", "");
        segs[6] = segs[6].replaceAll("\"", "");
        segs[9] = segs[9].replaceAll("\"", "");
        
        try {
            this.initialVals(segs[1], segs[2], segs[3]);
        } catch (BedFileException ex) {
            Logger.getLogger(CNMOPSCoord.class.getName()).log(Level.SEVERE, null, ex);
        }
        Pattern number = Pattern.compile("CN(\\d+)");
        Matcher match = number.matcher(segs[9]);
        match.find();
        this.cn = Integer.valueOf(match.group(1));
        this.name = segs[6];
    }

    @Override
    public int compareTo(BedAbstract t) {
        return this.Start() - t.Start();
    }
    
    @Override
    public String formatOutStrVal(ArrayList<String> sorteddbs) {
        StringBuilder str = new StringBuilder();
        str.append(chr).append("\t").append(start).append("\t").append(end).append("\t");
        str.append(name).append(";").append(cn);
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
        vals.add(String.valueOf(cn));
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
