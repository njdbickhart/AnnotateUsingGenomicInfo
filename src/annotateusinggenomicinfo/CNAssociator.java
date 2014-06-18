/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import file.BedMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author bickhart
 */
public class CNAssociator {
    private HashMap<String, String> filelist;
    private Set<String> animals = new HashSet<>();
    
    public CNAssociator(String cnfilelist){
        try(BufferedReader in = Files.newBufferedReader(Paths.get(cnfilelist), Charset.defaultCharset())){
            String line;
            while((line = in.readLine())!= null){
                String[] segs = line.split("\t");
                filelist.put(segs[1], segs[0]);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
        animals = filelist.keySet();
    }
    
    public CNAssociator(HashMap<String, String> cns){
        this.filelist = cns;
        animals = filelist.keySet();
    }
    
    public void Association(HashMap<String, BedMap> genes){
        int threads = 3;
        if(filelist.keySet().size() < 3){
            threads = filelist.keySet().size();
        }
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        
        System.out.println("Associating CN values from " + filelist.keySet().size() + " individuals with " + genes.keySet().size() + " gene databases.");
        System.out.print("Working on individual... ");
        for(String indiv : filelist.keySet()){
            String cnfile = filelist.get(indiv);
            executor.execute(new CNIntersector(cnfile, indiv, genes));
        }
        System.out.println();
        
        executor.shutdown();
        
        while(!executor.isTerminated()){
            
        }
        System.out.println("Done with CN intersection");
    }
    
    public ArrayList<String> getSortedAnimals(){
        ArrayList<String> animals = new ArrayList<>();
        animals.addAll(this.animals);
        Collections.sort(animals);
        return animals;
    }
}
