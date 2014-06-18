/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastructs;

import annotateusinggenomicinfo.IntersectionTool;
import file.BedSimple;
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
public class AnnotationFile {
    private AnnotationMap annotationDB;
    private String dbName;
    
    public AnnotationFile(String inputFile, String dbName){
        this.dbName = dbName;
        this.annotationDB = new AnnotationMap();
        loadFile(inputFile);
    }
    
    private void loadFile(String file){
        System.out.println("Loading file: " + file);
        try(BufferedReader input = Files.newBufferedReader(Paths.get(file), Charset.forName("UTF-8"))){
            String line;
            while((line = input.readLine()) != null){
                line = line.trim();
                String segs[] = line.split("\t");
                BedSimple entry = new BedSimple(segs[0], Integer.valueOf(segs[1]), Integer.valueOf(segs[2]), segs[3]);
                this.annotationDB.addBedData(entry);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(NumberFormatException ex){
            System.out.println("Error with Bed entry coordinates!");
            ex.printStackTrace();
        }
    }
    
    public AnnotationData intersectCoordinate(Coord c){
        return IntersectionTool.retAnno(this.annotationDB, c.Chr(), c.Start(), c.End());
    }
    
    public void clearOut(){
        this.annotationDB = null;
    }
}
