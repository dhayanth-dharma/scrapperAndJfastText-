package com.aitenders.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.text.StyleConstants.FontConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.aitenders.enums.AitendersLanguage;
import com.github.jfasttext.JFastText;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

//XML
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FasttextSingletonService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private final StopWordComponent stopwordComponent;
	private  static FasttextSingletonService instance;
	private  static String stopWordDir;
	 private  JFastText jft;
	private FasttextSingletonService(String stopWordDir){
		  jft = new JFastText();
		  stopwordComponent=new StopWordComponent();
		  this.stopWordDir=stopWordDir;
	} 
	public void  setup() {
		long startTime = System.nanoTime();	
      jft.runCmd(new String[] {
                "supervised",
                "-input", "src/test/resources/data/cross-validation/learning_data.txt",
                "-output", "src/test/resources/models/supervised.model",
                "-lr","0.4",
                "-label","__label__",
                "-lrUpdateRate","0.5",
                "-epoch","50",
                "-wordNgrams","5"
                
                
       });
//      
//      "-lr","0.5",
//      "-label","__label__",
//      "-lrUpdateRate","0.8",
//      "-epoch","50",
      long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		System.out.println("Execution time :"+duration);
    }
	public void load() {
		System.out.println("From AS ::"+ stopWordDir);

		 jft.loadModel("src/test/resources/models/supervised.model.bin");
		 System.out.println(stopWordDir);
	}
	
	public void classify2(String text){
	JFastText.ProbLabel probLabel = jft.predictProba(text);
		 System.out.printf("\nThe label of '%s' is '%s' with probability %f\n",
	                text, probLabel.label, Math.exp(probLabel.logProb));
	}
	
	public String[] classify(String text){
		JFastText.ProbLabel probLabel = jft.predictProba(text);
		if(probLabel!=null){
			String[] arr= {probLabel.label, String.valueOf(Math.exp(probLabel.logProb))};
			return arr;
		}
		else return null;
		
	}
	public static FasttextSingletonService getInstance(String stopWordDir){
		if(instance==null)
		{instance =new FasttextSingletonService(stopWordDir);}
		stopWordDir=stopWordDir;
		System.out.println("FROM GET INSTANCE" + stopWordDir);
		return instance;
	}
	
	
	public void readArrf(String filename)  throws IOException{
		LOGGER.trace("Loading data from ARFF file [{}].", filename);
	    FileReader fileReader = new FileReader("src/test/resources/data/cross-validation/"+filename+".arff");
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    ArffReader arffReader = new ArffReader(bufferedReader);
	    Instances data = arffReader.getData();
	    bufferedReader.close();
	    fileReader.close();
	    Attribute labels=data.attribute("textClassifier");
	    String[] label=labels.toString().substring(labels.toString().indexOf("{")+1,labels.toString().indexOf("}")).split(",");
	    String[] dataArr=data.toString().split("[\\r\\n]+");
        File file = new File("src/test/resources/data/cross-validation/learning_data.txt");
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int k=0; k<dataArr.length-1; k++){
	    	if(dataArr[k].lastIndexOf(",")>0){
	    		String labelText=dataArr[k].substring(dataArr[k].lastIndexOf(",") + 1);
		    	String text=dataArr[k].substring(0, dataArr[k].lastIndexOf(","));
		    	//stop word exclude
		    	text=stopwordComponent.tryStopWord(text, AitendersLanguage.ENGLISH, stopWordDir);
		    	text=stopwordComponent.tryStopWord(text, AitendersLanguage.FRENCH, stopWordDir);
		    	//stop word	exclude	 
		    	System.out.println(text);
		    	bw.write("__label__"+labelText+" "+ text+"\n" );
	    	}	    	
	    }    
	    bw.flush();
	    bw.close();
	}
	
	public void read_and_classify(String fileName) throws Exception{
				
		    //reader
			File file=new File("src/test/resources/data/"+fileName+".txt");    //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			String line;  
		      
		    //writter
			File fileResult = new File("src/test/resources/data/"+fileName+"_result_data.txt");
			fileResult.createNewFile();
			FileWriter fw = new FileWriter(fileResult);
	        BufferedWriter bw = new BufferedWriter(fw);
	        long startTime = System.nanoTime();	
	  
	        
			while((line=br.readLine())!=null){  
				String arr[]=classify(line);
				System.out.println("this label is "+arr[0]);
				if(arr!=null){
					bw.write("Label::>> " +arr[0]+"\n"); 
					bw.write("Strength::>> " +arr[1]+"\n"); 
					bw.write("Text::>> " +line+"\n"); 
					bw.write("\n");
					bw.write("\n");
				}
			}
			long endTime = System.nanoTime();

	  		long duration = (endTime - startTime);
	  		System.out.println("Execution time :"+duration);
			bw.flush();
			bw.close();
		    fr.close();    
	}
	
	public void prepare_xml_test_summary(String filename) throws Exception
	{
		LOGGER.trace("Loading data from ARFF file [{}].", filename);
	    FileReader fileReader = new FileReader("src/test/resources/data/cross-validation/"+filename+".arff");
	    BufferedReader bufferedReader = new BufferedReader(fileReader);
	    ArffReader arffReader = new ArffReader(bufferedReader);
	    Instances data = arffReader.getData();
	    bufferedReader.close();
	    fileReader.close();
	    Attribute labels=data.attribute("textClassifier");
	    String[] label=labels.toString().substring(labels.toString().indexOf("{")+1,labels.toString().indexOf("}")).split(",");
	    String[] dataArr=data.toString().split("[\\r\\n]+");
 	    XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Fast Text-Test Results ");
        createHeaderRow(sheet);
        
        for(int k=0; k<dataArr.length-1; k++){
        	Row row = sheet.createRow(k+1);
	    	if(dataArr[k].lastIndexOf(",")>0){
	    		String labelText=dataArr[k].substring(dataArr[k].lastIndexOf(",") + 1);
		    	String text=dataArr[k].substring(0, dataArr[k].lastIndexOf(","));
		    	text=stopwordComponent.tryStopWord(text, AitendersLanguage.FRENCH, stopWordDir);
		    	text=stopwordComponent.tryStopWord(text, AitendersLanguage.ENGLISH, stopWordDir);
		    	String arr[]=classify(text);

		    	if(arr!=null && arr.length>0){
		    		Cell cell1 = row.createCell(1);
			    	 cell1.setCellValue((String) text);//text 
			    	 
			    	 Cell cell2 = row.createCell(2);
			    	 cell2.setCellValue((String) labelText);//actual label
			    	 
			    	 Cell cell3 = row.createCell(3);
			    	 cell3.setCellValue((String) arr[0]);//predicted label
			    	 
			    	 Cell cell4 = row.createCell(4);
			    	 cell4.setCellValue((String) arr[1]);//label strength
			    	 
			    	 
			    	 if(arr[0].contains(labelText)){
			    		 Cell cell5 = row.createCell(5);
			    		 cell5.setCellStyle(getFontStyle(true, sheet));
				    	 cell5.setCellValue((String) "TRUE");//label strength
				    	 Cell cell6 = row.createCell(6);
				    	 cell6.setCellValue((Integer) 1);//label strength
			    	 }else{
			    		 Cell cell5 = row.createCell(5);
			    		 cell5.setCellStyle(getFontStyle(false, sheet));
				    	 cell5.setCellValue((String) "FALSE");//label strength
				    	 Cell cell6 = row.createCell(6);
				    	 cell6.setCellValue((Integer) 0);//label strength
			    	 }
		    	}
	    	}	    	
	    }
        
        try (FileOutputStream outputStream = new FileOutputStream("src/test/resources/data/cross-validation/TestResult.xlsx")) {
          workbook.write(outputStream);
        }
	}	
	
	
	private CellStyle getFontStyle(boolean correct, Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		if(correct){
		    Font font = sheet.getWorkbook().createFont();
		    font.setBoldweight((short)2);
		    font.setFontHeightInPoints((short) 16);
		    font.setColor(Font.COLOR_NORMAL);
		    cellStyle.setFont(font);
		}
		else{
		    Font font = sheet.getWorkbook().createFont();
		    font.setBoldweight((short)2);
		    font.setFontHeightInPoints((short) 16);
		    font.setColor(Font.COLOR_RED);
		    cellStyle.setFont(font);
		}
		return cellStyle;
	}
	
	private void createHeaderRow(Sheet sheet) {
		 
	    CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
	    Font font = sheet.getWorkbook().createFont();
	    font.setBoldweight((short)2);
	    font.setFontHeightInPoints((short) 16);
	    font.setColor(Font.COLOR_NORMAL);
	    cellStyle.setFont(font);
	 
	    Row row = sheet.createRow(0);
	    Cell cellTitle = row.createCell(1);
	    cellTitle.setCellStyle(cellStyle);
	    cellTitle.setCellValue("TEXT");
	 
	    Cell cellAuthor = row.createCell(2);
	    cellAuthor.setCellStyle(cellStyle);
	    cellAuthor.setCellValue("ACTUAL LABEL");
	
	    Cell cellP = row.createCell(3);
	    cellP.setCellStyle(cellStyle);    
	    cellP.setCellValue("PREDICTED LABLE");
	    
	    Cell cellS= row.createCell(4);
	    cellS.setCellStyle(cellStyle);
	    cellS.setCellValue("STRENGTH");
	    
	    Cell cellt = row.createCell(5);
	    cellt.setCellStyle(cellStyle);
	    cellt.setCellValue("CORRECT");
	    
	    Cell cellv = row.createCell(6);
	    cellv.setCellStyle(cellStyle);
	    cellv.setCellValue("VALUE");
	}
	
	/*OPTIONS
	 * The following arguments are mandatory:
  -input              training file path
  -output             output file path

The following arguments are optional:
  -verbose            verbosity level [2]

The following arguments for the dictionary are optional:
  -minCount           minimal number of word occurences [1]
  -minCountLabel      minimal number of label occurences [0]
  -wordNgrams         max length of word ngram [1]
  -bucket             number of buckets [2000000]
  -minn               min length of char ngram [0]
  -maxn               max length of char ngram [0]
  -t                  sampling threshold [0.0001]
  -label              labels prefix [__label__]

The following arguments for training are optional:
  -lr                 learning rate [0.4]
  -lrUpdateRate       change the rate of updates for the learning rate [0]
  -dim                size of word vectors [100]
  -ws                 size of the context window [5]
  -epoch              number of epochs [50]
  -neg                number of negatives sampled [5]
  -loss               loss function {ns, hs, softmax} [softmax]
  -thread             number of threads [12]
  -pretrainedVectors  pretrained word vectors for supervised learning []
  -saveOutput         whether output params should be saved [false]

The following arguments for quantization are optional:
  -cutoff             number of words and ngrams to retain [0]
  -retrain            whether embeddings are finetuned if a cutoff is applied [false]
  -qnorm              whether the norm is quantized separately [false]
  -qout               whether the classifier is quantized [false]
  -dsub               size of each sub-vector [2]
	 * */
}
