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
            for(int b : BinBed.getBins(start, end)){
                if(a.containsBin(chr, b)){
                    for(BedAbstract bed : a.getBedAbstractList(chr, b)){
                        BedSimple working = (BedSimple) bed;
                        int ovlp = ovCount(bed.Start(), bed.End(), start, end);
                        if( ovlp > 0){
                            data.addToAnnotation(working.Name(), (double) ovlp / (bed.End() - bed.Start()));
                        }
                    }
                }
            }
        }
        return data;
    }
    
    
}
