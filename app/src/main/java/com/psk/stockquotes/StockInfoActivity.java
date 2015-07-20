package com.psk.stockquotes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;


public class StockInfoActivity extends ActionBarActivity {

    private static final String TAG = "STOCKQUOTE";
    TextView companyNameTextView;
    TextView yearLowTextView;
    TextView yearHighTextView;
    TextView daysLowTextView;
    TextView daysHighTextView;
    TextView lastTradePriceOnlyTextView;
    TextView changeTextView;
    TextView daysRangeTextView;
    TextView stockExchangeTextView;

    private static final String KEY_ITEM = "Quote";
    static final String KEY_NAME = "Name";
    static final String KEY_YEAR_HIGH = "YearHigh";
    static final String KEY_DAYS_LOW = "DaysLow";
    static final String KEY_DAYS_HIGH = "DaysHigh";
    static final String KEY_LAST_TRADE_PRICE = "LostTradePriceOnly";
    static final String KEY_CHANGE = "Change";
    static final String KEY_DAYS_RANGE = "DaysRange";

    private String daysLow = "";
    private String daysHigh = "";
    private String yearLow = "";
    private String yearHigh = "";
    private String name = "";
    private String lastTradePriceOnly = "";
    private String change = "";
    private String daysRange = "";

    private String yahooUrlFirst = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
    private String yahooUrlSecond = "%22)&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    String[][] xmlPullParserArray = {{"AverageDailyVolume", "0"}, {"Change", "0"}, {"DaysLow", "0"}, {"DaysHigh", "0"}, {"YearLow", "0"}, {"YearHigh", "0"}, {"MarketCapitalization", "0"}, {"LastTradePriceOnly", "0"}, {"LastTradePriceOnly", "0"}, {"Name", "0"}, {"Symbol", "0"}, {"Volume", "0"}, {"StockExchange", "0"}};
    int parserArrayIncrement = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);
        //Getting the values from Intent
        Intent intent = getIntent();
        String stockSymbol = intent.getStringExtra(MainActivity.STOCK_SYMBOL);
        //Initializing TextViews
        companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
        yearLowTextView = (TextView) findViewById(R.id.yearLowTextView);
        yearHighTextView = (TextView) findViewById(R.id.yearHighTextView);
        daysLowTextView = (TextView) findViewById(R.id.daysLowTextView);
        daysHighTextView = (TextView) findViewById(R.id.daysHighTextView);
        lastTradePriceOnlyTextView = (TextView) findViewById(R.id.lastTradePriceOnlyTextView);
        changeTextView = (TextView) findViewById(R.id.changeTextView);
        daysRangeTextView = (TextView) findViewById(R.id.daysRangeTextView);
        stockExchangeTextView = (TextView) findViewById(R.id.stockExchangeTextView);
        //Creating URl
        final String yqlUrl = yahooUrlFirst + stockSymbol + yahooUrlSecond;

        //Calling AsyncTask Passing YQL url
        new MyAsyncTask().execute(yqlUrl);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            /*
            //-----------------------------Below method was to get information using DOM and DOM parser--------------------------------
            //It takes lot of CPU power, memory and needs longer time for execution.
            try {
                //Generation url from string url
                URL url = new URL(args[0]);
                //creating urlConnection
                URLConnection connection;
                //Opened Url connection
                connection = url.openConnection();
                //requesting http request with open url connection
                HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                //Checking response
                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //Getting Input Stream
                    InputStream is = httpURLConnection.getInputStream();
                    //Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    //Defines the API to obtain DOM Document instances from an XML document. Using this class, an application programmer can obtain a Document from XML.
                    //An instance of this class can be obtained from the DocumentBuilderFactory.newDocumentBuilder() method. Once an instance of this class is obtained,
                    // XML can be parsed from a variety of input sources. These input sources are InputStreams, Files, URLs, and SAX InputSources.

                    DocumentBuilder db = dbf.newDocumentBuilder();
                    //dom parser
                    Document dom = db.parse(is);
                    //The Element interface represents an element in an HTML or XML document
                    Element docEle = dom.getDocumentElement();
                    //The NodeList interface provides the abstraction of an ordered collection of nodes,
                   // without defining or constraining how this collection is implemented. NodeList objects in the DOM are live.
                    NodeList nodeList = docEle.getElementsByTagName("quote");
                    //Returns the index item in the collection. If index is greater than or equal to the number of nodes in the list, this returns null.

                    if (nodeList != null && nodeList.getLength() > 0) {
                        StockInfo theStock = getStockInformation(docEle);
                        name = theStock.getName();
                        daysLow = theStock.getDaysLow();
                        daysHigh = theStock.getDaysHigh();
                        yearLow = theStock.getYearLow();
                        yearHigh = theStock.getYearHigh();
                        lastTradePriceOnly = theStock.getLastTradePriceOnly();
                        change = theStock.getChange();
                        daysRange = theStock.getDaysRange();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }*/
            //--------------------Above method was to get information using DOM and DOM parser-----------------------------

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(getUrlData(args[0])));
                beginDocument(parser, "query");
                int eventType;
                eventType = parser.getEventType();
                do {
                    nextElement(parser);
                    parser.next();
                    eventType = parser.getEventType();
                    if (eventType == parser.TEXT) {
                        String valueFromXml = parser.getText();
                        xmlPullParserArray[parserArrayIncrement++][1] = valueFromXml;

                    }
                } while (eventType != XmlPullParser.END_DOCUMENT);


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        public InputStream getUrlData(String url) throws URISyntaxException, IOException {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet method = new HttpGet(new URI(url));
            HttpResponse response = client.execute(method);
            return response.getEntity().getContent();
        }

        public final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
            int type;
            while ((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT) {
                ;
            }
            if (type != parser.START_TAG) {
                throw new XmlPullParserException("No Start Tag Found");

            }
            if (!parser.getName().equals(firstElementName)) {
                throw new XmlPullParserException("Unexpected Start tag found i.e." + parser.getName() + " Expected " + firstElementName);
            }
        }

        public final void nextElement(XmlPullParser parser) throws IOException, XmlPullParserException {
            int type;
            while ((type = parser.next()) != parser.START_TAG && type != parser.END_DOCUMENT) {
                ;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            companyNameTextView.setText("Company: " + xmlPullParserArray[9][1]);
            yearLowTextView.setText("YearLow: " + xmlPullParserArray[4][1]);
            yearHighTextView.setText("YearHigh: " + xmlPullParserArray[5][1]);
            daysLowTextView.setText("Days Low:" + xmlPullParserArray[2][1]);
            daysHighTextView.setText("Days High: " + xmlPullParserArray[3][1]);
            lastTradePriceOnlyTextView.setText("Price: " + xmlPullParserArray[7][1]);
            changeTextView.setText("Change: " + xmlPullParserArray[1][1]);
            daysRangeTextView.setText("Range: " + xmlPullParserArray[8][1]);
            stockExchangeTextView.setText("Stock Exchange: " + xmlPullParserArray[12][1]);
        }
    }

    /*---------------------------------------------Part of a DOM----------------------------------------------------------------------
        private StockInfo getStockInformation(Element entry) {
            String stockName = getTextValue(entry, "Name");
            String stockDaysLow = getTextValue(entry, "DaysLow");
            String stockDaysHigh = getTextValue(entry, "DaysHigh");
            String stockYearLow = getTextValue(entry, "YearLow");
            String stockYearHigh = getTextValue(entry, "YearHigh");
            String stockLastTradePriceOnly = getTextValue(entry, "LastTradePriceOnly");
            String stockChange = getTextValue(entry, "Change");
            String stockDaysRange = getTextValue(entry, "DaysRange");
            StockInfo theStock = new StockInfo(stockDaysLow, stockDaysHigh, stockYearLow, stockYearHigh, stockName, stockLastTradePriceOnly, stockChange, stockDaysRange);
            return theStock;
        }
    //---------------------------------------------Part of a DOM----------------------------------------------------------------------
        private String getTextValue(Element entry, String tagName) {
            String TagValueToReturn = null;
            NodeList nodeList = entry.getElementsByTagName(tagName);
            if (nodeList != null && nodeList.getLength() > 0) {
                Element element = (Element) nodeList.item(0);
                TagValueToReturn = element.getTextContent();
            }
            return TagValueToReturn;
        }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
