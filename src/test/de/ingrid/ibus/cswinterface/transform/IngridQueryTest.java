/*
 * Created on 04.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;

import java.io.StringReader;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

import junit.framework.TestCase;



/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IngridQueryTest extends TestCase {
    
    
    public void testIngridQuery() throws Exception {
        
        IngridQuery query = null;
        
//        query = new IngridQuery();
//        
//        query.addTerm(new TermQuery(0, "weidenbach"));
//        
//        query.addTerm(new TermQuery(0, "karte"));
        
        //query = parse("ort:weidenbach OR karte");
        
        //query = parse("abstract:arte* OR title:Test");
        
        //query = parse("abstract:arte OR title:Test");
       
        //query = parse("ort:Halle OR land:germany");
       
        //query = parse("fische ort:halle NOT (saale OR Hufeisensee)");
        
         query = parse("ort:Halle AND t0:1990");
         
        //TODO should 'AND NOT'  be possible?
        //query = parse("ort:Halle AND NOT t0:1990");
        
         //query = parse("ort:Hal* ort:Darmstadt");
        
        //query = parse("fische bla bo nkl hjkjk izi ort:Halle land:BRD (zeit:1970 OR aktuell:monatlich) ");
        
        // query = parse("(zeit:1970 OR aktuell:täglich)  fische OR bla bo nkl hjkjk izi ort:Halle land:BRD ");
        
        //TODO why not an error??
         //query = parse("west:[7]");
        

         //query = parse("(FIELD1:10 OR FIELD1:20) AND (STATUS:VALID)");
        
      
//        query = new IngridQuery();
//        
//         query.addTerm(new TermQuery(IngridQuery.AND, "fische"));
//
//         //query.addTerm(new TermQuery(IngridQuery.AND, "seegurke"));
//         
//         query.addField(new FieldQuery(IngridQuery.AND, "ort", "halle"));
//         
//         //query.addField(new FieldQuery(IngridQuery.AND, "land:BRD"));
//         
//         
//         ClauseQuery clauseQuery = new ClauseQuery(IngridQuery.NOT);
//         
//         //query.addClause(clauseQuery);
//         
//         clauseQuery.addTerm(new TermQuery(IngridQuery.OR, "saale"));
//         
//         clauseQuery.addTerm(new TermQuery(IngridQuery.OR, "hufeisensee"));
//         
//         //clauseQuery.addField(new FieldQuery(IngridQuery.AND, "ort:FFM"));
//         
//         query.addClause(clauseQuery);
//         
       
        
//        String strDesc = query.getDescription();
//        
//        System.out.println("query strDesc: " + strDesc);
        
        
        IngridQueryToLuceneQuery ingridQueryToLuceneQuery = new IngridQueryToLuceneQuery();
        
        //String strExp = query.toLogExp();
        
        //System.out.println("query strExp: " + strExp);
       
        
        System.out.println("query ingridQueryToLuceneQuery: " + ingridQueryToLuceneQuery.transform(query));
        
        
    }

    
    /**
     * 
     * @param q
     * @return The parsed {@link IngridQuery}
     * @throws ParseException
     */
    private IngridQuery parse(String q) throws ParseException {
        QueryStringParser parser = new QueryStringParser(new StringReader(q));
        IngridQuery query = parser.parse();
        assertNotNull(query);

        return query;
    }
    
}
