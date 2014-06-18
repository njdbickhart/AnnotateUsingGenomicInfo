/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annotateusinggenomicinfo;

import datastructs.BedCoord;
import datastructs.Coord;
import datastructs.GeneBed;
import file.BedAbstract;
import file.BedMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author bickhart
 */
public class OutputSecondExcel {
    private BedMap CNVR;
    private HashMap<String, BedMap> genes;
    
    public OutputSecondExcel(BedMap CNVR, HashMap<String, BedMap> genes){
        this.CNVR = CNVR;
        this.genes = genes;
    }
    public void JustCNVRs(String outbase, ArrayList<String> sorteddbs){
        //Work with CNVRs first, then overlap CNVRs with Genes to get number tallies
        // Returns: # chr start end #animals animals dbs
        ArrayList<BedCoord> orderedList = OrderCNVRS();
        int i = 0;
        ArrayList<String> cnvhead = new ArrayList<>();
        cnvhead.add("CNVR#");
        cnvhead.add("chr");
        cnvhead.add("start");
        cnvhead.add("end");
        cnvhead.add("#animals");
        cnvhead.add("animals");
        
        
        for(int x = 0; x < sorteddbs.size(); x++){
            cnvhead.add("#" + sorteddbs.get(x));
            cnvhead.add(sorteddbs.get(x));
            cnvhead.add("%ovlp-" + sorteddbs.get(x));
            
        }
        try(BufferedWriter cnvrs = Files.newBufferedWriter(Paths.get(outbase + "_cnvrs.tab"), Charset.defaultCharset())){
            printTabTextLine(cnvhead, cnvrs);
            for(BedCoord b : orderedList){
                ArrayList<String> vals = b.formatOutArrayCNVR(sorteddbs);
                printTabTextLine(vals, cnvrs);                
            }
            i++;
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public void PrintToText(String outbase, ArrayList<String> sorteddbs, ArrayList<String> cnnames){
        //Work with CNVRs first, then overlap CNVRs with Genes to get number tallies
        // Returns: # chr start end #animals animals dbs
        ArrayList<BedCoord> orderedList = OrderCNVRS();
        int i = 0;
        ArrayList<String> cnvhead = new ArrayList<>();
        cnvhead.add("CNVR#");
        cnvhead.add("chr");
        cnvhead.add("start");
        cnvhead.add("end");
        cnvhead.add("#animals");
        cnvhead.add("animals");
        
        
        for(int x = 0; x < sorteddbs.size(); x++){
            cnvhead.add("#" + sorteddbs.get(x));
            cnvhead.add(sorteddbs.get(x));
            cnvhead.add("%ovlp-" + sorteddbs.get(x));
            
        }
        try(BufferedWriter cnvrs = Files.newBufferedWriter(Paths.get(outbase + "_cnvrs.tab"), Charset.defaultCharset())){
            printTabTextLine(cnvhead, cnvrs);
            for(BedCoord b : orderedList){
                ArrayList<String> vals = b.formatOutArrayCNVR(sorteddbs);
                printTabTextLine(vals, cnvrs);                
            }
            i++;
        }catch(IOException ex){
            ex.printStackTrace();
        }
        
        //Now for the cn values;
        System.out.print("Working on cn tables for CNVR spreadsheet for... ");
        for(String fname : this.genes.keySet()){
            System.out.print(fname + " ");
            String sname = sanitizeString(fname);
            Set<String> chrset = genes.get(fname).getListChrs();
            ArrayList<String> sortedchrs = utils.SortByChr.ascendingChr(chrset);

            // Create headers
            //String[] info = {"gene", "chr", "start", "end", "CNVRoverlap", "CNVRintersections"};
            ArrayList<String> info = new ArrayList<>();
            info.add("gene");
            info.add("chr");
            info.add("start");
            info.add("end");
            info.add("CNVRoverlap");
            info.add("CNVRintersections");
            
            for(int x = 0; x < cnnames.size(); x++){
                info.add(cnnames.get(x));
               
            }

            // Print out data
            try(BufferedWriter out = Files.newBufferedWriter(Paths.get(outbase + "_cngenes_" + fname + ".tab"), Charset.defaultCharset())){
                printTabTextLine(info, out);
                for(String chr : sortedchrs){
                    for(BedAbstract b : genes.get(fname).getSortedBedAbstractList(chr)){
                        GeneBed c = (GeneBed) b;
                        ArrayList<String> vals = c.formatOutArray(cnnames);
                        printTabTextLine(vals, out);
                    }
                }               
                i++;
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    
    public void PrintToFile(String outbase, ArrayList<String> sorteddbs, ArrayList<String> cnnames){
        WritableFont times12pt = new WritableFont(WritableFont.TIMES, 12);
        WritableFont timesheader = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
        WritableCellFormat header = new WritableCellFormat();
        WritableCellFormat regular = new WritableCellFormat();
        
        try{
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outbase));
            
            header.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            header.setFont(timesheader);
            regular.setFont(times12pt);
            
            //Work with CNVRs first, then overlap CNVRs with Genes to get number tallies
            // Returns: # chr start end #animals animals dbs
            ArrayList<BedCoord> orderedList = OrderCNVRS();
            int i = 0;
            WritableSheet cnvrsheet = workbook.createSheet("CNVRs", i);
            String[] cnvhead = {"CNVR#", "chr", "start", "end", "#animals", "animals"};
            for(int x = 0; x < cnvhead.length; x++){
                writeString(cnvrsheet, x, 0, cnvhead[x], header);
            }
            int v = 0;
            for(int x = cnvhead.length; x < cnvhead.length + (sorteddbs.size() * 3); x += 3){
                writeString(cnvrsheet, x, 0, "#" + sorteddbs.get(v), header);
                writeString(cnvrsheet, x + 1, 0, sorteddbs.get(v), header);
                writeString(cnvrsheet, x + 2, 0, "%ovlp-" + sorteddbs.get(v), header);
                v++;
            }
            int cr = 1;
            for(BedCoord b : orderedList){
                ArrayList<String> vals = b.formatOutArrayCNVR(sorteddbs);
                for(int a = 0; a < vals.size(); a++){
                    writeDetermine(cnvrsheet, a, cr, vals.get(a), regular);
                }
                cr += 1;
            }
            i++;
            cnvrsheet.getSettings().setHorizontalFreeze(0);
            
            // Now for the Gene CN tables
            System.out.print("Working on cn tables for CNVR spreadsheet for... ");
            for(String fname : this.genes.keySet()){
                System.out.print(fname + " ");
                String sname = sanitizeString(fname);
                WritableSheet sheet = workbook.createSheet(sname, i);
                Set<String> chrset = genes.get(fname).getListChrs();
                ArrayList<String> sortedchrs = utils.SortByChr.ascendingChr(chrset);
                
                // Create headers
                String[] info = {"gene", "chr", "start", "end", "CNVRoverlap", "CNVRintersections"};
                for(int x = 0; x < info.length; x++){
                    writeString(sheet, x, 0, info[x], header);
                }
                int y = 0;
                for(int x = info.length; x < info.length + (cnnames.size()); x++){
                    writeString(sheet, x, 0, cnnames.get(y), header);
                    y++;
                }
                
                // Print out data
                int row = 1;
                for(String chr : sortedchrs){
                    for(BedAbstract b : genes.get(fname).getSortedBedAbstractList(chr)){
                        GeneBed c = (GeneBed) b;
                        ArrayList<String> vals = c.formatOutArray(cnnames);
                        for(int a = 0; a < vals.size(); a++){
                            writeDetermine(sheet, a, row, vals.get(a), regular);
                        }
                        row += 1;
                    }
                }               
                i++;
                sheet.getSettings().setHorizontalFreeze(0);
                sheet.getSettings().setVerticalFreeze(0);
                
            }
            System.out.println();
            workbook.write();
            workbook.close();
        }catch(WriteException | IOException ex){
            ex.printStackTrace();
        }
    }
    
    /*
     * Private methods
     */
    private void printTabTextLine(ArrayList<String> vals, BufferedWriter write) throws IOException{
        StringBuilder str = new StringBuilder();
        for(int x = 0; x < vals.size() - 1; x++){
            str.append(vals.get(x)).append("\t");
        }
        str.append(vals.get(vals.size() -1)).append(System.lineSeparator());
        write.write(str.toString());
    }
    private String sanitizeString(String str){
        String[] base = str.split("[/\\\\]");
        return base[base.length - 1];
    }
    protected boolean isNumeric(String s){
        if(s.matches(".*;+.*")){
            return false;
        }
        return(s.matches("^-?\\d+(.\\d+)?$"));
    }
    private void writeDetermine(WritableSheet sheet, int col, int row, String s, WritableCellFormat format) throws WriteException{
        if(isNumeric(s)){
            writeNumber(sheet, col, row, Double.valueOf(s), format);
        }else{
            writeString(sheet, col, row, s, format);
        }
    }
    private void writeString(WritableSheet sheet, int col, int row, String s, WritableCellFormat format) throws WriteException{
        Label label = new Label(col, row, s, format);
        sheet.addCell(label);
    }
    private void writeNumber(WritableSheet sheet, int col, int row, Double v, WritableCellFormat format) throws WriteException{
        Number num = new Number(col, row, v, format);
        sheet.addCell(num);
    }
    private ArrayList<BedCoord> OrderCNVRS(){
        ArrayList<BedCoord> orderedcoords = new ArrayList<>();
        int counter = 1;
        for(String chr : utils.SortByChr.ascendingChr(this.CNVR.getListChrs())){
            for(BedAbstract b : this.CNVR.getSortedBedAbstractList(chr)){
                BedCoord c = (BedCoord) b;
                c.setNum(counter);
                
                orderedcoords.add(c);
                //Now that I have the CNVR number, going to see if any genes intersect it
                for(String g : this.genes.keySet()){
                    UpdateGeneCNVR(utils.LineIntersect.returnIntersect(genes.get(g), chr, c.Start(), c.End()), counter, c.Start(), c.End());
                }
                counter++;
            }
        }
        return orderedcoords;
    }
    private void UpdateGeneCNVR(ArrayList<BedAbstract> genes, int counter, int start, int end){
        if(!genes.isEmpty()){
            for(BedAbstract b : genes){
                GeneBed g = (GeneBed) b;
                g.setOverlap(start, end);
                g.setCNVRStr(counter);
            }
        }
    }
}
