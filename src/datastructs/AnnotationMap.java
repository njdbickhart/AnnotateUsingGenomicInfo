/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import file.BedAbstract;
import file.BedMap;
import file.BedSimple;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author bickhart
 */
public class AnnotationMap extends BedMap<BedAbstract>{
    
    public void addBedData(BedSimple bed){
       String chr = bed.Chr();
       int bin = utils.BinBed.getBin(bed.Start(), bed.End());
       if(this.bedFile.containsKey(chr)){
           if(this.bedFile.get(chr).containsKey(bin)){
               this.bedFile.get(chr).get(bin).add(bed);
           }else{
               ArrayList<BedAbstract> temp = new ArrayList<>(100);
               temp.add(bed);
               this.bedFile.get(chr).put(bin, temp);
           }
       }else{
           ArrayList<BedAbstract> temp = new ArrayList<>(100);
           temp.add(bed);
           ConcurrentHashMap<Integer, ArrayList<BedAbstract>> thash = new ConcurrentHashMap<>(3000);
           thash.put(bin, temp);
           this.bedFile.put(chr, thash);
       }
    }
}
