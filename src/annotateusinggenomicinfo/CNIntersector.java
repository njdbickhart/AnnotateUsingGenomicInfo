/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.GeneBed;
import file.BedAbstract;
import file.BedMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import utils.LineIntersect;

/**
 *
 * @author bickhart
 */
public class CNIntersector extends LineIntersect implements Runnable{
    private String cnfile;
    private String indiv;
    private HashMap<String, BedMap> genes;
    public CNIntersector(String cnfile, String indiv, HashMap<String, BedMap> genes){
        this.cnfile = cnfile;
        this.indiv = indiv;
        this.genes = genes;
    }

    @Override
    public void run() {
        try(BufferedReader input = Files.newBufferedReader(Paths.get(this.cnfile), Charset.defaultCharset())){
            String line;
            while((line = input.readLine()) != null){
                String[] segs = line.split("\t");
                for(String db : genes.keySet()){
                    intersectCN(indiv, genes.get(db), segs[0], segs[1], segs[2], segs[3]);
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
        System.out.print(this.indiv + " ");
    }
    
    private void intersectCN(String indiv, BedMap a, String chr, String start, String end, String value){
        int s = Integer.valueOf(start);
        int e = Integer.valueOf(end);
        double v = Double.valueOf(value);
        if(a.containsChr(chr)){
            Set<Integer> bins = utils.BinBed.getBins(s, e);
            for(int bin : bins){
                if(a.containsBin(chr, bin)){
                    for(BedAbstract b : a.getBedAbstractList(chr, bin)){
                        if(ovCount(b.Start(), b.End(), s, e) > 0){
                            GeneBed working = (GeneBed) b;
                            working.addCNValue(indiv, v);
                        }
                    }
                }
            }
        }
    }
}
