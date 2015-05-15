package com.notedroid.fuck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class NoteDroid extends ActionBarActivity {

	private static final int ACTIVITY_VIEW_NOTE_LIST = 0;
	private static final int ACTIVITY_CREATE_NEW_NOTE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_droid);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Opened when the user clicked the About button
	public void openAboutDialog(View view) {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	// Called when the user clicks the New Note button
	public void openNewNote(View view) {
		Intent i = new Intent(this, NoteEditor.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, new Long(-1));
		i.putExtra(NoteEditor.NOTEEDITOR_MODE, NoteEditor.NOTEEDITOR_MODE_EDIT);
		startActivityForResult(i, ACTIVITY_CREATE_NEW_NOTE);
	}

	// Called when the use clicks the View Notes Button
	public void openNoteList(View view) {
		Intent i = new Intent(this, NoteList.class);
		startActivityForResult(i, ACTIVITY_VIEW_NOTE_LIST);
	}
}
