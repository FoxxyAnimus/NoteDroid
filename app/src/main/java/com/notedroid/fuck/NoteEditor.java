package com.notedroid.fuck;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditor extends Activity {

    // Constant variables
    public static final String NOTEEDITOR_MODE = "NOTEEDITOR_MODE";
    public static final String NOTEEDITOR_MODE_EDIT = "NOTEEDITOR_MODE_EDIT";
    public static final String NOTEEDITOR_MODE_SHOW = "NOTEEDITOR_MODE_SHOW";

    // Note variables
    private EditText mTitleText;
    private EditText mBodyText;
    private String mNoteMode;
    private Long mRowId;

    // Database variable
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Open database
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        // Set the layout to use activity_note_editor
        setContentView(R.layout.activity_note_editor);

        // Get the Title and Body EditText fields
        mTitleText = (EditText) findViewById(R.id.NoteEditor_NoteName);
        mBodyText = (EditText) findViewById(R.id.NoteEditor_Body);

        // Get the extra information that was passed into the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
            mNoteMode = extras.getString(NoteEditor.NOTEEDITOR_MODE);
        }

        // Make sure the text fields are not covered by the input keyboard when that appears
        if (mNoteMode.equals(NOTEEDITOR_MODE_EDIT)) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        // We populate the data of the fields. If this is a new note
        // then nothing will be added to the fields, otherwise if this
        // is am edit to an existing note, the note will be populated
        populateFields();
    }

    // Save the note and exit
    public void saveAndExit(View view) {
        saveData();
        setResult(RESULT_OK);
        finish();

    }

    // Exit the note without saving
    public void exitWithoutSave(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
        outState.putString(NoteEditor.NOTEEDITOR_MODE, mNoteMode);
    }

    // Save the note information
    private void saveData() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if ((title == null) || (title.length() == 0)) {
            title = this.getString(R.string.NoteEditor_NewNote);
        }

        if (mRowId == -1) {
            long id = mDbHelper.createNote(title, body);
            if (id > 0) {
                mRowId = id;

                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    extras.putLong(NotesDbAdapter.KEY_ROWID, mRowId);
                    extras.putString(NoteEditor.NOTEEDITOR_MODE, mNoteMode);
                }
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body);
        }

        Toast toast = Toast.makeText(getApplicationContext(), R.string.NoteEditor_SavedNoteToast, Toast.LENGTH_SHORT);
        toast.show();
    }

    // Populate the fields of the note when in edit mode
    private void populateFields() {
        if (mRowId != -1) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        } else {
            mTitleText.setHint(R.string.NoteEditor_NewNote);
        }
    }
}
