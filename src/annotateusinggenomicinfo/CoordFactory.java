/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.BedCoord;
import datastructs.CNMOPSCoord;
import datastructs.CnvnatorCoord;
import datastructs.Coord;
import datastructs.DellyCoord;
import datastructs.RPSRCoord;
import datastructs.SimpleCoord;
import file.BedAbstract;
import file.BedMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author bickhart
 */
public class CoordFactory {
    private String[] headers;
    private BedMap<BedAbstract> coords;
    private String filename;
    
    public CoordFactory(String input, String indiv){
        this.coords = new BedMap<BedAbstract>();
        this.filename = indiv;
        LoadValueFile(input);
    }
    
    private void LoadValueFile(String input){
        try(BufferedReader in = Files.newBufferedReader(Paths.get(input), Charset.forName("UTF-8"))){
            String line;
            while((line = in.readLine()) != null){
                line = line.trim();
                String[] segs = line.split("\t");
                Coord coord = this.determineInput(segs);
                if(coord == null){
                    continue;
                }
                insertCoord(coord);
            }
        }catch( Exception ex){
            ex.printStackTrace();
        }
    }
    
    private void insertCoord(Coord coord){
        /*if(this.coords.containsKey(coord.Chr())){
            this.coords.get(coord.Chr()).add(coord);
        }else{
            this.coords.put(coord.Chr(), new ArrayList<Coord>());
            this.coords.get(coord.Chr()).add(coord);
        }
        * */
        this.coords.addBedData(coord);
    }
    
    private Coord determineInput(String[] segs) throws Exception{
        switch(segs.length){            
            case 3:
                checkHeaders("chr", "start", "end");
                return new SimpleCoord(segs[0], segs[1], segs[2]);
            case 4:
                if(isNumeric(segs[3])){
                    checkHeaders("chr", "start", "end", "value");
                    return new BedCoord(segs[0], segs[1], segs[2], Double.valueOf(segs[3]));
                }
                checkHeaders("chr", "start", "end", "name");
                return new BedCoord(segs[0], segs[1], segs[2], segs[3]);
            case 5:
                if(isNumeric(segs[4]) && !isNumeric(segs[3])){
                    checkHeaders("chr", "start", "end", "name", "value");
                    return new BedCoord(segs[0], segs[1], segs[2], segs[3], Double.valueOf(segs[4]));
                }else if(isNumeric(segs[3]) && !isNumeric(segs[4])){
                    checkHeaders("chr", "start", "end", "name", "value");
                    return new BedCoord(segs[0], segs[1], segs[2], segs[4], Double.valueOf(segs[3]));
                }else{
                    throw new Exception("Error with file! Bed file with 5 columns and two strings in name is currently not supported!");
                }
            case 7:
                checkHeaders("chr", "start", "end", "svtype", "svsupport");
                return new DellyCoord(segs);
            case 9:
                checkHeaders("chr", "start", "end", "svtype", "svsupport");
                return new CnvnatorCoord(segs);
            case 10:
                if(!isNumeric(segs[0]) && !isNumeric(segs[2]) && !isNumeric(segs[3])){
                    // This is just the header
                    return null;
                }else if(isNumeric(segs[1]) && isNumeric(segs[6])){
                    checkHeaders("chr", "start", "end", "svtype", "svsupport");
                    return new RPSRCoord(segs[0], segs[1], segs[2], segs[3], segs[4], segs[5], segs[6], segs[7], segs[8], segs[9]);
                }else if(isNumeric(segs[2]) && isNumeric(segs[3]) && isNumeric(segs[4])){
                    checkHeaders("chr", "start", "end", "name", "copynumber");
                    return new CNMOPSCoord(segs);
                }else{
                    throw new Exception("Error with file! Bed file with 10 columns is unrecognizable!");
                }
            default:
                throw new Exception("Error with file! Column length of " + segs.length + " is currently not supported!");
        }      
    }
    
    private void checkHeaders(String... heads) throws Exception{
        if(this.headers == null){
            this.headers = heads;
        }else{
            if(this.headers.length != heads.length){
                throw new Exception("Error with file! Disparate column sizes for entries!");
            }else{
                for(int x = 0; x < heads.length; x++){
                    if(!heads[x].equals(this.headers[x])){
                        throw new Exception("Error with file! Disparate output header entries! " + heads[x] + " " + this.headers[x]);
                    }
                }
            }
        }
        
    }
    
    protected boolean isNumeric(String s){
         return(s.matches("-?\\d+(.\\d+)?"));
    }
    
     /*
      * Getters
      */
    public Set<String> coordKeyset(){
        return this.coords.getListChrs();
    }
    
    public ArrayList<Coord> getCoords(String chr){
        ArrayList<Coord> coords = new ArrayList<>();
        for(BedAbstract b : this.coords.getSortedBedAbstractList(chr)){
            coords.add((Coord) b);
        }
        return coords;
    }
    public BedMap getModCoords(){
        BedMap ret = new BedMap();
        for(String chr : this.coords.getListChrs()){
            for(int bin : this.coords.getBins(chr)){
                for(BedAbstract b : this.coords.getBedAbstractList(chr, bin)){
                    ret.addBedData(chr, bin, new BedCoord(b.Chr(), b.Start(), b.End(), this.filename));
                }
            }
        }
        return ret;
    }
    public BedMap getAllCoords(){
        return this.coords;
    }
    
    public String[] getHeaders(){
        return this.headers;
    }
    public String getFileName(){
        return this.filename;
    }
    
    /*
     * Destroyer
     */
    public void close(){
        this.coords = null;
    }
}
