/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.AnnotationData;
import datastructs.AnnotationMap;
import file.BedAbstract;
import file.BedMap;
import file.BedSimple;
import utils.BinBed;
import utils.LineIntersect;

/**
 *
 * @author bickhart
 */
public class IntersectionTool extends LineIntersect{
    public static AnnotationData retAnno(AnnotationMap a, String chr, int start, int end){
        AnnotationData data = new AnnotationData();
        if(a.containsChr(chr)){
            int bin = BinBed.getBin(start, end);
            if(a.containsBin(chr, bin)){
                for(BedAbstract bed : a.getBedAbstractList(chr, bin)){
                    BedSimple working = (BedSimple) bed;
                    int ovlp = ovCount(bed.Start(), bed.End(), start, end);
                    if( ovlp > 0){
                        data.addToAnnotation(working.Name(), (double) ovlp / (bed.End() - bed.Start()));
                    }
                }
            }else{
                return data;
            }
        }
        return data;
    }
    
    
}
