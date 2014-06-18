/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.AnnotationData;
import datastructs.AnnotationFile;
import datastructs.BedCoord;
import datastructs.Coord;
import file.BedAbstract;
import file.BedMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author bickhart
 */
public class AnnotationFactory {
    private HashMap<String, String> dbFileList;
    private BedMap<BedAbstract> coords;
    private ArrayList<String> dbs;
    
    
    public AnnotationFactory(String dbFileList, CoordFactory coords){
        this.dbFileList = new HashMap<>();
        try(BufferedReader input = Files.newBufferedReader(Paths.get(dbFileList), Charset.forName("UTF-8"))){
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
        this.coords = coords.getAllCoords();
        this.dbs = new ArrayList<>();
    }
    
    public AnnotationFactory(String dbFileList, BedMap map){
        this.dbFileList = new HashMap<>();
        try(BufferedReader input = Files.newBufferedReader(Paths.get(dbFileList), Charset.forName("UTF-8"))){
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
        this.coords =  map;
        this.dbs = new ArrayList<>();
    }

    public void run(){
        for(String dbName : dbFileList.keySet()){
            this.dbs.add(dbName);
            //System.out.println("Working on " + dbName);
            AnnotationFile db = new AnnotationFile(dbFileList.get(dbName), dbName);
            //System.out.append("Finished Loading " + dbName);
            for(String chr : this.coords.getListChrs()){
                ArrayList<BedAbstract> working = this.coords.getSortedBedAbstractList(chr);
                for(BedAbstract b : working){
                    Coord c = (Coord) b;
                    AnnotationData dat = db.intersectCoordinate(c);
                    c.addAnnotation(dat, dbName);
                }
            }
            db.clearOut();
        } 
    }
    /*
     * Getters
     */
    public ArrayList<String> getDbs(){
        return this.dbs;
    }
}
