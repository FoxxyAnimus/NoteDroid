package com.notedroid.fuck;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class NoteList extends ListActivity {
    private static final int ACTIVITY_CREATE_NOTE = 0;
    private static final int ACTIVITY_EDIT_NOTE = 1;
    private static final int ACTIVITY_PROPERTIES_NOTE = 2;
    private static final int MENU_DELETE_NOTE_ID = Menu.FIRST + 1;
    private static final int MENU_SHOW_PROPERTIES_ID = Menu.FIRST + 2;

    private NotesDbAdapter mDbHelper;

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_note_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    public void createNote(View veiw) {
        Intent i = new Intent(this, NoteEditor.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, new Long(-1));
        i.putExtra(NoteEditor.NOTEEDITOR_MODE, NoteEditor.NOTEEDITOR_MODE_EDIT);
        startActivityForResult(i, ACTIVITY_CREATE_NOTE);
    }

    // Displays a delete dialog to confirm the deletion of the note
    // @param rowId The Id of the note being deleted
    private void doDeleteNote(final long rowId) {
        String message = this.getString(R.string.NoteList_DeleteNoteQuestion);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(this.getString(R.string.NoteList_DeleteConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mDbHelper.deleteNote(rowId);
                fillData();

                Toast toast = Toast.makeText(getApplicationContext(), R.string.NoteList_DeletedNoteToast, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        builder.setNegativeButton(this.getString(R.string.NoteList_DeleteCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setIcon(R.drawable.delete);
        builder.create().show();
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllNotes();
        startManagingCursor(c);

        String[] from = new String[] { NotesDbAdapter.KEY_TITLE };
        int[] to = new int[] { R.id.text1 };
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
        setListAdapter(notes);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent i = new Intent(this, NoteEditor.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        i.putExtra(NoteEditor.NOTEEDITOR_MODE, NoteEditor.NOTEEDITOR_MODE_SHOW);
        startActivityForResult(i, ACTIVITY_EDIT_NOTE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        long selectedId = ((AdapterView.AdapterContextMenuInfo) menuInfo).id;

        if (selectedId != -1) {
            menu.setHeaderTitle(mDbHelper.getNoteById(selectedId).getTitle());
        }

        menu.add(0, MENU_DELETE_NOTE_ID, 0, R.string.NoteList_MenuDeleteNote);
        menu.add(0, MENU_SHOW_PROPERTIES_ID, 0, R.string.NoteList_MenuShowProperties);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;

        switch(item.getItemId()) {
            case MENU_DELETE_NOTE_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                doDeleteNote(info.id);
                return true;
            case MENU_SHOW_PROPERTIES_ID:
                // Display dialog with the note properties
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent i = new Intent(this, PropertiesActivity.class);
                i.putExtra(NotesDbAdapter.KEY_ROWID, info.id);
                startActivityForResult(i, ACTIVITY_PROPERTIES_NOTE);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
