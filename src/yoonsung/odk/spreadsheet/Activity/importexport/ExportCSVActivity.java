package yoonsung.odk.spreadsheet.Activity.importexport;

import java.io.File;
import java.util.Map;

import yoonsung.odk.spreadsheet.R;
import yoonsung.odk.spreadsheet.Activity.importexport.IETabActivity.PickFileButtonListener;
import yoonsung.odk.spreadsheet.Database.DataTable;
import yoonsung.odk.spreadsheet.Database.TableList;
import yoonsung.odk.spreadsheet.csvie.CSVException;
import yoonsung.odk.spreadsheet.csvie.CSVExporter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class ExportCSVActivity extends IETabActivity {
	
	/** view IDs (for use in testing) */
	public static final int TABLESPIN_ID = 1;
	public static final int FILENAMEVAL_ID = 2;
	public static final int EXPORTBUTTON_ID = 3;
	
	/* the list of table names */
	private String[] tableNames;
	/* the table name spinner */
	private Spinner tableSpin;
	/* the text field for getting the filename */
	private EditText filenameValField;
	/* the checkbox for including source phone numbers */
	private CheckBox incPNCheck;
	/* the checkbox for including timestamps */
	private CheckBox incTSCheck;
	/* the pick file button */
	private Button pickFileButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getView());
	}
	
	/**
	 * @return the view
	 */
	private View getView() {
		LinearLayout v = new LinearLayout(this);
		v.setOrientation(LinearLayout.VERTICAL);
		// selecting table
		TextView est = new TextView(this);
		est.setText("Exporting Table:");
		est.setTextColor(R.color.black);
		v.addView(est);
		// adding the table spinner
		tableSpin = new Spinner(this);
		tableSpin.setId(TABLESPIN_ID);
		Map<String, String> tableMap = (new TableList()).getAllTableList();
		tableNames = new String[tableMap.size()];
		int counter = 0;
		for(String id : tableMap.keySet()) {
			tableNames[counter] = tableMap.get(id);
			counter++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, tableNames);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		tableSpin.setAdapter(adapter);
		tableSpin.setSelection(0);
		v.addView(tableSpin);
		// Horizontal divider
		View ruler1 = new View(this); ruler1.setBackgroundColor(R.color.black);
		v.addView(ruler1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		// options
		TextView opt = new TextView(this);
		opt.setText("Options:");
		opt.setTextColor(R.color.black);
		v.addView(opt);
		// adding the include source phone numbers checkbox
		LinearLayout incPN = new LinearLayout(this);
		incPNCheck = new CheckBox(this);
		incPNCheck.setChecked(true);
		incPN.addView(incPNCheck);
		TextView incPNLabel = new TextView(this);
		incPNLabel.setText("Include Phone Numbers");
		incPNLabel.setTextColor(R.color.black);
		incPN.addView(incPNLabel);
		v.addView(incPN);
		// adding the include timestamps checkbox
		LinearLayout incTS = new LinearLayout(this);
		incTSCheck = new CheckBox(this);
		incTSCheck.setChecked(true);
		incTS.addView(incTSCheck);
		TextView incTSLabel = new TextView(this);
		incTSLabel.setText("Include Timestamps");
		incTSLabel.setTextColor(R.color.black);
		incTS.addView(incTSLabel);
		v.addView(incTS);
		// Horizontal divider
		View ruler2 = new View(this); ruler2.setBackgroundColor(R.color.black);
		v.addView(ruler2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		// adding the filename field
		LinearLayout fn = new LinearLayout(this);
		fn.setOrientation(LinearLayout.VERTICAL);
		TextView fnLabel = new TextView(this);
		fnLabel.setText("Filename:");
		fnLabel.setTextColor(R.color.black);
		fn.addView(fnLabel);
		filenameValField = new EditText(this);
		filenameValField.setId(FILENAMEVAL_ID);
		fn.addView(filenameValField);
		v.addView(fn);
        pickFileButton = new Button(this);
        pickFileButton.setText("Pick File");
        pickFileButton.setOnClickListener(new PickFileButtonListener());
        v.addView(pickFileButton);
		// Horizontal divider
		View ruler3 = new View(this); ruler3.setBackgroundColor(R.color.black);
		v.addView(ruler3, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 2));
		// adding the export button
		Button button = new Button(this);
		button.setId(EXPORTBUTTON_ID);
		button.setText("Export");
		button.setOnClickListener(new ButtonListener());
		v.addView(button);
		// wrapping in a scroll view
		ScrollView scroll = new ScrollView(this);
		scroll.addView(v);
		return scroll;
	}
	
	/**
	 * Attempts to export a table.
	 */
	private void exportSubmission() {
		File file = new File(filenameValField.getText().toString());
		String tableName = tableNames[tableSpin.getSelectedItemPosition()];
		String tableID =
			(new Integer(new TableList().getTableID(tableName))).toString();
		try {
			(new CSVExporter()).exportTable(
					(new DataTable(tableID)).getCompleteTable(), file,
					incPNCheck.isChecked(), incTSCheck.isChecked());
			showDialog(CSVEXPORT_SUCCESS_DIALOG);
		} catch (CSVException e) {
			showDialog(CSVEXPORT_FAIL_DIALOG);
			return;
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if(resultCode == RESULT_CANCELED) {return;}
        Uri fileUri = data.getData();
        String filepath = fileUri.getPath();
        filenameValField.setText(filepath);
    }
	
	private class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			exportSubmission();
		}
	}
	
}