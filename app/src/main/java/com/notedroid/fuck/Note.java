package com.notedroid.fuck;

import java.util.Date;

public class Note {
    // Define the note variables.
    private long mId;
    private Date mCreationDate;
    private Date mModificationDate;
    private String mTitle;
    private String mBody;

    public Note(long id, Date creationDate, Date modificationDtae, String title, String body) {
        mId = id;
        mCreationDate = creationDate;
        mModificationDate = modificationDtae;
        mTitle = title;
        mBody = body;
    }

    // Returns the id of the note
    public long getId() {
        return mId;
    }

    // Returns the creation date of the note
    public Date getCreationDate() {
        return mCreationDate;
    }

    //Returns the update date of the note
    public Date getModificationDate() {
        return mModificationDate;
    }

    // Returns the title of the note
    public String getTitle() {
        return mTitle;
    }
    //Returns the body of the note
    public String getBody() {
        return mBody;
    }
}
