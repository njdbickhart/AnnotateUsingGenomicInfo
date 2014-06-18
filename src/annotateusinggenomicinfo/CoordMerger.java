/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.BedCoord;
import file.BedMap;
import file.BedMapNoBin;
import file.BedSimple;
import utils.BedMerge;

/**
 *
 * @author bickhart
 */
public class CoordMerger extends BedMerge{
    
    public CoordMerger(int threads, BedMap ... a){
        super(threads, a);
    }
    
    @Override
    public BedMap getBedMap(){
        BedMap map = new BedMap();
        for(String c : this.mergeStore.keySet()){
            for(BedMapNoBin.bedData b : this.mergeStore.get(c)){
                map.addBedData(new BedCoord(c, String.valueOf(b.getStart()), String.valueOf(b.getEnd()), b.getName()));
            }
        }
        return map;        
    }
}
