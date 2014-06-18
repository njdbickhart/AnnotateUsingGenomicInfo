/*
 * This program is designed to take a wide variety of files that have chr, start and end positions
 * And to intersect them against genomic positions
 * It prints two excel workbooks; one with the CNVRs and gene coordinates and one with the individual CNVs and gene intersections
 */
package annotateusinggenomicinfo;

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
public class ParseCommandLine {
    public String dbfilelist = null;
    public HashMap<String, String> intersectionbed = new HashMap<>();
    public HashMap<String, String> cnfilelist = new HashMap<>();
    public String outputfile = null;
    public boolean text = false;
    public boolean cnintersect = false;
    public String usage = "java -jar AnnotateUsingGenomicInfo "
            + System.lineSeparator() + "\t-d <dbfile list> "
            + System.lineSeparator() + "\t-i <list of beds to intersect, or single bed file> "
            + System.lineSeparator() + "\t-c <list of cn files[optional]> "
            + System.lineSeparator() + "\t-o <output file name> "
            + System.lineSeparator() + "\t-t <boolean: print to text>";
    
    public ParseCommandLine(String[] args){
        for (int i = 0; i < args.length; i++){
            switch(args[i]){
                case "-d":
                    this.dbfilelist = args[i+1];
                    break;
                case "-i":
                    CheckIfBed(args[i+1]);
                    break;
                case "-c":
                    AssignCN(args[i+1]);
                    this.cnintersect = true;
                    break;
                case "-o":
                    this.outputfile = args[i+1];
                    break;
                case "-t":
                    this.text = true;
                    break;                    
            }
        }
        if(this.dbfilelist == null || this.intersectionbed == null || this.outputfile == null){
            System.out.println("Incorrect number of arguments supplied");
            System.out.println(usage);
            System.exit(0);
        }
    }
    
    private void CheckIfBed(String input){
        try(BufferedReader in = Files.newBufferedReader(Paths.get(input), Charset.defaultCharset())){
            String line;
            line = in.readLine();
            line = line.trim();
            String[] segs = line.split("\t");
            if(segs.length == 2){
                this.intersectionbed.put(segs[1], segs[0]);
                while((line = in.readLine()) != null){
                    line = line.trim();
                    segs = line.split("\t");
                    this.intersectionbed.put(segs[1], segs[0]);
                }                
            }else if(segs.length > 2 && segs.length < 12){
                this.intersectionbed.put(input, input);
            }else{
                System.out.println("Input file is improperly formatted!");
                System.out.println("Please use a tab delimited format with <file\tindividual>");
                System.out.println(usage);
                System.exit(-1);
            }
        }catch(IOException ex){
            System.out.println("Could not open input file: " + input + "!");
            System.out.println(usage);
        }        
    }
    private void AssignCN(String input){
        try(BufferedReader in = Files.newBufferedReader(Paths.get(input), Charset.defaultCharset())){
            String line;
            while((line = in.readLine()) != null){
                line = line.trim();
                String[] segs = line.split("\t");
                if(segs.length != 2){
                    System.out.println("CN file list is improperly formatted!");
                    System.out.println("Please use a tab delimited format with <file\tindividual>");
                    System.out.println(usage);
                    System.exit(-1);
                }
                this.cnfilelist.put(segs[1], segs[0]);
            }
        }catch(IOException ex){
            System.out.println("Could not open CN file list: " + input + "!");
        }
    }
}
