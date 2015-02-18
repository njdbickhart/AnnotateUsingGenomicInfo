AnnotateUsingGenomicInfo
========================

An annotation program I've written that can compare several data file types to UCSC data tracks.

### Getting Started
First, please download the latest JDK for Java version 1.8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

Then, click on the "release" link at the top of this repository page, or navigate to: https://github.com/njdbickhart/AnnotateUsingGenomicInfo/releases

You can download the latest AnnotateUsingGenomicInfo jar files from that link. In order to run AnnotateUsingGenomicInfo, you will have to invoke it with the java 1.8 executable. Here is an example of how to run the program:

> /path/to/jdk1.8.0/bin/java -jar /path/to/AnnotateUsingGenomicInfo.jar

Running the program without any arguments will list the program help menu.

### Database file creation
In order to use this program, you will need to create a "database" of genomic element locations within your reference genome. These can be regions that you have identified yourself, or from public databases. I prefer to use the annotation tracks from the UCSC genome browser (http://hgdownload.soe.ucsc.edu/downloads.html ). 

Here are the steps to process data from UCSC:
  1. For your reference genome of choice, click the "Annotation database" link, and it will take you to a FTP site with a list of text files. 
  2. Select a compressed text file (ie. "*.txt.gz") to download it, and uncompress it with gunzip on the command line.
  3. You'll need to convert the text file into BED file format (http://genome.ucsc.edu/FAQ/FAQformat.html#format1 ), using the first four columns of the bed specification on the UCSC page (ie. "chr", "start", "end", "name"). 

Regardless of how you generate or create the BED files, you will need to pass the locations and names of these files to the program so that it can annotate your variant calls. In order to do this, you will need to create a text file (hereafter called a "DBList" that lists the locations of all of these BED files on your current server/hard disk. Here is the standard format for the DBList file:

> /path/to/bed/file/1/file1.bed(tab)name_of_database

> /path/to/bed/file/2/file2.bed(tab)second_name

> ... (repeat for all other database BED files)

The first column of the DBList file lists the location of the file on the server. The second column (delimited by a TAB) is a name that you are calling this database (eg. "RefseqGenes", "PreviousResults", or "CpG Islands"). It is possible for the DBList to only have one line (and therefore, only one database), but there must be at least one BED file database for AnnotateUsingGenomicInfo to run. I would recommend that you include as many database BED files as you can so that the intersection output has more information for each variant call.

### Variant file input
AnnotateUsingGenomicInfo was designed to be flexible in terms of input variant file formats. Currently, VCF files are not supported but I hope to include that functionality soon. In order to see which file formats are supported, please invoke the help menu on the command line by running the program without any arguments. 

For convenience, the program can take an individual variant file in the (-i) argument, or a list of files in the same format as the DBList file. In order to pass a list of files for simultaneous intersection, please follow this example:

Within the text file:
> /path/to/variant/file/1/variants.bed(tab)name_of_sample1

> /path/to/variant/file/2/variants.bed(tab)name_of_sample2

> ... (repeat for all other samples to be intersected)

### Usage
The program has three required arguments and two optional arguments. Here is a brief listing of the arguments:

> -d (file name) (REQUIRED) This is the DBList file that contains the locations of the BED file databases that you would like to intersect with your variant calls.

> -i (file name) (REQUIRED) The input to the program can be a single file, or a list of files (see the above section for details).

> -o (file prefix) (REQUIRED) The output file prefix. This string will be appended to all output excel/text files from this program


> -c (file name) (OPTIONAL) If you would like to intersect genomic windows with values (ie. copy number estimates) to the database files, you can input the files from each sample according to the example for the DBList file above.

> -t (nothing) (OPTIONAL) This is a flag that tells the program to print all output to text instead of to an excel spreadsheet. The default behavior is to create excel spreadsheets with all of the information, whereas the text files generated with this flag will be more useful in a UNIX environment.

### Output

# Concepts
AnnotateUsingGenomicInfo will attempt to merge all of your variants, across all samples, into cumulative variant locations called "Regions." If your variants overlap by one base, they will be merged together and the program will calculate how many samples shared this Region, and if the Region intersected with any databases input using the "-d" option.

There are two types of output for this program, and the user can select between them by using the "-t" flag on the command line. The **default** mode is to print out one **excel spreadsheet.**

# Excel (DEFAULT)
AnnotateUsingGenomicInfo will produce one excel spreadsheet with the following file name:

> (output file prefix)_anno.xls

This spreadsheet will contain multiple sheets (following this scheme: (# of samples in the -i input) + 1 (the "Regions" sheet)). The first sheet is always the "Regions" sheet, where all variants across all samples are merged into overlapping coordinates. The second sheet to the last sheet will be the listing of all of the variants identified in each sample, intersected with the databases provided in the "-d" option. The last sheets will also have a "Region" number to allow the user to refer back to the first "Regions" tab.

If you use the "-c" option, you will receive another excel spreadsheet with the following file name:

> (output file prefix)_windows.xls

This spreadsheet will have a number of sheets that corresponds to each DBList entry, and will show the average values from the windows for each sample at each DBList coordinate in a matrix.

# Text (must invoke using "-t"; overrides excel file creation)
AnnotatedUsingGenomicInfo will create tab delimited text files instead of excel spreadsheets. Instead of combining all files into one larger spreadsheet, the user will get a separate text file for each "sheet" in the previous section.

Here are the file formats that you will receive:

> (output file prefix).(input sample name).anno     These are the original variants that you entered, but with their notable intersections with the DBList files

> (output file prefix).regions.tab     This is the "Regions" file showing a merged list of variants with intersections with the DBList files

If you use the "-c" option, you will receive more tab delimited files corresponding to each DBList entry:

> (output file prefix)_windows_(DBList file name).tab