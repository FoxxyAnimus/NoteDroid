package com.notedroid.fuck;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.text.DateFormat;


public class PropertiesActivity extends Activity {

    private NotesDbAdapter mDbHelper;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_properties);

        // Open the database connection
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        // Get the extra information that was passed into the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
        }

        // Get the contents of the notes based on the ID
        Note note = mDbHelper.getNoteById(mRowId);

        // Set the text of the first TextView
        TextView text1 = (TextView) this.findViewById(R.id.PropertiesDialog_Text1);
        text1.setText(this.getString(R.string.PropertiesDialog_Name) + ": " + note.getTitle());

        // Set the text of the second TextView
        TextView text2 = (TextView) this.findViewById(R.id.PropertiesDialog_Text2);
        text2.setText(this.getString(R.string.PropertiesDialog_Type) + ": " + this.getString(R.string.PropertiesDialog_NoteType));

        // Set the text of the third TextView
        TextView text3 = (TextView) this.findViewById(R.id.PropertiesDialog_Text3);
        String creationDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(note.getCreationDate());
        text3.setText(this.getString(R.string.PropertiesDialog_CreationDate) + ": " + creationDate);

        // Set the text of the fourth TextView
        TextView text4 = (TextView) this.findViewById(R.id.PropertiesDialog_Text4);
        String modificationDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(note.getModificationDate());
        text4.setText(this.getString(R.string.PropertiesDialog_ModificationDate) + ": " + modificationDate);
    }

    public void closePropertiesDialog(View view) {
        finish();
    }
}
