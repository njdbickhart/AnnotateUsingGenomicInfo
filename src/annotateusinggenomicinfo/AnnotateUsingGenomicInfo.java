/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import file.BedAbstract;
import file.BedMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author bickhart
 */
public class AnnotateUsingGenomicInfo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ParseCommandLine cmd = new ParseCommandLine(args);
        
        ArrayList<CoordFactory> coordlist = new ArrayList<>();
        for(String name : cmd.intersectionbed.keySet()){
            coordlist.add(new CoordFactory(cmd.intersectionbed.get(name), name));
        }
        BedMap<BedAbstract> CNVRs = null;
        // Do CNVR intersection
        BedMap<BedAbstract> CNVS = new BedMap<BedAbstract>();
        for(CoordFactory c : coordlist){
            CNVS.combineBedMaps(c.getModCoords());
        }

        CoordMerger merger = new CoordMerger(3, CNVS);
        merger.RunMerger();

        CNVRs = merger.getBedMap();
        merger.close();

        CNVS = null;
        System.gc();

        AnnotationFactory anno1 = new AnnotationFactory(cmd.dbfilelist, CNVRs);
        anno1.run();
        
        
        ArrayList<String> dblist = null;
        for(CoordFactory c : coordlist){
            AnnotationFactory anno2 = new AnnotationFactory(cmd.dbfilelist, c);
            anno2.run();
            if(dblist == null){
                dblist = anno2.getDbs();
            }
        }
        
        if(cmd.text){
            for(CoordFactory c : coordlist){
                OutputText out = new OutputText(c);
                out.PrintToFile(cmd.outputfile + "." + c.getFileName() + ".anno", dblist);
                GeneFactory gf = new GeneFactory(cmd.dbfilelist);
                HashMap<String, BedMap<BedAbstract>> genes = gf.GetGeneMaps();
                OutputSecondExcel out2 = new OutputSecondExcel(CNVRs, genes);
                out2.JustCNVRs(cmd.outputfile, dblist);
            }
            if(cmd.cnintersect){
                // Do gene CN intersections
                System.out.println("Working on db window value intersections...");
                GeneFactory gf = new GeneFactory(cmd.dbfilelist);
                HashMap<String, BedMap<BedAbstract>> genes = gf.GetGeneMaps();
                
                CNAssociator assoc = new CNAssociator(cmd.cnfilelist);
                assoc.Association(genes);
                
                System.out.println("Working on Text output...");
                OutputSecondExcel out2 = new OutputSecondExcel(CNVRs, genes);
                ArrayList<String> sortedans = assoc.getSortedAnimals();
                out2.PrintToText(cmd.outputfile, dblist, sortedans);
            }
        }else{
            HashMap<String, CoordFactory> coordhash = new HashMap<>();
            for(CoordFactory c : coordlist){
                coordhash.put(c.getFileName(), c);
            }
            OutputExcel out = new OutputExcel(coordhash);
            out.PrintToFile(cmd.outputfile + "_anno.xls", dblist);
            System.out.println("Created individual region annotation spreadsheet: " + cmd.outputfile + "_anno.xls");
            
            for(CoordFactory c : coordlist){
                c.close();
            }
            System.gc();
            // Now for the other excel spreadsheet with the CNVRs and gene CN lists
            if(cmd.cnintersect){
                // Do gene CN intersections
                System.out.println("Working on db window value intersections...");
                GeneFactory gf = new GeneFactory(cmd.dbfilelist);
                HashMap<String, BedMap<BedAbstract>> genes = gf.GetGeneMaps();
                
                CNAssociator assoc = new CNAssociator(cmd.cnfilelist);
                assoc.Association(genes);
                
                System.out.println("Working on Excel output...");
                OutputSecondExcel out2 = new OutputSecondExcel(CNVRs, genes);
                ArrayList<String> sortedans = assoc.getSortedAnimals();
                out2.PrintToFile(cmd.outputfile + "_windows_genetable.xls", dblist, sortedans);
            }
        }
    }
    
    public static String getBaseName(String input){
        String[] segs = input.split("/");
        return segs[segs.length - 1];
    }
}
