package com.topekalabs.jcsvtool;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Maps;
import com.topekalabs.error.utils.ErrorThrower;
import com.topekalabs.error.utils.ErrorThrowerFactory;
import com.topekalabs.error.utils.RecoverableError;
import com.topekalabs.java.utils.ObjectUtils;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main class which executes commands.
 * @author tfarkas
 */
public final class JCSVTool
{
    private static final Logger logger = LoggerFactory.getLogger(JCSVTool.class.getName());
    public static final String ERROR_NAMESPACE = "com.topekalabs.jcsvtool";
    public static ErrorThrower THROWER = ErrorThrowerFactory.createThrower(ERROR_NAMESPACE);
    
    
    private JCSVTool()
    {
    }
    
    public static void main(String[] args)
    {
        JCommander jCommander = new JCommander();
        Map<String, Command> commandMap = Maps.newHashMap();
        
        // Commands
        
        CommandConcat commandConcat = new CommandConcat();
        
        // Add Them
        
        jCommander.addCommand(CommandConcat.COMMAND, commandConcat);
        commandMap.put(CommandConcat.COMMAND, commandConcat);
        
        jCommander.parse(args);
        Command command = commandMap.get(jCommander.getParsedCommand());
        
        try
        {
            command.execute();
        }
        catch(RecoverableError error)
        {
            logger.error(error.getError().getMessage(), error.getError().getErrorData());
            
            if(!ObjectUtils.isNull(error.getCause()))
            {
                logger.debug("Exception:", error.getCause());
            }
        }
    }
}
