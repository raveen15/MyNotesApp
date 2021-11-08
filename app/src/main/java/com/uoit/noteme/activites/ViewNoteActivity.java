package com.uoit.noteme.activites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uoit.noteme.R;
import com.uoit.noteme.database.NotesDatabase;
import com.uoit.noteme.entities.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;

    private String selectedNoteColor;

    private Note note;
    private Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);

        textDateTime = findViewById(R.id.textDateTime);
        textDateTime.setText(new SimpleDateFormat(
                "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date().getTime())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(v -> saveNote());

        selectedNoteColor = "#333333";

        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        initMiscellaneous();
        setSubtitleIndicatorColor();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            note = (Note) extras.get("note");
        }

        Button btn = (Button) findViewById(R.id.delButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage();
            }
        });
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
    }
    private void setViewOrUpdateNote() {
        inputNoteSubtitle.setText((alreadyAvailableNote.getSubtitle()));
        inputNoteTitle.setText((alreadyAvailableNote.getTitle()));
        inputNoteText.setText((alreadyAvailableNote.getNoteText()));
        textDateTime.setText((alreadyAvailableNote.getDateTime()));

        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            //imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            //imageNote.setVisibility(View.VISIBLE);
            //selectedImagePath = alreadyAvailableNote.getImagePath();
        }

        if (alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            //textWebURL.setText(alreadyAvailableNote.getWebLink());
            //layoutWebURL.setVisibility(View.VISIBLE);
        }
    }

        private void saveNote(){
            final String noteTitle = inputNoteTitle.getText().toString().trim();
            final String noteSubtitle = inputNoteSubtitle.getText().toString().trim();
            final String noteText = inputNoteText.getText().toString().trim();
            final String dateTimeStr = textDateTime.getText().toString().trim();

            if (noteTitle.isEmpty()) {
                Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            final Note note = new Note();
            note.setTitle(noteTitle);
            note.setSubtitle(noteSubtitle);
            note.setNoteText(noteText);
            note.setDateTime(dateTimeStr);
            note.setColor(selectedNoteColor);
                if(alreadyAvailableNote != null){
                    note.setId(alreadyAvailableNote.getId());
                }

            @SuppressLint("StaticFieldLeak")
            class SaveNoteTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
                new SaveNoteTask().execute();
    }

    private void deleteNotes() {

        @SuppressLint("StaticFieldLeak")
        class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                return NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().deleteNote(note);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                intent.putExtra("isNoteDeleted", true);
                setResult(RESULT_OK, intent);
            }
        }

        new DeleteNoteTask().execute();
    }

    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutColor);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(v -> {
            selectedNoteColor = "#333333";
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(v -> {
            selectedNoteColor = "#FFFF00";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(v -> {
            selectedNoteColor = "#7B99F4";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(v -> {
            selectedNoteColor = "#98D319";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(v -> {
            selectedNoteColor = "#19CED3";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
            setSubtitleIndicatorColor();
        });
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    public void alertMessage(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
//                        Toast.makeText(ViewNoteActivity.this, "Yes Clicked",
//                                Toast.LENGTH_LONG).show();
                        deleteNotes();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent, 1);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        Toast.makeText(ViewNoteActivity.this, "No Clicked",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}