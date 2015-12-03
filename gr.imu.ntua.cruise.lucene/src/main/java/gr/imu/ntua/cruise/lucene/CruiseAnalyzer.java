package gr.imu.ntua.cruise.lucene;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;



/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/07/13
 * Time: 10:59 AM
 */
public class CruiseAnalyzer extends Analyzer{

    private Logger logger = LoggerFactory.getLogger(CruiseAnalyzer.class);


    @Override
    protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_41, reader);

        LowerCaseFilter filter = new LowerCaseFilter(Version.LUCENE_41, source);

        StandardFilter filter1 = new StandardFilter(Version.LUCENE_41, filter);
        
      


    /* SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_41);			
		ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(simpleAnalyzer, 2, 0x2);
 
		TokenStream stream;
    
            stream = shingleAnalyzer.tokenStream("contents", reader);
    
		CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class);
*/

        List<String> strings = null;
        try{
            strings = IOUtils.readLines(
                    new InputStreamReader(
                            this.getClass().getResourceAsStream("/stopwords.txt"),
                            "UTF-8"));
        }catch(Exception e){
            logger.warn("Couldn't read the stop words {}",e);
        }

        StopFilter filter2 = new StopFilter(Version.LUCENE_41, filter1, new CharArraySet(Version.LUCENE_41,strings, true));

        CustomFilter filter3 = new CustomFilter(filter2);

        return new Analyzer.TokenStreamComponents(source, filter3);
    }

    public class CustomFilter extends TokenFilter {
        /**
         * Construct filtering <i>in</i>.
         */

        private CharTermAttribute charTermAttr;

        public CustomFilter(TokenStream in) {
            super(in);
            this.charTermAttr = addAttribute(CharTermAttribute.class);
        }

        
        public final boolean findrepeatingCharacters(char[] buffer) throws java.io.IOException {
            ArrayList list = new ArrayList();
            for (int i=0; i< buffer.length-1;i++)
            {
                list.add(buffer[i]);
                if (i>2 && list.get(i)== list.get(i-1) && list.get(i)== list.get(i-2))
                    // if ( i>1 && buffer[i-1].equals(buffer[i]) && buffer[i-2].equals(buffer[i]) )
                    return true;
            }

            return false;
        }
        
        public final boolean findrepeatedSyllables(char[] buffer) throws java.io.IOException {
            ArrayList list = new ArrayList();
            for (int i=0; i< buffer.length;i++)
            {
                list.add(buffer[i]);
                if (i>3 && list.get(i-3)== list.get(i-1) && list.get(i-2)== list.get(i))
                    // if ( i>1 && buffer[i-1].equals(buffer[i]) && buffer[i-2].equals(buffer[i]) )
                    return true;
            }

            return false;
        }
        
        public final boolean IsCharacter(char[] buffer) throws java.io.IOException {
            ArrayList list = new ArrayList();
            for (int i=0; i< buffer.length;i++)
            {
                if (!Character.isLetter(new String(buffer).charAt(i)))
                    return false;
            }

            return true;
        }
                
        
        public final boolean countRepeatedCharector(char[] buffer) { 
            
            int count = 0; 
            String pw = String.valueOf(buffer);
            
            for (int i = 0; i < buffer.length; i++) { 
                String c; 
                c = StringUtils.substring(pw, i, i + 1);
                int tmpCount = StringUtils.countMatches(pw, c); 
                if (tmpCount > count) { 
                    count = tmpCount; } 
            } 
            if (count>1)
                return true;
            else 
                return false;
        }
        
          public final boolean mainFilter(char[] newBuffer, int length) throws IOException {
              
              
              if (newBuffer[0] == '@'){
                  charTermAttr.setEmpty();           

                  return true;
              }


              else if ((new String(newBuffer)).startsWith("http")){
                return false;
            }
            
            else if (!Character.isLetter((new String(newBuffer)).charAt(length-1))){
                String str = new String(newBuffer);
              
                str = str.replaceAll("[^\\dA-Za-z#]", "");                               
                 charTermAttr.setEmpty();
                 charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                
                 if (str.toCharArray().length<3){
                    return false;
                 } 
                 else{
                     
                 return true;
                 }
            }   
            
             else if (!Character.isLetter((new String(newBuffer)).charAt(0))){
                String str = new String(newBuffer);              
                str = str.replaceAll("[^\\dA-Za-z#]", "");                               
                 charTermAttr.setEmpty();
                 charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                
                 if (str.toCharArray().length<3){
                       charTermAttr.setEmpty();           

                        return true;
                 } 
                 else{                     
                 return true;
                 }
            }    

            
            else if ((new String(newBuffer)).length() < 3){                
                   charTermAttr.setEmpty();           

                    return true;
            }
           

            else if ( ( (newBuffer.length)>1 && newBuffer[0] == '"' && newBuffer[1] == '@') ){
                String str = new String(newBuffer);
                 str = "";                               
                 charTermAttr.setEmpty();
                 charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                
                return true;
            }

            else if (findrepeatingCharacters(newBuffer))
            {  
              //  String str = new String(newBuffer);
                // str = "";                               
                 charTermAttr.setEmpty();
                 //charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                
                return true;
            }            
          /*  else if (findrepeatedSyllables(newBuffer))
                return false;*/
            
            else if (new String(newBuffer).contains(":")){
                return true;
            }
            
            else if (new String(newBuffer).contains("-")){
                return true;
            }
               
            return true;
              
          } 

        /**
         *
         * <p>Removes zeroes if first char in token
         */

        public final boolean incrementToken() throws java.io.IOException {

         
            if (!input.incrementToken()) {
                return false;
            }

            int length = charTermAttr.length();
            char[] buffer = charTermAttr.buffer();
            char[] newBuffer = new char[length];
            for (int i = 0; i < length; i++) {
                newBuffer[i] = buffer[i];
            }

            logger.trace("boolean incrementToken([]) Length {} ",(new String(newBuffer)).length());
           // System.out.println("Primitive Term=  "+new String(newBuffer));

            if (newBuffer[0] == '@'){
                
                 charTermAttr.setEmpty();
                               
                return true;
            }
            
            else if ( ( (newBuffer.length)>1 && newBuffer[0] == '"' && newBuffer[1] == '@') ){
                                           
                 charTermAttr.setEmpty();
                               
                return true;
            }
            else if ( ( (newBuffer.length)>1 && newBuffer[0] == '.' && newBuffer[1] == '@') ){
                                           
                 charTermAttr.setEmpty();
              
                return true;
            }


            else if ((new String(newBuffer)).startsWith("http")){
                                            
                 charTermAttr.setEmpty();
                              
                return true;
            }
            
            else if (!Character.isLetter((new String(newBuffer)).charAt(length-1))){
                String str = new String(newBuffer);              
                str = str.replaceAll("[^\\dA-Za-z#]", "");                               
                 charTermAttr.setEmpty();
                 charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                
                 if (str.toCharArray().length<3){
                    charTermAttr.setEmpty();
                     return true;
                 } 
                 else{
                     
                 return true;
                 }
            }   
            
             else if (!Character.isLetter((new String(newBuffer)).charAt(0))){
                String str = new String(newBuffer);
              
                str = str.replaceAll("[^\\dA-Za-z#]", "");                               
                 charTermAttr.setEmpty();
                 charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                
                 if (str.toCharArray().length<3){
                  
                                            
                    charTermAttr.setEmpty();           

                    return true;
                 } 
                 else{                     
                    return true;
                 }
            }    

            
            else if ((new String(newBuffer)).length() < 3){                
                                         
                charTermAttr.setEmpty();     
                
                return true;
            }
        
                        

            else if (findrepeatingCharacters(newBuffer)){
                                   
                 charTermAttr.setEmpty();
       
                return true;
            }
          /*  else if (findrepeatedSyllables(newBuffer))
                return false;*/
           // if(mainFilter(newBuffer,length)){
            
             else   if (new String(newBuffer).contains(":")){
                   return true;
               }

               else if (new String(newBuffer).contains("-")){
                   return true;
               }

               else{
                   String str = new String(newBuffer);
                   str = str.replaceAll("[^\\dA-Za-z#]", "");                
                   charTermAttr.setEmpty();
                   int k=str.toCharArray().length;
                   charTermAttr.copyBuffer(str.toCharArray(), 0, str.toCharArray().length);
                  // if (!mainFilter(charTermAttr.buffer(),k))
                    //   return false;
               /*    if ( str.length()<4 && (!IsCharacter(charTermAttr.buffer())))
                   {
                       System.out.println("I am not character="+str);
                       return false;
                   }*/
                      /*  else if ( (str.length()>3) && (findrepeatedSyllables(charTermAttr.buffer())))
                            return false;*/
              /*     else if ( str.equals("#RT")||str.equals("#rt")||str.equals("omg") || str.equals("wtf") || str.equals("lmfao") || str.equals("fuck")|| str.equals("you") ||  str.equals("but") || str.equals("and"))
                   { 
                       return false;
                   }
                   else */
                            return true;
                }
                
            }
           // else 
             //   return false;


        //}

        @Override
        public void reset() throws IOException {
            super.reset();
        }

    }
}
