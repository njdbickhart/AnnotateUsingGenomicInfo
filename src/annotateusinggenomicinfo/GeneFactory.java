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

/**
 *
 * @author bickhart
 */
public class GeneFactory {
    private HashMap<String, String> dbFileList;
    
    public GeneFactory(String dbfile){
        this.dbFileList = new HashMap<>();
        try(BufferedReader input = Files.newBufferedReader(Paths.get(dbfile), Charset.forName("UTF-8"))){
            String line;
            while((line = input.readLine()) != null){
                line = line.trim();
                String[] segs = line.split("\t");
                if(segs.length != 2){
                    System.out.println("Improperly formatted db list! Please use the following tab delimited format:");
                    System.out.println("Genedb_file\tGenedb_name");
                    System.exit(-1);
                }
                this.dbFileList.put(segs[1], segs[0]);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public HashMap<String, BedMap<BedAbstract>> GetGeneMaps(BedMap<BedAbstract> cnvrs){
        HashMap<String, BedMap<BedAbstract>> genes = new HashMap<>();
        for(String db : dbFileList.keySet()){
            String file = dbFileList.get(db);
            try(BufferedReader input = Files.newBufferedReader(Paths.get(file), Charset.defaultCharset())){
                String line;
                genes.put(db, new BedMap());
                BedMap working = genes.get(db);
                while((line = input.readLine()) != null){
                    String[] segs = line.split("\t");
                    working.addBedData(new GeneBed(segs[0], segs[1], segs[2], segs[3]));
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        
        // Now, add the CNVR coordinates in exhaustive fashion
        genes.put("CNVRs", new BedMap());
        for(String chr : cnvrs.getListChrs()){
            for(BedAbstract b : cnvrs.getSortedBedAbstractList(chr)){
                genes.get("CNVRs").addBedData(new GeneBed(b.Chr(), String.valueOf(b.Start()), String.valueOf(b.End()), b.Name()));
            }
        }
        return genes;
    }
    
    public HashMap<String, BedMap<BedAbstract>> GetGeneMaps(){
        HashMap<String, BedMap<BedAbstract>> genes = new HashMap<>();
        for(String db : dbFileList.keySet()){
            String file = dbFileList.get(db);
            try(BufferedReader input = Files.newBufferedReader(Paths.get(file), Charset.defaultCharset())){
                String line;
                genes.put(db, new BedMap());
                BedMap working = genes.get(db);
                while((line = input.readLine()) != null){
                    String[] segs = line.split("\t");
                    working.addBedData(new GeneBed(segs[0], segs[1], segs[2], segs[3]));
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return genes;
    }
}
