/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.Coord;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author bickhart
 */
public class OutputExcel {
    private HashMap<String, CoordFactory> coord;
    public OutputExcel(HashMap<String, CoordFactory> coord){
        this.coord = coord;
    }
    
    public void PrintToFile(String outbase, ArrayList<String> sorteddbs){
        WritableFont times12pt = new WritableFont(WritableFont.TIMES, 12);
        WritableFont timesheader = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
        WritableCellFormat header = new WritableCellFormat();
        WritableCellFormat regular = new WritableCellFormat();
        
        try{
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outbase));
            
            header.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            header.setFont(timesheader);
            regular.setFont(times12pt);
            
            int i = 0;
            ArrayList<String> animalNames = new ArrayList<>();
            animalNames.addAll(this.coord.keySet());
            Collections.sort(animalNames);
            for(String fname : animalNames){
                String sname = sanitizeString(fname);
                WritableSheet sheet = workbook.createSheet(sname, i);
                Set<String> chrset = coord.get(fname).coordKeyset();
                ArrayList<String> sortedchrs = utils.SortByChr.ascendingChr(chrset);
                
                // Create headers
                String[] info = coord.get(fname).getHeaders();
                for(int x = 0; x < info.length; x++){
                    writeString(sheet, x, 0, info[x], header);
                }
                int y = 0;
                for(int x = info.length; x < info.length + (sorteddbs.size() * 3); x += 3){
                    writeString(sheet, x, 0, "#" + sorteddbs.get(y), header);
                    writeString(sheet, x + 1, 0, sorteddbs.get(y), header);
                    writeString(sheet, x + 2, 0, "%ovlp-" + sorteddbs.get(y), header);
                    y++;
                }
                
                // Print out data
                int row = 1;
                for(String chr : sortedchrs){
                    for(Coord c : coord.get(fname).getCoords(chr)){
                        ArrayList<String> vals = c.formatOutArrayVal(sorteddbs);
                        for(int a = 0; a < vals.size(); a++){
                            writeString(sheet, a, row, vals.get(a), regular);
                        }
                        row += 1;
                    }
                }               
                i++;
                sheet.getSettings().setHorizontalFreeze(0);
                
            }
            workbook.write();
            workbook.close();
        }catch(WriteException | IOException ex){
            ex.printStackTrace();
        }
    }
    private String sanitizeString(String str){
        String[] base = str.split("[/\\\\]");
        return base[base.length - 1];
    }
    
    private void writeString(WritableSheet sheet, int col, int row, String s, WritableCellFormat format) throws WriteException{
        Label label = new Label(col, row, s, format);
        sheet.addCell(label);
    }
}
