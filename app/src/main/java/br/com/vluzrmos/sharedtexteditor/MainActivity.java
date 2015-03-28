package br.com.vluzrmos.sharedtexteditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.vluzrmos.sharedtexteditor.util.SharedFilesHelper;


public class MainActivity extends Activity {
    protected SharedFilesHelper filesHelper;

    protected ListView listViewListFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filesHelper = new SharedFilesHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listViewListFiles = (ListView) findViewById(R.id.list_files);

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return filesHelper.getFilenames().length;
            }

            @Override
            public Object getItem(int position) {
                return filesHelper.getSharedFile(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                ViewFileItemTag fileItemTag;

                if(view==null){
                    fileItemTag = new ViewFileItemTag();

                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.file_item, null);
                    fileItemTag.textViewFilename = (TextView) view.findViewById(R.id.filename);
                    fileItemTag.textViewFileContents = (TextView) view.findViewById(R.id.file_contents);

                    view.setTag(fileItemTag);
                }
                else{
                    fileItemTag = (ViewFileItemTag) view.getTag();
                }

                if(position>=0){
                    SharedPreferences prefs = (SharedPreferences)getItem(position);

                    fileItemTag.textViewFilename.setText(prefs.getString(SharedFilesHelper.FILENAME_KEY, null));
                    fileItemTag.textViewFileContents.setText(prefs.getString(SharedFilesHelper.CONTENT_KEY, null));
                }

                return view;
            }

            class ViewFileItemTag {
                TextView textViewFilename;
                TextView textViewFileContents;
            }
        };


        listViewListFiles.setAdapter(adapter);

        listViewListFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = filesHelper.getFilenames()[position];

                Intent intent = new Intent(getApplicationContext(), AddTextFileActivity.class);

                intent.putExtra(SharedFilesHelper.FILENAME_KEY, filename);
                startActivity(intent);
            }
        });

        listViewListFiles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int p = position;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.alert_removing_file));

                SharedPreferences shared = filesHelper.getSharedFile(p);

                alertDialogBuilder.setMessage(getString(R.string.alert_removing_file_message, shared.getString(SharedFilesHelper.FILENAME_KEY, "")));

                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        removeSharedFile(p);
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_file_removed), Toast.LENGTH_LONG).show();
                    }
                });

                // set negative button: No message
                alertDialogBuilder.setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();

                return true;
            }
        });
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
        else if(id == R.id.action_add_text){
            startAddTextFileActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Action for a button
     * @param view
     */
    public void btnAddSharedFile(View view) {
        startAddTextFileActivity();
    }

    /**
     * Start an activity
     */
    private void startAddTextFileActivity() {
        Intent intent = new Intent(this, AddTextFileActivity.class);

        startActivity(intent);
    }

    /**
     * Remove um arquivo
     * @param position
     */
    private void removeSharedFile(int position) {
        filesHelper.removeSharedFile(position);
        ((BaseAdapter)listViewListFiles.getAdapter()).notifyDataSetChanged();
    }
}
