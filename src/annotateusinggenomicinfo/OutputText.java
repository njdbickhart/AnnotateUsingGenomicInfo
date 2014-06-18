/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.BedCoord;
import datastructs.Coord;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author bickhart
 */
public class OutputText {
    private CoordFactory coord;
    public OutputText(CoordFactory coord){
        this.coord = coord;
    }
    
    public void PrintToFile(String output, ArrayList<String> sorteddbs){
        try(BufferedWriter out = Files.newBufferedWriter(Paths.get(output), Charset.forName("UTF-8"))){
            Set<String> chrset = coord.coordKeyset();
            ArrayList<String> sortedchrs = utils.SortByChr.ascendingChr(chrset);
            for(String chr : sortedchrs){
                for(Coord c : coord.getCoords(chr)){
                    out.write(c.formatOutStrVal(sorteddbs));
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
