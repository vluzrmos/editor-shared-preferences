package br.com.vluzrmos.sharedtexteditor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.vluzrmos.sharedtexteditor.util.SharedFilesHelper;


public class AddTextFileActivity extends Activity {

    EditText edtTxtFilename;

    EditText edtTxtFileContents;

    protected SharedFilesHelper filesHelper;

    private boolean is_editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text_file);

        filesHelper = new SharedFilesHelper(this);

        edtTxtFilename = (EditText) findViewById(R.id.edt_filename);
        edtTxtFileContents = (EditText) findViewById(R.id.edt_file_contents);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setEditingSharedFile();
    }

    private void setEditingSharedFile() {
        Bundle bundle = getIntent().getExtras();

        if(bundle != null && bundle.containsKey(SharedFilesHelper.FILENAME_KEY)){
            SharedPreferences shared = filesHelper.getSharedFile(bundle.getString(SharedFilesHelper.FILENAME_KEY, ""));

            edtTxtFilename.setText(shared.getString(SharedFilesHelper.FILENAME_KEY, ""));
            edtTxtFilename.setVisibility(View.GONE);

            String contents = shared.getString(SharedFilesHelper.CONTENT_KEY, "");

            edtTxtFileContents.append(contents);

            setTitle(getString(R.string.title_activity_filename, shared.getString(SharedFilesHelper.FILENAME_KEY, "")));

            is_editing = true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_text_file, menu);

        if(is_editing){
            MenuItem item = menu.findItem(R.id.action_save);

            item.setTitle(R.string.edit);
        }

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
        else if(id == R.id.action_save) {
            saveNewSharedFile();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * save a new shared file
     */
    private void saveNewSharedFile() {
        String filename  = edtTxtFilename.getText().toString().trim();
        String contents  = edtTxtFileContents.getText().toString().trim();

        if(filename.isEmpty()){
            setFilenameError(getString(R.string.validation_filename_required));
        }
        else if(!SharedFilesHelper.isValidName(filename)){
            setFilenameError(getString(R.string.validation_filename_invalid));
        }
        else if((!is_editing) && filesHelper.sharedFileExists(filename)){
            setFilenameError(getString(R.string.validation_filename_exists));
        }
        else{
            filesHelper.addSharedFile(filename, contents);
            startMainActivity();
            Toast.makeText(this, getString(R.string.file_saved), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void setFilenameError(String error){
        Toast.makeText(this, getString(R.string.validation_error), Toast.LENGTH_LONG).show();
        edtTxtFilename.setError(error);
    }


    /**
     * start the main activity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

        finishAffinity();
    }
}
