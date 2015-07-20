package com.psk.stockquotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
    private EditText enterSymbolEditText;
    private Button enterSymbolButton;
    private Button clearSymbolButton;
    private TableLayout stockTableScrollView;
    private SharedPreferences stockSymbolEntered;
    public static final String STOCK_SYMBOL = "com.psk.stockquotes.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockSymbolEntered = getSharedPreferences("stockList", MODE_PRIVATE);
        enterSymbolEditText = (EditText) findViewById(R.id.enterSymbolEditText);
        enterSymbolButton = (Button) findViewById(R.id.enterSymbolButton);
        clearSymbolButton = (Button) findViewById(R.id.clearSymbolButton);
        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
        enterSymbolButton.setOnClickListener(enterSymbolButtonListener);
        clearSymbolButton.setOnClickListener(clearSymbolButtonListener);

        //For already present stocks to show call update stock symbol method
        updateStockSymbol(null);
        // since we are passing null first it will update present stocks only

    }

    public View.OnClickListener enterSymbolButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (enterSymbolEditText.length() > 0) {
                saveStockSymbol(enterSymbolEditText.getText().toString());
                enterSymbolEditText.setText("");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.ErrorTitle);
                builder.setMessage(R.string.ErrorMessage);
                AlertDialog theAlter = builder.create();
                theAlter.show();
            }
        }
    };

    public View.OnClickListener clearSymbolButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteAll();
            SharedPreferences.Editor pEdit = stockSymbolEntered.edit();
            pEdit.clear();
            pEdit.apply();
        }
    };

    private void deleteAll() {
        stockTableScrollView.removeAllViews();
    }


    private void updateStockSymbol(String newStockSymbol) {
        // get all the stock symbols already saved from SharedPreferences into string array
        String[] stockList = stockSymbolEntered.getAll().keySet().toArray(new String[0]);
        //Sort without considering case sensitivity
        Arrays.sort(stockList, String.CASE_INSENSITIVE_ORDER);
        //while updating if the new symbol is entered, insert it in array
        if (newStockSymbol != null) {
            //while inserting new stock pass new stock symbol AND arrayIndex
            insertInScrollView(newStockSymbol, Arrays.binarySearch(stockList, newStockSymbol));
        } else {
            for (int i = 0; i < stockList.length; i++) {
                //this will update existing stocks
                insertInScrollView(stockList[i], i);
            }
        }

    }

    //Actual insertion of new stock in ScrollView happens here
    private void insertInScrollView(String stock, int arrayIndex) {
        //Inflating layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Adding a new row
        View newStockRow = inflater.inflate(R.layout.symbol_list, null);
        TextView symbolListQuotesTextView = (TextView) newStockRow.findViewById(R.id.symbolListQuotesTextView);
        //Setting value of stock
        symbolListQuotesTextView.setText(stock);
        //------Buttons-------
        Button symbolListQuotesButton = (Button) newStockRow.findViewById(R.id.symbolListQuotesButton);
        symbolListQuotesButton.setOnClickListener(symbolListQuotesButtonListener);
        Button symbolListWebButton = (Button) newStockRow.findViewById(R.id.symbolListWebButton);
        symbolListWebButton.setOnClickListener(symbolListWebButtonListener);
        //Adding View to Scroll View
        stockTableScrollView.addView(newStockRow);
    }

    public View.OnClickListener symbolListQuotesButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TableRow tableRow = (TableRow) v.getParent();
            TextView symbolListQuotesTextView = (TextView) tableRow.findViewById(R.id.symbolListQuotesTextView);
            String StockSymbol = symbolListQuotesTextView.getText().toString();
            Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
            intent.putExtra(STOCK_SYMBOL, StockSymbol);
            startActivity(intent);
        }
    };
    public View.OnClickListener symbolListWebButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow tableRow = (TableRow) v.getParent();
            TextView symbolListQuotesTextView = (TextView) tableRow.findViewById(R.id.symbolListQuotesTextView);
            String StockSymbol = symbolListQuotesTextView.getText().toString();
            String stockUrl = getString(R.string.url) + StockSymbol;
            Intent getWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockUrl));
            startActivity(getWebPage);
        }
    };

    private void saveStockSymbol(String newStockSymbol) {
        String isStockNew = stockSymbolEntered.getString(newStockSymbol, null);
        SharedPreferences.Editor pEditor = stockSymbolEntered.edit();
        pEditor.putString(newStockSymbol, newStockSymbol);
        pEditor.apply();
        if (isStockNew == null) {
            updateStockSymbol(newStockSymbol);
        }
    }

    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
