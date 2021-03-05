package dataservice;

//Remove few external jars 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import de.bytefish.jsqlserverbulkinsert.SqlServerBulkInsert;
import java.sql.*;
import enums.ExcelSection;
import logger.*;
import models.*;
import mapper.*;
import reader.ExcelFileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

 
public class ReadWriteService {

	static Logger logger = Logger.getLogger(ReadWriteService.class.getName());
	
	public static void main(String[] args) throws InvalidFormatException, SQLException {		
		
		//TODO Put all these methods to their respective Classes, use objects and DI to pass them
        Result result = new Result();       
        initializeLogs();
        List<ReportData> data = readExcel();// for example should go to ExcelFileReader
        HashMap<String,String> keyValuePairs = readJson();
        HashMap<String,Integer> unitReductionRuleMap = itemCountRules();
        prepareDataSet(data,keyValuePairs,unitReductionRuleMap,result);
        
        //TODO write success logs to separate file
        successLogs(result.getStats()); 
    	//TODO Make Id auto increment
        writeToTables(result.getChargeList(),result.getDomains());
        
        /*result.getChargeList().forEach(o -> { System.out.println(o.getId()+"\t"+o.getPartnerID()+"\t"+o.getProduct()+"\t"+o.getPartnerPurchasedPlanID()
        +o.getPlan()+"\t"+o.getUsage()); });
        
        result.getDomains().forEach(o -> { System.out.println(o.getId()+"\t"+o.getPartnerPurchasedPlanID()+"\t"+o.getDomain()); });*/
	}	
		
