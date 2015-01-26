package com.topekalabs.jcsvtool;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This command concatenates csv files together.
 * @author tfarkas
 */
@Parameters(commandDescription="Concatenate multiple csv files")
public final class CommandConcat implements Command
{
    private static final Logger LOG = LoggerFactory.getLogger(JCSVTool.class);
    public static final String COMMAND = "concat";
    
    private static final Map<String, CSVFormat> STRING_TO_FORMAT;
    
    static
    {
        Map<String, CSVFormat> tempStringToFormat = Maps.newHashMap();
        
        tempStringToFormat.put("default", CSVFormat.DEFAULT);
        tempStringToFormat.put("excel", CSVFormat.EXCEL);
        tempStringToFormat.put("mysql", CSVFormat.MYSQL);
        tempStringToFormat.put("rfc4180", CSVFormat.RFC4180);
        tempStringToFormat.put("tdf", CSVFormat.TDF);
        
        STRING_TO_FORMAT = Collections.unmodifiableMap(tempStringToFormat);
    }
    
    public CommandConcat()
    {
    }
    
    @Parameter(names="-ihh",
               description="This flag indicates that input csv files have headers.")
    public Boolean ihh = false;
    
    @Parameter(names="-ohh",
               description="This flag indicates that output csv files have headers.")
    public Boolean ohh = false;
    
    @Parameter(names="-id",
               description="This is the input delimeter character. " +
                           "The default value is")
    public Character id = null;
    
    @Parameter(names="-od",
               description="This is the input delimeter character.")
    public Character od = null;
    
    @Parameter(names="-iq",
               description="This is the input quote character.")
    public Character iq = null;
    
    @Parameter(names="-oq",
               description="This is the output quote character.")
    public Character oq = null;
    
    @Parameter(names="-ir",
               description="This is the input record separator string.")
    public String ir = null;
    
    @Parameter(names="-or",
              description="This is the output record separator string.")
    public String or = null;
    
    @Parameter(names="-itype",
               description="This describes the type of the input csv files." +
                           "The standard values are:\n" +
                           "\tdefault\n" +
                           "\texcel\n" +
                           "\tmysql\n" +
                           "\trfc4180\n" +
                           "\ttdf\n")
    public String itype = null;
    
    @Parameter(names="-otype",
               description="This describes the type of the input csv files." +
                           "The standard values are:\n" +
                           "\tdefault\n" +
                           "\texcel\n" +
                           "\tmysql\n" +
                           "\trfc4180\n" +
                           "\ttdf\n")
    public String otype = null;
    
    @Parameter(description="The list of csv files to concat",
               required = true)
    public List<String> filePaths;
    
    @Parameter(names="-out",
               description="The concatenated output csv file.",
               required = true)
    public String outputFilePathString = null;
    
    //
    
    private CSVFormat inputType;
    private CSVFormat outputType;
    
    public void validate()
    {   
        if(ohh && !ihh)
        {
            JCSVTool.THROWER.throwRecoverableError("NO_INPUT_HEADERS");
        }
        
        if(filePaths.isEmpty())
        {
            JCSVTool.THROWER.throwRecoverableError("NO_INPUT_FILES");
        }
        
        if(filePaths.size() == 1)
        {
            JCSVTool.THROWER.throwRecoverableError("SINGLE_INPUT_FILE");
        }
        
        // Validate input
        
        inputType = null;
        
        if(itype != null)
        {
            inputType = STRING_TO_FORMAT.get(itype.toLowerCase());
            
            if(inputType == null)
            {
                JCSVTool.THROWER.throwRecoverableError("INVALID_CSV_TYPE",
                                                       new Object[] {itype});
            }
        }
        
        if(inputType == null)
        {
            if(id == null)
            {
                id = CSVFormat.DEFAULT.getDelimiter();
            }
            
            if(iq == null)
            {
                iq = CSVFormat.DEFAULT.getQuoteCharacter();
            }
            
            if(ir == null)
            {
                ir = CSVFormat.DEFAULT.getRecordSeparator();
            }
            
            inputType = CSVFormat.newFormat(id);
            inputType.withQuote(iq);
            inputType.withRecordSeparator(ir);
        }
        else
        {
            if(id != null)
            {
                JCSVTool.THROWER.throwRecoverableError("INPUT_FORMAT_AND_DELIMETER");
            }
            
            if(iq != null)
            {
                JCSVTool.THROWER.throwRecoverableError("INPUT_FORMAT_AND_QUOTE");
            }
            
            if(ir != null)
            {
                JCSVTool.THROWER.throwRecoverableError("INPUT_FORMAT_AND_RECORD_SEPERATOR");
            }
        }
        
        // Validate output
        
        outputType = null;
        
        if(otype != null)
        {
            outputType = STRING_TO_FORMAT.get(itype.toLowerCase());
            
            if(outputType == null)
            {
                JCSVTool.THROWER.throwRecoverableError("INVALID_CSV_TYPE",
                                                       new Object[] {otype});
            }
        }
        
        if(outputType == null)
        {
            if(od == null)
            {
                od = CSVFormat.DEFAULT.getDelimiter();
            }
            
            if(oq == null)
            {
                oq = CSVFormat.DEFAULT.getQuoteCharacter();
            }
            
            if(or == null)
            {
                or = CSVFormat.DEFAULT.getRecordSeparator();
            }
            
            outputType = CSVFormat.newFormat(od);
            outputType.withQuote(od);
            outputType.withRecordSeparator(or);
        }
        else
        {
            if(id != null)
            {
                JCSVTool.THROWER.throwRecoverableError("OUTPUT_FORMAT_AND_DELIMETER");
            }
            
            if(iq != null)
            {
                JCSVTool.THROWER.throwRecoverableError("OUTPUT_FORMAT_AND_QUOTE");
            }
            
            if(ir != null)
            {
                JCSVTool.THROWER.throwRecoverableError("OUTPUT_FORMAT_AND_RECORD_SEPERATOR");
            }
        }
    }
    
    @Override
    public void execute()
    {
        validate();
        
        File outputFile = createFile(outputFilePathString);
        BufferedWriter outputWriter = null;
        
        try
        {
            outputWriter = new BufferedWriter(new FileWriter(outputFile));
        }
        catch(IOException ex)
        {
            JCSVTool.THROWER.throwRecoverableError("CONCAT_OUTPUT_FAIL",
                                                   ex,
                                                   new Object[] {outputFile.getAbsolutePath()});
        }
        
        CSVPrinter csvPrinter = null;
                
        try
        {
            csvPrinter = new CSVPrinter(outputWriter, outputType);
        }
        catch(IOException ex)
        {
            JCSVTool.THROWER.throwRecoverableError("CONCAT_OUTPUT_FAIL",
                                                   ex,
                                                   new Object[] {outputFile.getAbsolutePath()});
        }
        
        ////

        List<String> headerNames = validateHeaders();
        
        try
        {
            csvPrinter.printRecord(headerNames);
        }
        catch(IOException ex)
        {
            JCSVTool.THROWER.throwRecoverableError("CONCAT_OUTPUT_HEADER_FAIL",
                                                   ex,
                                                   new Object[] {outputFile.getAbsolutePath()});
        }
        
        ////
        
        String[] values = new String[headerNames.size()];
        
        for(String filePathString: filePaths)
        {
            File file = createFile(filePathString);
            CSVParser csvParser = null;
            
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                csvParser = new CSVParser(bufferedReader, inputType);
                
                for(CSVRecord csvRecord: csvParser)
                {
                    for(int index = 0;
                        index < headerNames.size();
                        index++)
                    {
                        values[index] = csvRecord.get(index);
                    }
                    
                    csvPrinter.printRecord(values);
                }

                try
                {
                    csvParser.close();
                }
                catch(IOException ex)
                {
                    JCSVTool.THROWER.throwRecoverableError("FAILED_TO_CLOSE_INPUT",
                                                           ex,
                                                           new Object[] {file.getAbsoluteFile()});
                }
            }
            catch(IOException ex)
            {
                JCSVTool.THROWER.throwRecoverableError("INPUT_READ_ERROR",
                                                       ex,
                                                       new Object[] {file.getAbsoluteFile()});
            }
        }
        
        try
        {
            csvPrinter.close();
        }
        catch(IOException ex)
        {
            JCSVTool.THROWER.throwRecoverableError("FAILED_TO_CLOSE_OUTPUT",
                                                   ex,
                                                   new Object[] {outputFile.getAbsoluteFile()});
        }
    }
    
    private File createFile(String filePathString)
    {
        Path filePath = Paths.get(filePathString);
            
        if(!filePath.isAbsolute())
        {
            filePath = Paths.get(System.getProperty("user.dir") + File.separator +
                                 filePath);
        }
            
        return filePath.toFile();        
    }
    
    private List<String> validateHeaders()
    {
        if(!ihh)
        {
            return null;
        }
        
        Map<String, Integer> headerMap = null;
        
        for(String filePathString: filePaths)
        {
            File file = createFile(filePathString);
            CSVParser csvParser = null;
            
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                csvParser = new CSVParser(bufferedReader, inputType);
            }
            catch(IOException ex)
            {
                JCSVTool.THROWER.throwRecoverableError("INPUT_READ_ERROR",
                                                       ex,
                                                       new Object[] {file.getAbsoluteFile()});
            }
            
            Map<String, Integer> tempHeaderMap = csvParser.getHeaderMap();
            
            if(headerMap == null)
            {
                headerMap = tempHeaderMap;
            }
            else
            {
                if(!headerMap.equals(tempHeaderMap))
                {
                    JCSVTool.THROWER.throwRecoverableError("CONCAT_INPUT_HEADERS_NO_MATCH");
                }
            }
            
            try
            {
                csvParser.close();
            }
            catch(IOException ex)
            {
                JCSVTool.THROWER.throwRecoverableError("FAILED_TO_CLOSE_INPUT",
                                                       ex,
                                                       new Object[] {file.getAbsoluteFile()});
            }
        }
        
        ////
        
        String[] headerNames = new String[headerMap.size()];
        
        for(Map.Entry<String, Integer> headerEntry: headerMap.entrySet())
        {
            headerNames[headerEntry.getValue()] = headerEntry.getKey();
        }
        
        return Lists.newArrayList(headerNames);
    }
}