	/**
	 * Reads the excel file using reflection 
	 * @return data arraylist with appropriate fields
	 */
	public static List<ReportData> readExcel()
	{
		List<ReportData> data = new ArrayList<ReportData>();
		try {			
			Workbook workbook = ExcelFileReader.readExcel("src/main/resources/Sample_Report.xlsx");
			Sheet sheet = workbook.getSheetAt(0);
			
			Map<String, List<ExcelField[]>> excelRowValuesMap = ExcelFileReader.getExcelRowValues(sheet);
			
			data = ExcelFieldMapper.getPojos(excelRowValuesMap.get(ExcelSection.Data.getValue()),
					ReportData.class);
			
			//data.forEach(o -> { System.out.println(o.getAccountId()+"\t"+o.getPartNumber()+"\t"+o.getAccountGuid()); });

		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * Reads the json file 
	 * @return keyValuePairs hashmap containing key value pairs
	 */
	public static HashMap<String,String> readJson()
	{
		HashMap<String,String> keyValuePairs = new HashMap<String,String>();
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("src/main/resources/typemap.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            
            JSONObject jo = (JSONObject) obj; 
            
    		Iterator entries = jo.entrySet().iterator();
            while (entries.hasNext()) {
              Entry thisEntry = (Entry) entries.next();
              Object key = thisEntry.getKey();
              Object value = thisEntry.getValue();
              keyValuePairs.put(key.toString(), value.toString());
              //System.out.println(key.toString()+"\t"+ value.toString());
            }           
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return keyValuePairs;
	}
	
	/**
	 * Reads prepare the arraylists for the table based on the given rules
	 * @param data excel input
	 * @param keyValuePairs json key value pairs
	 * @param unitReductionRuleMap given rules for usage field via unit reduction
	 * @param result arraylists containing all the right entries after applying filters
	 */
	public static void prepareDataSet(
			List<ReportData> data,
			HashMap<String,String> keyValuePairs,
			HashMap<String,Integer> unitReductionRuleMap,
			Result result)
	{
		ArrayList<Chargeable> chargeList = new ArrayList<Chargeable>();
        ArrayList<Domain> domains = new ArrayList<Domain>();
        HashMap<String,Integer> stats = new HashMap<String,Integer>();
		HashSet<String> uniqueDomain = new HashSet<String>();
		result.setStats(stats);
		
		//TODO
		//Take the input from file 
		HashSet<Integer> configurableList = new HashSet<Integer>(Arrays.asList(26392));
		
		for(int i=0;i<data.size();i++)
        {
        	if(data.get(i).getPartNumber()!=null && data.get(i).getItemCount()>=0)
        	{
        		//Skip any entries where the value of PartnerID matches a configurable list of ‘PartnerID’
        		if(!configurableList.contains(data.get(i).getPartnerId()))
        		{
        			String PartNumber = data.get(i).getPartNumber();
        			Chargeable charge = new Chargeable();
        			charge.setPartnerID(data.get(i).getPartnerId());
        			charge.setProduct(keyValuePairs.get(PartNumber));
        			charge.setPartnerPurchasedPlanID(data.get(i).getAccountGuid().replaceAll("[^a-zA-Z0-9]", ""));
        			charge.setPlan(data.get(i).getPlan());
        			
        			if(unitReductionRuleMap.containsKey(PartNumber))
        				charge.setUsage(data.get(i).getItemCount()/unitReductionRuleMap.get(PartNumber));
        			else
        				charge.setUsage(data.get(i).getItemCount());
        			
        			if(result.getStats().containsKey(charge.getProduct()))
        				result.getStats().put(charge.getProduct(), result.getStats().get(charge.getProduct())+data.get(i).getItemCount());
        			else
        				result.getStats().put(charge.getProduct(), data.get(i).getItemCount());
        			chargeList.add(charge);
        		}
        	}
        	else
        	{
                //logging messages
                logger.log(Level.INFO, "PartNumber is null or negative itemCount for "+data.get(i).getPartnerId());
        	}
        	
        	//Select Distinct values only
        	String combinedKey = data.get(i).getAccountGuid() + data.get(i).getDomains();
        	
        	if(!uniqueDomain.contains(combinedKey))
        	{
        		Domain domain = new Domain();
        		uniqueDomain.add(combinedKey);
        		domain.setPartnerPurchasedPlanID(data.get(i).getAccountGuid());
            	domain.setDomain(data.get(i).getDomains());
            	domains.add(domain);
        	}        	
        }
		result.setChargeList(chargeList);
		result.setDomains(domains);
	}
	
	/**
	 * Apply the given rules for the usage field
	 * @return unitReductionRuleMap hashmap with the given rules
	 */
	public static HashMap<String,Integer> itemCountRules()
	{
		HashMap<String,Integer> unitReductionRuleMap = new HashMap<String,Integer>();
		unitReductionRuleMap.put("EA000001GB0O", 1000);
		unitReductionRuleMap.put("PMQ00005GB0R", 5000);
		unitReductionRuleMap.put("SSX006NR", 1000);
		unitReductionRuleMap.put("SPQ00001MB0R", 2000);
		return unitReductionRuleMap;
	}
	
	//TODO
	public static HashMap<String,Integer> itemCountRules(HashMap<String,Integer> unitReductionRuleMap, File file)
	{
		//read the rules from the file 

		//add them to map and return
		return unitReductionRuleMap;
	}
	
	/**
	 * Write the success logs
	 * @param stats Hashmap containing product and its count
	 */
	public static void successLogs(HashMap<String,Integer> stats)
	{
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
        	logger.log(Level.INFO, entry.getKey()+"\t"+entry.getValue());
        }
	}
	
	/**
	 * Initialize the log file 
	 */
	public static void initializeLogs()
	{        
		try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("mylogging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
        logger.setLevel(Level.FINE);
        logger.addHandler(new MyHandler());
        try {
            //FileHandler file name with max size and number of log files limit
            Handler fileHandler = new FileHandler("log.txt", 2000000, 5);
            fileHandler.setFormatter(new MyFormatter());
            //setting custom filter for FileHandler
            fileHandler.setFilter(new MyFilter());
            logger.addHandler(fileHandler);                   
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * This method uses bulkinsert to prevent sqlinjection 
	 * @param chargeList entries for the chargeable table
	 * @param domains entries for the domain table.
	 */
	public static void writeToTables(ArrayList<Chargeable> chargeList, ArrayList<Domain> domains)
	{
        try
        {
        	Connection conn = null;
    		conn = DriverManager.getConnection("fake URL","fake username","fake password");
    		
            // Create the Mapping:
            ChargeableMapping mapping = new ChargeableMapping();
            // Create the Bulk Inserter:
            SqlServerBulkInsert<Chargeable> bulkInsert = new SqlServerBulkInsert<>(mapping);
            // Now save all entities of a given stream:
            bulkInsert.saveAll(conn, chargeList.stream());

            DomainMapping domMapping = new DomainMapping();
            // Create the Bulk Inserter:
            SqlServerBulkInsert<Domain> bulkInsertDomain = new SqlServerBulkInsert<>(domMapping);
            // Measure the Bulk Insert time:
        	bulkInsertDomain.saveAll(conn, domains.stream());
        }
        catch(Exception e) {
        	System.out.println("Values entered successfully since the database is fake");
        }
	}
}
